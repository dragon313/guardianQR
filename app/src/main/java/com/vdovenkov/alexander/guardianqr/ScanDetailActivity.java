package com.vdovenkov.alexander.guardianqr;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.vdovenkov.alexander.guardianqr.db.DataBaseHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanDetailActivity extends AppCompatActivity {
    private static final String TAG = "ScanDetailActivity";
    private static final String ITEM_ID = "ITEM_ID";
    //Переменные для работы с БД
    private static DataBaseHelper dataBaseHelper;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView shortDescriptionTextView;
    private String itemId;
    private static SQLiteDatabase database;


    public static void showScanResult(Context context, String id) {
        dataBaseHelper = new DataBaseHelper(context);
        if (dataBaseHelper.checkRules(Integer.parseInt(id), context)) {
            Intent intent = new Intent(context, ScanDetailActivity.class);
            intent.putExtra(ITEM_ID, id);
            context.startActivity(intent);
        } else {
            String querry = "SELECT alter_description FROM magic_items where _id=?";
            Cursor cursor = database.rawQuery(querry, new String[]{id});
            cursor.moveToFirst();
            Toast.makeText(context, cursor.getString(7), Toast.LENGTH_SHORT).show();
            cursor.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_detail);
        itemId = getIntent().getStringExtra(ITEM_ID);
        initDB();
        initGUI();
        fetchInfo();
    }

    private void fetchInfo() {
        String querry = "SELECT * FROM magic_items WHERE _id = ?";
        String setReadDate = "UPDATE magic_items SET read_date = '" + new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ROOT).format(new Date()) + "' WHERE _id = ? AND is_read != 1";
        String isReadQuery = "UPDATE magic_items SET is_read = 1 WHERE _id = ?";
        database.execSQL(isReadQuery, new Object[]{itemId});
        database.execSQL(setReadDate, new Object[]{itemId});
        Cursor cursor = database.rawQuery(querry, new String[]{itemId});
        cursor.moveToFirst();
        titleTextView.setText(cursor.getString(1));
        descriptionTextView.setText(cursor.getString(2));
        shortDescriptionTextView.setText(cursor.getString(5));
        cursor.close();
    }

    private void initGUI() {
        titleTextView = findViewById(R.id.scan_title_text_view);
        descriptionTextView = findViewById(R.id.scan_description_text_view);
        shortDescriptionTextView = findViewById(R.id.short_description_text_view);
    }

    private void initDB() {
        try {
            dataBaseHelper.updateDataBase();
        } catch (IOException e) {
            throw new Error("Unable To Update Database");
        }
        database = dataBaseHelper.getWritableDatabase();
    }
}
