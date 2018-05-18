package com.mysterymaster.androidapp;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mysterymaster.puzzle.*;
import com.mysterymaster.solver.IViewer;
import com.mysterymaster.solver.Solver;
import com.mysterymaster.solver.Spots;

/**
 * The Main Activity class manages the UI and implements the IViewer interface.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-14
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
public class MainActivity extends AppCompatActivity implements IViewer {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		initFab();
		initActivity();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) return true;

		return super.onOptionsItemSelected(item);
	}

	// <editor-fold defaultstate="collapsed" desc="Floating Action Button">

	/** Floating Action Button. Must sync with Solve button. See https://icons8.com/icon/set/ic-media-play/color */
	private FloatingActionButton fab;

	/** Initializes the Floating Action Button (fab). Mode is either: 0=play, 1=pause, 2=resume. */
	private void initFab() {
		fab = findViewById(R.id.fab);
		fab.setTag(0);
		fab.setOnClickListener(
			view -> {
				int mode = (int) fab.getTag();
				String labels[] = {"Solve", "Pause", "Resume"};
				saySnackbar(view, "You clicked the " + labels[mode] + " fab.");
				//Snackbar.make(view, "You clicked the " + labels[mode] + " fab!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
				switch (mode) {
					case 0: doSolve(); break;
					case 1: doPause(); break;
					case 2: doResume(true); break;
				}
			}
		);
	}

	/** Sets the Fab icon to play (mode=0). Called by reset, sayStopped. */
	private void setFabToPlay() {
		fab.setImageResource(android.R.drawable.ic_media_play);
		fab.setTag(0);
	}

	/** Sets the Fab icon to pause (mode=1). Called by doResume, sayStarted. */
	private void setFabToPause() {
		fab.setImageResource(android.R.drawable.ic_media_pause);
		fab.setTag(1);
	}

	/** Sets the Fab icon to resume (mode=2). Called by sayWait. */
	private void setFabToResume() {
		fab.setImageResource(android.R.drawable.ic_media_next);
		fab.setTag(2);
	}

	/** Sets the visibility of the Fab. Called by initActivity. */
	void setFabVisibility() {
		 if (spots.okShowFab) showFab(); else hideFab();
	}

	private void showFab() {
		fab.show();
		CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
		FloatingActionButton.Behavior behavior = (FloatingActionButton.Behavior) params.getBehavior();
		if (behavior != null) behavior.setAutoHideEnabled(true);
	}

	private void hideFab() {
		CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
		FloatingActionButton.Behavior behavior = (FloatingActionButton.Behavior) params.getBehavior();
		if (behavior != null) behavior.setAutoHideEnabled(false);
		fab.hide();
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Snackbar">

	/**
	 * Displays the message in a Snackbar. Called when the fab or a board button is pressed.
	 * Duration can be LENGTH_INDEFINITE, LENGTH_LONG, or LENGTH_SHORT.
	 * @param view View defined for the fab or board button.
	 * @param msg Message.
	 */
	void saySnackbar(final View view, final String msg) {
		Snackbar.make(view, msg, 25).setAction("Action", null).show();
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Activity">

	/** Locker object. */
	Locker locker;

	/** Setup Options object. */
	Spots spots;

	/** Solver object. */
	Solver solver;

	/** Worker thread. */
	private Thread worker = null;

	/** Puzzle picker. */
	private PuzzlePicker puzzlePicker;

	/** Board manager. */
	private BoardViewer boardViewer;

	/** Tabbed interface manager. */
	private TabbyViewer tabbyViewer;

	/** Initializes the Activity. Called by the onCreate method. */
	private void initActivity() {
		locker = new Locker(getPreferences(MODE_PRIVATE));
		spots = new Spots(locker);
		solver = new Solver(this, spots);

		setFabVisibility();

		// Create the Board, Tabby, and Puzzle picker components in that order.
		boardViewer = new BoardViewer(this);
		tabbyViewer = new TabbyViewer(this);
		puzzlePicker = new PuzzlePicker(this);

		setPuzzle(null);
	}

	/**
	 * Returns the display metrics for the current screen orientation.<br>
	 * This method must be manually called. Called by baseViewer.updateAppDimensions.
	 * @return Display Metrics.
	 */
	public DisplayMetrics getDisplayMetrics() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics;
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="Viewer">

	/** Returns true if the worker thread is working, otherwise false. */
	private boolean isWorking() {
		return worker != null && worker.isAlive();
	}

	/**
	 * Sets and validates the puzzle.<br>Called by the puzzle picker.
	 * @param puzzle Puzzle, or null.
	 */
	void setPuzzle(final Puzzle puzzle) {
		int rs = 0;
		if (puzzle != null) {
			rs = puzzle.validate(solver);
		}
		solver.setPuzzle(puzzle);
		reset();

		if (puzzle != null) {
			String msg = "You selected " + puzzle.myTitle + " rs=" + rs;
			boardViewer.setMsg(msg);
		}

		boardViewer.setPuzzle(puzzle);
		tabbyViewer.setPuzzle(puzzle);
		fab.setEnabled(puzzle != null);

		// Automatically solve the valid puzzle if auto-run is true.
		if (puzzle != null && rs == 0 && spots.okAutorun) doSolve();
	}

	/**
	 * Resets the solver and viewer.<br>
	 * Called by setPuzzle, doSolve, or when the Reset button is clicked in the Board form.
	 */
	void reset() {
		setFabToPlay();
		solver.reset();
		boardViewer.reset();
		tabbyViewer.reset();
	}

	/**
	 * Solves the puzzle.<br>
	 * Called when the Solve button is clicked, or a puzzle is selected and auto-run is true.
	 */
	void doSolve() {
		reset();
		worker = solver.getThread(0);
		worker.start();
	}

	/** Requests the solver to pause. Called by the BoardViewer when the Pause button is pressed. */
	void doPause() {
		spots.okPauseNext = true;
	}

	/**
	 * Resumes the worker thread by calling its interrupt method.
	 * Called when the Resume button is clicked or by sayWait if no pause is necessary.
	 */
	void doResume(final boolean okPause) {
		if (okPause) {
			boardViewer.sayPause();
			setFabToPause();
		}
		if (worker != null) worker.interrupt();
	}

	/** Requests the solver to stop solving. Called when the Quit button is clicked. */
	void doQuit() {
		solver.doQuit();
	}

	/**
	 * Submits the user's mark. Called when a grid cell containing the "Maybe" verb is clicked.
	 * Note: The mark is ignored if the solver is currently working.
	 * @param noun1 Noun 1.
	 * @param verb Verb.
	 * @param noun2 Noun 2.
	 */
	void addMarkByUser(final Noun noun1, final Verb verb, final Noun noun2) {
		jot("exec activity.addMarkByUser noun1=" + noun1 + " verb=" + verb + " noun2=" + noun2 + " isWorking? " + isWorking());
		if (isWorking()) return;

		int rs = solver.addMarkByUser(noun1, verb, noun2);
		jot("activity.addMarkByUser rs=" + rs);
		if (rs != 0) return;

		worker = solver.getThread(1);
		worker.start();
		jot("done activity.addMarkByUser");
	}

	/**
	 * Undo all marks back to and including the last mark entered by the user.<br>
	 * Called by the grids viewer when the Undo button is pressed.
	 */
	void undoUserMark() {
		jot("exec activity.undoUserMark isWorking? " + isWorking());
		if (isWorking()) return;

		Mark mark = solver.getLastUserMark();
		if (mark == null) return;

		worker = solver.getThread(2);
		worker.start();
		jot("done activity.undoUserMark");
	}

	/**
	 * Updates the board if the user requested a pause.
	 * @param okPause Boolean.
	 */
	private void sayWait(final boolean okPause) {
		if (okPause) {
			spots.okPauseNext = false;
			boardViewer.sayResume();
			setFabToResume();
		}
		else
			doResume(false);
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="IViewer">

	@Override
	public void jot(final String msg) {
		BaseViewer.print(msg);
	}

	@Override
	public void sayStarted(final String msg) {
		boolean okPause = spots.sayStarted(msg);
		runOnUiThread(() -> {
			boardViewer.sayStarted(msg);
			tabbyViewer.sayStarted();
			puzzlePicker.setEnabled(false);
			setFabToPause();
			sayWait(okPause);
		});
	}

	@Override
	public void sayStopped(final String msg) {
		boolean okPause = spots.sayStopped();
		runOnUiThread(() -> {
			boardViewer.sayStopped(msg);
			tabbyViewer.sayStopped();
			puzzlePicker.setEnabled(true);
			setFabToPlay();
			sayWait(okPause);
		});
	}

	@Override
	public void sayLevel(final String msg) {
		boolean okPause = spots.sayLevel();
		runOnUiThread(() -> {
			boardViewer.sayLevel(msg);
			sayWait(okPause);
		});
	}

	@Override
	public void saySolution(final String msg) {
		boolean okPause = spots.saySolution();
		runOnUiThread(() -> {
			boardViewer.setMsg(msg);
			sayWait(okPause);
		});
	}

	@Override
	public void sayAddMark(final String msg, final Mark mark) {
		boolean okPause = spots.sayAddMark(mark);
		runOnUiThread(() -> {
			boardViewer.sayAddMark(msg);
			tabbyViewer.sayAddMark(mark);
			sayWait(okPause);
		});
	}

	@Override
	public void sayRemoveMark(final String msg, final Mark mark) {
		boolean okPause = spots.sayRemoveMark(mark);
		runOnUiThread(() -> {
			boardViewer.sayRemoveMark(msg);
			tabbyViewer.sayRemoveMark(mark);
			sayWait(okPause);
		});
	}

	@Override
	public void sayValidMark(final String msg, final Mark mark) {
		boolean okPause = spots.sayValidMark();
		runOnUiThread(() -> {
			boardViewer.setMsg(msg);
			tabbyViewer.sayValidMark(mark);
			sayWait(okPause);
		});
	}

	@Override
	public void sayContradiction(final String msg) {
		boolean okPause = spots.sayContradiction();
		runOnUiThread(() -> {
			boardViewer.setMsg(msg);
			sayWait(okPause);
		});
	}

	@Override
	public void sayFactViolation(final String msg, final Mark mark, final Fact fact) {
		boolean okPause = spots.sayFactViolation();
		runOnUiThread(() -> {
			boardViewer.sayFactViolation(msg);
			tabbyViewer.sayFactViolation(fact);
			sayWait(okPause);
		});
	}

	@Override
	public void sayRuleViolation(final String msg, final Mark mark, final Rule rule) {
		boolean okPause = spots.sayRuleViolation();
		runOnUiThread(() -> {
			boardViewer.sayRuleViolation(msg);
			tabbyViewer.sayRuleViolation(rule);
			sayWait(okPause);
		});
	}

	@Override
	public void sayLawViolation(final String msg, final Mark mark) {
		boolean okPause = spots.sayLawViolation();
		runOnUiThread(() -> {
			boardViewer.setMsg(msg);
			sayWait(okPause);
		});
	}

	@Override
	public void sayPlacers(final String msg, final Mark mark, final Rule rule) {
		boolean okPause = spots.sayPlacers();
		runOnUiThread(() -> {
			boardViewer.setMsg(msg);
			tabbyViewer.sayPlacers(mark, rule);
			sayWait(okPause);
		});
	}

	// </editor-fold>
}
