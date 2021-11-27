package com.example.androideducationdatastate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.androideducationdatastate.Serialization.SerializationCsv;
import com.example.androideducationdatastate.Serialization.SerializationJson;
import com.example.androideducationdatastate.Serialization.SerializationTxt;
import com.example.androideducationdatastate.Serialization.SerializationXML;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class OpenAndSaveFiles extends AppCompatActivity {

    private static final int PICKFILE_RESULT = 1;
    private ArrayList<StudentItem> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_and_save_files);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            studentList = extras.getParcelableArrayList("students");
            if (studentList.size() == 0){
                Toast.makeText(getApplicationContext(),
                        "Список студентов пуст",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
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

    // This catches click from button to open file .txt
    public void onOpenTxtClick(View v){
        Intent intent = new Intent();
        studentList = SerializationTxt.Read(this);

        intent.putParcelableArrayListExtra("students", studentList);
        intent.putExtra(MainActivity.MODE_KEY, "open");
        setResult(RESULT_OK, intent);
        finish();
    }

    // This catches click from button to open file .csv
    public void onOpenCSVClick(View v){
        Intent intent = new Intent();
        studentList = SerializationCsv.Read(this);

        intent.putParcelableArrayListExtra("students", studentList);
        intent.putExtra(MainActivity.MODE_KEY, "open");
        setResult(RESULT_OK, intent);
        finish();
    }

    // This catches click from button to open file .json
    public void onOpenJsonClick(View v){
        Intent intent = new Intent();
        studentList = SerializationJson.Read(this);

        intent.putParcelableArrayListExtra("students", studentList);
        intent.putExtra(MainActivity.MODE_KEY, "open");
        setResult(RESULT_OK, intent);
        finish();
    }

    // This catches click from button to open file .XML
    public void onOpenXMLClick(View v){
        Intent intent = new Intent();
        studentList = SerializationXML.Read(this);

        intent.putParcelableArrayListExtra("students", studentList);
        intent.putExtra(MainActivity.MODE_KEY, "open");
        setResult(RESULT_OK, intent);
        finish();
    }

    // This catches click from button to save file .txt
    public void onSaveTxtClick(View v){
        Intent intent = new Intent();

        String students = getStudentsListTxt();
        if (SerializationTxt.Write(students, this)){
            intent.putExtra(MainActivity.MODE_KEY, "save");
            setResult(RESULT_OK, intent);
        }else{
            Toast.makeText(this, "Ошибка при сохранении в .txt файл", Toast.LENGTH_SHORT).show();
            intent.putExtra(MainActivity.MODE_KEY, "save");
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    // This catches click from button to save file .csv
    public void onSaveCSVClick(View v){
        Intent intent = new Intent();

        String students = getStudentsListCsv();
        if (SerializationCsv.Write(students, this)){
            intent.putExtra(MainActivity.MODE_KEY, "save");
            setResult(RESULT_OK, intent);
        }else{
            Toast.makeText(this, "Ошибка при сохранении в .csv файл", Toast.LENGTH_SHORT).show();
            intent.putExtra(MainActivity.MODE_KEY, "save");
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    // This catches click from button to save file .json
    public void onSaveJsonClick(View v) throws JSONException {
        Intent intent = new Intent();

        JSONObject students = getStudentsListJSON();
        if (SerializationJson.Write(students, this)){
            intent.putExtra(MainActivity.MODE_KEY, "save");
            setResult(RESULT_OK, intent);
        }else{
            Toast.makeText(this, "Ошибка при сохранении в .json файл", Toast.LENGTH_SHORT).show();
            intent.putExtra(MainActivity.MODE_KEY, "save");
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    // This catches click from button to save file .XML
    public void onSaveXMLClick(View v) throws JSONException {
        Intent intent = new Intent();

        JSONObject students = getStudentsListXML();
        if (SerializationXML.Write(students, this)){
            intent.putExtra(MainActivity.MODE_KEY, "save");
            setResult(RESULT_OK, intent);
        }else{
            Toast.makeText(this, "Ошибка при сохранении в .json файл", Toast.LENGTH_SHORT).show();
            intent.putExtra(MainActivity.MODE_KEY, "save");
            setResult(RESULT_CANCELED, intent);
        }
        finish();
    }

    private String getStudentsListTxt(){
        StringBuilder result = new StringBuilder();
        for(StudentItem student : studentList){
            result.append(student.getFullName()).append("\n");
            result.append(student.getGenderField()).append("\n");
            result.append(student.getProgramLang()).append("\n");
            result.append(student.getPreferredIDE()).append("\n");
        }

        return result.toString();
    }

    private String getStudentsListCsv(){
        StringBuilder result = new StringBuilder();
        for(StudentItem student : studentList){
            result.append(student.getFullName()).append(";");
            result.append(student.getGenderField()).append(";");
            result.append(student.getProgramLang()).append(";");
            result.append(student.getPreferredIDE()).append(";");
            result.append("\n");
        }
        return result.toString();
    }

    private JSONObject getStudentsListJSON() throws JSONException {
        JSONObject resultJson = new JSONObject();
        resultJson.put("count", studentList.size());

        int index = 1;
        for(StudentItem student : studentList) {
            JSONObject newItem = new JSONObject();
            newItem.put("fullName", student.getFullName());
            newItem.put("gender", student.getGenderField());
            newItem.put("programLang", student.getProgramLang());
            newItem.put("preferredIDE", student.getPreferredIDE());
            resultJson.put(Integer.toString(index), newItem);
            index++;
        }
        return resultJson;
    }

    private JSONObject getStudentsListXML() throws JSONException {
        JSONObject resultJson = new JSONObject();
        resultJson.put("count", studentList.size());

        int index = 1;
        for(StudentItem student : studentList) {
            JSONObject newItem = new JSONObject();
            newItem.put("fullName", student.getFullName());
            newItem.put("gender", student.getGenderField());
            newItem.put("programLang", student.getProgramLang());
            newItem.put("preferredIDE", student.getPreferredIDE());
            resultJson.put(Integer.toString(index), newItem);
            index++;
        }
        return resultJson;
    }

}