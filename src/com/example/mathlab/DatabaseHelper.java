/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mathlab;

import java.util.HashSet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Database helper class for {@link MyProvider}. Mostly just has a bit
 * {@link #onCreate} to initialize the database.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    // private static final String DATABASE_NAME =
    // Environment.getExternalStorageDirectory()
    // + "/mydatabase.db";
    private static final String DATABASE_NAME = "mydatabase.db";

    // Please, please please. If you update the database version, check to make
    // sure the database gets upgraded properly. At a minimum, please confirm
    // that 'upgradeVersion' is properly propagated through your change. Not
    // doing so will result in a loss of user settings.
    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    private static final HashSet<String> mValidTables = new HashSet<String>();

    static final String TABLE_MATHDATA = "mathdata";
    static final String TABLE_ERRORDATA = "errordata";
    		

    static {
        mValidTables.add(TABLE_MATHDATA);
        mValidTables.add(TABLE_ERRORDATA);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static boolean isValidTable(String name) {
        return mValidTables.contains(name);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_MATHDATA + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "total INTEGER," +
                "correct INTEGER," +
                "totaltime INTEGER" +
                ");");
        db.execSQL("CREATE TABLE " + TABLE_ERRORDATA + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
        		"firstNum INTEGER," +
                "secondNum INTEGER," +
        		"operType INTEGER," +
                "errorAnswer INTEGER," +
        		"rightAnswer INTEGER" +
        		");");
        

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int currentVersion) {
        int upgradeVersion = oldVersion;

        // Pattern for upgrade blocks:
        //
        // if (upgradeVersion == [the DATABASE_VERSION you set] - 1) {
        // .. your upgrade logic..
        // upgradeVersion = [the DATABASE_VERSION you set]
        // }

        // *** Remember to update DATABASE_VERSION above!

        if (upgradeVersion != currentVersion) {
            Log.w(TAG, "Got stuck trying to upgrade from version " + upgradeVersion
                    + ", must wipe the settings provider");
            db.execSQL("DROP INDEX IF EXISTS " + TABLE_MATHDATA);
            db.execSQL("DROP INDEX IF EXISTS " + TABLE_ERRORDATA);
            onCreate(db);
        }
    }

}
