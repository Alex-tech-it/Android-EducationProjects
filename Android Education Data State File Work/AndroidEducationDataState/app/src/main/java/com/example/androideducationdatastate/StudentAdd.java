package com.example.androideducationdatastate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StudentAdd extends AppCompatActivity {

    Spinner spinner;
    String[] genderList = {"Мужской", "Женский", "Трансгендер"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.spinner = (Spinner) findViewById(R.id.genderSpinner);
        spinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, genderList));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onAddClick(View v){
        Intent intent = new Intent();

        EditText fullNameView = (EditText) findViewById(R.id.fullNameEditText);
        Spinner genderSpinnerView = (Spinner) findViewById(R.id.genderSpinner);
        EditText langView = (EditText) findViewById(R.id.langEditText);
        EditText ideView = (EditText) findViewById(R.id.ideEditText);

        if (!fullNameView.getText().toString().equals("")) {
            intent.putExtra(MainActivity.FULLNAME_KEY, fullNameView.getText().toString());
            intent.putExtra(MainActivity.GENDER_KEY, genderSpinnerView.getSelectedItem().toString());
            intent.putExtra(MainActivity.LANG_KEY, langView.getText().toString());
            intent.putExtra(MainActivity.IDE_KEY, ideView.getText().toString());
            intent.putExtra(MainActivity.MODE_KEY, "add");

            setResult(RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Поле ФИО обязательно для заполнения",
                    Toast.LENGTH_SHORT).show();
        }

    }
}