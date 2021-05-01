package com.ten.lifecat.phone.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class MyDatabaseHelper(val context: Context, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {

    private val createUser = "create table User (" +
            "id integer primary key autoincrement," +
            "user_name text," +
            "user_email text," +
            "user_password text," +
            "is_login integer," + //0为登陆，1为登陆
            "other text)"

    private val createPhoto = "create table Photo (" +
            "id integer primary key autoincrement," +
            "photo_name text," +
            "photo_path text," +
            "other text)"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(createUser)
//        db.execSQL(createCategory)
        Toast.makeText(context, "Create succeeded", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion <= 1) {
//            db.execSQL(createCategory)
        }
        if (oldVersion <= 2) {
//            db.execSQL("alter table Book add column category_id integer")
        }
    }

}