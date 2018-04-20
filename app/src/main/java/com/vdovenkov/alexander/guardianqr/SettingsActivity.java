package com.vdovenkov.alexander.guardianqr;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vdovenkov.alexander.guardianqr.db.DataBaseHelper;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "SettingsActivity";
    Button confirmButton;
    Spinner fractionSpinner;
    TextInputEditText passwordEditText;

    //Метки уровней доступа
    TextView guardAccessTextView;
    TextView chearchAccessTextView;
    TextView ordenTextView;

    //Переменные для работы с БД
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initDB();
        initGUI();

        fractionSpinner = findViewById(R.id.settings_spinner);
        passwordEditText = findViewById(R.id.settings_edit_text);
        confirmButton = findViewById(R.id.settings_cast_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Введён пароль: " + passwordEditText.getText().toString());
                Log.d(TAG, "На спиннере выбран: " + fractionSpinner.getSelectedItem().toString());
                // TODO: 017 17.02.18 реализовать получение списка фракций и паролей к ним из БД
                switch (passwordEditText.getText().toString()) {
                    case "1111": //пароль для стражей
                        if (fractionSpinner.getSelectedItem().toString().equals("Страж")) {
                            changeAccessRule(fractionSpinner.getSelectedItem().toString());
                            Toast.makeText(SettingsActivity.this, "Введён верный пароль для стражей", Toast.LENGTH_SHORT).show();
                        } else {
                            initGUI();
                            Toast.makeText(SettingsActivity.this, "Введён неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "2222":
                        if (fractionSpinner.getSelectedItem().toString().equals("Монах")) {
                            changeAccessRule(fractionSpinner.getSelectedItem().toString());
                            Toast.makeText(SettingsActivity.this, "Введён верный пароль для монахов", Toast.LENGTH_SHORT).show();
                        } else {
                            initGUI();
                            Toast.makeText(SettingsActivity.this, "Введён неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "3333":
                        if (fractionSpinner.getSelectedItem().toString().equals("Орденец")) {
                            changeAccessRule(fractionSpinner.getSelectedItem().toString());
                            Toast.makeText(SettingsActivity.this, "Введён верный пароль для орденцев", Toast.LENGTH_SHORT).show();
                        } else {
                            initGUI();
                            Toast.makeText(SettingsActivity.this, "Введён неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "9999":
                        resetAccessRules();
                        Toast.makeText(SettingsActivity.this, "Магический сканер больше неактивен. Для активации заново снимите печать.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SettingsActivity.this, "Магический камень раскалился и нанёс тебе урон в 1 хит!", Toast.LENGTH_SHORT).show();
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

        String querry = "SELECT * FROM access_rules";
        Cursor cursor = database.rawQuery(querry, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String s = cursor.getString(1);
            if ("Страж".equals(s)) {
                guardAccessTextView.setText(String.format(getResources().getString(R.string.guard_access_text), cursor.getString(2).equals("0") ? "недоступны" : "доступны"));

            } else if ("Монах".equals(s)) {
                chearchAccessTextView.setText(String.format(getResources().getString(R.string.chearch_access_text), cursor.getString(2).equals("0") ? "недоступны" : "доступны"));

            } else if ("Орденец".equals(s)) {
                ordenTextView.setText(String.format(getResources().getString(R.string.orden_access_text), cursor.getString(2).equals("0") ? "недоступны" : "доступны"));

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
