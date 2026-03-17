package edu.birzeit.courseproject.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class TransactionRepo {

    private final DBHelper dbHelper;

    public TransactionRepo(Context ctx) {
        dbHelper = new DBHelper(ctx);
    }

    public long addTransaction(String userEmail, String type, String category,
                               double amount, String note, String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("user_email", userEmail);
        cv.put("type", type);
        cv.put("category", category);
        cv.put("amount", amount);
        cv.put("note", note);
        cv.put("date", date);
        return db.insert("transactions", null, cv);
    }

    public ArrayList<String> getTransactions(String userEmail, String type) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id, category, amount, date FROM transactions " +
                        "WHERE user_email=? AND type=? ORDER BY id DESC",
                new String[]{userEmail, type}
        );

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String category = c.getString(1);
            double amount = c.getDouble(2);
            String date = c.getString(3);

            list.add("#" + id + " | " + category + " | " + amount + " | " + date);
        }
        c.close();
        return list;
    }

    public boolean deleteTransaction(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete("transactions", "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
    public double getTotal(String userEmail, String type) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(amount),0) FROM transactions WHERE user_email=? AND type=?",
                new String[]{userEmail, type}
        );

        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        return total;
    }

    public double getBalance(String userEmail) {
        double income = getTotal(userEmail, "INCOME");
        double expense = getTotal(userEmail, "EXPENSE");
        return income - expense;
    }
    public ArrayList<String> getTransactionsSortedByDate(String userEmail, String type) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT id, category, amount, note, date FROM transactions " +
                        "WHERE user_email=? AND type=? " +
                        "ORDER BY date DESC, id DESC",
                new String[]{userEmail, type}
        );

        while (c.moveToNext()) {
            int id = c.getInt(0);
            String category = c.getString(1);
            double amount = c.getDouble(2);
            String note = c.getString(3);
            String date = c.getString(4);

            // include note to help in edit
            list.add("#" + id + " | " + category + " | " + amount + " | " + date + " | " + (note == null ? "" : note));
        }
        c.close();
        return list;
    }

    public boolean updateTransaction(int id, String category, double amount, String note, String date) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("category", category);
        cv.put("amount", amount);
        cv.put("note", note);
        cv.put("date", date);

        int rows = db.update("transactions", cv, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
    public double getTotalBetween(String userEmail, String type, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IFNULL(SUM(amount),0) FROM transactions " +
                        "WHERE user_email=? AND type=? AND date>=? AND date<=?",
                new String[]{userEmail, type, startDate, endDate}
        );
        double total = 0;
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        return total;
    }

    public ArrayList<String> getCategoryTotalsBetween(String userEmail, String type, String startDate, String endDate) {
        ArrayList<String> res = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT category, IFNULL(SUM(amount),0) as total FROM transactions " +
                        "WHERE user_email=? AND type=? AND date>=? AND date<=? " +
                        "GROUP BY category ORDER BY total DESC",
                new String[]{userEmail, type, startDate, endDate}
        );
        while (c.moveToNext()) {
            String cat = c.getString(0);
            double t = c.getDouble(1);
            res.add(cat + " : " + t);
        }
        c.close();
        return res;
    }
    public double getSpentThisMonth(String email, String category) {
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
