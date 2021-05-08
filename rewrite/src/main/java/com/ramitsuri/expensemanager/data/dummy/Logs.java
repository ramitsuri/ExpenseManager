package com.ramitsuri.expensemanager.data.dummy;

import com.ramitsuri.expensemanager.entities.Log;

import java.util.ArrayList;
import java.util.List;

public class Logs {
    public static long BASE_DATE_TIME = 1565818852014L; // Wed Aug 14 2019 17:40:52
    public static long ONE_DAY = 86400000;

    public static List<Log> getLogs() {
        List<Log> logs = new ArrayList<>();

        Log log = new Log();
        log.setTime(BASE_DATE_TIME - 10 * ONE_DAY);
        log.setType("Type1");
        log.setResult("SUCCESS");
        log.setMessage(null);
        log.setIsAcknowledged(false);

        log = new Log();
        log.setTime(BASE_DATE_TIME - 5 * ONE_DAY);
        log.setType("Type2");
        log.setResult("SUCCESS");
        log.setMessage(null);
        log.setIsAcknowledged(true);

        log = new Log();
        log.setTime(BASE_DATE_TIME - 6 * ONE_DAY);
        log.setType("Type1");
        log.setResult("SUCCESS");
        log.setMessage(null);
        log.setIsAcknowledged(false);

        log = new Log();
        log.setTime(BASE_DATE_TIME + 10 * ONE_DAY);
        log.setType("Type3");
        log.setResult("FAILURE");
        log.setMessage(null);
        log.setIsAcknowledged(false);

        return logs;
    }

    public static List<Log> getUnacknowledgedLogs() {
        List<Log> logs = new ArrayList<>();
        for (Log log : getLogs()) {
            if (!log.isAcknowledged()) {
                logs.add(log);
            }
        }
        return logs;
    }
}
