package com.vdovenkov.alexander.guardianqr;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vdovenkov.alexander.guardianqr.db.DataBaseHelper;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    Button confirmButton;
    TextInputEditText passwordEditText;

    //Метки уровней доступа
    TextView guardAccessTextView;
    TextView chearchAccessTextView;
    TextView ordenTextView;
    TextView medicTextView;
    TextView cityGuardianTextView;
    TextView geniyTextView;
    TextView scientistTextView;

    //Переменные для работы с БД
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initDB();
        initGUI();

        passwordEditText = findViewById(R.id.settings_edit_text);
        confirmButton = findViewById(R.id.settings_cast_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Введён пароль: " + passwordEditText.getText().toString());
                // TODO: 017 17.02.18 реализовать получение списка фракций и паролей к ним из БД
                switch (passwordEditText.getText().toString()) {
                    case "1434": //пароль для стражей
                        changeAccessRule("Страж");
                        initGUI();
                        break;
                    case "2465":
                        changeAccessRule("Монах");
                        initGUI();
                        break;
                    case "666":
                        changeAccessRule("Орденец");
                        initGUI();
                        break;
                    case "0003":
                        changeAccessRule("Медик/алхимик");
                        initGUI();
                        break;
                    case "0002":
                        changeAccessRule("Городская стража");
                        initGUI();
                        break;
                    case "0000":
                        changeAccessRule("Одаренный");
                        initGUI();
                        break;
                    case "314":
                        changeAccessRule("Ученый");
                        initGUI();
                        break;
                    case "211088":
                        resetAccessRules();
                        Toast.makeText(SettingsActivity.this, "Магический сканер больше неактивен. Для активации заново снимите печать.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SettingsActivity.this, "Магический камень раскалился и нанёс тебе урон в 1 хит!", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void changeAccessRule(String fraction) {
        String query = "UPDATE access_rules SET is_granted = 1 WHERE description = ?";
        database.execSQL(query, new Object[]{fraction});
        initGUI();
    }

    private void resetAccessRules() {
        String query = "UPDATE access_rules SET is_granted = '0'";
        database.execSQL(query);
        initGUI();
    }

    private void initGUI() {
        guardAccessTextView = findViewById(R.id.guard_access_text_view);
        chearchAccessTextView = findViewById(R.id.chearch_access_text_view);
        ordenTextView = findViewById(R.id.orden_access_text_view);
        medicTextView = findViewById(R.id.medic_access_text_view);
        cityGuardianTextView = findViewById(R.id.city_guardian_access_text_view);
        geniyTextView = findViewById(R.id.geniy_access_text_view);
        scientistTextView = findViewById(R.id.scientist_access_text_view);

        String querry = "SELECT * FROM access_rules";
        Cursor cursor = database.rawQuery(querry, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String s = cursor.getString(1);
            switch (s) {
                case "Страж":
                    guardAccessTextView.setText(String.format(getResources().getString(R.string.guard_access_text), cursor.getString(2).equals("0") ? "недоступны" : "доступны"));
                    break;
                case "Монах":
                    chearchAccessTextView.setText(String.format(getResources().getString(R.string.chearch_access_text), cursor.getString(2).equals("0") ? "недоступны" : "доступны"));
                    break;
                case "Орденец":
                    ordenTextView.setText(String.format(getResources().getString(R.string.orden_access_text), cursor.getString(2).equals("0") ? "недоступны" : "доступны"));
                    break;
                case "Медик/алхимик":
                    medicTextView.setText(String.format(getResources().getString(R.string.medic_access_text), cursor.getString(2).equals("0") ? "недоступны" : "доступны"));
                    break;
                case "Городская стража":
                    cityGuardianTextView.setText(String.format(getResources().getString(R.string.city_guardian_access_text), cursor.getString(2).equals("0") ? "недоступны" : "доступны"));
                    break;
                case "Одаренный":
                    geniyTextView.setText(String.format(getResources().getString(R.string.geniy_access_text), cursor.getString(2).equals("0") ? "недоступны" : "доступны"));
                    break;
                case "Ученый":
                    scientistTextView.setText(String.format(getResources().getString(R.string.scientist_access_text), cursor.getString(2).equals("0") ? "недоступны" : "доступны"));
                    break;
            }
            cursor.moveToNext();
        }
        cursor.close();
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
}
