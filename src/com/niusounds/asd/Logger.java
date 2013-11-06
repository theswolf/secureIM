package com.niusounds.asd;

import android.util.Log;

class Logger {
	private static final String TAG = "SQLiteDAO log";

	public static void log(String sql) {
		Log.d(TAG, sql);
	}
}
