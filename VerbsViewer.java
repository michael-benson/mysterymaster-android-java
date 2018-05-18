package com.mysterymaster.androidapp;

import android.widget.TextView;

import com.mysterymaster.puzzle.Puzzle;
import com.mysterymaster.puzzle.Verb;

/**
 * The Verbs Viewer class manages the Verbs form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class VerbsViewer extends BaseViewer {
	/**
	 * Constructor. Note: The Solver object is not required.
	 * @param activity Main Activity.
	 */
	VerbsViewer(final MainActivity activity) {
		this.activity = activity;

		Verb verb;
		int i;

		verb = Puzzle.IsNot; i = verb.num;
		types[i] = activity.findViewById(R.id.txtNegativeType);
		names[i] = activity.findViewById(R.id.txtNegativeName);
		codes[i] = activity.findViewById(R.id.txtNegativeCode);

		verb = Puzzle.Is; i = verb.num;
		types[i] = activity.findViewById(R.id.txtPositiveType);
		names[i] = activity.findViewById(R.id.txtPositiveName);
		codes[i] = activity.findViewById(R.id.txtPositiveCode);

		verb = Puzzle.Maybe; i = verb.num;
		types[i] = activity.findViewById(R.id.txtPossibleType);
		names[i] = activity.findViewById(R.id.txtPossibleName);
		codes[i] = activity.findViewById(R.id.txtPossibleCode);

		update();
	}

	/**
	 * Updates the form when a puzzle is selected. Called by tabViewer.setPuzzle.
	 * @param puzzle Puzzle, or null.
	 */
	void setPuzzle(final Puzzle puzzle) {
		this.puzzle = puzzle;
		update();
	}

	private final TextView[] types = new TextView[Verb.MAX_VERBS];
	private final TextView[] names = new TextView[Verb.MAX_VERBS];
	private final TextView[] codes = new TextView[Verb.MAX_VERBS];

	/**
	 * Updates the form when a puzzle is loaded. Called by constructor, setPuzzle.<br>
	 * This form uses the static verbs, which are updated when the puzzle is loaded.
	 */
	private void update() {
		Verb verb;
		int i;

		verb = Puzzle.IsNot; i = verb.num;
		types[i].setText(verb.type);
		names[i].setText(verb.name);
		codes[i].setText(verb.code);

		verb = Puzzle.Is; i = verb.num;
		types[i].setText(verb.type);
		names[i].setText(verb.name);
		codes[i].setText(verb.code);

		verb = Puzzle.Maybe; i = verb.num;
		types[i].setText(verb.type);
		names[i].setText(verb.name);
		codes[i].setText(verb.code);
	}
}
