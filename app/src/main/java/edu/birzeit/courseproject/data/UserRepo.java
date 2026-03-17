package edu.birzeit.courseproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserRepo {

    private final DBHelper dbHelper;

    public UserRepo(Context ctx) {
        dbHelper = new DBHelper(ctx);
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + DBHelper.TABLE_USERS +
                        " WHERE " + DBHelper.COL_EMAIL + "=?",
                new String[]{email}
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    public boolean insertUser(String email, String first, String last, String pass) {
        if (emailExists(email)) return false;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COL_EMAIL, email);
        cv.put(DBHelper.COL_FIRST, first);
        cv.put(DBHelper.COL_LAST, last);
        cv.put(DBHelper.COL_PASS, pass);

        long row = db.insert(DBHelper.TABLE_USERS, null, cv);
        return row != -1;
    }

    public boolean checkLogin(String email, String pass) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT 1 FROM " + DBHelper.TABLE_USERS +
                        " WHERE " + DBHelper.COL_EMAIL + "=? AND " +
                        DBHelper.COL_PASS + "=?",
                new String[]{email, pass}
        );
        boolean ok = c.moveToFirst();
        c.close();
        return ok;
    }
    public String[] getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT first_name, last_name, password FROM users WHERE email=?",
                new String[]{email}
        );

        String[] res = null;
        if (c.moveToFirst()) {
            res = new String[]{ c.getString(0), c.getString(1), c.getString(2) };
        }
        c.close();
        return res;
    }

    public boolean updateProfile(String email, String first, String last) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("first_name", first);
        cv.put("last_name", last);
        int rows = db.update("users", cv, "email=?", new String[]{email});
        return rows > 0;
    }

    public boolean changePassword(String email, String newPass) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("password", newPass);
        int rows = db.update("users", cv, "email=?", new String[]{email});
        return rows > 0;
    }
    public String[] getUserName(String email){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT first_name, last_name FROM users WHERE email=?",
                new String[]{email});
        String[] res = new String[]{"", ""};
        if (c.moveToFirst()){
            res[0] = c.getString(0);
            res[1] = c.getString(1);
        }
        c.close();
        return res;
    }

    public boolean updateUserName(String email, String first, String last){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("first_name", first);
        cv.put("last_name", last);
        int rows = db.update("users", cv, "email=?", new String[]{email});
        return rows > 0;
    }

    public boolean changePassword(String email, String oldPass, String newPass){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT password FROM users WHERE email=?",
                new String[]{email});

        if (!c.moveToFirst()){
            c.close();
            return false;
        }

        String current = c.getString(0);
        c.close();

        if (!current.equals(oldPass)) return false;

        ContentValues cv = new ContentValues();
        cv.put("password", newPass);
        int rows = db.update("users", cv, "email=?", new String[]{email});
        return rows > 0;
    }

}
