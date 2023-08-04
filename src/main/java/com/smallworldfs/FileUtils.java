package com.smallworldfs;

import com.smallworldfs.bd.model.TransferDocument;
import com.smallworldfs.error.GenerateDocumentException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class FileUtils {

    private FileUtils() {

    }

    public static Path createDirectory(String path) {
        try {
            Path maybePath = Paths.get(path);

            if (Files.isDirectory(maybePath)) {
                return maybePath;
            }

            return Files.createDirectory(maybePath);
        } catch (IOException e) {
            throw new GenerateDocumentException("error creating directory", e);
        }
    }


    public static void writePicturesToDisk(List<TransferDocument> transferDocuments, String path) {
        for (TransferDocument transferDocument : transferDocuments) {
            String name = path + transferDocument.getSenderFileName();
            try (FileOutputStream outputStream = new FileOutputStream(name)) {
                outputStream.write(transferDocument.getPicture());
            } catch (IOException e) {
                throw new GenerateDocumentException("Error generating the file " + transferDocument.getFileName(), e);
            }

        }
    }


}
