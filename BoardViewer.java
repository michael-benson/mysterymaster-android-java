package com.mysterymaster.androidapp;

import android.content.res.Resources;
import android.widget.Button;
import android.widget.TextView;

import com.mysterymaster.puzzle.Puzzle;

/**
 * The Board Viewer class manages the Board form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class BoardViewer extends BaseViewer {
	private final String strPause;
	private final String strQuit;
	private final String strReset;
	private final String strResume;
	private final String strSolve;

	private final String msgReset;

	// References to fields in the Board form.
	private final TextView txtLevelNum;
	private final TextView txtNumGuesses;
	private final TextView txtNumPairs;
	private final TextView txtMaxPairs;
	private final TextView txtNumMarks;
	private final TextView txtMaxMarks;
	private final TextView txtNumFacts;
	private final TextView txtMaxFacts;
	private final TextView txtNumFactHits;
	private final TextView txtNumRules;
	private final TextView txtMaxRules;
	private final TextView txtNumRuleHits;
	private final TextView txtMessage;

	private final Button btnSolve;
	private final Button btnReset;

	/**
	 * Constructor.
	 * @param activity Main Activity.
	 */
	BoardViewer(final MainActivity activity) {
		this.activity = activity;
		this.solver = activity.solver;

		// Initialize string resources.
		Resources res = activity.getResources();
		strPause  = res.getString(R.string.Pause);
		strQuit   = res.getString(R.string.Quit);
		strReset  = res.getString(R.string.Reset);
		strResume = res.getString(R.string.Resume);
		strSolve  = res.getString(R.string.Solve);

		msgReset = String.format("%s\n%s\n%s\n%s", res.getString(R.string.msgReset1), res.getString(R.string.msgReset2), res.getString(R.string.msgReset3), res.getString(R.string.msgReset4));

		txtLevelNum    = activity.findViewById(R.id.txtLevelNum);
		txtNumGuesses  = activity.findViewById(R.id.txtNumGuesses);
		txtNumPairs    = activity.findViewById(R.id.txtNumPairs);
		txtMaxPairs    = activity.findViewById(R.id.txtMaxPairs);
		txtNumMarks    = activity.findViewById(R.id.txtNumMarks);
		txtMaxMarks    = activity.findViewById(R.id.txtMaxMarks);
		txtNumFacts    = activity.findViewById(R.id.txtNumFacts);
		txtMaxFacts    = activity.findViewById(R.id.txtMaxFacts);
		txtNumFactHits = activity.findViewById(R.id.txtNumFactHits);
		txtNumRules    = activity.findViewById(R.id.txtNumRules);
		txtMaxRules    = activity.findViewById(R.id.txtMaxRules);
		txtNumRuleHits = activity.findViewById(R.id.txtNumRuleHits);
		txtMessage     = activity.findViewById(R.id.txtMessage);

		btnSolve = activity.findViewById(R.id.btnSolve);
		btnSolve.setEnabled(false);
		btnSolve.setOnClickListener(view -> {
			String caption = btnSolve.getText().toString();
			activity.saySnackbar(view, "You clicked the " + caption + " button.");
			switch (caption) {
				case "Solve" : activity.doSolve(); break;
				case "Pause" : activity.doPause(); break;
				case "Resume": activity.doResume(true); break;
			}
		});

		btnReset = activity.findViewById(R.id.btnReset);
		btnReset.setEnabled(false);
		btnReset.setOnClickListener(view -> {
			String caption = btnReset.getText().toString();
			activity.saySnackbar(view, "You clicked the " + caption + " button.");
			switch (caption) {
				case "Reset": activity.reset(); break;
				case "Quit" :
					activity.doQuit();
					if ("Resume".equals(btnSolve.getText().toString())) activity.doResume(true);
					break;
			}
		});

		reset();
	}

	/**
	 * Sets the puzzle when a puzzle is selected.<br>Called by activity.setPuzzle.
	 * @param puzzle Puzzle, or null.
	 */
	void setPuzzle(final Puzzle puzzle) {
		this.puzzle = puzzle;
		reset();
		boolean b = puzzle != null;
		btnSolve.setEnabled(b);
	}

	/** Resets the form.<br>Called by constructor, activity.reset. */
	void reset() {
		txtLevelNum.setText("");
		setText(txtNumGuesses, solver.numGuesses);
		setText(txtNumPairs, solver.numPairs);
		setText(txtMaxPairs, solver.maxPairs);
		setText(txtNumMarks, solver.numMarks);
		setText(txtMaxMarks, solver.maxMarks);
		setText(txtNumFacts, solver.numFacts);
		setText(txtNumFactHits, solver.numFactHits);
		setText(txtNumRules, solver.numRules);
		setText(txtNumRuleHits, solver.numRuleHits);

		if (puzzle != null) {
			setText(txtMaxFacts, puzzle.maxFacts);
			setText(txtMaxRules, puzzle.maxRules);
		}
		else {
			txtMaxFacts.setText("0");
			txtMaxRules.setText("0");
		}

		btnSolve.setText(strSolve);
		btnReset.setText(strReset);
		btnReset.setEnabled(false);
		txtMessage.setText(msgReset);
	}

	/**
	 * Sets the text for the multiline Message field.
	 * @param msg Message, or null.
	 */
	void setMsg(final String msg) {
		if (msg != null) txtMessage.setText(msg);
	}

	/** Sets the caption for the Solve button to "Pause". */
	void sayPause() {
		btnSolve.setText(strPause);
	}

	/** Sets the caption for the Solve button to "Resume".<br>Called by activity.sayWait. */
	void sayResume() {
		btnSolve.setText(strResume);
	}

	// <editor-fold defaultstate="collapsed" desc="IViewer">

	/**
	 * Updates the board when a thread starts running.<br>Called by activity.sayStarted.
	 * @param msg Message.
	 */
	void sayStarted(final String msg) {
		setMsg(msg);
		btnSolve.setText(strPause);
		btnReset.setText(strQuit);
		btnReset.setEnabled(true);
	}

	/**
	 * Updates the board when a thread stops running.<br>Called by activity.sayStopped.
	 * @param msg Message.
	 */
	void sayStopped(final String msg) {
		setMsg(msg);
		btnSolve.setText(strSolve);
		btnReset.setText(strReset);
	}

	/**
	 * Updates the board when the level changes.<br>Called by activity.sayLevel.
	 * @param msg Message.
	 */
	void sayLevel(final String msg) {
		txtLevelNum.setText(msg);
	}

	/**
	 * Updates the board when a mark is entered.<br>Called by activity.sayAddMark.
	 * @param msg Message.
	 */
	void sayAddMark(final String msg) {
		setMsg(msg);
		setText(txtNumGuesses, solver.numGuesses);
		setText(txtNumMarks, solver.numMarks);
		setText(txtNumPairs, solver.numPairs);
		setText(txtNumFacts, solver.numFacts); setText(txtNumFactHits, solver.numFactHits);
		setText(txtNumRules, solver.numRules); setText(txtNumRuleHits, solver.numRuleHits);
	}

	/**
	 * Updates the board when a mark is removed.<br>Called by activity.sayRemoveMark.
	 * @param msg Message.
	 */
	void sayRemoveMark(final String msg) {
		setMsg(msg);
		setText(txtNumMarks, solver.numMarks);
		setText(txtNumPairs, solver.numPairs);
		setText(txtNumFacts, solver.numFacts);
		setText(txtNumRules, solver.numRules);
	}

	/**
	 * Updates the board when a fact is violated.<br>Called by activity.sayFactViolation.
	 * @param msg Message.
	 */
	void sayFactViolation(final String msg) {
		setMsg(msg);
		setText(txtNumFacts, solver.numFacts);
		setText(txtNumFactHits, solver.numFactHits);
	}

	/**
	 * Updates the board when a rule is violated.<br>Called by activity.sayRuleViolation.
	 * @param msg Message.
	 */
	void sayRuleViolation(final String msg) {
		setMsg(msg);
		setText(txtNumRules, solver.numRules);
		setText(txtNumRuleHits, solver.numRuleHits);
	}

	// </editor-fold>
}
