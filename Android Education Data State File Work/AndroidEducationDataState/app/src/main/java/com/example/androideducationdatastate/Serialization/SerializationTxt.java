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

public class SerializationTxt {

    public static boolean Write(String text, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("DataTxt.txt", 0));
            outputStreamWriter.write(text);
            outputStreamWriter.close();

            return true;
        } catch (IOException e){
            Log.e("Exception In write TXT", "File write failed: " + e.toString());
            return false;
        }
    }

    public static ArrayList<StudentItem> Read(Context context) {
        ArrayList<StudentItem> studentList = new ArrayList<StudentItem>();

        try {
            InputStream inputStream = context.openFileInput("DataTxt.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String buff = "";

                String FullName = "";
                String Gender = "";
                String ProgramLang = "";
                String PreferedIDE = "";

                while ( (buff = bufferedReader.readLine()) != null ) {
                    FullName = buff;

                    buff = bufferedReader.readLine();
                    Gender = buff;

                    buff = bufferedReader.readLine();
                    ProgramLang = buff;

                    buff = bufferedReader.readLine();
                    PreferedIDE = buff;

                    studentList.add(new StudentItem(FullName, Gender, ProgramLang, PreferedIDE));
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("SerializationTxt", "File isn't found: " + e.toString());
            return null;
        } catch (IOException e) {
            Log.e("SerializationTxt", "File isn't readable " + e.toString());
            return null;
        }

        return studentList;
    }
}
