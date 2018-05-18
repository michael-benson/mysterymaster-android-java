package com.mysterymaster.androidapp;

import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mysterymaster.solver.LevelCounter;
import com.mysterymaster.solver.Solver;

/**
 * The Stats Viewer class manages the Stats form, which contains two tables:<br>
 * "Pairs By Level" and "Marks By Level".<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class StatsViewer extends BaseViewer  {
	/** Pairs by Level table. */
	private TableLayout tblPairsByLevel;

	/** Marks by Level table. */
	private TableLayout tblMarksByLevel;

	/**
	 * Constructor.
	 * @param activity Main Activity.
	 */
	StatsViewer(final MainActivity activity) {
		this.activity = activity;
		this.solver = activity.solver;
		initForm();
	}

	/** Updates the form when a puzzle is selected. Called by tabbyViewer.setPuzzle. */
	void setPuzzle() {
		update();
	}

	/** Resets the form. Called by tabbyViewer.reset. */
	void reset() {
		update();
	}

	/** Initializes the form. Called by the constructor. */
	private void initForm() {
		tblPairsByLevel = getSubTable(R.id.tblPairsByLevel);
		tblMarksByLevel = getSubTable(R.id.tblMarksByLevel);
	}

	/**
	 * Initializes the Pairs or Marks table and sets the statistics to zero.
	 * @param id Identifier for the table.
	 */
	private TableLayout getSubTable(final int id) {
		TableLayout tbl = activity.findViewById(id);
		tbl.removeAllViews();
		tbl.setGravity(Gravity.CENTER_HORIZONTAL);

		final int ncols = Solver.MAX_LAWS + 4;
		final int nrows = Solver.MAX_LEVELS + 2;

		final int colMaxWidth = 120;
		updateAppDimensions();
		final int colWidth = Math.min((appWidth - colNumWidth) / (ncols - 1), colMaxWidth);

		String[] colHeaders = LevelCounter.getColHeaders();
		String[] rowHeaders = LevelCounter.getRowHeaders();

		for (int irow = 0; irow < nrows; irow++) {
			TableRow row = new TableRow(activity);
			row.setWeightSum(ncols);
			for (int icol = 0; icol < ncols; icol++) {
				TextView txt = new TextView(activity);
				txt.setGravity(Gravity.END);
				txt.setWidth(icol > 0 ? colWidth : colNumWidth);

				if (irow > 0) {
					if (icol == 0) {
						txt.setTextAppearance(R.style.TableHeader);
						txt.setText(rowHeaders[irow]);
					}
					else
						setText(txt, 0);
				}
				else {
					txt.setTextAppearance(R.style.TableHeader);
					txt.setText(colHeaders[icol]);
				}
				row.addView(txt);
			}
			tbl.addView(row);
		}
		return tbl;
	}

	/** Updates the Stats form. */
	void update() {
		updateTable(tblPairsByLevel, solver.stats.levelPairs);
		updateTable(tblMarksByLevel, solver.stats.levelMarks);
	}

	/**
	 * Updates the given table for the Stats form.
	 * @param tbl Table.
	 * @param levelCounters Level counters for the table.
	 */
	private void updateTable(final TableLayout tbl, final LevelCounter[] levelCounters) {
		final int ncols = Solver.MAX_LAWS + 4;
		final int nrows = Solver.MAX_LEVELS + 2;

		for (int irow = 1; irow < nrows; irow++) {
			TableRow row = (TableRow) tbl.getChildAt(irow);
			LevelCounter levelCounter = levelCounters[irow - 1];
			int[] counts = levelCounter.getCounts();
			for (int icol = 1; icol < ncols; icol++) {
				TextView txt = (TextView) row.getChildAt(icol);
				setText(txt, counts[icol - 1]);
			}
		}
	}
}
