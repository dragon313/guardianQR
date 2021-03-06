package com.vdovenkov.alexander.guardianqr.db;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.vdovenkov.alexander.guardianqr.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DataBaseHelper";
    private static final int DB_VERSION = 9; // TODO: 027 27.05.18 Не забывать инкрементить!!!
    private static String DB_NAME = "guardDB.db";
    private static String DB_PATH = "";
    private final Context context;
    private SQLiteDatabase database;
    private boolean needUpdate = false;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.context = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    public void updateDataBase() throws IOException {
        if (needUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists()) {
                dbFile.delete();
            }
            copyDataBase();
            needUpdate = false;
        }
    }

    public boolean checkRules(int id, Context context) {
        String groupQuerry = "SELECT magic_items.group_id FROM magic_items WHERE magic_items._id=?";
        openDataBase();
        Cursor groupCursor = database.rawQuery(groupQuerry, new String[]{String.valueOf(id)});
        groupCursor.moveToFirst();
        String groupId = groupCursor.getString(0);
        Log.d(TAG, "Данный предмет относится к группе: " + groupId);

        String querry = "SELECT * FROM access_rules WHERE access_rules._id = (SELECT magic_items.group_id FROM magic_items WHERE magic_items._id=?)";
        Cursor cursor = database.rawQuery(querry, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        boolean result = cursor.getString(2).equals("1");
        cursor.close();
        return result;
    }

    public String getAlterDescription(String id) {
        String querry = "SELECT alter_description FROM magic_items where _id=?";
        Cursor cursor = database.rawQuery(querry, new String[]{id});
        cursor.moveToFirst();
        String alterDescription = cursor.getString(7);
        cursor.close();
        return alterDescription;
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException e) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
//        InputStream inputStream = context.getAssets().open(DB_NAME);
        InputStream inputStream = context.getResources().openRawResource(R.raw.guard_db);
        OutputStream outputStream = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        Log.d(TAG, "Идёт копирование ДБ");
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public boolean openDataBase() throws SQLException {
        database = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return database != null;
    }

    @Override
    public synchronized void close() {
        if (database != null) {
            database.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            needUpdate = true;
        }
    }
}
