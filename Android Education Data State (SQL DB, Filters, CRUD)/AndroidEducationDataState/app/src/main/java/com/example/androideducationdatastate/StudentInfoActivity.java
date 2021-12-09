package com.example.androideducationdatastate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
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

import java.util.ArrayList;

public class StudentInfoActivity extends AppCompatActivity {

    int index;
    private CompanyItem _companyItem;

    // Class fields
    private ArrayList<ProductItem> productItems;
    private RecyclerView activeRecyclerView;
    private ProductAdapter activeAdapter;
    private RecyclerView.LayoutManager activeLayoutManager;

    private DBService db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DBService(StudentInfoActivity.this);

        // Getter the saved data
        if (savedInstanceState != null){
            productItems = savedInstanceState.getParcelableArrayList("products");
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            EditText companyName = (EditText) findViewById(R.id.companyNameEditText);
            EditText companyFounder = (EditText) findViewById(R.id.companyFounderEditText);

            String companyNameStr = extras.getString(MainActivity.OLD_COMPANY_NAME_KEY);
            String companyFounderStr = extras.getString(MainActivity.OLD_COMPANY_FOUNDER_KEY);
            this.index = extras.getInt(MainActivity.INDEX_KEY);

            companyName.setText(companyNameStr);
            companyFounder.setText(companyFounderStr);

            _companyItem = new CompanyItem(companyNameStr, companyFounderStr);
        }

        createProductList();

        // Init productItem & Build RecyclerView to Activity
        buildRecyclerView();
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("products", productItems);
        super.onSaveInstanceState(outState);
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

        if (!companyName.getText().toString().equals("") &&
                !companyFounder.getText().toString().equals("")) {
            intent.putExtra(MainActivity.INDEX_KEY, index);
            intent.putExtra(MainActivity.NEW_COMPANY_NAME_KEY, companyName.getText().toString());
            intent.putExtra(MainActivity.NEW_COMPANY_FOUNDER_KEY, companyFounder.getText().toString());
            intent.putExtra(MainActivity.MODE_KEY, "changed");

            intent.putExtra(MainActivity.OLD_COMPANY_NAME_KEY, _companyItem.getCompanyName());
            intent.putExtra(MainActivity.OLD_COMPANY_FOUNDER_KEY, _companyItem.getCompanyFounderField());

            setResult(RESULT_OK, intent);

            db.close();
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

        db.close();
        setResult(RESULT_OK, intent);
        finish();
    }


    // This func init the productItems
    private void createProductList(){
        this.productItems = db.GetProducts(_companyItem.getCompanyName());
    }

    // This func build a new RecyclerView in Activity
    private void buildRecyclerView(){
        this.activeRecyclerView = findViewById(R.id.activeRecyclerViewProduct);
        this.activeRecyclerView.setHasFixedSize(true);
        this.activeLayoutManager = new LinearLayoutManager(this);
        this.activeAdapter = new ProductAdapter(productItems);

        this.activeRecyclerView.setLayoutManager(this.activeLayoutManager);
        this.activeRecyclerView.setAdapter(this.activeAdapter);

        this.activeAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                changeItemIntent(position);
            }
        });
    }

    // To active new activity to get a new result
    private void changeItemIntent(int position){
        Intent intent = new Intent(this, ProductInfo.class);
        ProductItem productItem = productItems.get(position);
        intent.putExtra(StudentAdd.INDEX_KEY, position);
        intent.putExtra(StudentAdd.OLD_PRODUCT_NAME_KEY, productItem.getProductName());
        intent.putExtra(StudentAdd.OLD_PRODUCT_PRICE_KEY, productItem.getProductPrice());
        intent.putExtra(StudentAdd.OLD_PRODUCT_TYPE_KEY, productItem.getProductType());

        mStartForResult.launch(intent);
    }

    public void onAddProductClick(View v){
        mStartForResult.launch(new Intent(v.getContext(), ActivityProductAdd.class));
    }

    // Add new Product in studentList RecyclerView
    private void addItem(ProductItem newProduct){
        int res = 0;
        try{
            db.AddProduct(newProduct, _companyItem);
        } catch (NullPointerException exception){
            Toast.makeText(getApplicationContext(),
                    "Невозможно добавить продукт, так как он уже существует в списке",
                    Toast.LENGTH_SHORT).show();
            res = -1;
        }
        if(res != -1){
            productItems.add(newProduct);
            activeAdapter.notifyDataSetChanged();
        }
    }
    // Change the item from studentList in RecyclerView
    private void changeItem(ProductItem oldProductItem, ProductItem newProductItem, int position){
        db.ChangeProduct(oldProductItem, newProductItem);
        productItems.set(position, newProductItem);
        activeAdapter.notifyItemChanged(position);
    }
    // Remove the item from studentList in RecyclerView
    private void removeItem(int position){
        ProductItem item = productItems.get(position);
        db.DeleteProduct(item);
        productItems.remove(position);
        activeAdapter.notifyItemRemoved(position);
    }

    // Launch intent for Add, Change and Delete Students
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        ProductItem newProduct = new ProductItem(
                                intent.getStringExtra(StudentAdd.PRODUCT_NAME_KEY),
                                intent.getStringExtra(StudentAdd.PRODUCT_PRICE_KEY),
                                intent.getStringExtra(StudentAdd.PRODUCT_TYPE_KEY));

                        switch (intent.getStringExtra(StudentAdd.MODE_KEY)){
                            case "add":
                                addItem(newProduct);
                                break;
                            case "changed":
                                ProductItem oldProduct = new ProductItem(
                                        intent.getStringExtra(StudentAdd.OLD_PRODUCT_NAME_KEY),
                                        intent.getStringExtra(StudentAdd.OLD_PRODUCT_PRICE_KEY),
                                        intent.getStringExtra(StudentAdd.OLD_PRODUCT_TYPE_KEY));
                                changeItem(oldProduct, newProduct,
                                        intent.getIntExtra(StudentAdd.INDEX_KEY, 0));
                                break;
                            case "remove":
                                removeItem(intent.getIntExtra(StudentAdd.INDEX_KEY, 0));
                                break;
                        }
                    }
                    else if (result.getResultCode() != Activity.RESULT_CANCELED){
                        Toast.makeText(getApplicationContext(),
                                "Не корректный ввод данных",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
}