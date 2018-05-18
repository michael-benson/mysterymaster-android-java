package com.mysterymaster.androidapp;

import android.content.SharedPreferences;

import com.mysterymaster.solver.ILocker;

/**
 * The Locker handles application-specific storage where data is stored as key-value pairs.<br>
 * Copyright (c) 2018 mysterymaster.com. All rights reserved.
 * @version 2018-05-15
 * @author Michael Benson <michael.benson@mysterymaster.com>
 */
final class Locker implements ILocker {
	/** Application-specific storage. */
	private final SharedPreferences storage;

	/**
	 * Constructor.
	 * @param storage Storage.
	 */
	public Locker(final SharedPreferences storage) {
		this.storage = storage;
	}

	@Override
	public boolean getBoolean(final String key, final boolean def) {
		return storage.getBoolean(key, def);
	}

	@Override
	public boolean setBoolean(final String key, final boolean val) {
		SharedPreferences.Editor editor = storage.edit();
		editor.putBoolean(key, val);
		editor.apply();
		return val;
	}

	@Override
	public int getInt(final String key, final int def) {
		return storage.getInt(key, def);
	}

	@Override
	public int setInt(final String key, final int val) {
		SharedPreferences.Editor editor = storage.edit();
		editor.putInt(key, val);
		editor.apply();
		return val;
	}
}
