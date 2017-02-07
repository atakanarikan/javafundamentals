package org.zeroturnaround.jf.android.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.Stack;

public class CalculatorActivity extends Activity {
    Stack<String> stack = new Stack<>();
    boolean isAfterAddition = false;
    boolean inputChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
         // set our calculator layout
        handleListeners();
        Button button_one = (Button) findViewById(R.id.button_one);
        Button button_zero = (Button) findViewById(R.id.button_zero);
        Button button_clear = (Button) findViewById(R.id.button_clear);
        Button button_equals = (Button) findViewById(R.id.button_equals);
        Button button_plus = (Button) findViewById(R.id.button_plus);

    }
    private void handleListeners(){
        Button button_one = (Button) findViewById(R.id.button_one);
        Button button_zero = (Button) findViewById(R.id.button_zero);
        Button button_clear = (Button) findViewById(R.id.button_clear);
        Button button_plus = (Button) findViewById(R.id.button_plus);
        Button button_equals = (Button) findViewById(R.id.button_equals);
        final TextView text_display = (TextView) findViewById(R.id.text_display);

        button_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stack.empty() && isAfterAddition) {
                    text_display.setText("");
                    isAfterAddition = false;
                }
                inputChanged = true;
                CharSequence current = text_display.getText();
                if (current.length() < 15) {
                    current = current.toString().concat("1");
                    text_display.setText(current);
                } else {
                    Toast.makeText(v.getContext(), R.string.too_many_chars, Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stack.empty() && isAfterAddition) {
                    text_display.setText("");
                    isAfterAddition = false;
                }
                inputChanged = true;
                CharSequence current = text_display.getText();
                if (current.length() < 15) {
                    current = current.toString().concat("0");
                    text_display.setText(current);
                } else {
                    Toast.makeText(v.getContext(), R.string.too_many_chars, Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_display.setText("");
                stack.clear();
            }
        });

        button_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAfterAddition = true;
                String current = text_display.getText().toString();
                if (stack.empty() && inputChanged) {
                    stack.push(current);
                } else if (inputChanged) {
                    inputChanged = false;
                    String first = stack.pop();
                    int result = Integer.parseInt(first, 2) + Integer.parseInt(current, 2);
                    stack.push(Integer.toBinaryString(result));
                    text_display.setText(Integer.toBinaryString(result));
                }
            }
        });
        button_equals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current = text_display.getText().toString();
                if (stack.empty()) {
                    Toast.makeText(v.getContext(), R.string.no_first_input, Toast.LENGTH_SHORT).show();
                } else if (inputChanged){
                    inputChanged = false;
                    isAfterAddition = true;
                    String first = stack.pop();
                    int result = Integer.parseInt(first, 2) + Integer.parseInt(current, 2);
                    stack.push(Integer.toBinaryString(result));
                    text_display.setText(Integer.toBinaryString(result));
                }
            }
        });
    }
}
