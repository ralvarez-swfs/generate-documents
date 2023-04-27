package com.smallworldfs;

import com.smallworldfs.bd.model.TransferDocument;
import com.smallworldfs.error.GenerateDocumentException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

public class DBConnection {

    private static DataSource dataSource;


    public DBConnection(String url, String user, String password) {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
        dataSource = ds;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


    public List<TransferDocument> getDocumentBetweenDates(Set<Long> inserted, String sql, LocalDate initDate, LocalDate endDate, Map<Long, Integer> filesPerMtn) {

        System.out.println("Creating files for the day: "+ initDate);
        List<TransferDocument> transferredDocuments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(initDate));
            statement.setDate(2, Date.valueOf(endDate));

            ResultSet resultSet = statement.executeQuery();
            int count = 0;
            int discarted = 0;
            while (resultSet.next()) {
                Long pictureId = resultSet.getLong("pictureId");
                if (!inserted.contains(pictureId)) {
                    count++;
                    TransferDocument transferDocument = new TransferDocument();
                    transferDocument.setPictureId(pictureId);
                    transferDocument.setFileName(resultSet.getString("fileName"));
                    transferDocument.setPicture(resultSet.getBytes("picture"));
                    transferDocument.setMtn(resultSet.getLong("mtn"));


                    inserted.add(pictureId);
                    insertMtnInMap(filesPerMtn, transferDocument.getMtn());
                    transferDocument.setSenderFileName(filesPerMtn.get(transferDocument.getMtn()));
                    transferredDocuments.add(transferDocument);
                } else {
                    discarted++;
                }
            }
            System.out.println("Total number of files stored on disk: " + count);
            System.out.println("Total number of discarded files: " + discarted);
        } catch (SQLException sq) {
            System.out.println(sq.getCause());
            throw new GenerateDocumentException("error consulting database: ", sq);
        }

        return transferredDocuments;
    }

    private void insertMtnInMap(Map<Long, Integer> filesPerMtn, Long mtn) {
        if (filesPerMtn.containsKey(mtn)) {
            filesPerMtn.put(mtn, filesPerMtn.get(mtn) + 1);
        } else {
            filesPerMtn.put(mtn, 0);
        }
    }
}