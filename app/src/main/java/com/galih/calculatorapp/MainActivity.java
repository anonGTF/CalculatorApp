package com.galih.calculatorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.galih.calculatorapp.databinding.ActivityMainBinding;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean hasDot = false;
    private boolean lastCharNegative = false;
    private String lastChar = "";
    private int longChar = 0;
    private int openBracketCount = 0;
    private final List<String> operators = Arrays.asList("+", "-", "x", "/");
    private final List<String> numbers = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnNol.setOnClickListener(v -> addChar("0"));
        binding.btnOne.setOnClickListener(v -> addChar("1"));
        binding.btnTwo.setOnClickListener(v -> addChar("2"));
        binding.btnThree.setOnClickListener(v -> addChar("3"));
        binding.btnFour.setOnClickListener(v -> addChar("4"));
        binding.btnFive.setOnClickListener(v -> addChar("5"));
        binding.btnSix.setOnClickListener(v -> addChar("6"));
        binding.btnSeven.setOnClickListener(v -> addChar("7"));
        binding.btnEight.setOnClickListener(v -> addChar("8"));
        binding.btnNine.setOnClickListener(v -> addChar("9"));
        binding.btnDot.setOnClickListener(v -> handleDot());
        binding.btnPlus.setOnClickListener(v -> handleOperator("+"));
        binding.btnSubstract.setOnClickListener(v ->handleOperator("-"));
        binding.btnTimes.setOnClickListener(v -> handleOperator("x"));
        binding.btnDivide.setOnClickListener(v -> handleOperator("/"));
        binding.btnPositiveNegative.setOnClickListener(v -> handlePositiveNegative());
        binding.btnKurung.setOnClickListener(v -> handleKurung());
        binding.btnDelete.setOnClickListener(v -> handleDeletion());
        binding.btnClear.setOnClickListener(v -> handleClear());
        binding.btnEquals.setOnClickListener(v -> handleEquals());
    }

    private void handleKurung() {
        if (longChar > 0 && openBracketCount == 0 && !operators.contains(lastChar)) {
            openBracketCount++;
            addChar("x(");
        } else if(lastChar.equals("(")) {
            openBracketCount++;
            addChar("(");
        } else if (!operators.contains(lastChar) && openBracketCount > 0) {
            openBracketCount--;
            addChar(")");
        } else {
            openBracketCount++;
            addChar("(");
        }
    }

    private void handlePositiveNegative() {
        StringBuilder temp = new StringBuilder();
        if (lastCharNegative) {
            lastCharNegative = false;
            while (numbers.contains(lastChar) && longChar != 0) {
                temp.append(lastChar);
                handleDeletion();
            }
            handleDeletion(); // delete -
            handleDeletion(); // delete (
            for (String s : temp.reverse().toString().split("")) {
                addChar(s);
            }
            openBracketCount--;
        } else {
            lastCharNegative = true;
            while (numbers.contains(lastChar) && longChar != 0) {
                temp.append(lastChar);
                handleDeletion();
            }
            addChar("(-");
            for (String s : temp.reverse().toString().split("")) {
                addChar(s);
            }
            openBracketCount++;
        }
    }

    private void handleDot() {
        if (hasDot) {
            Toast.makeText(this, "invalid format", Toast.LENGTH_LONG).show();
        } else {
            hasDot = true;
            addChar(".");
        }
    }

    private void handleEquals() {
        if (operators.contains(lastChar)) {
            Toast.makeText(this, "invalid format", Toast.LENGTH_LONG).show();
            return;
        }

        while (openBracketCount != 0) {
            addChar(")");
            openBracketCount--;
        }

        try {
            String res = hitung(binding.etPerhitungan.getText().toString());
            binding.tvHasil.setText(res);
        } catch (Exception e) {
            Toast.makeText(this,"Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void handleClear() {
        lastChar = "";
        longChar = 0;
        openBracketCount = 0;
        lastCharNegative = false;
        hasDot = false;
        binding.etPerhitungan.setText("");
        binding.tvHasil.setText("");
    }

    private void handleDeletion() {
        if (longChar > 0) {
            String temp = binding.etPerhitungan.getText().toString();
            String res = temp.substring(0, temp.length() - 1);
            lastChar = (longChar != 1)
                    ? "" + res.charAt(res.length() - 1)
                    : "";
            longChar = res.length();
            Log.d("coba", "handleDeletion: " + res);
            binding.etPerhitungan.setText(res);
        }
    }

    private void handleOperator(String operator) {
        if (binding.etPerhitungan.getText().toString().isEmpty()) {
            Toast.makeText(this, "Invalid format", Toast.LENGTH_LONG).show();
        } else {
            hasDot = false;
            addChar(operator);
        }
    }

    private void addChar(String character) {
        if (longChar < 57) {
            String temp = binding.etPerhitungan.getText().toString();
            String res = (lastChar.equals("0") && longChar == 1 && !operators.contains(character))
                    ? character
                    : temp + character;
            lastChar = character;
            longChar = res.length();
            binding.etPerhitungan.setText(res);
        } else {
            Toast.makeText(this, "max character is 57", Toast.LENGTH_LONG).show();
        }
    }

    public String hitung(String operation) throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
        Double res = (Double) engine.eval(operation.replaceAll("x", "*"));
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        return df.format(res);
    }
}