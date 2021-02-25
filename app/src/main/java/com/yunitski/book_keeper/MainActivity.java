package com.yunitski.book_keeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView keeps, limit, rem;
    Spinner spinner;
    EditText etSum;
    Button apply;
    ListView listView;
    final String[] names = {"Расход", "Приход", "Лимит", "Сбережения"};
    ArrayAdapter<String> adapter;
    String currentChoose = "";
    int keepsValue;
    int remValue;
    SharedPreferences sharedPreferences;
    final String SAVED_KEEPS = "saved_keeps";
    final String SAVED_LIM = "saved_limit";
    final String SAVED_REM = "saved_rem";
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keeps = findViewById(R.id.keeps);
        limit = findViewById(R.id.limit);
        rem = findViewById(R.id.rem);
        spinner = findViewById(R.id.spinner);
        etSum = findViewById(R.id.edit);
        apply = findViewById(R.id.apply);
        apply.setOnClickListener(this);
        listView = findViewById(R.id.listHist);
        //arrayList = new ArrayList<String>();
        //arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        //listView.setAdapter(arrayAdapter);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, names);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        currentChoose = "Расход";
                        break;
                    case 1:
                        currentChoose = "Приход";
                        break;
                    case 2:
                        currentChoose = "Лимит";
                        break;
                    case 3:
                        currentChoose = "Сбережения";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        load();
        dbHelper = new DBHelper(this);
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Сбросить");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        keeps.setText("0");
        limit.setText("0");
        rem.setText("0");
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
        this.deleteDatabase(InputData.DB_NAME);
        updateUI();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        remValue = Integer.parseInt(rem.getText().toString());
        keepsValue = Integer.parseInt(keeps.getText().toString());
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (!etSum.getText().toString().isEmpty()) {
            int summ = Integer.parseInt(etSum.getText().toString());
            if (currentChoose.equals(names[1])) {
                keepsValue += summ;
                keeps.setText("" + keepsValue);
                etSum.setText("");
                String listS = historyOp(keeps, summ, currentChoose);
                String dd = dateC();
                cv.put(InputData.TaskEntry.DATE, dd);
                cv.put(InputData.TaskEntry.TOTAL_VALUE, keeps.getText().toString());
                cv.put(InputData.TaskEntry.VALUE, ""+summ);
                cv.put(InputData.TaskEntry.OP_NAME, currentChoose);
                db.insert(InputData.TaskEntry.TABLE, null, cv);
                arrayAdapter.insert(listS, 0);
                db.close();
                updateUI();
            } else if (currentChoose.equals(names[0])) {
                if (keepsValue > 0 && keepsValue > summ) {
                    keepsValue -= summ;
                    keeps.setText("" + keepsValue);
                    remValue -= summ;
                    rem.setText("" + remValue);
                    etSum.setText("");
                    String listS = historyOp(keeps, summ, currentChoose);
                    String dd = dateC();
                    cv.put(InputData.TaskEntry.DATE, dd);
                    cv.put(InputData.TaskEntry.TOTAL_VALUE, keeps.getText().toString());
                    cv.put(InputData.TaskEntry.VALUE, ""+summ);
                    cv.put(InputData.TaskEntry.OP_NAME, currentChoose);
                    db.insert(InputData.TaskEntry.TABLE, null, cv);
                    arrayAdapter.insert(listS, 0);
                    db.close();
                    updateUI();
                } else {
                    Toast.makeText(this, "Недостаточно средств", Toast.LENGTH_SHORT).show();
                }
            } else if (currentChoose.equals(names[2])) {
                limit.setText("" + summ);
                ;
                rem.setText("" + summ);
                etSum.setText("");
            } else if (currentChoose.equals(names[3])) {
                keepsValue = summ;
                keeps.setText("" + keepsValue);
                etSum.setText("");
            }
        } else {
            Toast.makeText(this, "Введите сумму", Toast.LENGTH_SHORT).show();

        }
    }

    public String historyOp(TextView totalValue, int value, String choose){
        Calendar c = new GregorianCalendar();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        String dat = d + "." + m + "." + y;
        String totVal = totalValue.getText().toString();
        String val = ""+value;
        String op = choose;
        String s = dat + " " + totVal + " " + val + " " + op;
        return s;
    }
    public String dateC(){
        Calendar c = new GregorianCalendar();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        return d + "." + m + "." + y;
    }

    private void updateUI(){
        arrayList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(InputData.TaskEntry.TABLE, new String[]{InputData.TaskEntry._ID, InputData.TaskEntry.DATE, InputData.TaskEntry.TOTAL_VALUE, InputData.TaskEntry.VALUE, InputData.TaskEntry.OP_NAME}, null, null, null, null, null);
        while (cursor.moveToNext()){
            int idx = cursor.getColumnIndex(InputData.TaskEntry.DATE);
            int idx1 = cursor.getColumnIndex(InputData.TaskEntry.TOTAL_VALUE);
            int idx2 = cursor.getColumnIndex(InputData.TaskEntry.VALUE);
            int idx3 = cursor.getColumnIndex(InputData.TaskEntry.OP_NAME);
            arrayList.add(0, cursor.getString(idx) + " " + cursor.getString(idx3) + ": " + cursor.getString(idx2) + " Остаток: " + cursor.getString(idx1));
        }
        if (arrayAdapter == null) {
            arrayAdapter = new ArrayAdapter<>(this,  android.R.layout.simple_expandable_list_item_1,
                    arrayList);
            listView.setAdapter(arrayAdapter);
        } else {
            arrayAdapter.clear();
            arrayAdapter.addAll(arrayList);
            arrayAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    void save() {
        int keepInt = Integer.parseInt(keeps.getText().toString());
        int limitInt = Integer.parseInt(limit.getText().toString());
        int remInt = Integer.parseInt(rem.getText().toString());
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SAVED_KEEPS, keepInt);
        editor.putInt(SAVED_LIM, limitInt);
        editor.putInt(SAVED_REM, remInt);
        editor.apply();
    }

    void load() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        int savedKeep = sharedPreferences.getInt(SAVED_KEEPS, 0);
        keeps.setText("" + savedKeep);
        int savedLim = sharedPreferences.getInt(SAVED_LIM, 0);
        limit.setText("" + savedLim);
        int saveRem = sharedPreferences.getInt(SAVED_REM, 0);
        rem.setText("" + saveRem);
    }

}