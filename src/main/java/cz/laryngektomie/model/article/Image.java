package cz.laryngektomie.model.article;

import cz.laryngektomie.model.EntityBase;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;

@Entity
public class Image extends EntityBase {

    @NotBlank
    private String fileName;

    @NotBlank
    private String fileType;

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] data;

    public Image() {
    }

    public Image(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getEncodeBase64() {
        return Base64.encodeBase64String(data);
    }
}