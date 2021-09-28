package com.example.calculator;

import android.annotation.SuppressLint;
import android.util.Log;

import java.util.ArrayList;

public class Calculator {
    private StringBuilder inputString;
    private boolean countComma = true;
    private double result;
    private String textResult;
    private final String stringOperations = "%×÷-+";

    public Calculator(){
        inputString = new StringBuilder();
    }

    public void onNumberClicked(int buttonId, String str){
        if(buttonId == R.id.zero){
            if (inputString.length() == 0 || !inputString.toString().equals("0")) {
                inputString.append("0");
            }
        }else{
            if (inputString.toString().equals("0")){
                inputString.setLength(0);
            }
            inputString.append(str);
        }
        this.calculate();
    }

    @SuppressLint("NonConstantResourceId")
    public void onActionClicked(int actionId, String str){
        if (inputString.length() != 0) {
            char lastOperation = inputString.charAt(inputString.length() - 1);
            switch (actionId) {
                case R.id.clear:
                    inputString.setLength(0);
                    textResult="";
                    result = 0.0;
                    countComma = true;
                    break;
                case R.id.minus:
                    break;
                case R.id.precent:
                case R.id.multiplication:
                case R.id.division:
                case R.id.subtraction:
                case R.id.adding:
                    if (stringOperations.contains(Character.toString(lastOperation))) {
                        inputString.deleteCharAt(inputString.length() - 1);
                        inputString.append(str);
                    } else {
                        inputString.append(str);
                    }
                    countComma = true;
                    break;
                case R.id.comma:
                    if(stringOperations.contains(Character.toString(lastOperation)) && countComma){
                        countComma = false;
                        inputString.append("0");
                        inputString.append(str);
                    }else if(lastOperation != ',' && countComma){
                        countComma = false;
                        inputString.append(str);
                    }
                    break;
                case R.id.equals:
                    this.calculate();
                    inputString.setLength(0);
                    String textResult = Double.toString(result);
                    textResult = textResult.replace('.',',');
                    this.textResult = textResult;
                    inputString.append(textResult);
                    countComma = true;
                    break;
            }
            this.calculate();
        }
    }

    public String getText() {
        return inputString.toString();
    }

    public String getResult(){
        return this.textResult;
    }

    public Boolean getComma() { return this.countComma; }

    public void setText(String value) { inputString.setLength(0); inputString.append(value); }

    public void setResult(String value) { textResult = value; }

    public void setComma(Boolean value) { countComma = value; }

    private void calculate(){
        String resultExpresion = inputString.toString();
        ArrayList<Double> testListNumbers = new ArrayList<Double>();
        ArrayList<String> testListOperations = new ArrayList<String>();
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < resultExpresion.length(); i++) {
            if (i == 0 && resultExpresion.charAt(i) == '-'){
                number.append("-");
            } else {
                if (resultExpresion.charAt(i) >= '0' && resultExpresion.charAt(i) <= '9' ||
                        resultExpresion.charAt(i) == ',' || resultExpresion.charAt(i) == 'E') {
                    if (resultExpresion.charAt(i) == ',') {
                        number.append('.');
                    } else {
                        number.append(resultExpresion.charAt(i));
                    }
                } else {
                    if (number.length() != 0) {
                        testListNumbers.add(Double.parseDouble(number.toString()));
                        number.setLength(0);
                        testListOperations.add(Character.toString(resultExpresion.charAt(i)));
                    }
                }
                if (i == resultExpresion.length() - 1 && number.length() != 0) {
                    testListNumbers.add(Double.parseDouble(number.toString()));
                }
            }
        }
        try {
            ArrayList<Double> copyOfListNumbers = new ArrayList<Double>(testListNumbers);
            ArrayList<String> copyOfListOperations = new ArrayList<String>(testListOperations);
            while (!copyOfListOperations.isEmpty() && copyOfListNumbers.size() > 1) {
                int indexOperation = -1;
                double mediateResult = 0.0;
                double firstNumber = 0.0;
                double secondNumber = 0.0;
                if (copyOfListOperations.contains("÷") || copyOfListOperations.contains("×")) {
                    if (!copyOfListOperations.contains("÷")) {
                        indexOperation = copyOfListOperations.indexOf("×");
                    } else if (!copyOfListOperations.contains("×")) {
                        indexOperation = copyOfListOperations.indexOf("÷");
                    } else {
                        indexOperation = Math.min(copyOfListOperations.indexOf("÷"), copyOfListOperations.indexOf("×"));
                    }
                } else {
                    if (!copyOfListOperations.contains("-")) {
                        indexOperation = copyOfListOperations.indexOf("+");
                    } else if (!copyOfListOperations.contains("+")) {
                        indexOperation = copyOfListOperations.indexOf("-");
                    } else {
                        indexOperation = Math.min(copyOfListOperations.indexOf("-"), copyOfListOperations.indexOf("+"));
                    }
                }
                switch (copyOfListOperations.get(indexOperation)) {
                    case "+":
                        mediateResult = copyOfListNumbers.get(indexOperation) + copyOfListNumbers.get(indexOperation + 1);
                        break;
                    case "-":
                        mediateResult = copyOfListNumbers.get(indexOperation) - copyOfListNumbers.get(indexOperation + 1);
                        break;
                    case "×":
                        mediateResult = copyOfListNumbers.get(indexOperation) * copyOfListNumbers.get(indexOperation + 1);
                        break;
                    case "÷":
                        mediateResult = copyOfListNumbers.get(indexOperation) / copyOfListNumbers.get(indexOperation + 1);
                        break;
                }
                copyOfListNumbers.remove(indexOperation + 1);
                copyOfListNumbers.set(indexOperation, mediateResult);
                copyOfListOperations.remove(indexOperation);
            }

            result = (Double.isNaN(result)) ? 0.0 : copyOfListNumbers.get(0);
            String textResult = Double.toString(result);
            this.textResult = textResult.replace('.',',');
        }catch (Exception e){
            Log.e("Calculater",e.toString());
            this.textResult = "";
        }
    }

}
