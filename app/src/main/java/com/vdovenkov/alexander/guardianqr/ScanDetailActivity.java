package com.vdovenkov.alexander.guardianqr;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.vdovenkov.alexander.guardianqr.db.DataBaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanDetailActivity extends AppCompatActivity {
    private static final String TAG = "ScanDetailActivity";
    private static final String ITEM_ID = "ITEM_ID";

    private TextView titleTextView;
    private TextView descriptionTextView;
    private String itemm_id;

    //Переменные для работы с БД
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_detail);
        itemm_id = getIntent().getStringExtra(ITEM_ID);
        initDB();
        initGUI();
        fetchInfo();

    }

    private void fetchInfo() {
        String magicItems = "";
        //language=RoomSql
        String querry = "SELECT * FROM magic_items WHERE _id = ?";
        Cursor cursor = database.rawQuery(querry, new String[]{itemm_id});
        cursor.moveToFirst();
        titleTextView.setText(cursor.getString(1));
        descriptionTextView.setText(cursor.getString(2));
        cursor.close();
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
            throw new Error("UnableToUpdateDatabase");
        }

        database = dataBaseHelper.getWritableDatabase();
    }

    public static void showScanResult(Context context, String id) {
        Intent intent = new Intent(context, ScanDetailActivity.class);
        intent.putExtra(ITEM_ID, id);
        context.startActivity(intent);
    }

    private void fillList() {
        //Список предметов
        List<Map<String, Object>> magicItems = new ArrayList<>();

        //Список параметров конкретного предмета
        Map<String, Object> items;
        //Условия можно менять, ура!
        Cursor cursor = database.rawQuery("SELECT * FROM magic_items WHERE group_id = ?", new String[]{"2"});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            items = new HashMap<>();

            items.put("title", cursor.getString(1));
            items.put("description", cursor.getString(2));

            magicItems.add(items);

            cursor.moveToNext();
        }
        cursor.close();

        String[] from = {"title", "description"};
        int[] to = {R.id.title_text_view, R.id.description_text_view};

        SimpleAdapter adapter = new SimpleAdapter(this, magicItems, R.layout.list_item,
                from, to);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

    }
}
