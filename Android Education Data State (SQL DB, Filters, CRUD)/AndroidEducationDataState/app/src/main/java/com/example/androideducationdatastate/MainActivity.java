package com.example.androideducationdatastate;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static final String NEW_COMPANY_NAME_KEY = "newCompanyName";
    static final String OLD_COMPANY_NAME_KEY = "oldCompanyName";
    static final String NEW_COMPANY_FOUNDER_KEY = "newCompanyFounder";
    static final String OLD_COMPANY_FOUNDER_KEY = "oldCompanyFounder";
    static final String NEW_COMPANY_PRODUCT_KEY = "newCompanyProduct";
    static final String OLD_COMPANY_PRODUCT_KEY = "oldCompanyProduct";
    static final String ADD_PRODUCTS_LIST_KEY = "addProductsList";
    static final String MODE_KEY = "mode";
    static final String INDEX_KEY = "index";

    private ArrayList<CompanyItem> companyList;
    private ArrayList<CompanyItem> companyListSave;
    private ArrayList<ProductItem> productItems;
    private DBService db;
    private RecyclerView activeRecyclerView;
    private StudentAdapter activeAdapter;
    private ProductAdapter activeAdapterProduct;
    private RecyclerView.LayoutManager activeLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBService(MainActivity.this);
        EditText filter = findViewById(R.id.searchEditText);
        RadioGroup radioGroupObject = findViewById(R.id.radioGroupObject);

        // Getter the saved data
        if (savedInstanceState != null){
            companyList = savedInstanceState.getParcelableArrayList("сompanies");
            companyListSave = db.GetCompanies();
        } else {
            if(db.GetCompanies().size() == 0){
                createStudentList();
            }else{
                companyList = db.GetCompanies();
                companyListSave = new ArrayList<>(companyList);
            }

        }

        // Init studentList & Build RecyclerView to Activity
        buildRecyclerView();

        // Setter function for click on FloatActionButtons
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartForResult.launch(new Intent(view.getContext(), StudentAdd.class));
            }
        });

        filter.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable s) {
                // Прописываем то, что надо выполнить после изменения текста
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ChangeDataList(s.toString());
            }
        });

        radioGroupObject.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_company:
                        companyList.clear();
                        ArrayList<CompanyItem> newCompanyList =  db.GetCompanies();
                        companyList.addAll(newCompanyList);
                        buildRecyclerView();
                        RadioButton radioButtonFounderVis = findViewById(R.id.radio_checkBox_Founder);
                        radioButtonFounderVis.setVisibility(View.VISIBLE);

                        RadioButton radioButtonNameSet = findViewById(R.id.radio_checkBox_Name);
                        radioButtonNameSet.setChecked(true);
                        break;
                    case R.id.radio_Product:
                        buildRecyclerProductView();
                        RadioButton radioButtonFounderOnVis = findViewById(R.id.radio_checkBox_Founder);
                        radioButtonFounderOnVis.setVisibility(View.INVISIBLE);

                        RadioButton radioButtonNameOnSet = findViewById(R.id.radio_checkBox_Name);
                        radioButtonNameOnSet.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("сompanies", companyList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
    }

    // Remove the item from studentList in RecyclerView
    private void removeItem(int position){
        CompanyItem item = companyList.get(position);
        companyList.remove(position);
        companyListSave.remove(position);
        db.DeleteCompany(item);
        activeAdapter.notifyItemRemoved(position);
    }

    // Add new Student in studentList RecyclerView
    private void addItem(CompanyItem newCompany, ArrayList<ProductItem> productItems){
        int res = 0;
        try{
            db.AddCompany(newCompany, productItems);
        } catch (NullPointerException exception){
            Toast.makeText(getApplicationContext(),
                    "Невозможно добавить компанию, так как она уже существует в списке",
                    Toast.LENGTH_SHORT).show();
            res = -1;
        }
        if(res != -1){
            companyList.add(newCompany);
            companyListSave.add(newCompany);
            activeAdapter.notifyDataSetChanged();
        }
    }

    // Change the item from studentList in RecyclerView
    private void changeItem(CompanyItem oldCompany, CompanyItem newCompany, int position){
        db.ChangeCompany(oldCompany, newCompany);
        companyList.set(position, newCompany);
        companyListSave.set(position, newCompany);
        activeAdapter.notifyItemChanged(position);
    }

    // To active new activity to get a new result
    private void changeItemIntent(int position){
        Intent intent = new Intent(this, StudentInfoActivity.class);
        CompanyItem companyItem = companyList.get(position);
        intent.putExtra(INDEX_KEY, position);
        intent.putExtra(OLD_COMPANY_NAME_KEY, companyItem.getCompanyName());
        intent.putExtra(OLD_COMPANY_FOUNDER_KEY, companyItem.getCompanyFounderField());

        mStartForResult.launch(intent);
    }

    // To active new activity to get a new result
    private void changeItemIntentProduct(int position){
        Intent intent = new Intent(this, ProductInfo.class);
        ProductItem productItem = productItems.get(position);
        intent.putExtra(INDEX_KEY, position);
        intent.putExtra(StudentAdd.OLD_PRODUCT_NAME_KEY, productItem.getProductName());
        intent.putExtra(StudentAdd.OLD_PRODUCT_PRICE_KEY, productItem.getProductPrice());
        intent.putExtra(StudentAdd.OLD_PRODUCT_TYPE_KEY, productItem.getProductType());

        mProductStartForResult.launch(intent);
    }

    // This func init the studentList
    private void createStudentList(){
        this.companyList = new ArrayList<>();
        this.companyListSave = new ArrayList<>();
    }

    // This func build a new RecyclerView in Activity
    private void buildRecyclerView(){
        this.activeRecyclerView = findViewById(R.id.activeRecyclerView);
        this.activeRecyclerView.setHasFixedSize(true);
        this.activeLayoutManager = new LinearLayoutManager(this);
        this.activeAdapter = new StudentAdapter(companyList, this);

        this.activeRecyclerView.setLayoutManager(this.activeLayoutManager);
        this.activeRecyclerView.setAdapter(this.activeAdapter);

        this.activeAdapter.setOnItemClickListener(new StudentAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                changeItemIntent(position);
            }
        });
    }

    // This func build a new RecyclerView in Activity
    private void buildRecyclerProductView(){
        productItems = db.GetProducts();
        this.activeAdapterProduct = new ProductAdapter(productItems);
        this.activeRecyclerView.setAdapter(this.activeAdapterProduct);

        this.activeAdapterProduct.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                changeItemIntentProduct(position);
            }
        });
    }


    // Launch intent for Add, Change and Delete Students
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        CompanyItem newCompany = new CompanyItem(
                                intent.getStringExtra(NEW_COMPANY_NAME_KEY),
                                intent.getStringExtra(NEW_COMPANY_FOUNDER_KEY));

                        switch (intent.getStringExtra(MODE_KEY)){
                            case "add":
                                addItem(newCompany, intent.getParcelableArrayListExtra(ADD_PRODUCTS_LIST_KEY));
                                break;
                            case "changed":
                                CompanyItem oldCompany = new CompanyItem(
                                        intent.getStringExtra(OLD_COMPANY_NAME_KEY),
                                        intent.getStringExtra(OLD_COMPANY_FOUNDER_KEY));
                                changeItem(oldCompany, newCompany,
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

    // Launch intent for Add, Change and Delete Students
    ActivityResultLauncher<Intent> mProductStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        ProductItem newProduct = new ProductItem(
                                intent.getStringExtra(StudentAdd.PRODUCT_NAME_KEY),
                                intent.getStringExtra(StudentAdd.PRODUCT_PRICE_KEY),
                                intent.getStringExtra(StudentAdd.PRODUCT_TYPE_KEY));

                        switch (intent.getStringExtra(MODE_KEY)){
                            case "changed":
                                ProductItem oldProduct = new ProductItem(
                                        intent.getStringExtra(StudentAdd.OLD_PRODUCT_NAME_KEY),
                                        intent.getStringExtra(StudentAdd.OLD_PRODUCT_PRICE_KEY),
                                        intent.getStringExtra(StudentAdd.OLD_PRODUCT_TYPE_KEY));
                                changeItemProduct(oldProduct, newProduct,
                                        intent.getIntExtra(INDEX_KEY, 0));
                                break;
                            case "remove":
                                removeItemProduct(intent.getIntExtra(INDEX_KEY, 0));
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

    // Change the item from studentList in RecyclerView
    private void changeItemProduct(ProductItem oldProductItem, ProductItem newProductItem, int position){
        db.ChangeProduct(oldProductItem, newProductItem);
        productItems.set(position, newProductItem);
        activeAdapterProduct.notifyItemChanged(position);
    }
    // Remove the item from studentList in RecyclerView
    private void removeItemProduct(int position){
        ProductItem item = productItems.get(position);
        db.DeleteProduct(item);
        productItems.remove(position);
        activeAdapterProduct.notifyItemRemoved(position);
    }

    // Filter's func
    private void ChangeDataList(String filter){
        RadioButton radioButtonCompany = findViewById(R.id.radio_company);
        RadioButton radioButtonName = findViewById(R.id.radio_checkBox_Name);
        RadioButton radioButtonPrice = findViewById(R.id.radio_checkBox_Price);
        RadioButton radioButtonFounder = findViewById(R.id.radio_checkBox_Founder);

        if(radioButtonCompany.isChecked()){
            if(filter.equals("")){
                companyList.clear();
                companyList.addAll(companyListSave);
            }
            if(radioButtonName.isChecked()){
                ArrayList<CompanyItem> filterCompanyList = new ArrayList<>(companyListSave);
                filterCompanyList.removeIf(s -> !s.getCompanyName().contains(filter));

                companyList.clear();
                companyList.addAll(filterCompanyList);
                activeAdapter.notifyDataSetChanged();

                return;
            }
            if(radioButtonPrice.isChecked()){
                ArrayList<CompanyItem> filterCompanyList = new ArrayList<>(companyListSave);
                companyList.clear();
                for(CompanyItem item : filterCompanyList){
                    HashMap<String, Double> mapPrice = db.GetCompanyGeneralPriceProduct(item.getCompanyName());
                    String generalPrice = new DecimalFormat("#0.00").format(mapPrice.get("GeneralPrice"));
                    if (generalPrice.contains(filter)){
                        companyList.add(item);
                    }
                }
                activeAdapter.notifyDataSetChanged();
                return;
            }
            if(radioButtonFounder.isChecked()){
                ArrayList<CompanyItem> filterCompanyList = new ArrayList<>(companyListSave);
                filterCompanyList.removeIf(s -> !s.getCompanyFounderField().contains(filter));

                companyList.clear();
                companyList.addAll(filterCompanyList);
                activeAdapter.notifyDataSetChanged();

                return;
            }
        }else{
            if(filter.equals("")){
                productItems.clear();
                productItems.addAll(db.GetProducts());
            }
            if(radioButtonName.isChecked()){
                ArrayList<ProductItem> filterCompanyList = db.GetProducts();
                filterCompanyList.removeIf(s -> !s.getProductName().contains(filter));

                productItems.clear();
                productItems.addAll(filterCompanyList);
                activeAdapterProduct.notifyDataSetChanged();

                return;
            }
            if(radioButtonPrice.isChecked()){
                ArrayList<ProductItem> filterCompanyList = db.GetProducts();
                filterCompanyList.removeIf(s -> !s.getProductPrice().replaceAll(" ", "").contains(filter));

                productItems.clear();
                productItems.addAll(filterCompanyList);
                activeAdapterProduct.notifyDataSetChanged();

                return;
            }
        }
    }
}