package com.fendrixx.aurus.util;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.concurrent.ConcurrentHashMap;

public final class MathUtil {

    private static final ConcurrentHashMap<String, Expression> EXPRESSION_CACHE = new ConcurrentHashMap<>();

    private MathUtil() {
    }

    public static float normalizeAngle(float angle) {
        while (angle <= -180)
            angle += 360;
        while (angle > 180)
            angle -= 360;
        return angle;
    }

    /**
     * Compiles a formula safely. Invalid formulas are ignored instead of
     * preventing the whole menu from opening.
     */
    public static Expression compile(String formula) {
        if (formula == null || formula.isBlank()) {
            return null;
        }

        String key = formula.trim();
        Expression cached = EXPRESSION_CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        try {
            Expression expression = new ExpressionBuilder(key)
                    .variable("t")
                    .build();
            EXPRESSION_CACHE.putIfAbsent(key, expression);
            return expression;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public static double evaluate(String formula, double t) {
        try {
            Expression expression = compile(formula);
            if (expression == null) {
                return 0;
            }
            return expression.setVariable("t", t).evaluate();
        } catch (RuntimeException ignored) {
            return 0;
        }
    }
}
