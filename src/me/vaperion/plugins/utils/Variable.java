package me.vaperion.plugins.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Variable {

    private String path, name, scope, type, fallbackValue;
    private Map<String, String> resultsMap;

    public String getFormatted() {
        return "{{" + name + "}}";
    }

    public String getStringForResult(Object result) {
        if (result == null) return fallbackValue;
        for (Map.Entry<String, String> entry : resultsMap.entrySet()) {
            String condition = entry.getKey(), text = entry.getValue();
            if (!testCondition(result, condition)) continue;
            return text;
        }
        return fallbackValue;
    }

    private boolean testCondition(Object result, String condition) {
        if (condition.length() < 2) return false;
        char operation = condition.charAt(0);
        String value = condition.substring(1);
        switch (operation) {
            case '=': {
                return String.valueOf(result).equalsIgnoreCase(value);
            }
            case '>': {
                long firstValue = 0L, secondValue = 0L;
                try {
                    firstValue = Long.parseLong(value);
                    secondValue = Long.parseLong(String.valueOf(result));
                } catch (Exception ex) {return false;}
                return secondValue > firstValue;
            }
            case '<': {
                long firstValue = 0L, secondValue = 0L;
                try {
                    firstValue = Long.parseLong(value);
                    secondValue = Long.parseLong(String.valueOf(result));
                } catch (Exception ex) {return false;}
                return secondValue < firstValue;
            }
        }
        return false;
    }

    public boolean isVar(String var) {
        return name.equalsIgnoreCase(var) && path.endsWith(var.toLowerCase());
    }

}
