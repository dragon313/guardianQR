package com.vdovenkov.alexander.guardianqr;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.vdovenkov.alexander.guardianqr.db.DataBaseHelper;

import java.io.IOException;

public class ScanDetailActivity extends AppCompatActivity {
    private static final String TAG = "ScanDetailActivity";
    private static final String ITEM_ID = "ITEM_ID";

    private TextView titleTextView;
    private TextView descriptionTextView;
    private String itemId;

    //Переменные для работы с БД
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase database;

    public static void showScanResult(Context context, String id) {
        Intent intent = new Intent(context, ScanDetailActivity.class);
        intent.putExtra(ITEM_ID, id);
        context.startActivity(intent);
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
        if (dataBaseHelper.checkRules(Integer.parseInt(itemId), this)) {
            String querry = "SELECT * FROM magic_items WHERE _id = ?";
            String isReadQuery = "UPDATE magic_items SET is_read = 1 WHERE _id = ?";
            database.execSQL(isReadQuery, new Object[]{itemId});
            Cursor cursor = database.rawQuery(querry, new String[]{itemId});
            cursor.moveToFirst();
            titleTextView.setText(cursor.getString(1));
            descriptionTextView.setText(cursor.getString(2));
            cursor.close();
        } else {
            Toast.makeText(this, "Ты не знаешь, что это", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initGUI() {

        titleTextView = findViewById(R.id.scan_title_text_view);
        descriptionTextView = findViewById(R.id.scan_description_text_view);
    }

    private void initDB() {
        dataBaseHelper = new DataBaseHelper(this);

        try {
            dataBaseHelper.updateDataBase();
        } catch (IOException e) {
            throw new Error("Unable To Update Database");
        }

        database = dataBaseHelper.getWritableDatabase();
    }
}
