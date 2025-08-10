package es.codeurjc.practica1.service;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.practica1.model.Post;
import es.codeurjc.practica1.utils.ImageUtils;
import jakarta.annotation.PostConstruct;
@Component
public class DatabaseInitializer {


    @Autowired
    private PostService productService;

    @Autowired
	private ImageUtils imageUtils;

    @PostConstruct
    public void init() throws IOException {
        
        // Create some books
        Post post1 = new Post("Cuerda","resistente");
        saveProductWithURLImage(post1,"selfie.jpg");
    }

    private Post saveProductWithURLImage(Post product,String image) throws IOException {
		product.addImageFile(imageUtils.localImageToBlob("images/" + image));
		return productService.save(product);
	}
}