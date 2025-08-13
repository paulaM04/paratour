package es.codeurjc.practica1.model;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    @Lob // Large Object for storing large text data
    private String text;
    private boolean deletedPost;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    public Post() {
    }

    public Post(String title, String description) { // Blob img
        this.title = title;
        this.text = description;
        this.deletedPost = false;
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

    public void addImage(Blob blob) {
        PostImage postImage = new PostImage(blob, this);
        this.images.add(postImage);
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

    public List<PostImage> getImages() {
        return images;
    }
    
    public void setImages(List<PostImage> images) {
        this.images = images;
    }   

}
