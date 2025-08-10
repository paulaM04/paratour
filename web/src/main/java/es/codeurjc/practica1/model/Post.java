package es.codeurjc.practica1.model;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String text;
    private boolean deletedPost;
    private List<Blob> imageFile;

    public Post() {
    }

    public Post(String title, String description) { // Blob img
        this.title = title;
        this.text = description;
        this.deletedPost = false;
        this.imageFile = new ArrayList<>();
        // this.imageFile = img;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public boolean isDeletedPost() {
        return deletedPost;
    }
    public void setDeletedPost(boolean deletedPost) {
        this.deletedPost = deletedPost;
    }

    public List<Blob> getImageFile() {
        return imageFile;
    }
    public void addImageFile(Blob imageFile) {
        if (this.imageFile == null) {
            this.imageFile = new ArrayList<>();
        }
        this.imageFile.add(imageFile);
    }


}
