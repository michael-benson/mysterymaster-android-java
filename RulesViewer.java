package com.mysterymaster.androidapp;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mysterymaster.puzzle.Mark;
import com.mysterymaster.puzzle.Puzzle;
import com.mysterymaster.puzzle.Rule;

/**
 * The Rules Viewer class manages the Rules form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class RulesViewer extends BaseViewer {
	/** Rules form. */
	private final TableLayout tblRules;

	/**
	 * Constructor. Note: The Solver object is not required.
	 * @param activity Main Activity.
	 */
	RulesViewer(final MainActivity activity, final TableLayout tblRules) {
		this.activity = activity;
		this.tblRules = tblRules;
	}

	/**
	 * Updates the form when a puzzle is selected. Called by tabViewer.setPuzzle.
	 * @param puzzle Puzzle, or null.
	 */
	void setPuzzle(final Puzzle puzzle) {
		this.puzzle = puzzle;
		initForm();
	}

	/** Resets the form. Called by tabViewer.reset. */
	void reset() {
		if (puzzle != null) for (Rule rule : puzzle.rules) update(rule);
	}

	/** Creates and populates the Rules form when a puzzle is loaded. Called by setPuzzle. */
	private void initForm() {
		tblRules.removeAllViews();
		if (puzzle == null || puzzle.maxRules == 0) return;

		final String[] colHeaders = new String[]{"#", "X", "Hits", "Name"};
		final int[] colLengths = new int[]{colNumWidth, colChkWidth, colHitsWidth, 1300};
		final int[] colAligns = new int[] {Gravity.CENTER, Gravity.CENTER, Gravity.CENTER, Gravity.START};

		final int ncols = colHeaders.length;
		final int nrows = puzzle.maxRules + 1;

		for (int irow = 0; irow < nrows; irow++) {
			TableRow row = new TableRow(activity);
			for (int icol = 0; icol < ncols; icol++) {
				TextView fld;
				if (irow > 0) {
					Rule rule = puzzle.rules.get(irow - 1);
					fld = icol != 1 ? new TextView(activity) : new CheckBox(activity);
					if (icol == 0) fld.setTextAppearance(R.style.TableHeader);
					switch (icol) {
						case 0 : setText(fld, rule.num); break;
						case 1 : ((CheckBox) fld).setChecked(rule.enabled); break;
						case 2 : setText(fld, rule.hits); break;
						case 3 : fld.setText(rule.name);
					}
					if (icol == 3) {
						fld.setMaxLines(1);
						fld.setEllipsize(TextUtils.TruncateAt.END);
					}
				}
				else {
					fld = new TextView(activity);
					fld.setTextAppearance(R.style.TableHeader);
					fld.setText(colHeaders[icol]);
				}
				fld.setWidth(colLengths[icol]);
				fld.setGravity(colAligns[icol]);
				row.addView(fld);
			}
			tblRules.addView(row);
		}
	}

	/**
	 * Updates the form when the mark is appended. Called by tabViewer.sayMarkAddition.
	 * @param mark Mark.
	 */
	void update(final Mark mark) {
		// TODO: Update only if a rule triggered this mark.
		for (Rule rule : puzzle.rules) update(rule);
	}

	/**
	 * Updates the form for the given rule. Called by reset, tabViewer.sayPlacers.
	 * @param rule Rule.
	 */
	void update(final Rule rule) {
		View view = tblRules.getChildAt(rule.num);
		if (view instanceof TableRow) {
			TableRow row = (TableRow) view;
			view = row.getChildAt(1);
			if (view instanceof CheckBox) {
				CheckBox chk = (CheckBox) view;
				chk.setChecked(rule.enabled);
			}
			view = row.getChildAt(2);
			if (view instanceof TextView) {
				TextView txt = (TextView) view;
				setText(txt, rule.hits);
			}
		}
	}
}
