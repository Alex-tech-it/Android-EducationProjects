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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final String NEW_COMPANY_NAME_KEY = "newCompanyName";
    static final String OLD_COMPANY_NAME_KEY = "oldCompanyName";
    static final String NEW_COMPANY_FOUNDER_KEY = "newCompanyFounder";
    static final String OLD_COMPANY_FOUNDER_KEY = "oldCompanyFounder";
    static final String NEW_COMPANY_PRODUCT_KEY = "newCompanyProduct";
    static final String OLD_COMPANY_PRODUCT_KEY = "oldCompanyProduct";
    static final String MODE_KEY = "mode";
    static final String INDEX_KEY = "index";

    private ArrayList<CompanyItem> companyList;
    private DBService db;
    private RecyclerView activeRecyclerView;
    private StudentAdapter activeAdapter;
    private RecyclerView.LayoutManager activeLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBService(MainActivity.this);

        // Getter the saved data
        if (savedInstanceState != null){
            companyList = savedInstanceState.getParcelableArrayList("сompanies");
        } else {
            if(db.GetCompanies().size() == 0){
                createStudentList();
            }else{
                companyList = db.GetCompanies();
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
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("сompanies", companyList);
        super.onSaveInstanceState(outState);
    }

    // Remove the item from studentList in RecyclerView
    private void removeItem(int position){
        CompanyItem item = companyList.get(position);
        companyList.remove(position);
        db.DeleteCompany(item);
        activeAdapter.notifyItemRemoved(position);
    }

    // Add new Student in studentList RecyclerView
    private void addItem(CompanyItem newCompany){
        int res = 0;
        try{
            db.AddCompany(newCompany);
        } catch (NullPointerException exception){
            Toast.makeText(getApplicationContext(),
                    "Невозможно добавить компанию, так как она уже существует в списке",
                    Toast.LENGTH_SHORT).show();
            res = -1;
        }
        if(res != -1){
            companyList.add(newCompany);
            activeAdapter.notifyDataSetChanged();
        }
    }

    // Change the item from studentList in RecyclerView
    private void changeItem(CompanyItem oldCompany, CompanyItem newCompany, int position){
        db.ChangeCompany(oldCompany, newCompany);
        companyList.set(position, newCompany);
        activeAdapter.notifyItemChanged(position);
    }

    // To active new activity to get a new result
    private void changeItemIntent(int position){
        Intent intent = new Intent(this, StudentInfoActivity.class);
        CompanyItem companyItem = companyList.get(position);
        intent.putExtra(INDEX_KEY, position);
        intent.putExtra(OLD_COMPANY_NAME_KEY, companyItem.getCompanyName());
        intent.putExtra(OLD_COMPANY_FOUNDER_KEY, companyItem.getCompanyFounderField());
        intent.putExtra(OLD_COMPANY_PRODUCT_KEY, companyItem.getCompanyProductField());

        mStartForResult.launch(intent);
    }

    // This func init the studentList
    private void createStudentList(){
        this.companyList = new ArrayList<>();
        companyList.add(new CompanyItem("Apple",
                                        "Тип Кук",
                                        "IPhone"));
        companyList.add(new CompanyItem("Microsoft Inc.",
                                        "Билл Гейтс",
                                        "Windows"));
        companyList.add(new CompanyItem("OOO ≪ВКонтакте≫",
                                        "Павел Дуров",
                                        "СС ВКонтакте"));

        db.AddCompany(new CompanyItem("Apple",
                "Тип Кук",
                "IPhone"));
        db.AddCompany(new CompanyItem("Microsoft Inc.",
                "Билл Гейтс",
                "Windows"));
        db.AddCompany(new CompanyItem("OOO ≪ВКонтакте≫",
                "Павел Дуров",
                "СС ВКонтакте"));
    }

    // This func build a new RecyclerView in Activity
    private void buildRecyclerView(){
        this.activeRecyclerView = findViewById(R.id.activeRecyclerView);
        this.activeRecyclerView.setHasFixedSize(true);
        this.activeLayoutManager = new LinearLayoutManager(this);
        this.activeAdapter = new StudentAdapter(companyList);

        this.activeRecyclerView.setLayoutManager(this.activeLayoutManager);
        this.activeRecyclerView.setAdapter(this.activeAdapter);

        this.activeAdapter.setOnItemClickListener(new StudentAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                changeItemIntent(position);
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
                                intent.getStringExtra(NEW_COMPANY_FOUNDER_KEY),
                                intent.getStringExtra(NEW_COMPANY_PRODUCT_KEY));

                        switch (intent.getStringExtra(MODE_KEY)){
                            case "add":
                                addItem(newCompany);
                                break;
                            case "changed":
                                CompanyItem oldCompany = new CompanyItem(
                                        intent.getStringExtra(OLD_COMPANY_NAME_KEY),
                                        intent.getStringExtra(OLD_COMPANY_FOUNDER_KEY),
                                        intent.getStringExtra(OLD_COMPANY_PRODUCT_KEY));
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
}