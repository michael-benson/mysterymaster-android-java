package com.mysterymaster.androidapp;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mysterymaster.solver.Spots;

/**
 * The Setup Viewer class manages the Setup form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-17
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class SetupViewer extends BaseViewer {
	/** Column headers. */
	private final TextView[] colHeaders;

	/** Setup Options object. */
	private Spots spots;

	/**
	 * Constructor. Note: The Puzzle object is not used.
	 * @param activity Main Activity.
	 */
	SetupViewer(final MainActivity activity) {
		this.activity = activity;
		this.solver = activity.solver;
		spots = activity.spots;

		final TextView txtSetupPauses  = activity.findViewById(R.id.txtSetupPauses);
		final TextView txtSetupGeneral = activity.findViewById(R.id.txtSetupGeneral);
		final TextView txtSetupLevels  = activity.findViewById(R.id.txtSetupLevels);
		final TextView txtSetupLaws    = activity.findViewById(R.id.txtSetupLaws);

		colHeaders = new TextView[]{txtSetupPauses, txtSetupGeneral, txtSetupLevels, txtSetupLaws};

		// Pauses.
		getCheckbox(R.id.chkPauseAll      ,"okPauseAll");
		getCheckbox(R.id.chkPauseLevel    ,"okPauseLevel");
		getCheckbox(R.id.chkPauseSolution ,"okPauseSolution");
		getCheckbox(R.id.chkPauseViolation,"okPauseViolation");
		getCheckbox(R.id.chkPauseMark     ,"okPauseMark");
		getCheckbox(R.id.chkPauseTrigger  ,"okPauseTrigger");
		getCheckbox(R.id.chkPauseGuess    ,"okPauseGuess");
		getCheckbox(R.id.chkPausePlacers  ,"okPausePlacers");

		// General.
		getCheckbox(R.id.chkAutorun ,"okAutorun");
		getCheckbox(R.id.chkRechart ,"okRechart");
		getCheckbox(R.id.chkShowFab ,"okShowFab");
		getCheckbox(R.id.chkRules   ,"okRules");
		getCheckbox(R.id.chkTriggers,"okTriggers");

		// Levels.
		getCheckbox(R.id.chkLevel0,"okLevel0");
		getCheckbox(R.id.chkLevel1,"okLevel1");
		getCheckbox(R.id.chkLevel2,"okLevel2");
		getCheckbox(R.id.chkLevel3,"okLevel3");
		getCheckbox(R.id.chkLevel4,"okLevel4");

		// Laws.
		getCheckbox(R.id.chkLaw0,"okLaw0");
		getCheckbox(R.id.chkLaw1,"okLaw1");
		getCheckbox(R.id.chkLaw2,"okLaw2");
		getCheckbox(R.id.chkLaw3,"okLaw3");
		getCheckbox(R.id.chkLaw4,"okLaw4");
		getCheckbox(R.id.chkLaw5,"okLaw5");
	}

	/**
	 * Initializes and returns the CheckBox object given by its Id. Called by the constructor.
	 * @param id Id.
	 * @param key Key.
	 * @return CheckBox.
	 */
	private CheckBox getCheckbox(final int id, final String key) {
		CheckBox chk = activity.findViewById(id);
		boolean  val = spots.getValue(key);
		chk.setTag(key);
		chk.setChecked(val);
		chk.setOnClickListener(onClickCheckbox);
		return chk;
	}

	private final View.OnClickListener onClickCheckbox = view -> {
		CheckBox chk = (CheckBox) view;
		String key = chk.getTag().toString();
		boolean val = chk.isChecked();
		BaseViewer.print("You clicked the " + chk.getText() + " checkbox. " + key + " = " + val);
		spots.setValue(key, val);

		// The visibility of the Fab must be updated here.
		if ("okShowFab".equals(key)) activity.setFabVisibility();
	};

	/** Updates the form. Called when the Setup tab is pressed. */
	void update() {
		final int colMaxWidth = 320;
		final int ncols = colHeaders.length;

		updateAppDimensions();

		int colWidth = Math.min((appWidth) / ncols, colMaxWidth);
		for (TextView txt : colHeaders) txt.setWidth(colWidth);
	}
}
