package com.example.androideducationdatastate.Serialization;

import android.content.Context;
import android.util.Log;

import com.example.androideducationdatastate.StudentItem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SerializationCsv {

    public static boolean Write(String text, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("DataCsv.csv", 0));
            outputStreamWriter.write(text);
            outputStreamWriter.close();

            return true;
        } catch (IOException e){
            Log.e("Exception In write CSV", "File write failed: " + e.toString());
            return false;
        }
    }

    public static ArrayList<StudentItem> Read(Context context) {
        ArrayList<StudentItem> studentList = new ArrayList<StudentItem>();

        try {
            InputStream inputStream = context.openFileInput("DataCsv.csv");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String buff = "";

                String FullName = "";
                String Gender = "";
                String ProgramLang = "";
                String PreferedIDE = "";

                while ( (buff = bufferedReader.readLine()) != null ) {
                    Log.e("buff", buff);
                    String [] dataItem = buff.split(";");
                    FullName = dataItem[0];
                    Gender = dataItem[1];
                    if(dataItem.length == 3){
                        ProgramLang = dataItem[2];
                    }else if (dataItem.length == 4){
                        ProgramLang = dataItem[2];
                        PreferedIDE = dataItem[3];
                    }else{
                        ProgramLang = "";
                        PreferedIDE = "";
                    }

                    studentList.add(new StudentItem(FullName, Gender, ProgramLang, PreferedIDE));
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("SerializationCSV", "File isn't found: " + e.toString());
            return null;
        } catch (IOException e) {
            Log.e("SerializationCSV", "File isn't readable " + e.toString());
            return null;
        }

        return studentList;
    }
}
