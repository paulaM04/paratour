package es.codeurjc.practica1.service;

import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.practica1.model.Post;
import es.codeurjc.practica1.utils.ImageUtils;
import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    @Autowired
    private PostService postService;

    @Autowired
    private ImageUtils imageUtils;

    @PostConstruct  
    public void initDatabase() {
          try {
            // Example of how to save a product with an image from a URL.
            String image = "selfie.jpg"; // Replace with your actual image file name.

            Post post = new Post("Sample Product", "This is a sample product description.", 19.99, 100,
                    "Sample Provider");
            System.out.println("Saving product: " + post.getTitle());
            // Save the product with the image from the URL.
            Post savedPost = saveProductWithURLImage(post, image);
            postService.save(savedPost);

            if (savedPost != null) {
                System.out.println("Product saved successfully: " + savedPost.getTitle());
            } else {
                System.out.println("Failed to save product.");
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print error in console for debugging.
        }
    }

    private Post saveProductWithURLImage(Post post, String image) {
        try {
            // GitHub RAW URL to access the image.
            String imageUrl = "https://raw.githubusercontent.com/paulaM04/personal-web/main/web/images/" + image;

            // Download the image from the URL.
            InputStream imageStream = new URL(imageUrl).openStream();
            byte[] imageBytes = imageStream.readAllBytes(); // Java 9+

            // Convert the byte[] into a Blob.
            Blob imageBlob = new SerialBlob(imageBytes);
            post.addImageFile(imageBlob);

            // Save the product with the image.
            return postService.save(post);
        } catch (Exception e) {
            e.printStackTrace(); // Print error in console for debugging.
            return null; // Or handle with a custom exception.
        }
    }

}