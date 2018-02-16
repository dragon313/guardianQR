package com.vdovenkov.alexander.guardianqr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.vdovenkov.alexander.guardianqr.db.DataBaseHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private Class<?> mClss;
    Button readDbButton;
    TextView textView;

    //Переменные для работы с БД
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase database;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        setupToolbar();
        dataBaseHelper = new DataBaseHelper(this);

        try {
            dataBaseHelper.updateDataBase();
        } catch (IOException e) {
            throw new Error("UnableToUpdateDatabase");
        }

        database = dataBaseHelper.getWritableDatabase();

        textView = findViewById(R.id.textView);
        readDbButton = findViewById(R.id.read_db_button);
        readDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String magicItems = "";
                String querry = "SELECT * FROM magic_items";
                Cursor cursor = database.rawQuery(querry, null);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    magicItems += cursor.getString(1) + " | ";
                    cursor.moveToNext();
                }
                cursor.close();
                textView.setText(magicItems);
            }
        });

        fillList();
    }

    private void fillList() {
        //Список предметов
        List<Map<String, Object>> magicItems = new ArrayList<>();

        //Список параметров конкретного предмета
        Map<String, Object> items;

        Cursor cursor = database.rawQuery("SELECT * FROM magic_items", null);
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

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void launchSimpleActivity(View v) {
        Toast.makeText(this, "Запуск сканера магических отпечатков", Toast.LENGTH_SHORT).show();
        launchActivity(SimpleScannerActivity.class);
    }
//
//    public void launchSimpleFragmentActivity(View v) {
//        launchActivity(SimpleScannerFragmentActivity.class);
//    }
//
//    public void launchFullActivity(View v) {
//        launchActivity(FullScannerActivity.class);
//    }
//
//    public void launchFullFragmentActivity(View v) {
//        launchActivity(FullScannerFragmentActivity.class);
//    }
//
//    public void launchFullScreenScannerFragmentActivity(View v) {
//        launchActivity(FullScreenScannerFragmentActivity.class);
//    }
//
//    public void launchCustomViewFinderScannerActivity(View v) {
//        launchActivity(CustomViewFinderScannerActivity.class);
//    }
//
//    public void launchScalingScannerActivity(View v) {
//        launchActivity(ScalingScannerActivity.class);
//    }

    public void launchActivity(Class<?> clss) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            mClss = clss;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, clss);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZXING_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mClss != null) {
                        Intent intent = new Intent(this, mClss);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Искренне просим выдать доступ к магическому оку. Без него никак, хозяин...", Toast.LENGTH_SHORT).show();
                }
        }
    }
}