package com.example.androideducationdatastate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StudentInfoActivity extends AppCompatActivity {

    int index;
    Spinner spinner;
    String[] genderList = {"Мужской", "Женский", "Трансгендер"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.spinner = (Spinner) findViewById(R.id.genderSpinner);
        spinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, genderList));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            EditText fullNameView = (EditText) findViewById(R.id.fullNameEditText);
            EditText programLangView = (EditText) findViewById(R.id.langEditText);
            EditText IDEView = (EditText) findViewById(R.id.ideEditText);

            String fullName = extras.getString(MainActivity.FULLNAME_KEY);
            String gender = extras.getString(MainActivity.GENDER_KEY).toString();
            String programLang = extras.getString(MainActivity.LANG_KEY);
            String preferredIDE = extras.getString(MainActivity.IDE_KEY);
            this.index = extras.getInt(MainActivity.INDEX_KEY);

            fullNameView.setText(fullName);
            spinner.setSelection(getIndex(spinner, gender));
            programLangView.setText(programLang);
            IDEView.setText(preferredIDE);
        }
    }

    private int getIndex(Spinner spinner, String gender) {
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(gender)){
                return i;
            }
        }
        return 0;
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

    public void onSaveClick(View v){
        Intent intent = new Intent();

        EditText fullNameView = (EditText) findViewById(R.id.fullNameEditText);
        Spinner genderSpinnerView = (Spinner) findViewById(R.id.genderSpinner);
        EditText langView = (EditText) findViewById(R.id.langEditText);
        EditText ideView = (EditText) findViewById(R.id.ideEditText);

        if (!fullNameView.getText().toString().equals("")) {
            intent.putExtra(MainActivity.INDEX_KEY, index);
            intent.putExtra(MainActivity.FULLNAME_KEY, fullNameView.getText().toString());
            intent.putExtra(MainActivity.GENDER_KEY, genderSpinnerView.getSelectedItem().toString());
            intent.putExtra(MainActivity.LANG_KEY, langView.getText().toString());
            intent.putExtra(MainActivity.IDE_KEY, ideView.getText().toString());
            intent.putExtra(MainActivity.MODE_KEY, "changed");

            setResult(RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Поле ФИО обязательно для заполнения",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void onDeleteClick(View v){
        Intent intent = new Intent();

        intent.putExtra(MainActivity.INDEX_KEY, index);
        intent.putExtra(MainActivity.MODE_KEY, "remove");

        setResult(RESULT_OK, intent);
        finish();
    }


}