package com.mysterymaster.androidapp;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mysterymaster.puzzle.Link;
import com.mysterymaster.puzzle.Noun;
import com.mysterymaster.puzzle.NounType;
import com.mysterymaster.puzzle.Puzzle;
import com.mysterymaster.puzzle.Verb;

import java.util.List;

/**
 * The Links Viewer class manages the Links form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class LinksViewer extends BaseViewer {
	/** Links table. */
	private final TableLayout tblLinks;

	/** Link Grid label. */
	private final TextView txtLinkGrid;

	/** Link Grid table. */
	private final TableLayout tblLinkGrid;

	/** Current link for the Link Grid table. If null, do not show the Link Grid table. */
	private Link link = null;

	/**
	 * Constructor. Note: The Solver object is not required.
	 * @param activity Main Activity.
	 */
	LinksViewer(final MainActivity activity) {
		super();

		this.activity = activity;
		this.tblLinks = activity.findViewById(R.id.tblLinks);
		this.tblLinkGrid = activity.findViewById(R.id.tblLinkGrid);
		this.txtLinkGrid = activity.findViewById(R.id.txtLinkGrid);
	}

	/**
	 * Updates the form when a puzzle is selected. Called by tabViewer.setPuzzle.
	 * @param puzzle Puzzle, or null.
	 */
	void setPuzzle(final Puzzle puzzle) {
		this.puzzle = puzzle;

		tblLinks.removeAllViews();
		tblLinkGrid.removeAllViews();
		link = null;
		if (puzzle == null) return;

		updateAppDimensions();

		initLinksTable();
		initLinkGridTable();
		updateLinkGridTable(null);
	}

	/** Creates and populates the Links table. Called by initForm. */
	private void initLinksTable() {
		final String[] colHeaders = new String[]{"#", "Noun Type", "Name", "1:1"};
		final int[] colLengths = new int[]{colNumWidth, 256, 512, colChkWidth};
		final int[] colAligns = new int[] {Gravity.CENTER, Gravity.START, Gravity.START, Gravity.CENTER};

		final int ncols = colHeaders.length;
		final int nrows = puzzle.maxLinks + 1;

		for (int irow = 0; irow < nrows; irow++) {
			TableRow row = new TableRow(activity);
			for (int icol = 0; icol < ncols; icol++) {
				TextView fld; // Note: TextView is an ancestor of CheckBox.
				if (irow > 0) {
					Link link = puzzle.links.get(irow - 1);
					if (icol < 3) {
						fld = new TextView(activity);
						if (icol == 0) fld.setTextAppearance(R.style.TableHeader);
						switch (icol) {
							case 0 : setText(fld, link.num); break;
							case 1 : fld.setText(link.nounType.name); break;
							case 2 : fld.setText(link.name); break;
						}
						if (icol == 2) {
							fld.setOnClickListener(v -> {
								print("You clicked link " + link.num);
								updateLinkGridTable(link);
							});
						}
					}
					else {
						CheckBox chk = new CheckBox(activity);
						chk.setClickable(false);
						chk.setChecked(link.oneToOne);
						fld = chk;
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
			tblLinks.addView(row);
		}
	}

	/** Initializes the Link Grid table. */
	private void initLinkGridTable() {
		final int colMaxWidth = 320;

		int ncols = puzzle.maxNouns + 1;
		int nrows = puzzle.maxNouns + 1;

		// Adjust width for margins, padding, etc.
		int del = 4 + 10*ncols;
		int colWidth = Math.min((appWidth - del) / ncols, colMaxWidth);

		// Layout Parameter for cells.
		TableRow.LayoutParams gcp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		gcp.setMargins(4, 4, 4, 4);

		for (int irow = 0; irow < nrows; irow++) {
			TableRow row = new TableRow(activity);
			for (int icol = 0; icol < ncols; icol++) {
				TextView txv = new TextView(activity);
				txv.setWidth(colWidth);
				txv.setGravity(Gravity.CENTER_HORIZONTAL);
				txv.setText("");
				if (irow > 0 && icol > 0)
					txv.setBackground(border);
				else
					txv.setTextAppearance(R.style.TableHeader);
				if (irow > 0 && icol == 0) {
					txv.setMaxLines(1);
					txv.setEllipsize(TextUtils.TruncateAt.END);
				}
				row.addView(txv, gcp);
			}
			tblLinkGrid.addView(row);
		}
	}

	/**
	 * Populates the Link Grid table.<br>
	 * Note: The user can click the same link to show/hide the Link Grid table.
	 * @param link Link.
	 */
	private void updateLinkGridTable(final Link link) {
		this.link = link == null || link == this.link ?  null : link;
		if (this.link == null) {
			txtLinkGrid.setVisibility(View.GONE);
			tblLinkGrid.setVisibility(View.GONE);
			return;
		}

		txtLinkGrid.setVisibility(View.VISIBLE);
		tblLinkGrid.setVisibility(View.VISIBLE);
		txtLinkGrid.setText(link.name);

		NounType nounType = link.nounType;
		List<Noun> nouns = link.nounType.nouns;

		int ncols = puzzle.maxNouns + 1;
		int nrows = puzzle.maxNouns + 1;

		for (int irow = 0; irow < nrows; irow++) {
			TableRow row = (TableRow) tblLinkGrid.getChildAt(irow);
			for (int icol = 0; icol < ncols; icol++) {
				TextView txv = (TextView) row.getChildAt(icol);
				if (irow > 0) {
					Noun noun1 = nouns.get(irow - 1);
					if (icol > 0) {
						Noun noun2 = nouns.get(icol - 1);
						Verb verb = link.getVerb(noun1, noun2);
						txv.setText(verb.code);
						setTextColor(txv, verb);
					}
					else
						txv.setText(noun1.name);
				}
				else
					txv.setText(icol > 0 ? nouns.get(icol - 1).name : nounType.name);
			}
		}
	}
}
