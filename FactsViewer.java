package com.mysterymaster.androidapp;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mysterymaster.puzzle.Fact;
import com.mysterymaster.puzzle.Mark;
import com.mysterymaster.puzzle.Puzzle;

import java.util.ArrayList;
import java.util.List;

/**
 * The Facts Viewer class manages the Facts form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class FactsViewer extends BaseViewer {
	/** Facts data table. */
	private final RecyclerView tblFacts;

	/** Adapter for the Facts recycler view. */
	private final RecyclerView.Adapter factsAdapter;

	/** Copy of the facts. Needed to avoid the "IndexOutOfBounds Exception: Inconsistency detected" fatal exception. */
	private final List<Fact> facts = new ArrayList<>();

	/**
	 * Constructor. Note: The Solver object is not required.
	 * @param activity Main Activity.
	 */
	FactsViewer(final MainActivity activity) {
		this.activity = activity;
		tblFacts = activity.findViewById(R.id.tblFacts);

		// Create adapter and pass the dataset.
		factsAdapter = new FactsAdapter(facts);
		tblFacts.setAdapter(factsAdapter);

		// Set the layout manager.
		LinearLayoutManager llm = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
		tblFacts.setLayoutManager(llm);
	}

	/**
	 * Updates the form when a puzzle is selected. Called by tabbyViewer.setPuzzle.
	 * @param puzzle Puzzle, or null.
	 */
	void setPuzzle(final Puzzle puzzle) {
		this.puzzle = puzzle;
		facts.clear();
		if (puzzle != null) facts.addAll(puzzle.facts);
		update();
	}

	/** Resets the form. Called by tabbyViewer.reset. */
	void reset() {
		update();
	}

	/** Updates the form. Called by setPuzzle, reset. */
	private void update() {
		factsAdapter.notifyDataSetChanged();
	}

	/**
	 * Updates the form when the mark is appended. Called by tabViewer.sayAddMark.
	 * @param mark Mark.
	 */
	void update(final Mark mark) {
		for (Fact fact : mark.facts) update(fact);
		for (Fact fact : mark.disabledFacts) update(fact);
	}

	/**
	 * Updates the Facts form for the fact.
	 * @param fact Fact.
	 */
	void update(final Fact fact) {
		int pos = fact.num - 1;
		facts.set(pos, fact);
		factsAdapter.notifyItemChanged(pos);
		tblFacts.scrollToPosition(pos);
		factsAdapter.notifyDataSetChanged();
	}
}
