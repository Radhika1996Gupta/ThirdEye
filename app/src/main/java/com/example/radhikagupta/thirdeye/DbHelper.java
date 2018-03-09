/**
 * Created by RADHIKA GUPTA on 09-Mar-18.
 */
package com.example.radhikagupta.thirdeye;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "SignupLoginDatabase";
    private static final String TBL_NAME = "users";
    private static final String COL_EMAIL = "email";
    private static final String COL_PHONE1 = "phone1";
    private static final String COL_PHONE2 = "phone2";
    private static final String COL_PHONE3 = "phone3";
    private static final String COL_PASSWORD = "password";

    private Context context;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        //context is needed to create a database which is private to this application.
        //DB_NAME is the database name.
        //null is passed to the CursorFactory parameter because we don't want custom cursors to be returned form this database. The standard SQLineCursors will be enough.
        //1 is passed as the default version.
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //invoked when getWritableDatabase() is called and iff the database doesn't already exist.
        String CREATE_TABLE = "CREATE TABLE " + TBL_NAME + " (" + COL_EMAIL + " TEXT PRIMARY KEY, " + COL_PHONE1 + " TEXT, " + COL_PHONE2 + " TEXT, " + COL_PHONE3 + " TEXT, " + COL_PASSWORD + ", TEXT)"; //sql query to create the table.
        //PASSWORDS SHOULD NEVER BE STORED IN PLAIN TEXT FORMAT.
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int new_) {
        //used to change the database schema.
        // If schema is changed then the version number should be incremented while creating the database and only then this method is invoked.
        // In this method we just recreate the table as data loss is not an issue with this application.
        String DROP_TABLE = "DROP TABLE IF EXISTS " + TBL_NAME; //sql query to delete the table
        db.execSQL(DROP_TABLE);
        onCreate(db); //create the table again.
    }

    //helper functions for CURD operations:

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_EMAIL, user.email);
        contentValues.put(COL_PHONE1, user.phone1);
        contentValues.put(COL_PHONE2, user.phone2);
        contentValues.put(COL_PHONE3, user.phone3);
        contentValues.put(COL_PASSWORD, user.password);
        db.insert(TBL_NAME, null, contentValues);
        db.close();
    }

    public User checkUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        String CHECK_SQL = "SELECT " + COL_PHONE1 + ", " + COL_PHONE2 + ", " + COL_PHONE3 + " FROM " + TBL_NAME + " WHERE " + COL_EMAIL + " = '" + user.email + "' AND " + COL_PASSWORD + " = '" + user.password + "'";
        Cursor cursor = db.rawQuery(CHECK_SQL, null);
        if (cursor.moveToFirst()) {
            user.phone1 = cursor.getString(cursor.getColumnIndex(COL_PHONE1));
            user.phone2 = cursor.getString(cursor.getColumnIndex(COL_PHONE2));
            user.phone3 = cursor.getString(cursor.getColumnIndex(COL_PHONE3));
            cursor.close();
            return user;
        } else {
            cursor.close();
            return null;
        }
    }

    public List<User> getAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        String SELECT_ALL = "SELECT * FROM " + TBL_NAME;
        Cursor cursor = db.rawQuery(SELECT_ALL, null);
        List<User> userList = new ArrayList<User>();
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.email = cursor.getString(0);
                user.phone1 = cursor.getString(1);
                user.phone2 = cursor.getString(2);
                user.phone3 = cursor.getString(3);
                user.password = cursor.getString(4);
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }
}
