package com.mysterymaster.androidapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mysterymaster.puzzle.Helper;
import com.mysterymaster.puzzle.Mark;

import java.util.List;

/**
 * The adapter for the recycler view to display the marks form.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-03
 * @author Michael Benson <michael.benson@mysterymaster.com>

 */
public final class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.ViewHolder> {
	/** List of marks. */
	private final List<Mark> marks;

	/**
	 * Constructor.
	 * @param marks List of marks.
	 */
	MarksAdapter(final List<Mark> marks) {
		this.marks = marks;
	}

	// Inflates a layout from XML and returns the holder.
	@Override
	public MarksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);

		// Inflate the layout for a mark from the mark_row.xml file.
		View markView = inflater.inflate(R.layout.mark_row, parent, false);

		// Returns the new holder instance.
		return new ViewHolder(markView);
	}

	// Populates the data into the item through the holder via its zero-based position.
	@Override
	public void onBindViewHolder(MarksAdapter.ViewHolder holder, final int position) {
		Mark mark = marks.get(position);
		BaseViewer.setText(holder.txtMarkNum, mark.num);
		holder.chkMarkValid.setChecked(mark.valid);
		holder.txtMarkLevel.setText(mark.levelAsString);
		holder.txtMarkType.setText(mark.type.toString());
		holder.txtMarkName.setText(Helper.getMsgAsOneLine(mark.name, " "));
	}

	/** Returns the size of the dataset (invoked by the layout manager). */
	@Override
	public int getItemCount() {
		return marks.size();
	}

	/** Provides a reference to the views for each data item. */
	class ViewHolder extends RecyclerView.ViewHolder {
		final TextView txtMarkNum;
		final CheckBox chkMarkValid;
		final TextView txtMarkLevel;
		final TextView txtMarkType;
		final TextView txtMarkName;

		/**
		 * Constructor.
		 * @param view View.
		 */
		ViewHolder(final View view) {
			super(view);
			//BaseViewer.print("ViewHolder constructor view=" + view);

			txtMarkNum = view.findViewById(R.id.txtMarkNum);
			chkMarkValid = view.findViewById(R.id.chkMarkValid);
			txtMarkLevel = view.findViewById(R.id.txtMarkLevel);
			txtMarkType = view.findViewById(R.id.txtMarkType);
			txtMarkName = view.findViewById(R.id.txtMarkName);
		}
	}
}
