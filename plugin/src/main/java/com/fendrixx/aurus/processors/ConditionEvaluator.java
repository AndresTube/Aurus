package com.fendrixx.aurus.processors;

import org.bukkit.entity.Player;
import java.util.List;

public class ConditionEvaluator {

    public static boolean evaluate(Player player, List<String> conditions, ActionProcessor actionProcessor) {
        if (conditions == null || conditions.isEmpty()) return true;

        for (String condition : conditions) {
            if (!evaluateSingle(player, condition, actionProcessor)) {
                return false;
            }
        }
        return true;
    }

    private static boolean evaluateSingle(Player player, String condition, ActionProcessor actionProcessor) {
        condition = condition.trim();

        if (condition.toLowerCase().startsWith("permission:")) {
            String perm = condition.substring(11).trim();
            return player.hasPermission(perm);
        }

        String parsed = actionProcessor.parse(player, condition);

        String[] operators = {"==", "!=", ">=", "<=", ">", "<"};
        for (String op : operators) {
            int index = parsed.indexOf(op);
            if (index != -1) {
                String left = parsed.substring(0, index).trim();
                String right = parsed.substring(index + op.length()).trim();

                try {
                    double leftNum = Double.parseDouble(left);
                    double rightNum = Double.parseDouble(right);
                    return switch (op) {
                        case "==" -> leftNum == rightNum;
                        case "!=" -> leftNum != rightNum;
                        case ">=" -> leftNum >= rightNum;
                        case "<=" -> leftNum <= rightNum;
                        case ">" -> leftNum > rightNum;
                        case "<" -> leftNum < rightNum;
                        default -> false;
                    };
                } catch (NumberFormatException e) {
                    return switch (op) {
                        case "==" -> left.equalsIgnoreCase(right);
                        case "!=" -> !left.equalsIgnoreCase(right);
                        default -> false;
                    };
                }
            }
        }

        return Boolean.parseBoolean(parsed);
    }
}
