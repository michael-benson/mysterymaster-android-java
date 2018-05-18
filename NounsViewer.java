package com.mysterymaster.androidapp;

import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mysterymaster.puzzle.NounType;
import com.mysterymaster.puzzle.Puzzle;

/**
 * The Nouns Viewer class manages the Nouns form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class NounsViewer extends BaseViewer {
	/** Nouns form. */
	private final TableLayout tblNouns;

	/**
	 * Constructor. Note: The Solver object is not required.
	 * @param activity Main Activity.
	 */
	NounsViewer(final MainActivity activity, final TableLayout tblNouns) {
		this.activity = activity;
		this.tblNouns = tblNouns;
	}

	/**
	 * Updates the form when a puzzle is selected. Called by tabViewer.setPuzzle.
	 * @param puzzle Puzzle, or null.
	 */
	void setPuzzle(final Puzzle puzzle) {
		this.puzzle = puzzle;

		tblNouns.removeAllViews();
		if (puzzle == null) return;

		updateAppDimensions();

		final int colMaxWidth = 320;

		final int ncols = puzzle.maxNounTypes + 1;
		final int nrows = puzzle.maxNouns + 1;

		// Adjust for margins, paddings, etc.
		int del = 4 - 2*ncols;
		int colWidth = Math.min((appWidth - colNumWidth - del) / (ncols - 1), colMaxWidth);

		for (int irow = 0; irow < nrows; irow++) {
			TableRow row = new TableRow(activity);
			for (int icol = 0; icol < ncols; icol++) {
				TextView txt = new TextView(activity);
				if (icol > 0) {
					txt.setWidth(colWidth);
					NounType nounType = puzzle.nounTypes.get(icol - 1);
					txt.setText(irow > 0 ? nounType.nouns.get(irow - 1).title : nounType.name);
					if (irow > 0) {
						txt.setMaxLines(1);
						txt.setEllipsize(TextUtils.TruncateAt.END);
					}
				}
				else {
					txt.setTextAppearance(R.style.TableHeader);
					txt.setWidth(colNumWidth);
					txt.setGravity(Gravity.CENTER_HORIZONTAL);
					if (irow > 0) setText(txt, irow); else txt.setText(" # ");
				}
				if (irow == 0) txt.setTextAppearance(R.style.TableHeader);
				row.addView(txt);
			}
			tblNouns.addView(row);
		}
	}
}
