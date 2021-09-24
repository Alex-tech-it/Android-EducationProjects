package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.activity_main_horizontal);
        }

        // Array of "number" buttons
        final int[] numberIds = new int[] {
          R.id.zero, R.id.one, R.id.two,
          R.id.three, R.id.four, R.id.five, R.id.six,
          R.id.seven, R.id.eight, R.id.nine
        };

        // Array of "action" buttons
        final int[] actionsIds = new int[] {
          R.id.clear, R.id.minus, R.id.precent,
          R.id.division, R.id.multiplication, R.id.subtraction,
          R.id.adding, R.id.equals, R.id.comma
        };

        Calculator calculator = new Calculator();
        TextView textInput = (TextView) findViewById(R.id.textInput);
        TextView textOutput = (TextView) findViewById(R.id.outputText);

        View.OnClickListener numberButtonOnClickListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textButton = ((Button) v).getText().toString();
                calculator.onNumberClicked(v.getId(), textButton);
                textInput.setText(calculator.getText());
                textOutput.setText(calculator.getResult());
            }
        };

        View.OnClickListener actionButtonOnClickListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textButton = ((Button) v).getText().toString();
                calculator.onActionClicked(v.getId(), textButton);
                textInput.setText(calculator.getText());
                textOutput.setText(calculator.getResult());
            }
        };

        for (int numberId : numberIds) {
            findViewById(numberId).setOnClickListener(numberButtonOnClickListner);
        }
        for (int actionId : actionsIds) {
            findViewById(actionId).setOnClickListener(actionButtonOnClickListner);
        }
    }
}