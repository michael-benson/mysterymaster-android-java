package com.mysterymaster.androidapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mysterymaster.puzzle.Fact;

import java.util.List;

/**
 * The adapter for the recycler view to display the facts.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-11
 * @author Michael Benson <michael.benson@mysterymaster.com>

 */
public final class FactsAdapter extends RecyclerView.Adapter<FactsAdapter.ViewHolder> {
	/** List of facts. */
	private final List<Fact> facts;

	/**
	 * Constructor.
	 * @param facts List of facts.
	 */
	FactsAdapter(final List<Fact> facts) {
		this.facts = facts;
	}

	// Inflates a layout from XML and returns the holder.
	@Override
	public FactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		Context context = parent.getContext();
		LayoutInflater inflater = LayoutInflater.from(context);

		// Inflate the layout for a fact from the fact_row.xml file.
		View factView = inflater.inflate(R.layout.fact_row, parent, false);

		// Returns the new holder instance.
		return new ViewHolder(factView);
	}

	// Populates the data into the item through the holder via its zero-based position.
	@Override
	public void onBindViewHolder(FactsAdapter.ViewHolder holder, final int position) {
		Fact fact = facts.get(position);
		BaseViewer.setText(holder.txtFactNum, fact.num);
		holder.chkFactEnabled.setChecked(fact.enabled);
		BaseViewer.setText(holder.txtFactHits, fact.hits);
		holder.txtFactName.setText(fact.name);
	}

	/** Returns the size of the dataset (invoked by the layout manager). */
	@Override
	public int getItemCount() {
		return facts.size();
	}

	/** Provides a reference to the views for each data item. */
	class ViewHolder extends RecyclerView.ViewHolder {
		final TextView txtFactNum;
		final CheckBox chkFactEnabled;
		final TextView txtFactHits;
		final TextView txtFactName;

		/**
		 * Constructor.
		 * @param view View.
		 */
		ViewHolder(final View view) {
			super(view);
			//BaseViewer.print("ViewHolder constructor view=" + view);

			txtFactNum = view.findViewById(R.id.txtFactNum);
			chkFactEnabled = view.findViewById(R.id.chkFactEnabled);
			txtFactHits = view.findViewById(R.id.txtFactHits);
			txtFactName = view.findViewById(R.id.txtFactName);
		}
	}
}
