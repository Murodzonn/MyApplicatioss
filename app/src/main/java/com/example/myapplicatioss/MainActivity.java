package com.example.myapplicatioss;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    // Views
    private TextView tvExpression, tvResult;
    private StringBuilder expression = new StringBuilder();
    private boolean lastNumeric;
    private boolean stateError;
    private boolean lastDot;
    private boolean lastOperator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        setNumberListeners();
        setOperatorListeners();
        setUtilityListeners();
    }

    // Установка обработчиков для числовых кнопок
    private void setNumberListeners() {
        int[] numberButtons = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot};
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String text = btn.getText().toString();

                if (stateError) {
                    expression.setLength(0); // Очистка после ошибки
                    stateError = false;
                    tvResult.setText("0");
                }

                // Проверка для точки (только одна точка на число)
                if (text.equals(".")) {
                    if (lastDot) return;
                    lastDot = true;
                } else {
                    lastDot = false;
                }

                expression.append(text);
                tvExpression.setText(expression.toString());
                lastNumeric = true; // Обновляем состояние для последней цифры
                lastOperator = false; // Оператор не последний
            }
        };
        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    // Установка обработчиков для операторов
    private void setOperatorListeners() {
        int[] operatorButtons = {R.id.btnAdd, R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide, R.id.btnOpenBracket, R.id.btnCloseBracket};
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                String operator = btn.getText().toString();

                // Обработка для операторов, чтобы не было дублирования
                if (operator.equals("(")) {
                    // Если перед открывающей скобкой стоит число или закрывающая скобка, добавляем умножение
                    if (lastNumeric || (expression.length() > 0 && expression.charAt(expression.length() - 1) == ')')) {
                        expression.append("*");
                    }
                    expression.append("(");
                } else if (operator.equals(")")) {
                    // Закрывающая скобка добавляется, если перед ней стоит число или другая закрывающая скобка
                    if (lastNumeric || (expression.length() > 0 && expression.charAt(expression.length() - 1) == ')')) {
                        expression.append(")");
                    }
                } else {
                    // Проверка, чтобы не было двух операторов подряд
                    if ((lastNumeric || expression.length() > 0 && expression.charAt(expression.length() - 1) == ')') && !lastOperator) {
                        expression.append(operator);
                    }
                }

                tvExpression.setText(expression.toString());
                lastNumeric = false;
                lastOperator = !operator.equals("(") && !operator.equals(")");
                lastDot = false; // Сбрасываем флаг точки
            }
        };
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    // Обработчики для AC и C
    private void setUtilityListeners() {
        // Полный сброс (AC)
        findViewById(R.id.btnAC).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression.setLength(0);
                tvExpression.setText("");
                tvResult.setText("0");
                lastNumeric = false;
                stateError = false;
                lastDot = false;
                lastOperator = false;
            }
        });

        // Удаление последнего символа (C)
        findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expression.length() > 0) {
                    expression.deleteCharAt(expression.length() - 1);
                    tvExpression.setText(expression.toString());

                    // Проверка последнего символа после удаления
                    if (expression.length() > 0) {
                        char lastChar = expression.charAt(expression.length() - 1);
                        lastNumeric = Character.isDigit(lastChar);
                        lastOperator = !lastNumeric && (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/');
                        lastDot = lastChar == '.';
                    } else {
                        lastNumeric = false;
                        lastOperator = false;
                        lastDot = false;
                    }
                }
            }
        });

        // Кнопка "=" для вычисления результата
        findViewById(R.id.btnEquals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateResult();
            }
        });
    }

    // Вычисление результата
    private void calculateResult() {
        try {
            if (expression.length() == 0) return;
            Expression exp = new ExpressionBuilder(expression.toString()).build();
            double result = exp.evaluate();
            tvResult.setText(String.valueOf(result));
            lastNumeric = true; // После результата можно вводить новое число
        } catch (Exception e) {
            tvResult.setText("Ошибка");
            stateError = true;
            lastNumeric = false;
        }
    }
}
