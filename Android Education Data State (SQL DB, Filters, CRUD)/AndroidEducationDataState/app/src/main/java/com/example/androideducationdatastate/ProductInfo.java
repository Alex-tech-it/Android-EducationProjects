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

public class ProductInfo extends AppCompatActivity {

    int index;
    private ProductItem _productItem;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.spinner = (Spinner) findViewById(R.id.productTypeSpinner);
        spinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, StudentAdd.categoryProduct));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            EditText productName = (EditText) findViewById(R.id.productNameEditText);
            EditText productPrice = (EditText) findViewById(R.id.productPriceEditText);

            String productNameStr = extras.getString(StudentAdd.OLD_PRODUCT_NAME_KEY);
            String productPriceStr = extras.getString(StudentAdd.OLD_PRODUCT_PRICE_KEY);
            String productTypeStr = extras.getString(StudentAdd.OLD_PRODUCT_TYPE_KEY);
            this.index = extras.getInt(MainActivity.INDEX_KEY);

            productName.setText(productNameStr);
            productPrice.setText(productPriceStr);
            spinner.setSelection(getIndex(spinner, productTypeStr));

            _productItem = new ProductItem(productNameStr, productPriceStr, productTypeStr);
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

    private int getIndex(Spinner spinner, String type) {
        for(int i = 0; i < spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).toString().equalsIgnoreCase(type)){
                return i;
            }
        }
        return 0;
    }

    public void onSaveClick(View v){
        Intent intent = new Intent();

        EditText productName = (EditText) findViewById(R.id.productNameEditText);
        EditText productPrice = (EditText) findViewById(R.id.productPriceEditText);
        Spinner productSpinnerView = (Spinner) findViewById(R.id.productTypeSpinner);

        if (!productName.getText().toString().equals("") &&
                !productPrice.getText().toString().equals("")) {
            int price = 0;
            try {
                price = Integer.parseInt(productPrice.getText().toString().replaceAll(" ", ""));
                if(price <= 0){
                    Toast.makeText(getApplicationContext(),
                            "Цена не может быть отрицательной или ровняться нулю",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),
                        "Некорретный ввод данных в поле Цена",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            intent.putExtra(StudentAdd.INDEX_KEY, index);
            intent.putExtra(StudentAdd.PRODUCT_NAME_KEY, productName.getText().toString());
            intent.putExtra(StudentAdd.PRODUCT_PRICE_KEY, String.valueOf(price));
            intent.putExtra(StudentAdd.PRODUCT_TYPE_KEY, productSpinnerView.getSelectedItem().toString());
            intent.putExtra(MainActivity.MODE_KEY, "changed");

            intent.putExtra(StudentAdd.OLD_PRODUCT_NAME_KEY, _productItem.getProductName());
            intent.putExtra(StudentAdd.OLD_PRODUCT_PRICE_KEY, _productItem.getProductPrice());
            intent.putExtra(StudentAdd.OLD_PRODUCT_TYPE_KEY, _productItem.getProductType());

            setResult(RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Все поля обязательны для заполнения",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void onDeleteClick(View v){
        Intent intent = new Intent();

        intent.putExtra(StudentAdd.INDEX_KEY, index);
        intent.putExtra(StudentAdd.MODE_KEY, "remove");

        setResult(RESULT_OK, intent);
        finish();
    }
}