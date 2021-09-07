/*
 * Author @Alex-tech-it
 * Min SDK Version = 19
 *
 * */

package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Button and init listener
        Button button = (Button) findViewById(R.id.calculate);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextA = (EditText) findViewById(R.id.memberA);
                EditText editTextB = (EditText) findViewById(R.id.memberB);
                EditText editTextC = (EditText) findViewById(R.id.memberC);

                try {
                    // Throw an error if there is a problem with parsing values from Int to String
                    int a = Integer.parseInt(editTextA.getText().toString());
                    int b = Integer.parseInt(editTextB.getText().toString());
                    int c = Integer.parseInt(editTextC.getText().toString());

                    // Calculate the roots of the equation
                    double d = Math.pow(b,2) - 4*a*c;
                    double x1 = (-b - Math.pow(d, 0.5))/(2*a);
                    double x2 = (-b + Math.pow(d, 0.5))/(2*a);
                    String ans = "Результат: x = " + String.valueOf(x1) + ", x2 = " + String.valueOf(x2);

                    if(Double.isNaN(x1) || Double.isNaN(x2)){
                        ans = "Результат: корней не существует";
                    }
                    // Put result in TextView under the button
                    TextView textView = (TextView) findViewById(R.id.result);
                    textView.setText(ans);

                } catch (NumberFormatException e){
                    // Notify the user of incorrectly entered data
                    Snackbar.make(v, "Неверно введенные данные", Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

}