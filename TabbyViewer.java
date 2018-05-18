package com.mysterymaster.androidapp;

import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.mysterymaster.puzzle.Fact;
import com.mysterymaster.puzzle.Mark;
import com.mysterymaster.puzzle.Puzzle;
import com.mysterymaster.puzzle.Rule;

/**
 * The Tab Viewer class manages the views (aka forms) that correspond to each tab.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class TabbyViewer extends BaseViewer {
	/** Zero-based number of the current tab. */
	private int tabNum;

	/** Viewer for the Nouns form. */
	private final NounsViewer nounsViewer;

	/** Viewer for the Verbs form. */
	private final VerbsViewer verbsViewer;

	/** Viewer for the Links form. */
	private final LinksViewer linksViewer;

	/** Viewer for the Facts form. */
	private final FactsViewer factsViewer;

	/** Viewer for the Rules form. */
	private final RulesViewer rulesViewer;

	/** Viewer for the Marks form. */
	private final MarksViewer marksViewer;

	/** Viewer for the Chart form. */
	private final ChartViewer chartViewer;

	/** Viewer for the Grids form. */
	private final GridsViewer gridsViewer;

	/** Viewer for the Stats form. */
	private final StatsViewer statsViewer;

	/** Viewer for the Setup form. */
	private final SetupViewer setupViewer;

	/**
	 * Constructor.
	 * @param activity Main Activity.
	 */
	TabbyViewer(final MainActivity activity) {
		this.activity = activity;
		this.solver = activity.solver;

		tabLayout = activity.findViewById(R.id.tabLayout);

		final int maxTabs = 10;

		// Put the parent view that corresponds to each tab in a list.
		// Note: View.VISIBLE = 0, View.INVISIBLE = 4, View.GONE = 8.
		tabForms = new View[maxTabs];
		int i = -1;

		TableLayout  tblNouns = (TableLayout)  getTabForm(R.id.tblNouns, ++i);
		TableLayout  tblVerbs = (TableLayout)  getTabForm(R.id.tblVerbs, ++i);
		LinearLayout tblLinks = (LinearLayout) getTabForm(R.id.lltLinks, ++i);
		LinearLayout lltFacts = (LinearLayout) getTabForm(R.id.lltFacts, ++i);
		TableLayout  tblRules = (TableLayout)  getTabForm(R.id.tblRules, ++i);
		LinearLayout lltMarks = (LinearLayout) getTabForm(R.id.lltMarks, ++i);
		TableLayout  tblChart = (TableLayout)  getTabForm(R.id.tblChart, ++i);
		TableLayout  tblGrids = (TableLayout)  getTabForm(R.id.tblGrids, ++i);
		LinearLayout lltStats = (LinearLayout) getTabForm(R.id.lltStats, ++i);
		GridLayout   grdSetup = (GridLayout)   getTabForm(R.id.grdSetup, ++i);

		// Initialize these forms before adding the tab listener.
		nounsViewer = new NounsViewer(activity, tblNouns);
		verbsViewer = new VerbsViewer(activity);
		linksViewer = new LinksViewer(activity);
		factsViewer = new FactsViewer(activity);
		rulesViewer = new RulesViewer(activity, tblRules);
		marksViewer = new MarksViewer(activity);
		chartViewer = new ChartViewer(activity, tblChart);
		gridsViewer = new GridsViewer(activity, tblGrids);
		statsViewer = new StatsViewer(activity);
		setupViewer = new SetupViewer(activity);

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				int pos = tab.getPosition();
				setTab(pos);
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
				int pos = tab.getPosition();
				View view = tabForms[pos];
				view.setVisibility(View.GONE);
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
				int pos = tab.getPosition();
				setTab(pos);
			}
		});

		// Get the initial tab number from storage.
		tabNum = activity.locker.getInt("tabNum", tabNum);
		setTab();
	}

	/**
	 * Sets the puzzle when a new puzzle is selected.<br>
	 * Called by activity.setPuzzle.
	 * @param puzzle Puzzle.
	 */
	void setPuzzle(final Puzzle puzzle) {
		this.puzzle = puzzle;

		nounsViewer.setPuzzle(puzzle);
		verbsViewer.setPuzzle(puzzle);
		linksViewer.setPuzzle(puzzle);
		factsViewer.setPuzzle(puzzle);
		rulesViewer.setPuzzle(puzzle);
		marksViewer.setPuzzle(puzzle);
		chartViewer.setPuzzle(puzzle);
		gridsViewer.setPuzzle(puzzle);
		statsViewer.setPuzzle();
	}

	/**
	 * Resets the tab forms. Called by activity.reset.<br>
	 * The forms for the nouns, verbs, and links are updated only when the puzzle is loaded.<br>
	 * The Facts and Rules need to be updated when a fact or rule's checkbox or hits field changes.
	 */
	void reset() {
		factsViewer.reset();
		rulesViewer.reset();
		marksViewer.reset();
		chartViewer.reset();
		gridsViewer.reset();
		statsViewer.reset();
	}

	// <editor-fold defaultstate="collapsed" desc="Tabs">

	private final TabLayout tabLayout;
	private final View[] tabForms;

	/**
	 * Returns the View given by its id. This also places the View in the tabForms array.
	 * @param id Id.
	 * @param i Index.
	 * @return View.
	 */
	private View getTabForm(final int id, final int i) {
		View form = activity.findViewById(id);
		tabForms[i] = form;
		form.setVisibility(View.GONE);
		return form;
	}

	/**
	 * Displays the default tab. Called by the constructor.<br>
	 * Note: getTabAt is one-based. Rotating device will reset app, so check for null objects!
	 */
	private void setTab() {
		//print("setTab() tabNum=" + tabNum + " tabLayout=" + tabLayout);
		if (tabLayout != null) {
			TabLayout.Tab tab = tabLayout.getTabAt(tabNum);
			//print("setTab() tab=" + tab);
			if (tab != null) {
				tab.select();
				setTab(tabNum);
			}
		}
	}

	/**
	 * Displays the selected tab and saves its number to storage.<br>
	 * Called by setTab(), or when a tab is pressed.
	 * @param pos Zero-based tab position.
	 */
	private void setTab(final int pos) {
		tabNum = activity.locker.setInt("tabNum", pos);
		View view = tabForms[pos];
		if (pos == 9) setupViewer.update();
		view.setVisibility(View.VISIBLE);
	}

	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="IViewer">

	/** Updates the forms when a thread starts running.<br>Called by activity.sayStarted. */
	void sayStarted() {
		gridsViewer.sayStarted();
	}

	/** Updates the forms when a thread stops running.<br>Called by activity.sayStopped. */
	void sayStopped() {
		gridsViewer.sayStopped();
	}

	/**
	 * Updates the forms when a mark is entered.<br>Called by activity.sayAddMark.
	 * @param mark Mark that was entered.
	 */
	void sayAddMark(final Mark mark) {
		factsViewer.update(mark);
		rulesViewer.update(mark); // TODO See if this is a trigger.
		marksViewer.update(mark, 1);
		chartViewer.update(mark);
		gridsViewer.update(mark);
		statsViewer.update();
	}

	/**
	 * Updates the forms when a mark is removed.<br>Called by activity.sayRemovedMark.
	 * @param mark Mark that was removed.
	 */
	void sayRemoveMark(final Mark mark) {
		factsViewer.update(mark);
		marksViewer.update(mark, -1);
		chartViewer.update(mark);
		gridsViewer.update(mark);
		statsViewer.update();
	}

	/**
	 * Updates the forms when a mark is validated.<br>Called by activity.sayValidMark.
	 * @param mark Mark that was validated.
	 */
	void sayValidMark(final Mark mark) {
		factsViewer.update(mark);
		marksViewer.update(mark, 0);
	}

	/**
	 * Updates the forms when a mark violates a fact.<br>Called by activity.sayFactViolation.
	 * @param fact Fact violated by a mark.
	 */
	void sayFactViolation(final Fact fact) {
		factsViewer.update(fact);
	}

	/**
	 * Updates the forms when a mark violates a rule.<br>Called by activity.sayRuleViolation.
	 * @param rule Rule violated by a mark.
	 */
	void sayRuleViolation(final Rule rule) {
		rulesViewer.update(rule);
	}

	/**
	 * Updates the forms when one or more nouns are updated.<br>Called by activity.sayPlacers.
	 * @param mark Mark containing the array of updated nouns.
	 * @param rule Rule that updated the nouns when invoked on the mark.
	 */
	void sayPlacers(final Mark mark, final Rule rule) {
		rulesViewer.update(rule);
		chartViewer.update(mark);
	}

	// </editor-fold>
}
