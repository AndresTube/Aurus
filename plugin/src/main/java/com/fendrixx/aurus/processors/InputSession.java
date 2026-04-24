package com.fendrixx.aurus.processors;

public class InputSession {
    private final String variableName;
    private final String regex;
    private final int minLength;
    private final int maxLength;
    private final String errorMessage;

    public InputSession(String variableName, String regex, int minLength, int maxLength, String errorMessage) {
        this.variableName = variableName;
        this.regex = regex != null ? regex : ".*";
        this.minLength = minLength;
        this.maxLength = maxLength > 0 ? maxLength : Integer.MAX_VALUE;
        this.errorMessage = (errorMessage != null && !errorMessage.isEmpty()) ? errorMessage : "<red>Invalid input format.";
    }

    public String getVariableName() { return variableName; }
    public String getErrorMessage() { return errorMessage; }

    public boolean validate(String input) {
        if (input == null) return false;
        if (input.length() < minLength || input.length() > maxLength) return false;
        return input.matches(regex);
    }
}
