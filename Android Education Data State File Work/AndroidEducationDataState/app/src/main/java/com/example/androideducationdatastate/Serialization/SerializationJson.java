package com.example.androideducationdatastate.Serialization;

import android.content.Context;
import android.util.Log;

import com.example.androideducationdatastate.StudentItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SerializationJson {

    public static boolean Write(JSONObject text, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("DataJson.json", 0));
            outputStreamWriter.write(text.toString());
            outputStreamWriter.close();

            return true;
        } catch (IOException e){
            Log.e("Exception In write JSON", "File write failed: " + e.toString());
            return false;
        }
    }

    public static ArrayList<StudentItem> Read(Context context) {
        ArrayList<StudentItem> studentList = new ArrayList<StudentItem>();

        try {
            InputStream inputStream = context.openFileInput("DataJson.json");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder data = new StringBuilder();
                String buff = "";

                while ( (buff = bufferedReader.readLine()) != null ) {
                    data.append(buff).append("\n");
                }

                bufferedReader.close();

                // Convert tot JSOn
                JSONObject jsonData = new JSONObject(data.toString());
                int count = Integer.parseInt(jsonData.getString("count"));
                for(int i = 1; i <= count; i++){
                    JSONObject item = jsonData.getJSONObject(Integer.toString(i));
                    StudentItem student = new StudentItem(item.getString("fullName"),
                            item.getString("gender"),
                            item.getString("programLang"),
                            item.getString("preferredIDE"));
                    studentList.add(student);
                }
            }
        }
        catch (FileNotFoundException e) {
            Log.e("SerializationJSON", "File isn't found: " + e.toString());
            return null;
        } catch (IOException e) {
            Log.e("SerializationJSON", "File isn't readable " + e.toString());
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return studentList;
    }
}
