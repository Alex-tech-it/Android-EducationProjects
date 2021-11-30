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
    private CompanyItem _companyItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            EditText companyName = (EditText) findViewById(R.id.companyNameEditText);
            EditText companyFounder = (EditText) findViewById(R.id.companyFounderEditText);
            EditText companyProduct = (EditText) findViewById(R.id.companyProductEditText);

            String companyNameStr = extras.getString(MainActivity.OLD_COMPANY_NAME_KEY);
            String companyFounderStr = extras.getString(MainActivity.OLD_COMPANY_FOUNDER_KEY);
            String companyProductStr = extras.getString(MainActivity.OLD_COMPANY_PRODUCT_KEY);
            this.index = extras.getInt(MainActivity.INDEX_KEY);

            companyName.setText(companyNameStr);
            companyFounder.setText(companyFounderStr);
            companyProduct.setText(companyProductStr);

            _companyItem = new CompanyItem(companyNameStr, companyFounderStr, companyProductStr);
        }
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

        EditText companyName = (EditText) findViewById(R.id.companyNameEditText);
        EditText companyFounder = (EditText) findViewById(R.id.companyFounderEditText);
        EditText companyProduct = (EditText) findViewById(R.id.companyProductEditText);

        if (!companyName.getText().toString().equals("") &&
                !companyFounder.getText().toString().equals("") &&
                !companyProduct.getText().toString().equals("")) {
            intent.putExtra(MainActivity.INDEX_KEY, index);
            intent.putExtra(MainActivity.NEW_COMPANY_NAME_KEY, companyName.getText().toString());
            intent.putExtra(MainActivity.NEW_COMPANY_FOUNDER_KEY, companyFounder.getText().toString());
            intent.putExtra(MainActivity.NEW_COMPANY_PRODUCT_KEY, companyProduct.getText().toString());
            intent.putExtra(MainActivity.MODE_KEY, "changed");

            intent.putExtra(MainActivity.OLD_COMPANY_NAME_KEY, _companyItem.getCompanyName());
            intent.putExtra(MainActivity.OLD_COMPANY_FOUNDER_KEY, _companyItem.getCompanyFounderField());
            intent.putExtra(MainActivity.OLD_COMPANY_PRODUCT_KEY, _companyItem.getCompanyProductField());

            setResult(RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Поле ≪Имя компании≫ обязательно для заполнения",
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