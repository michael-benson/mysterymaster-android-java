package com.mysterymaster.androidapp;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mysterymaster.puzzle.Mark;
import com.mysterymaster.puzzle.Puzzle;

import java.util.ArrayList;
import java.util.List;

/**
 * The Marks Viewer class manages the Marks form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class MarksViewer extends BaseViewer {
	/** Marks data table. */
	private final RecyclerView tblMarks;

	/** Adapter for the Marks recycler view. */
	private final RecyclerView.Adapter marksAdapter;

	/** Copy of the marks. Needed to avoid the "IndexOutOfBounds Exception: Inconsistency detected" fatal exception. */
	private final List<Mark> marks = new ArrayList<>();

	/**
	 * Constructor.
	 * @param activity Main Activity.
	 */
	MarksViewer(final MainActivity activity) {
		this.activity = activity;
		this.solver = activity.solver;
		tblMarks = activity.findViewById(R.id.tblMarks);

		// Create adapter and pass the dataset.
		marksAdapter = new MarksAdapter(marks);
		tblMarks.setAdapter(marksAdapter);

		// Set the layout manager.
		LinearLayoutManager llm = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
		tblMarks.setLayoutManager(llm);
	}

	/**
	 * Updates the form when a puzzle is selected. Called by tabbyViewer.setPuzzle.
	 * @param puzzle Puzzle, or null.
	 */
	void setPuzzle(final Puzzle puzzle) {
		this.puzzle = puzzle;
		marks.clear();
		update();
	}

	/** Resets the form. Called by tabbyViewer.reset. */
	void reset() {
		marks.clear();
		update();
	}

	/** Updates the Marks form. Called by reset. */
	private void update() {
		marksAdapter.notifyDataSetChanged();
	}

	/**
	 * Updates the Marks form for the mark. Called by sayAddMark, sayRemoveMark, sayValidMark.
	 * @param mark Mark.
	 * @param d Flag where value is either : -1=removed, 0=validated, or 1=entered.
	 */
	void update(final Mark mark, final int d) {
		//print("tabViewer.initForm " + mark.num);

		int pos = mark.num - 1;
		switch (d) {
			case -1:
				marks.remove(mark.num - 1);
				marksAdapter.notifyItemRemoved(pos);
				break;
			case 0:
				marksAdapter.notifyItemChanged(pos);
				break;
			case 1:
				marks.add(mark);
				marksAdapter.notifyItemInserted(pos);
				break;
		}

		tblMarks.scrollToPosition(pos);
		marksAdapter.notifyDataSetChanged();

		// The following does not work when a mark is removed.
		//tblMarks.scrollToPosition(pos);
		//marksAdapter.notifyDataSetChanged();

		// Move focus to item given by its position. THIS DOES NOT WORK WHEN MARKS ARE REMOVED!
		//int position = marksAdapter.getItemCount();
		//tblMarks.smoothScrollToPosition(position);
		//marksAdapter.notifyDataSetChanged();

		//if (mark.num > 5) for (long i = 0; i < Long.MAX_VALUE; i++);
	}
}
