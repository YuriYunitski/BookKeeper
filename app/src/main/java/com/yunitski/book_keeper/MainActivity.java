package com.yunitski.book_keeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView keeps, limit, rem;
    Spinner spinner;
    EditText etSum;
    Button apply;
    final String[] names = {"Расход", "Приход", "Лимит", "Сбережения"};
    ArrayAdapter <String> adapter;
    String currentChoose = "";
    int keepsValue;
    int remValue;
    SharedPreferences sharedPreferences;
    final String SAVED_KEEPS = "saved_keeps";
    final String SAVED_LIM = "saved_limit";
    final String SAVED_REM = "saved_rem";

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
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, names);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
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



    @Override
    public void onClick(View v) {
        remValue = Integer.parseInt(rem.getText().toString());
        keepsValue = Integer.parseInt(keeps.getText().toString());
        if (!etSum.getText().toString().isEmpty()) {
            int summ = Integer.parseInt(etSum.getText().toString());
            if (currentChoose.equals(names[1])) {
                keepsValue += summ;
                keeps.setText("" + keepsValue);
                etSum.setText("");
            } else if (currentChoose.equals(names[0])) {
                if (keepsValue > 0 && keepsValue > summ) {
                    keepsValue -= summ;
                    keeps.setText("" + keepsValue);
                    remValue -= summ;
                    rem.setText("" + remValue);
                    etSum.setText("");
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
    void save(){
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
    void load(){
        sharedPreferences = getPreferences(MODE_PRIVATE);
        int savedKeep = sharedPreferences.getInt(SAVED_KEEPS, 0);
        keeps.setText(""+savedKeep);
        int savedLim = sharedPreferences.getInt(SAVED_LIM, 0);
        limit.setText(""+savedLim);
        int saveRem = sharedPreferences.getInt(SAVED_REM, 0);
        rem.setText(""+saveRem);
    }

}