package com.example.androideducationdatastate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private DBService db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DBService(LoginActivity.this);
    }

    public void onAddClickLogin(View v){
        EditText login = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);

        if(login.getText().toString().equals("") &&
                !login.getText().toString().contains("@")) {
            Toast.makeText(getApplicationContext(),
                    "Некорректный Email",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.getText().toString().replaceAll(" ","").equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Пароль не может быть пустым",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(db.Login(login.getText().toString(), password.getText().toString())){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(),
                    "Неверный логин или пароль",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onAddClickRegistration(View v){
        Intent intent = new Intent(this, ActivityRegistration.class);
        startActivity(intent);
    }
}