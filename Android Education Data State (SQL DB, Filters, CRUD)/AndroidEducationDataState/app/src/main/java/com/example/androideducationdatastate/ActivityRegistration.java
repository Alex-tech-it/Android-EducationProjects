package com.example.androideducationdatastate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import Service.GMailSender;

public class ActivityRegistration extends AppCompatActivity {

    private DBService db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        db = new DBService(ActivityRegistration.this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onAddClickRegistration(View v){
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

        if(db.CheckLogin(login.getText().toString())){
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        GMailSender sender = new GMailSender("kornilovalexworkspace@gmail.com", "gamemode4316");
                        sender.sendMail("Successful registration",
                                "You have successfully registered in the Companis application, congratulations!",
                                "kornilovalexworkspace@gmail.com",
                                login.getText().toString());
                    } catch (Exception e) {
                        Log.e("SendMail", e.getMessage(), e);
                    }
                }
            }).start();


            boolean reg = db.Registration(login.getText().toString(), password.getText().toString());
            if(reg){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(),
                        "Невозможно зарегистрироваться",
                        Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),
                    "Такой пользователь уже зарегистрирован",
                    Toast.LENGTH_SHORT).show();
        }
    }

}