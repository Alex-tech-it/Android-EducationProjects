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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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

        EditText companyName = (EditText) findViewById(R.id.companyNameEditText);
        EditText companyFounder = (EditText) findViewById(R.id.companyFounderEditText);
        EditText companyProduct = (EditText) findViewById(R.id.companyProductEditText);

        if (!companyName.getText().toString().equals("") &&
                !companyFounder.getText().toString().equals("") &&
                !companyProduct.getText().toString().equals("")
        ) {
            intent.putExtra(MainActivity.NEW_COMPANY_NAME_KEY, companyName.getText().toString());
            intent.putExtra(MainActivity.NEW_COMPANY_FOUNDER_KEY, companyFounder.getText().toString());
            intent.putExtra(MainActivity.NEW_COMPANY_PRODUCT_KEY, companyProduct.getText().toString());
            intent.putExtra(MainActivity.MODE_KEY, "add");

            setResult(RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Все поля обязательны для заполнения",
                    Toast.LENGTH_SHORT).show();
        }

    }
}