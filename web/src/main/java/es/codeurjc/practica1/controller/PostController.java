package es.codeurjc.practica1.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Blob;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.practica1.model.Post;
import es.codeurjc.practica1.service.PostService;


@Controller
public class PostController {

    @Autowired
	private PostService postService;

@GetMapping("/")
public String home(Model model) {
    try {
        Post post = postService.findById(1).orElse(null);

        if (post == null) {
            model.addAttribute("message", "No se encontró el post con id=1");
            return "error";
        }

        Path imagePath = Path.of("src/main/resources/static/images/selfie.jpeg");
        byte[] imageBytes = Files.readAllBytes(imagePath);
        Blob imageBlob = new SerialBlob(imageBytes);

        post.addImageFile(imageBlob);
        postService.save(post);

        model.addAttribute("posts", postService.findById(1));
        return "home";

    } catch (Exception e) {
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}

}
