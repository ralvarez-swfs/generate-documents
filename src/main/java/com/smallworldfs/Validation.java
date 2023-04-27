package com.smallworldfs;

import com.smallworldfs.error.GenerateDocumentException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validation {

    private static Pattern DATE_PATTERN = Pattern.compile("^\\d{2}-\\d{2}-\\d{4}$");

    public static void validateInputString(String inputString) {
        if (inputString == null || inputString.isBlank()) {
            throw new GenerateDocumentException(inputString + " is invalid");
        }
    }

    public static void validatePath(String path) {
        Paths.get(path);
    }

    public static void validateDate(String date) {
        if (date == null || !DATE_PATTERN.matcher(date).matches()) {
            throw new GenerateDocumentException("error validating date: " + date);
        }
    }

    public static void validateInputDates(LocalDate initDate, LocalDate endDate) {
        if (!initDate.isBefore(initDate)) {
            throw new GenerateDocumentException("Wrongs dates");
        }
    }

}
