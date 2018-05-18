package com.mysterymaster.androidapp;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.mysterymaster.puzzle.Puzzle;
import com.mysterymaster.puzzle.Verb;
import com.mysterymaster.solver.Solver;

import java.util.Locale;

/**
 * The parent class for viewers that manage a form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
class BaseViewer {
	/** Quote character. */
	static final char Q = '"';

	/** Newline character. */
	static final char NL = '\n';

	/** Default locale for the String.format method. */
	private static final Locale defaultLocale = Locale.getDefault();

	/**
	 * Displays debug messages in the Logcat window of the Android Studio IDE. Filter on Warn.
	 * @param msg String message.
	 */
	static void print(final String msg) {
		final String TAG = MainActivity.class.getSimpleName();
		Log.w(TAG, msg);
	}

	/**
	 * Sets the text of a TextView or EditText field when the value is an integer.<br>
	 * Note: TextView is the parent of EditText.
	 * @param fld Field.
	 * @param val Integer value.
	 */
	static void setText(final TextView fld, final int val) {
		fld.setText(String.format(defaultLocale,"%d", val));
	}

	/**
	 * Sets the text of a TextView or EditText field when the value is an boolean.<br>
	 * Note: TextView is the parent of EditText.
	 * @param fld Field.
	 * @param val Boolean value.
	 */
	protected static void setText(final TextView fld, final boolean val) {
		fld.setText(Boolean.toString(val));
	}

	protected static void setText(final TextView fld, final String val) {
		fld.setText(val);
	}

	/**
	 * Updates the grid cell's text and color based on the given verb. Called by GridsViewer.
	 * @param txv TextView.
	 * @param verb Verb.
	 */
	static void setGridVerb(final TextView txv, final Verb verb) {
		GridsViewer.GridData tag = (GridsViewer.GridData) txv.getTag();
		tag.verb = verb;

		txv.setText(verb.code);
		setTextColor(txv, verb);
	}

	/**
	 * Sets the text color of the field based on the verb. Called by LinksViewer, GridsViewer.
	 * @param txv TextView.
	 * @param verb Verb.
	 */
	static void setTextColor(final TextView txv, final Verb verb) {
		switch (verb.num) {
			case 0: txv.setTextColor(Color.RED); break;
			case 1: txv.setTextColor(Color.BLACK); break;
			case 2: txv.setTextColor(Color.GRAY); break;
		}
	}

	/** Width in pixels for the "#" field in most tables. */
	final int colNumWidth = 100;

	/** Width in pixels for the checkbox field in most tables. */
	final int colChkWidth = 72;

	/** Width in pixels for the Hits field in the Facts and Rules tables. */
	final int colHitsWidth = 100;

	/** Screen height. Set by the updateAppDimensions method. */
	int appHeight;

	/** Screen width. Set by the updateAppDimensions method. */
	int appWidth;

	/** Updates the height and width variables for the current screen orientation. */
	void updateAppDimensions() {
		DisplayMetrics displayMetrics = activity.getDisplayMetrics();
		appHeight = displayMetrics.heightPixels;
		appWidth = displayMetrics.widthPixels;
		//BaseViewer.print("App width is " + appWidth + " and height is " + appHeight);
	}

	/** Main Activity of the android application. */
	MainActivity activity;

	/** Solver object. */
	Solver solver;

	/** Puzzle object. */
	Puzzle puzzle;

	/** Border object. Initialized in the constructor. */
	final ShapeDrawable border = new ShapeDrawable();

	/**
	 * Zero-argument constructor. This initializes the Border object.<br>
	 * Called by the LinksViewer, GridsViewer. */
	BaseViewer() {
		// Initialize the Border object.
		border.setShape(new RectShape());
		border.getPaint().setColor(Color.GRAY);
		border.getPaint().setStrokeWidth(1f);
		border.getPaint().setStyle(Paint.Style.STROKE);
	}
}
