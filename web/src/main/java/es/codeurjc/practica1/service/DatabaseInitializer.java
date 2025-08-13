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

        Post post1 = new Post("¡Hola! Soy Paula", "Soy estudiante del doble grado en ingeniería informática e ingeniería de compuradores. \n" + //
                        "        He creado esta página web para que me conozcas mejor y así poder ver el recorrido de aprendizaje que he tenido estos años.\n" + //
                        "        Estoy desarrollando dos Trabajos Fin de Grado, comenzando mis prácticas universitarias e intentado sobrevivir el último año de carrera, ¿quiéres acompañarme en el proceso?");
        // Crear el blob de la imagen
        Blob blob = imageUtils.localImageToBlob("static/images/selfie.jpeg");
        // Crear la entidad PostImage y asociarla al post
        PostImage postImage = new PostImage(blob, post1);

        // Añadir la imagen al post
        post1.getImages().add(postImage);

        // Guardar el post (y la imagen asociada)
        productService.save(post1);

        Post post2 = new Post("Hi! I'm Paula", "I am a student of the double degree in Computer Engineering and Computer Science. " +
        "I created this website so you can know me better and see the learning journey I’ve had over the last years. " +
        "I am currently working on two Final Degree Projects, starting my university internship, and trying to survive my last year of career, Want to join me on the process?");
        // Crear el blob de la imagen
        Blob blob2 = imageUtils.localImageToBlob("static/images/selfie.jpeg");
        // Crear la entidad PostImage y asociarla al post
        PostImage postImage2 = new PostImage(blob2, post2);

        // Añadir la imagen al post
        post2.getImages().add(postImage2);

        // Guardar el post (y la imagen asociada)
        productService.save(post2);    }

}
