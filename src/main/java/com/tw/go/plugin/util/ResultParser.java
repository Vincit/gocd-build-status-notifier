package com.tw.go.plugin.util;

import java.util.*;

public class ResultParser {

    private final String statusPrefix;
    private final String checkboxTrueValue;

    public ResultParser(String statusPrefix, String checkboxTrueValue) {
        this.statusPrefix = statusPrefix;
        this.checkboxTrueValue = checkboxTrueValue;
    }

    public Set<String> toSet(Map<String, String> configMap) {
        if (configMap != null) {
            Set<String> statuses = new HashSet<String>();
            for (Map.Entry<String, String> configEntry : configMap.entrySet()) {
                if (configEntry.getKey().startsWith(statusPrefix)) {
                    String resultName = configEntry.getKey().replace(statusPrefix, "");
                    String isResultOn = configEntry.getValue();

                    if (checkboxTrueValue.equalsIgnoreCase(isResultOn)) {
                        statuses.add(resultName.toUpperCase());
                    }
                }
            }
            return statuses;
        } else {
            return Collections.emptySet();
        }
    }

}
