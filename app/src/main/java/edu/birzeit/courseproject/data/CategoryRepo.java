package edu.birzeit.courseproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CategoryRepo {

    private DBHelper dbHelper;

    public CategoryRepo(Context c){
        dbHelper = new DBHelper(c);
    }

    public ArrayList<String> getCategories(String email, String type){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();

        Cursor c = db.rawQuery(
                "SELECT name FROM categories WHERE (user_email=? OR user_email='DEFAULT') AND type=? ORDER BY name",
                new String[]{email, type}
        );

        while(c.moveToNext()){
            list.add(c.getString(0));
        }
        c.close();
        return list;
    }

    public long addCategory(String email, String type, String name){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_email", email);
        cv.put("type", type);
        cv.put("name", name);
        return db.insert("categories", null, cv);
    }

    public boolean updateCategory(String email, String type, String oldName, String newName){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", newName);

        int rows = db.update("categories", cv,
                "user_email=? AND type=? AND name=?",
                new String[]{email, type, oldName});

        return rows > 0;
    }

    public boolean deleteCategory(String email, String type, String name){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("categories",
                "user_email=? AND type=? AND name=?",
                new String[]{email, type, name});
        return rows > 0;
    }public void seedDefaultsIfEmpty(String email) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM categories WHERE user_email=?",
                new String[]{email}
        );

        boolean empty = true;
        if (c.moveToFirst()) {
            empty = c.getInt(0) == 0;
        }
        c.close();

        if (!empty) return;

        addCategory(email, "EXPENSE", "Food");
        addCategory(email, "EXPENSE", "Rent");
        addCategory(email, "EXPENSE", "Bills");
        addCategory(email, "EXPENSE", "Entertainment");

        addCategory(email, "INCOME", "Salary");
        addCategory(email, "INCOME", "Scholarship");
        addCategory(email, "INCOME", "Gift");
    }

}
