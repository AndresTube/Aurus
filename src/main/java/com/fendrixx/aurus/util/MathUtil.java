package com.fendrixx.aurus.util;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.concurrent.ConcurrentHashMap;

public class MathUtil {

    private static final ConcurrentHashMap<String, Expression> EXPRESSION_CACHE = new ConcurrentHashMap<>();

    public static float normalizeAngle(float angle) {
        while (angle <= -180)
            angle += 360;
        while (angle > 180)
            angle -= 360;
        return angle;
    }

    public static Expression compile(String formula) {
        return EXPRESSION_CACHE.computeIfAbsent(formula, f ->
                new ExpressionBuilder(f).variable("t").build());
    }

    public static double evaluate(String formula, double t) {
        try {
            return compile(formula).setVariable("t", t).evaluate();
        } catch (Exception e) {
            return 0;
        }
    }
}
