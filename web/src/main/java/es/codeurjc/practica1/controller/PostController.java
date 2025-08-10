package es.codeurjc.practica1.controller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

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

            // Convertimos el Blob a Base64
            Blob blob = post.getImageFile().get(0);
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            String base64Image = Base64.getEncoder().encodeToString(bytes);

            // Añadimos la imagen como cadena Base64
            model.addAttribute("post", post);
            //model.addAttribute("imageBase64", base64Image);

            return "home";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/post/{id}/image")
	public ResponseEntity<Object> downloadImage(@PathVariable long id) throws SQLException, IOException {

		Optional<Post> op = postService.findById(id);

		if (op.isPresent()) {
			Post product = op.get();
			Resource image;
			try {
                image = new InputStreamResource(product.getImageFile().get(0).getBinaryStream());
			} catch (Exception e) {
				ClassPathResource resource = new ClassPathResource("static/no-image.png");
				byte[] imageBytes = resource.getInputStream().readAllBytes();
				return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(imageBytes);
			}
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/jpeg").body(image);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Film not found");
		}
	}
}
