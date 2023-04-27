package com.smallworldfs.bd.model;

public class TransferDocument {

    private byte[] picture;
    private Long mtn;
    private String fileName;
    private Long pictureId;

    private String senderFileName;

    public TransferDocument() {

    }

    public TransferDocument(byte[] picture, Long mtn, String fileName, Long pictureId) {
        this.picture = picture;
        this.mtn = mtn;
        this.fileName = fileName;
        this.pictureId = pictureId;

    }


    public byte[] getPicture() {
        return this.picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public Long getMtn() {
        return this.mtn;
    }

    public void setMtn(Long mtn) {
        this.mtn = mtn;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getPictureId() {
        return pictureId;
    }

    public void setPictureId(Long pictureId) {
        this.pictureId = pictureId;
    }

    public String getSenderFileName() {
        return senderFileName;
    }

    public void setSenderFileName(Integer numberOfDocuments) {
        this.senderFileName = this.mtn + "_" + numberOfDocuments + "." + getFileExtension();
    }

    private String getFileExtension() {

        if (fileName.lastIndexOf(".") == -1 || fileName.lastIndexOf(".") == 0) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
