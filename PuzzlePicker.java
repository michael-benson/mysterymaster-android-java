package com.mysterymaster.androidapp;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mysterymaster.puzzle.Puzzle;
import com.mysterymaster.puzzles.Puzzles;

/**
 * The Puzzle Picker class manages what puzzle is selected by the user.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-03
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class PuzzlePicker {
	/** Puzzle Picker control. */
	private final Spinner ddlPuzzles;

	/** Constructor. */
	PuzzlePicker(final MainActivity activity) {
		// Get the View object for the puzzle picker.
		ddlPuzzles = activity.findViewById(R.id.ddlPuzzles);

		// Create the adapter for the puzzles drop down list.
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, Puzzles.titles);

		// Set the style for the puzzles drop down list.
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Attach data adapter to spinner.
		ddlPuzzles.setAdapter(dataAdapter);

		// Set default puzzle.
		int puzzleNum = activity.locker.getInt("puzzleNum", 0);
		ddlPuzzles.setSelection(puzzleNum);

		// Add listener to spinner.
		ddlPuzzles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
				Puzzle puzzle = null;
				activity.locker.setInt("puzzleNum", pos);
				if (pos > 0) puzzle = Puzzles.getPuzzleByNum(pos);
				activity.setPuzzle(puzzle);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) { }
		});
	}

	/**
	 * Sets the enabled state of the puzzle picker control.
	 * @param flag Boolean.
	 */
	void setEnabled(final boolean flag) {
		ddlPuzzles.setEnabled(flag);
	}
}
