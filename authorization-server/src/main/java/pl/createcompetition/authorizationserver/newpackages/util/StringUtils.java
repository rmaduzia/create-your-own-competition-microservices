package pl.createcompetition.authorizationserver.newpackages.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtils {

    private static final String TIME_FORMATTER="HH:MM:SS";

    public static String getCurrentTimeStamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMATTER);
        return LocalDateTime.now().format(formatter);
    }




}
