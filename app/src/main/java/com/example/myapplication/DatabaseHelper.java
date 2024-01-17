package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.FileInputStream;
import java.io.IOException;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String databaseName = "Signup.db";
    public DatabaseHelper(@Nullable Context context) {
        super(context, "Signup.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        MyDatabase.execSQL("create table user(username TEXT primary key, name TEXT, password TEXT, confirm TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDatabase, int oldVersion, int newVersion) {
        MyDatabase.execSQL("drop table if exists user");
    }

    public boolean insertData(String username, String name, String password, String confirm){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("name", name);
        contentValues.put("password", password);
        contentValues.put("confirm", confirm);
        long result = MyDatabase.insert("user", null, contentValues);

        if (result == -1){
            return false;
        } else {
            return true;
        }
    }

    public boolean checkUsername(String username){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from user where username = ?", new String[]{username});

        if (cursor.getCount() > 0){
            return true;
        } else {
            return false;
        }
    }
    public Boolean checkPassword(String username, String password){
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from user where username = ? and password = ?", new String[]{username, password});

        if (cursor.getCount() > 0){
            return true;
        } else {
            return false;
        }
    }
}
