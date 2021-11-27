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
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final String FULLNAME_KEY = "fullName";
    static final String GENDER_KEY = "gender";
    static final String LANG_KEY = "programLang";
    static final String IDE_KEY = "preferredIDE";
    static final String MODE_KEY = "mode";
    static final String INDEX_KEY = "index";

    private ArrayList<StudentItem> studentList;

    private RecyclerView activeRecyclerView;
    private StudentAdapter activeAdapter;
    private RecyclerView.LayoutManager activeLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getter the saved data
        if (savedInstanceState != null){
            studentList = savedInstanceState.getParcelableArrayList("students");
        } else {
            createStudentList();
        }

        // Init studentList & Build RecyclerView to Activity
        buildRecyclerView();

        // Setter function for click on FloatActionButtons
        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton files = findViewById(R.id.files);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartForResult.launch(new Intent(view.getContext(), StudentAdd.class));
            }
        });
        files.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), OpenAndSaveFiles.class);
                intent.putParcelableArrayListExtra("students",studentList);
                mOpenSaveData.launch(intent);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("students", studentList);
        super.onSaveInstanceState(outState);
    }

    // Remove the item from studentList in RecyclerView
    private void removeItem(int position){
        studentList.remove(position);
        activeAdapter.notifyItemRemoved(position);
    }

    // Add new Student in studentList RecyclerView
    private void addItem(StudentItem newStudent){
        studentList.add(new StudentItem(
                newStudent.getFullName(),
                newStudent.getGenderField(),
                newStudent.getProgramLang(),
                newStudent.getPreferredIDE()
        ));
        activeAdapter.notifyDataSetChanged();
    }

    // Change the item from studentList in RecyclerView
    private void changeItem(StudentItem newStudent, int position){
        studentList.set(position, newStudent);
        activeAdapter.notifyItemChanged(position);
    }

    // To active new activity to get a new result
    private void changeItemIntent(int position){
        Intent intent = new Intent(this, StudentInfoActivity.class);
        StudentItem student = studentList.get(position);
        intent.putExtra(INDEX_KEY, position);
        intent.putExtra(FULLNAME_KEY, student.getFullName());
        intent.putExtra(GENDER_KEY, student.getGenderField());
        intent.putExtra(LANG_KEY, student.getProgramLang());
        intent.putExtra(IDE_KEY, student.getPreferredIDE());

        mStartForResult.launch(intent);
    }

    // This func init the studentList
    private void createStudentList(){
        this.studentList = new ArrayList<>();
        studentList.add(new StudentItem("Корнилов Александр Андреевич",
                                        "Мужской", "C#, C++",
                                        "Android Studio"));
        studentList.add(new StudentItem("Херуимов Андрей Петрович",
                                        "Мужской", "C#, C++",
                                        "Android Studio"));
        studentList.add(new StudentItem("Иванов Святослав Михайлович",
                                        "Мужской", "C#, C++",
                                        "Android Studio"));
    }

    // This func build a new RecyclerView in Activity
    private void buildRecyclerView(){
        this.activeRecyclerView = findViewById(R.id.activeRecyclerView);
        this.activeRecyclerView.setHasFixedSize(true);
        this.activeLayoutManager = new LinearLayoutManager(this);
        this.activeAdapter = new StudentAdapter(studentList);

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
                        StudentItem newStudent = new StudentItem(
                                intent.getStringExtra(FULLNAME_KEY),
                                intent.getStringExtra(GENDER_KEY),
                                intent.getStringExtra(LANG_KEY),
                                intent.getStringExtra(IDE_KEY));

                        switch (intent.getStringExtra(MODE_KEY)){
                            case "add":
                                addItem(newStudent);
                                break;
                            case "changed":
                                changeItem(newStudent, intent.getIntExtra(INDEX_KEY, 0));
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

    // Launch intent for Open or Save Student's data
    ActivityResultLauncher<Intent> mOpenSaveData = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        switch (intent.getStringExtra(MODE_KEY)){
                            case "open":
                                studentList = intent.getParcelableArrayListExtra("students");
                                buildRecyclerView();
                                break;
                            case "save":
                                Toast.makeText(getApplicationContext(),
                                        "Данные успешно сохранены",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                    else if (result.getResultCode() != Activity.RESULT_CANCELED){
                        Toast.makeText(getApplicationContext(),
                                "Невозможно сохранить или прочитать данные",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
}