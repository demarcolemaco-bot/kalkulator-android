package com.example.kalkulator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private TextView tvExpression;
    private TextView tvResult;

    private StringBuilder currentInput = new StringBuilder();
    private StringBuilder expression = new StringBuilder();
    private Double lastResult = null;
    private boolean isResultShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        setupListeners();
    }

    private void setupListeners() {
        int[] numIds = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : numIds) {
            Button btn = findViewById(id);
            btn.setOnClickListener(v -> onDigitPressed(((Button) v).getText().toString()));
        }

        findViewById(R.id.btnDot).setOnClickListener(v -> onDotPressed());
        findViewById(R.id.btnClear).setOnClickListener(v -> onClearPressed());
        findViewById(R.id.btnBackspace).setOnClickListener(v -> onBackspacePressed());
        findViewById(R.id.btnPlusMinus).setOnClickListener(v -> onPlusMinusPressed());

        findViewById(R.id.btnPlus).setOnClickListener(v -> onOperatorPressed("+"));
        findViewById(R.id.btnMinus).setOnClickListener(v -> onOperatorPressed("−"));
        findViewById(R.id.btnMultiply).setOnClickListener(v -> onOperatorPressed("×"));
        findViewById(R.id.btnDivide).setOnClickListener(v -> onOperatorPressed("÷"));
        findViewById(R.id.btnPercent).setOnClickListener(v -> onPercentPressed());

        findViewById(R.id.btnEquals).setOnClickListener(v -> onEqualsPressed());
    }

    private void onDigitPressed(String digit) {
        if (isResultShown) {
            currentInput.setLength(0);
            expression.setLength(0);
            tvExpression.setText("");
            isResultShown = false;
        }

        if (currentInput.toString().equals("0") && !digit.equals("0")) {
            currentInput.setLength(0);
        } else if (currentInput.toString().equals("0") && digit.equals("0")) {
            return;
        }

        currentInput.append(digit);
        tvResult.setText(currentInput.toString());
    }

    private void onDotPressed() {
        if (isResultShown) {
            currentInput.setLength(0);
            expression.setLength(0);
            tvExpression.setText("");
            isResultShown = false;
        }

        if (currentInput.length() == 0) {
            currentInput.append("0.");
        } else if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
        }
        tvResult.setText(currentInput.toString());
    }

    private void onOperatorPressed(String op) {
        if (isResultShown) {
            expression.setLength(0);
            expression.append(formatNumber(lastResult != null ? lastResult : 0.0))
                      .append(" ").append(op).append(" ");
            currentInput.setLength(0);
            isResultShown = false;
            tvExpression.setText(expression.toString());
            return;
        }

        if (currentInput.length() > 0) {
            expression.append(currentInput).append(" ").append(op).append(" ");
            currentInput.setLength(0);
            tvExpression.setText(expression.toString());
            tvResult.setText("0");
        } else if (expression.length() > 0) {
            String trimmed = expression.toString().trim();
            int lastSpace = trimmed.lastIndexOf(' ');
            if (lastSpace != -1) {
                expression = new StringBuilder(trimmed.substring(0, lastSpace + 1)).append(op).append(" ");
                tvExpression.setText(expression.toString());
            }
        }
    }

    private void onPercentPressed() {
        if (currentInput.length() > 0) {
            try {
                double val = Double.parseDouble(currentInput.toString()) / 100.0;
                currentInput.setLength(0);
                currentInput.append(formatNumber(val));
                tvResult.setText(currentInput.toString());
            } catch (Exception e) {
                tvResult.setText("Error");
            }
        }
    }

    private void onPlusMinusPressed() {
        if (currentInput.length() > 0 && !currentInput.toString().equals("0")) {
            if (currentInput.charAt(0) == '-') {
                currentInput.deleteCharAt(0);
            } else {
                currentInput.insert(0, "-");
            }
            tvResult.setText(currentInput.toString());
        }
    }

    private void onBackspacePressed() {
        if (isResultShown) {
            onClearPressed();
            return;
        }

        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
            tvResult.setText(currentInput.length() == 0 ? "0" : currentInput.toString());
        }
    }

    private void onClearPressed() {
        currentInput.setLength(0);
        expression.setLength(0);
        lastResult = null;
        isResultShown = false;
        tvExpression.setText("");
        tvResult.setText("0");
    }

    private void onEqualsPressed() {
        if (currentInput.length() == 0 && expression.length() == 0) return;

        String fullExpr = expression.toString() + currentInput.toString();
        fullExpr = fullExpr.trim();
        if (fullExpr.isEmpty()) return;

        try {
            double res = evaluateExpression(fullExpr);
            tvExpression.setText(fullExpr + " =");
            String formatted = formatNumber(res);
            tvResult.setText(formatted);
            lastResult = res;
            isResultShown = true;
        } catch (ArithmeticException e) {
            tvResult.setText(e.getMessage() != null ? e.getMessage() : "Error");
            isResultShown = true;
        } catch (Exception e) {
            tvResult.setText("Error");
            isResultShown = true;
        }
    }

    private double evaluateExpression(String expr) {
        String[] rawTokens = expr.split("\\s+");
        List<String> tokens = new ArrayList<>();
        for (String t : rawTokens) {
            tokens.add(t);
        }

        if (tokens.isEmpty()) return 0.0;

        String last = tokens.get(tokens.size() - 1);
        if (last.equals("+") || last.equals("−") || last.equals("×") || last.equals("÷")) {
            tokens.remove(tokens.size() - 1);
        }

        // Pass 1: Multiplication and Division
        int i = 0;
        while (i < tokens.size()) {
            String token = tokens.get(i);
            if (token.equals("×") || token.equals("÷")) {
                double left = Double.parseDouble(tokens.get(i - 1));
                double right = Double.parseDouble(tokens.get(i + 1));
                double res;
                if (token.equals("×")) {
                    res = left * right;
                } else {
                    if (right == 0.0) throw new ArithmeticException("Tidak bisa dibagi 0");
                    res = left / right;
                }
                tokens.set(i - 1, String.valueOf(res));
                tokens.remove(i);
                tokens.remove(i);
                i--;
            } else {
                i++;
            }
        }

        // Pass 2: Addition and Subtraction
        double result = Double.parseDouble(tokens.get(0));
        int j = 1;
        while (j < tokens.size()) {
            String op = tokens.get(j);
            double next = Double.parseDouble(tokens.get(j + 1));
            if (op.equals("+")) {
                result += next;
            } else if (op.equals("−")) {
                result -= next;
            }
            j += 2;
        }

        return result;
    }

    private String formatNumber(double value) {
        if (value % 1.0 == 0.0 && !Double.isInfinite(value) && !Double.isNaN(value)) {
            try {
                return BigDecimal.valueOf(value).toBigInteger().toString();
            } catch (Exception e) {
                return String.valueOf((long) value);
            }
        } else {
            DecimalFormat df = new DecimalFormat("#.########");
            return df.format(BigDecimal.valueOf(value).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros());
        }
    }
}
