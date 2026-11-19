#!/usr/bin/env python3
"""
CLI tester for calculator logic
"""

def evaluate(expr: str):
    tokens = expr.split()
    if not tokens:
        return 0.0

    if tokens[-1] in ["+", "−", "-", "×", "*", "÷", "/"]:
        tokens.pop()

    # Pass 1: Multiplication and Division
    i = 0
    while i < len(tokens):
        t = tokens[i]
        if t in ["×", "*", "÷", "/"]:
            left = float(tokens[i - 1])
            right = float(tokens[i + 1])
            if t in ["÷", "/"]:
                if right == 0:
                    raise ZeroDivisionError("Tidak bisa dibagi 0")
                res = left / right
            else:
                res = left * right
            tokens[i - 1] = str(res)
            tokens.pop(i)
            tokens.pop(i)
            i -= 1
        else:
            i += 1

    # Pass 2: Addition and Subtraction
    result = float(tokens[0])
    j = 1
    while j < len(tokens):
        op = tokens[j]
        val = float(tokens[j + 1])
        if op == "+":
            result += val
        elif op in ["−", "-"]:
            result -= val
        j += 2
    return result

if __name__ == "__main__":
    tests = [
        "12 + 5",
        "10 - 4 × 2",
        "100 ÷ 4 + 25",
        "8.5 + 1.5"
    ]
    for t in tests:
        print(f"{t} = {evaluate(t)}")
