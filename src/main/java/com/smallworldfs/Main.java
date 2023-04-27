package com.smallworldfs;

import com.smallworldfs.error.GenerateDocumentException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {


    public static void main(String[] args) {
        try {
            System.out.println("Document generation starting, please wait...");
            generateDocuments(args);
            System.exit(0);
        } catch (GenerateDocumentException gd) {
            System.err.println(gd.getMessage());
            if (gd.getStackTrace() != null) {
                System.err.println(Arrays.toString(gd.getStackTrace()));
            }
            System.exit(GenerateDocumentException.STATUS);
        }
    }

    private static void generateDocuments(String[] args) {
        validateInputs(args);

        String url = args[0];
        String user = args[1];
        String password = args[2];
        String path = args[3];

        LocalDate initDate = getDateFromString(args[4]);
        LocalDate endDate = getDateFromString(args[5]);

        // Validation.validateInputDates(initDate, endDate);

        FileUtils.createDirectory(path);

        DBConnection dbConnection = new DBConnection(url, user, password);

        Set<Long> inserted = new HashSet<>();
        Map<Long, Integer> filesPerMtn = new HashMap<>();

        String sql = "SELECT clientid, picture, mtn, pictureid, fileName " +
                "FROM EIS.CLI_ID_PICTURE p " +
                "JOIN EIS.TXN_TRANSACTION t on (p.CLIENTID = t.SENDINGCLIENTID) " +
                "WHERE t.SENDINGDATE BETWEEN ? AND ?" +
                "AND t.SENDINGCOUNTRYID = 4 " +
                "AND p.IDID is not null order by CLIENTID";

        while (initDate.compareTo(endDate) <= 0) {
            LocalDate limitSupDate = initDate.plusDays(1);
            FileUtils.writePicturesToDisk(
                    dbConnection.getDocumentBetweenDates(inserted, sql, initDate, limitSupDate, filesPerMtn), path,
                    filesPerMtn);
            initDate = initDate.plusDays(1);
        }
        System.out.println("Document generation finished .... TOTAL FILES: "+ inserted.size() );
    }

    private static void validateInputs(String[] args) {
        if (args.length != 6) {
            throw new GenerateDocumentException("Invalid number of params, 6 are required: url, user , pass, path, date init, date final");
        }

        Validation.validateInputString(args[0]);
        Validation.validateInputString(args[1]);
        Validation.validateInputString(args[2]);
        Validation.validatePath(args[3]);
        Validation.validateDate(args[4]);
        Validation.validateDate(args[5]);
    }

    private static LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(date, formatter);
    }

}



