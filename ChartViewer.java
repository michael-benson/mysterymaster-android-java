package com.mysterymaster.androidapp;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mysterymaster.puzzle.Mark;
import com.mysterymaster.puzzle.Noun;
import com.mysterymaster.puzzle.NounType;
import com.mysterymaster.puzzle.Puzzle;

/**
 * The Chart Viewer class manages the Chart form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-11
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class ChartViewer extends BaseViewer {
	/** Chart form. */
	private final TableLayout tblChart;

	/** One-based index of the noun type to display in the first column of the Chart form. */
	private int chartCol1 = 1;

	/**
	 * Constructor. Note: The Solver object is not required, but the Solver class is.
	 * @param activity Main Activity.
	 */
	ChartViewer(final MainActivity activity, final TableLayout tblChart) {
		this.activity = activity;
		this.tblChart = tblChart;
	}

	/**
	 * Updates the form when a puzzle is selected. Called by tabViewer.setPuzzle.
	 * @param puzzle Puzzle.
	 */
	void setPuzzle(final Puzzle puzzle) {
		this.puzzle = puzzle;
		chartCol1 = 1;
		initForm();
		update();
	}

	/** Initializes the form when a puzzle is loaded. Called by setPuzzle. */
	private void initForm() {
		tblChart.removeAllViews();
		if (puzzle == null) return;

		updateAppDimensions();

		final int colMaxWidth = 320;
		final int btnHeight = 150;

		final int ncols = puzzle.maxNounTypes;
		final int nrows = puzzle.maxNouns + 1;

		// Adjust width for margins, padding, etc.
		int del = 4 + 2*ncols;
		int colWidth = Math.min((appWidth - del) / ncols, colMaxWidth);

		for (int irow = 0; irow < nrows; irow++) {
			TableRow row = new TableRow(activity);
			// Setting baseline aligned to false gives single and multi-line buttons same vertical alignment!
			row.setBaselineAligned(false);
			TextView fld;
			for (int icol = 0; icol < ncols; icol++) {
				if (irow > 0) {
					fld = new TextView(activity);
					fld.setMaxLines(1);
					fld.setEllipsize(TextUtils.TruncateAt.END);
				}
				else {
					fld = new Button(activity);
					fld.setTextAppearance(R.style.ChartButton);
					fld.setTransformationMethod(null);
					fld.setHeight(btnHeight);
					fld.setMinimumWidth(0);

					fld.setOnClickListener(v -> {
						Button btn = (Button) v;
						String caption = btn.getText().toString();
						//print("You clicked the " + caption + " button!");
						updateCol1(caption);
					});

				}
				fld.setWidth(colWidth);
				row.addView(fld);
			}
			tblChart.addView(row);
		}
	}

	/** Resets the Chart form. Called by tabViewer.reset. */
	void reset() {
		update();
	}

	/**
	 * Updates first noun type to display in the Chart form based on what column the user clicked.
	 * @param caption String caption of the column header.
	 */
	private void updateCol1(final String caption) {
		for (NounType nounType : puzzle.nounTypes) {
			if (nounType.name.equals(caption)) {
				int i = nounType.num;
				if (i != chartCol1) {
					chartCol1 = i;
					update();
				}
				break;
			}
		}
	}

	/** Updates the Chart form. Called by reset, update(mark). */
	private void update() {
		if (puzzle == null) return;
		final int ncols = puzzle.maxNounTypes;
		final int nrows = puzzle.maxNouns + 1;

		NounType nounType1 = puzzle.getNounType(chartCol1);
		for (int irow = 0; irow < nrows; irow++) {
			Noun noun1 = irow > 0 ? nounType1.nouns.get(irow - 1) : null;
			TableRow row = (TableRow) tblChart.getChildAt(irow);
			int t2 = 0;
			for (int icol = 0; icol < ncols; icol++) {
				TextView txt = (TextView) row.getChildAt(icol);
				if (icol == 0) {
					txt.setText(irow > 0 ? noun1.title : nounType1.name);
				}
				else {
					if (icol == chartCol1) ++t2;
					NounType nounType2 = puzzle.nounTypes.get(t2);
					if (irow == 0)
						txt.setText(nounType2.name);
					else {
						Noun noun2 = Mark.getPairNoun(noun1, nounType2);
						txt.setText(noun2 != null ? noun2.title : "");
					}
					++t2;
				}
			}
		}
	}

	/**
	 * Updates the Chart form if the given mark is either (a) positive or (b) has placers.<br>
	 * Note: The mark may be either appended or removed.
	 * @param mark Mark.
	 */
	void update(final Mark mark) {
		if (mark.verb != Puzzle.Is && mark.rulePlacers.isEmpty()) return;
		if (activity.spots.okRechart) chartCol1 = mark.noun1.type.num;
		update();
	}
}
