package edu.birzeit.courseproject.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "pfm.db";
    private static final int DB_VERSION = 5;

    public static final String TABLE_USERS = "users";
    public static final String COL_EMAIL = "email";
    public static final String COL_FIRST = "first_name";
    public static final String COL_LAST  = "last_name";
    public static final String COL_PASS  = "password";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // users
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "email TEXT PRIMARY KEY," +
                "first_name TEXT NOT NULL," +
                "last_name TEXT NOT NULL," +
                "password TEXT NOT NULL" +
                ")");

        // transactions
        db.execSQL("CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_email TEXT NOT NULL," +
                "type TEXT NOT NULL," +
                "category TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "note TEXT," +
                "date TEXT NOT NULL" +
                ")");

        // categories
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_email TEXT NOT NULL," +
                "type TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "UNIQUE(user_email, type, name)" +
                ")");

        // budgets
        db.execSQL("CREATE TABLE IF NOT EXISTS budgets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_email TEXT NOT NULL," +
                "category TEXT NOT NULL," +
                "limit_amount REAL NOT NULL," +
                "UNIQUE(user_email, category)" +
                ")");

        seedDefaultCategories(db);
    }

    private void seedDefaultCategories(SQLiteDatabase db) {
        // We'll use user_email='DEFAULT'
        db.execSQL("INSERT OR IGNORE INTO categories(user_email,type,name) VALUES('DEFAULT','EXPENSE','Food')");
        db.execSQL("INSERT OR IGNORE INTO categories(user_email,type,name) VALUES('DEFAULT','EXPENSE','Rent')");
        db.execSQL("INSERT OR IGNORE INTO categories(user_email,type,name) VALUES('DEFAULT','EXPENSE','Bills')");
        db.execSQL("INSERT OR IGNORE INTO categories(user_email,type,name) VALUES('DEFAULT','EXPENSE','Entertainment')");

        db.execSQL("INSERT OR IGNORE INTO categories(user_email,type,name) VALUES('DEFAULT','INCOME','Salary')");
        db.execSQL("INSERT OR IGNORE INTO categories(user_email,type,name) VALUES('DEFAULT','INCOME','Scholarship')");
        db.execSQL("INSERT OR IGNORE INTO categories(user_email,type,name) VALUES('DEFAULT','INCOME','Gift')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Ensure tables exist
        db.execSQL("CREATE TABLE IF NOT EXISTS categories (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_email TEXT NOT NULL," +
                "type TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "UNIQUE(user_email, type, name)" +
                ")");


        db.execSQL("CREATE TABLE IF NOT EXISTS budgets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_email TEXT NOT NULL," +
                "category TEXT NOT NULL," +
                "limit_amount REAL NOT NULL DEFAULT 0," +
                "UNIQUE(user_email, category)" +
                ")");

        // If budgets has monthly_limit, add limit_amount and migrate
        if (tableHasColumn(db, "budgets", "monthly_limit") && !tableHasColumn(db, "budgets", "limit_amount")) {
            db.execSQL("ALTER TABLE budgets ADD COLUMN limit_amount REAL NOT NULL DEFAULT 0");
            db.execSQL("UPDATE budgets SET limit_amount = monthly_limit");
        }

        // If budgets exists but missing limit_amount
        if (tableExists(db, "budgets") && !tableHasColumn(db, "budgets", "limit_amount")) {
            db.execSQL("ALTER TABLE budgets ADD COLUMN limit_amount REAL NOT NULL DEFAULT 0");
        }

        seedDefaultCategories(db);
    }

    private boolean tableExists(SQLiteDatabase db, String tableName) {
        Cursor c = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{tableName}
        );
        boolean ok = c.moveToFirst();
        c.close();
        return ok;
    }

    private boolean tableHasColumn(SQLiteDatabase db, String tableName, String columnName) {
        Cursor c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        boolean found = false;
        while (c.moveToNext()) {
            String col = c.getString(c.getColumnIndexOrThrow("name"));
            if (columnName.equals(col)) {
                found = true;
                break;
            }
        }
        c.close();
        return found;
    }
}
