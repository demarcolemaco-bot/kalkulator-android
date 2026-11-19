package com.example.kalkulator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView

    private var currentInput = StringBuilder()
    private var expression = StringBuilder()
    private var lastResult: Double? = null
    private var isResultShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)

        setupListeners()
    }

    private fun setupListeners() {
        val numberButtons = listOf(
            R.id.btn0 to "0",
            R.id.btn1 to "1",
            R.id.btn2 to "2",
            R.id.btn3 to "3",
            R.id.btn4 to "4",
            R.id.btn5 to "5",
            R.id.btn6 to "6",
            R.id.btn7 to "7",
            R.id.btn8 to "8",
            R.id.btn9 to "9"
        )

        for ((id, digit) in numberButtons) {
            findViewById<Button>(id).setOnClickListener {
                onDigitPressed(digit)
            }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener { onDotPressed() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClearPressed() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { onBackspacePressed() }
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener { onPlusMinusPressed() }

        findViewById<Button>(R.id.btnPlus).setOnClickListener { onOperatorPressed("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { onOperatorPressed("−") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { onOperatorPressed("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { onOperatorPressed("÷") }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { onPercentPressed() }

        findViewById<Button>(R.id.btnEquals).setOnClickListener { onEqualsPressed() }
    }

    private fun onDigitPressed(digit: String) {
        if (isResultShown) {
            currentInput.clear()
            expression.clear()
            tvExpression.text = ""
            isResultShown = false
        }

        if (currentInput.toString() == "0" && digit != "0") {
            currentInput.clear()
        } else if (currentInput.toString() == "0" && digit == "0") {
            return
        }

        currentInput.append(digit)
        tvResult.text = currentInput.toString()
    }

    private fun onDotPressed() {
        if (isResultShown) {
            currentInput.clear()
            expression.clear()
            tvExpression.text = ""
            isResultShown = false
        }

        if (currentInput.isEmpty()) {
            currentInput.append("0.")
        } else if (!currentInput.contains(".")) {
            currentInput.append(".")
        }
        tvResult.text = currentInput.toString()
    }

    private fun onOperatorPressed(op: String) {
        if (isResultShown) {
            expression.clear()
            expression.append(formatNumber(lastResult ?: 0.0)).append(" ").append(op).append(" ")
            currentInput.clear()
            isResultShown = false
            tvExpression.text = expression.toString()
            return
        }

        if (currentInput.isNotEmpty()) {
            expression.append(currentInput).append(" ").append(op).append(" ")
            currentInput.clear()
            tvExpression.text = expression.toString()
            tvResult.text = "0"
        } else if (expression.isNotEmpty()) {
            // Replace previous operator if user changed their mind
            val trimmed = expression.trimEnd()
            val lastSpace = trimmed.lastIndexOf(' ')
            if (lastSpace != -1) {
                expression = StringBuilder(trimmed.substring(0, lastSpace + 1)).append(op).append(" ")
                tvExpression.text = expression.toString()
            }
        }
    }

    private fun onPercentPressed() {
        if (currentInput.isNotEmpty()) {
            try {
                val value = currentInput.toString().toDouble() / 100.0
                currentInput.clear()
                currentInput.append(formatNumber(value))
                tvResult.text = currentInput.toString()
            } catch (e: Exception) {
                tvResult.text = "Error"
            }
        }
    }

    private fun onPlusMinusPressed() {
        if (currentInput.isNotEmpty() && currentInput.toString() != "0") {
            if (currentInput.startsWith("-")) {
                currentInput.deleteCharAt(0)
            } else {
                currentInput.insert(0, "-")
            }
            tvResult.text = currentInput.toString()
        }
    }

    private fun onBackspacePressed() {
        if (isResultShown) {
            onClearPressed()
            return
        }

        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            tvResult.text = if (currentInput.isEmpty()) "0" else currentInput.toString()
        }
    }

    private fun onClearPressed() {
        currentInput.clear()
        expression.clear()
        lastResult = null
        isResultShown = false
        tvExpression.text = ""
        tvResult.text = "0"
    }

    private fun onEqualsPressed() {
        if (currentInput.isEmpty() && expression.isEmpty()) return

        val fullExpression = StringBuilder(expression).append(currentInput).toString().trim()
        if (fullExpression.isEmpty()) return

        try {
            val result = evaluateExpression(fullExpression)
            tvExpression.text = "$fullExpression ="
            val formatted = formatNumber(result)
            tvResult.text = formatted
            lastResult = result
            isResultShown = true
        } catch (e: ArithmeticException) {
            tvResult.text = e.message ?: "Error"
            isResultShown = true
        } catch (e: Exception) {
            tvResult.text = "Error"
            isResultShown = true
        }
    }

    private fun evaluateExpression(expr: String): Double {
        val tokens = expr.split("\\s+".toRegex()).toMutableList()
        if (tokens.isEmpty()) return 0.0

        // Handle trailing operator
        if (tokens.last() in listOf("+", "−", "×", "÷")) {
            tokens.removeAt(tokens.lastIndex)
        }

        // Pass 1: Multiplication and Division
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            if (token == "×" || token == "÷") {
                val left = tokens[i - 1].toDouble()
                val right = tokens[i + 1].toDouble()
                val res = if (token == "×") {
                    left * right
                } else {
                    if (right == 0.0) throw ArithmeticException("Tidak bisa dibagi 0")
                    left / right
                }
                tokens[i - 1] = res.toString()
                tokens.removeAt(i) // remove operator
                tokens.removeAt(i) // remove right operand
                i--
            } else {
                i++
            }
        }

        // Pass 2: Addition and Subtraction
        var result = tokens[0].toDouble()
        var j = 1
        while (j < tokens.size) {
            val op = tokens[j]
            val next = tokens[j + 1].toDouble()
            when (op) {
                "+" -> result += next
                "−" -> result -= next
            }
            j += 2
        }

        return result
    }

    private fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0 && !value.isInfinite() && !value.isNaN()) {
            try {
                BigDecimal.valueOf(value).toBigInteger().toString()
            } catch (e: Exception) {
                value.toLong().toString()
            }
        } else {
            val df = DecimalFormat("#.########")
            df.format(BigDecimal.valueOf(value).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros())
        }
    }
}
