package com.smallworldfs;

import com.smallworldfs.bd.model.TransferDocument;
import com.smallworldfs.error.GenerateDocumentException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private static int numS3;



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
                Long originalId = resultSet.getLong("originalId");
                Long pictureId = resultSet.getLong("pictureId");
                if (!inserted.contains(pictureId)) {
                    count++;
                    TransferDocument transferDocument = new TransferDocument();
                    transferDocument.setPictureId(pictureId);
                    transferDocument.setFileName(resultSet.getString("fileName"));

                    if(resultSet.getBytes("picture") == null) {
                            numS3++;
                            System.out.println("ID_PICTURE con valor PICTURE a null: "+pictureId);
                            continue;
                            //Long id =  originalId != null ? originalId : pictureId;
                            // transferDocument.setPicture(getDocumentFromS3(id));
                    } else {
                        transferDocument.setPicture(resultSet.getBytes("picture"));
                    }

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

    public byte[] getDocumentFromS3(Long id) throws IOException {
        System.out.println("Log: obteniendo imagen del s3: "+id);
        String sUrl = "http://document-management-service.dev.swfs.cloud/documents/CLIENT_ID_PICTURE/"+id;
        URL url = new URL(sUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }

    public static int getNumS3() {
        return numS3;
    }

    public static void setNumS3(int numS3) {
        DBConnection.numS3 = numS3;
    }
}