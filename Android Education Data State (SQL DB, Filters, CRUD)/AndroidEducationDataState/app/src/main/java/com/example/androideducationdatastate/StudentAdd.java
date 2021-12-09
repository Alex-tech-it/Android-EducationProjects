package com.example.androideducationdatastate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class StudentAdd extends AppCompatActivity {

    static final String[] categoryProduct = {
            "Техника", "Одежда", "Ресурсы",
            "Коммерческие услуги", "Нематериальные ценности", "Еда",
            "Оружие", "Метал",
    };

    static final String PRODUCT_NAME_KEY = "productName";
    static final String OLD_PRODUCT_NAME_KEY = "oldProductName";
    static final String PRODUCT_PRICE_KEY = "productPrice";
    static final String OLD_PRODUCT_PRICE_KEY = "oldProductPrice";
    static final String PRODUCT_TYPE_KEY = "productType";
    static final String OLD_PRODUCT_TYPE_KEY = "oldProductType";
    static final String MODE_KEY = "mode";
    static final String INDEX_KEY = "index";

    // Class fields
    private ArrayList<ProductItem> productItems;
    private RecyclerView activeRecyclerView;
    private ProductAdapter activeAdapter;
    private RecyclerView.LayoutManager activeLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_add);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // Getter the saved data
        if (savedInstanceState != null){
            productItems = savedInstanceState.getParcelableArrayList("products");
        }

        createProductList();

        // Init studentList & Build RecyclerView to Activity
        buildRecyclerView();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("products", productItems);
        super.onSaveInstanceState(outState);
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

    // This func init the productItems
    private void createProductList(){
        this.productItems = new ArrayList<>();
    }

    // To active new activity to get a new result
    private void changeItemIntent(int position){
        Intent intent = new Intent(this, ProductInfo.class);
        ProductItem productItem = productItems.get(position);
        intent.putExtra(INDEX_KEY, position);
        intent.putExtra(OLD_PRODUCT_NAME_KEY, productItem.getProductName());
        intent.putExtra(OLD_PRODUCT_PRICE_KEY, productItem.getProductPrice());
        intent.putExtra(OLD_PRODUCT_TYPE_KEY, productItem.getProductType());

        mStartForResult.launch(intent);
    }


    public void onAddClick(View v){
        Intent intent = new Intent();

        EditText companyName = (EditText) findViewById(R.id.companyNameEditText);
        EditText companyFounder = (EditText) findViewById(R.id.companyFounderEditText);

        if (!companyName.getText().toString().equals("") &&
                !companyFounder.getText().toString().equals("")
        ) {
            intent.putExtra(MainActivity.NEW_COMPANY_NAME_KEY, companyName.getText().toString());
            intent.putExtra(MainActivity.NEW_COMPANY_FOUNDER_KEY, companyFounder.getText().toString());
            intent.putParcelableArrayListExtra(MainActivity.ADD_PRODUCTS_LIST_KEY, productItems);
            intent.putExtra(MainActivity.MODE_KEY, "add");

            setResult(RESULT_OK, intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),
                    "Все поля обязательны для заполнения",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void onAddProductClick(View v){
        mStartForResult.launch(new Intent(v.getContext(), ActivityProductAdd.class));
    }


    // Add new Product in studentList RecyclerView
    private void addItem(ProductItem newProduct){
            productItems.add(newProduct);
            activeAdapter.notifyDataSetChanged();
    }
    // Change the item from studentList in RecyclerView
    private void changeItem(ProductItem oldProductItem, ProductItem newProductItem, int position){
        productItems.set(position, newProductItem);
        activeAdapter.notifyItemChanged(position);
    }
    // Remove the item from studentList in RecyclerView
    private void removeItem(int position){
        ProductItem item = productItems.get(position);
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
                                intent.getStringExtra(PRODUCT_NAME_KEY),
                                intent.getStringExtra(PRODUCT_PRICE_KEY),
                                intent.getStringExtra(PRODUCT_TYPE_KEY));

                        switch (intent.getStringExtra(MODE_KEY)){
                            case "add":
                                addItem(newProduct);
                                break;
                            case "changed":
                                ProductItem oldProduct = new ProductItem(
                                        intent.getStringExtra(OLD_PRODUCT_NAME_KEY),
                                        intent.getStringExtra(OLD_PRODUCT_PRICE_KEY),
                                        intent.getStringExtra(OLD_PRODUCT_TYPE_KEY));
                                changeItem(oldProduct, newProduct,
                                        intent.getIntExtra(INDEX_KEY, 0));
                                break;
                            case "remove":
                                removeItem(intent.getIntExtra(INDEX_KEY, 0));
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