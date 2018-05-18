package com.mysterymaster.androidapp;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mysterymaster.puzzle.Mark;
import com.mysterymaster.puzzle.Noun;
import com.mysterymaster.puzzle.NounType;
import com.mysterymaster.puzzle.Puzzle;
import com.mysterymaster.puzzle.Verb;

import java.util.List;

/**
 * The Grids Viewer class manages the Grids form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class GridsViewer extends BaseViewer {
	/** Grids form. */
	private final TableLayout tblGrids;

	/** Positive or negative verb to enter when the user clicks a grid cell. MUST be set! */
	private Verb gridVerb = Puzzle.IsNot;

	private final String strVerb;
	private final String strUndo;

	/**
	 * Constructor.
	 * @param activity Main Activity.
	 */
	GridsViewer(final MainActivity activity, final TableLayout tblGrids) {
		super();

		this.activity = activity;
		this.solver = activity.solver;
		this.tblGrids = tblGrids;

		// Initialize string resources.
		Resources res = activity.getResources();
		strVerb = res.getString(R.string.Verb);
		strUndo = res.getString(R.string.Undo);

		// Get the initial Grid Verb number from storage.
		int gridVerbNum = activity.locker.getInt("gridVerbNum", gridVerb.num);
		gridVerb = gridVerbNum == Puzzle.IsNot.num ? Puzzle.IsNot : Puzzle.Is;
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
		update();
	}

	/**
	 * Returns the name with a newline after each character. This is StringRiffle in Mathematica.
	 * @param msg Message.
	 * @return String.
	 */
	private static String getVerticalName(final String msg) {
		int n = msg.length();
		StringBuilder str = new StringBuilder(2*n);
		for (int i = 0; i < n; i++) {
			str.append(msg.charAt(i)).append(NL);
		}
		return str.toString();
	}

	private void rotateText(final TextView txv) {
		txv.setRotation(90);
	}

	/** Grid row and column to keep track where each subgrid is located. */
	class RowCol {
		int row = -1;
		int col = -1;

		void setRowCol(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}

	private void setRowCol(final int t1, final int t2, final int row, final int col) {
		rowCols[t1][t2].setRowCol(row, col);
		rowCols[t2][t1].setRowCol(row, col);
		//print("t1=" + t1 + " t2=" + t2 + " gridRow=" + row + " gridCol=" + col);
	}

	/** 2D array of row/col objects where rowCol = rowCols[t1][t2]. Initialized in initForm. */
	private RowCol[][] rowCols = null;

	/** Grid Verb button. */
	private Button btnGridVerb;

	/** Undo button. */
	private Button btnUndo;

	/** Initializes the form when a puzzle is loaded. Called by setPuzzle. */
	private void initForm() {
		tblGrids.removeAllViews();
		if (puzzle == null) return;

		updateAppDimensions();

		int m = puzzle.maxNounTypes;
		int n = puzzle.maxNouns;

		rowCols = new RowCol[m][m];
		for (int t1 = 0; t1 < m; ++t1) {
			for (int t2 = 0; t2 < m; ++t2) {
				rowCols[t1][t2] = new RowCol();
			}
		}

		// do-while loop decrements m, but n is unchanged.
		List<NounType> nounTypes = puzzle.nounTypes;

		// Layout Parameter for row 0 col 1 type names.
		TableRow.LayoutParams row0Col1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		row0Col1.span = n;
		row0Col1.weight = 1;
		row0Col1.setMargins(4, 4, 4, 4);

		// Layout Parameter for row 0 other type names.
		TableRow.LayoutParams row0ColX = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		row0ColX.span = n;
		row0ColX.weight = 1;
		row0ColX.setMargins(20, 4, 4, 4);

		// Layout Parameter for most columns.
		TableRow.LayoutParams gcp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		gcp.setMargins(4, 4, 4, 4);

		// Layout Parameter for first noun/verb for each noun type.
		TableRow.LayoutParams gcp0 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		gcp0.setMargins(20, 4, 4, 4);

		//TableRow.LayoutParams row1Params = new TableRow.LayoutParams();
		//row1Params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

		int col0Width = 200;
		int colXWidth = 60;
		int row1Height = 300;

		// Row headers are t1. Noun type indexes: 0, then m-1, m-2, ... where t1 > 1
		int t1 = 0;
		int gridRow = 0;
		int gridCol = -1;
		do {
			NounType nounType1 = nounTypes.get(t1);

			// Display column header nouns once. The layout comments are zero-based.
			if (t1 < 1) {
				// Row 0 layout: blank, type1, type2, type3... where each type spans n columns.
				TableRow row = new TableRow(activity);
				TextView txv = new TextView(activity);
				txv.setText("");
				txv.setWidth(col0Width);
				row.addView(txv, gcp);
				for (int t2 = 1; t2 < m; t2++) {
					NounType type2 = nounTypes.get(t2);
					TextView tv2 = new TextView(activity);
					tv2.setText(type2.name);
					tv2.setTypeface(null, Typeface.BOLD);
					tv2.setWidth(colXWidth);
					tv2.setGravity(Gravity.CENTER_HORIZONTAL);
					row.addView(tv2, t2 > 1 ? row0ColX : row0Col1);
				}
				tblGrids.addView(row);

				// Row 1 layout: gridverb control, type1 nouns, type2 nouns, type3 nouns...
				row = new TableRow(activity);

				// GridVerb control.
				LinearLayout lin = new LinearLayout(activity);
				lin.setOrientation(LinearLayout.VERTICAL);
				lin.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

				// Row 0 contains the label "Verb".
				txv = new TextView(activity);
				txv.setWidth(col0Width);
				txv.setGravity(Gravity.CENTER);
				txv.setText(strVerb);
				lin.addView(txv);

				// Row 1 contains the verb code button which toggles between the negative and positive verbs.
				Button btn = btnGridVerb = new Button(activity);
				btn.setTransformationMethod(null);
				btn.setTypeface(Typeface.DEFAULT);
				btn.setLayoutParams(new LinearLayout.LayoutParams(col0Width - 20, LinearLayout.LayoutParams.WRAP_CONTENT));
				setGridVerbButton(btn, gridVerb);
				btn.setOnClickListener(view -> {
					Button b = (Button) view;
					String caption = b.getText().toString();
					print("You clicked the GridVerb code='" + caption + "' button!");
					Verb v = (Verb) b.getTag();
					gridVerb = v == Puzzle.IsNot ? Puzzle.Is : Puzzle.IsNot;
					setGridVerbButton(b, gridVerb);
					activity.locker.setInt("gridVerbNum", gridVerb.num);
				});
				lin.addView(btn);

				// Row 2 contains the "Undo" button which will undo all marks back to and including the last mark entered by the user.
				btn = btnUndo = new Button(activity);
				btn.setTransformationMethod(null);
				btn.setTypeface(Typeface.DEFAULT);
				btn.setLayoutParams(new LinearLayout.LayoutParams(col0Width - 20, LinearLayout.LayoutParams.WRAP_CONTENT));
				btn.setText(strUndo);
				btn.setOnClickListener(v -> {
					Button b = (Button) v;
					String caption = b.getText().toString();
					print("You clicked the " + caption + " button!");
					activity.undoUserMark();
				});
				lin.addView(btn);

				row.addView(lin, gcp);

				// Names for each noun in type1, type2, type3, ...
				for (int t2 = 1; t2 < m; t2++) {
					NounType type2 = nounTypes.get(t2);
					for (int n2 = 0; n2 < n; n2++) {
						Noun noun2 = type2.nouns.get(n2);
						TextView tv2 = new TextView(activity);
						tv2.setText(getVerticalName(noun2.title));
						tv2.setGravity(Gravity.CENTER_HORIZONTAL);
						tv2.setHeight(row1Height);
						tv2.setWidth(colXWidth);
						row.addView(tv2, t2 == 1 || n2 > 0 ? gcp : gcp0);
					}
				}
				tblGrids.addView(row);

				// Row 2 layout: type0
				row = new TableRow(activity);
				txv = new TextView(activity);
				txv.setText(nounType1.name);
				txv.setTypeface(null, Typeface.BOLD);
				txv.setWidth(col0Width);
				txv.setMaxLines(1);
				txv.setEllipsize(TextUtils.TruncateAt.END);
				row.addView(txv, gcp);
				tblGrids.addView(row);
			}
			else {
				// Row(s) where column 0 contains just the noun type name.
				TableRow row = new TableRow(activity);
				TextView txv = new TextView(activity);
				txv.setText(nounType1.name);
				txv.setTypeface(null, Typeface.BOLD);
				txv.setWidth(col0Width);
				txv.setMaxLines(1);
				txv.setEllipsize(TextUtils.TruncateAt.END);
				row.addView(txv, gcp);
				tblGrids.addView(row);
			}

			// Display nouns for row header in first column, then verb codes for each row/col noun pair.
			for (int n1 = 0; n1 < n; n1++) {
				Noun noun1 = nounType1.nouns.get(n1);
				TableRow row = new TableRow(activity);
				TextView tv1 = new TextView(activity);
				tv1.setText(noun1.title);
				tv1.setWidth(colXWidth);
				tv1.setMaxLines(1);
				tv1.setEllipsize(TextUtils.TruncateAt.END);
				row.addView(tv1, gcp);
				for (int t2 = 1; t2 < m; t2++) {
					if (n1 == 0) setRowCol(t1, t2, gridRow, ++gridCol);
					NounType type2 = nounTypes.get(t2);
					for (int n2 = 0; n2 < n; n2++) {
						Noun noun2 = type2.nouns.get(n2);
						Verb verb = solver.getGridVerb(noun1, noun2);
						TextView tv2 = new TextView(activity);
						tv2.setWidth(colXWidth);
						tv2.setGravity(Gravity.CENTER_HORIZONTAL);
						tv2.setBackground(border);
						tv2.setTag(new GridData(noun1, noun2));
						setGridVerb(tv2, verb);
						tv2.setOnClickListener(onClickGridCell);
						row.addView(tv2, t2 == 1 || n2 > 0 ? gcp : gcp0);
					}
				}
				tblGrids.addView(row);
			}

			if (t1 < 1) t1 = m;
			++gridRow; gridCol = -1;
		} while (--t1 > 1 && --m > 0);
	}

	/**
	 * Points to the method when a grid cell is clicked.
	 * Note: I prefer defining the method here versus within the setOnClickListener.
	 * */
	private final View.OnClickListener onClickGridCell = view -> {
		TextView txv = (TextView) view;
		GridData tag = (GridData) txv.getTag();

		print("tabViewer.onClickGridCell tag.noun1=" + tag.noun1 + " tag.noun2=" + tag.noun2 + " tag.verb=" + tag.verb + " gridVerb=" + gridVerb + " Maybe? " + (tag.verb == Puzzle.Maybe));
		if (tag.verb != Puzzle.Maybe) return;
		activity.addMarkByUser(tag.noun1, gridVerb, tag.noun2);
	};

	/**
	 * Updates the GridVerb button based on the given verb.
	 * @param btn GridVerb button.
	 * @param verb Verb.
	 */
	private void setGridVerbButton(final Button btn, final Verb verb) {
		btn.setText(verb.code);
		btn.setTag(verb);
		setTextColor(btn, verb);
	}

	class GridData {
		/** Row noun. */
		final Noun noun1;
		/** Column noun. */
		final Noun noun2;
		/** Verb */
		Verb verb;

		GridData(Noun noun1, Noun noun2) {
			this.noun1 = noun1;
			this.noun2 = noun2;
		}
	}

	/**
	 * Updates the Grids form. Called by resetGridsForm.<br>
	 * If m is the number of noun types and n is the number of nouns per type:<br>
	 * Row 0 contains names of m - 1 noun types, row 1 contains the names of the nouns per type.<br>
	 * Row 2 contains the name of noun type 0, so the data starts in row 3 with n rows of data.<br>
	 * This pattern repeats for m - 1 noun types.<br>
	 */
	private void update() {
		int nrows = tblGrids.getChildCount();
		//print("initForm nrows=" + nrows);
		for (int irow = 0; irow < nrows; irow++) {
			if (irow < 3) continue;
			TableRow row = (TableRow) tblGrids.getChildAt(irow);
			int ncols = row.getChildCount();
			//print("initForm irow=" + irow + " ncols=" + ncols);
			if (ncols == 1) continue;
			for (int icol = 1; icol < ncols; icol++) {
				TextView txv = (TextView) row.getChildAt(icol);
				setGridVerb(txv, Puzzle.Maybe);
			}
		}
	}

	/** Updates the Grids form when a mark is entered/removed. Called by sayMarkAddition, sayMarkRemoval. */
	void update(final Mark mark) {
		int t1 = mark.noun1.type.num - 1; int n1 = mark.noun1.num - 1;
		int t2 = mark.noun2.type.num - 1; int n2 = mark.noun2.num - 1;

		int xrow = t1 > 0 ? n2 : n1;
		int xcol = t1 > 0 ? n1 : n2;

		RowCol rowCol = rowCols[t1][t2];
		int irow = 3 + xrow + (rowCol.row) * (puzzle.maxNouns + 1);
		int icol = 1 + xcol + (rowCol.col) * (puzzle.maxNouns);
		//print("initForm mark " + mark.num + " irow=" + irow + " icol=" + icol);

		TableRow row = (TableRow) tblGrids.getChildAt(irow);
		TextView txv = (TextView) row.getChildAt(icol);
		Verb verb = solver.getGridVerb(mark.noun1, mark.noun2);
		setGridVerb(txv, verb);
	}

	// <editor-fold defaultstate="collapsed" desc="IViewer">

	/** Updates the form when a thread starts running.<br>Called by tabbyViewer.sayStarted. */
	void sayStarted() {
		btnUndo.setEnabled(false);
		btnGridVerb.setEnabled(false);
	}

	/** Updates the form when a thread stops running.<br>Called by tabbyViewer.sayStopped. */
	void sayStopped() {
		btnUndo.setEnabled(true);
		btnGridVerb.setEnabled(true);
	}

	// </editor-fold>
}
