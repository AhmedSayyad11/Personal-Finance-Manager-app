package edu.birzeit.courseproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class BudgetRepo {

    private DBHelper dbHelper;

    public BudgetRepo(Context c){
        dbHelper = new DBHelper(c);
    }

    public long addBudget(String email, String category, double limit){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_email", email);
        cv.put("category", category);
        cv.put("limit_amount", limit);
        return db.insert("budgets", null, cv);
    }

    public boolean updateBudget(int id, double newLimit){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("limit_amount", newLimit);
        int rows = db.update("budgets", cv, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean deleteBudget(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("budgets", "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public ArrayList<String> getBudgets(String email){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();

        Cursor c = db.rawQuery(
                "SELECT id, category, limit_amount FROM budgets WHERE user_email=? ORDER BY category",
                new String[]{email}
        );

        while(c.moveToNext()){
            int id = c.getInt(0);
            String cat = c.getString(1);
            double lim = c.getDouble(2);

            list.add("#" + id + " | " + cat + " | " + lim);
        }
        c.close();
        return list;
    }

    // Total spent in this month for a category
    public double getSpentThisMonth(String email, String category){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(amount),0) FROM transactions " +
                        "WHERE user_email=? AND type='EXPENSE' AND category=? " +
                        "AND substr(date,1,7)=substr(date('now'),1,7)",
                new String[]{email, category}
        );

        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        return total;
    }
}
