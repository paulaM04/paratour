package es.codeurjc.practica1.service;

import java.io.IOException;
import java.sql.Blob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.practica1.model.Post;
import es.codeurjc.practica1.model.PostImage;
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
        Post post1 = new Post("Cuerda", "resistente");
        // Crear el blob de la imagen
        Blob blob = imageUtils.localImageToBlob("static/images/selfie.jpeg");
        // Crear la entidad PostImage y asociarla al post
        PostImage postImage = new PostImage(blob, post1);

        // Añadir la imagen al post
        post1.getImages().add(postImage);

        // Guardar el post (y la imagen asociada)
        productService.save(post1);

        System.out.println("IMGEEEEEEN " + post1.getImages().get(0).getImage());
    }

}
