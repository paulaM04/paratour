package es.codeurjc.practica1.controller;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
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
            model.addAttribute("post", post);

            if (post == null) {
                model.addAttribute("message", "No se encontró el post" );
                return "error";
            }

            // Accede a la imagen del primer PostImage (si existe)
            Blob blob = null;
            if (post.getImages() != null && !post.getImages().isEmpty() && post.getImages().get(0).getImage() != null) {
                blob = post.getImages().get(0).getImage();
            }

            if (blob == null) {
                model.addAttribute("message", "No hay imagen para este post");
                return "error";
            }
            long spanishId = (post.getId() % 2 == 1) ? post.getId() : post.getId()-1;
            model.addAttribute("spanishId", spanishId);
            // Determina el idioma según el id
            String currentLang = (post.getId() % 2 == 1) ? "Español" : "English";
            model.addAttribute("currentLang", currentLang);

            // Calcula el id del post en inglés. Si es impar está en ESPAÑOL, si es par en
            // INGLÉS
            long englishId = (post.getId() % 2 == 1) ? post.getId() + 1 : post.getId();
            model.addAttribute("englishId", englishId);

            boolean isSpanish = currentLang.equals("Español");
            // Textos traducidos
            model.addAttribute("txtUniversidad", isSpanish ? "Universidad" : "University");
            model.addAttribute("txtTFGComputadores", isSpanish ? "TFG Computadores" : "Computer Engineering Thesis");
            model.addAttribute("txtTFGInformatica", isSpanish ? "TFG Informática" : "Computer Science Thesis");
            model.addAttribute("txtProyectos", isSpanish ? "Proyectos" : "Projects");
            model.addAttribute("txtHobbies", isSpanish ? "Hobbies" : "Hobbies");
            model.addAttribute("txtBaile", isSpanish ? "Baile" : "Dance");
            model.addAttribute("txtIngles", isSpanish ? "Inglés" : "English");
            model.addAttribute("txtPatinaje", isSpanish ? "Patinaje" : "Skating");
            model.addAttribute("txtExperiencia", isSpanish ? "Experiencia" : "Experience");
            model.addAttribute("txtMonitora", isSpanish ? "Monitora" : "Monitor");
            model.addAttribute("txtProfe", isSpanish ? "Profe" : "Teacher");

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
            Post post = op.get();
            Resource image;
            try {
                if (post.getImages() != null && !post.getImages().isEmpty()
                        && post.getImages().get(0).getImage() != null) {
                    image = new InputStreamResource(post.getImages().get(0).getImage().getBinaryStream());
                } else {
                    throw new Exception("No image found");
                }
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
@GetMapping("/post/{id}/")
public String downloadInformation(@PathVariable long id, Model model) {

    Post post = postService.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post no encontrado"));

    boolean isSpanish = (id % 2 == 1);
    String currentLang = isSpanish ? "Español" : "English";
    model.addAttribute("currentLang", currentLang);

    long spanishId = isSpanish ? id : id - 1;
    long englishId = isSpanish ? id + 1 : id;
    model.addAttribute("spanishId", spanishId);
    model.addAttribute("englishId", englishId);

    // El post que se va a mostrar es el mismo que se ha pedido
    model.addAttribute("post", post);

    // Textos traducidos
    model.addAttribute("txtUniversidad", isSpanish ? "Universidad" : "University");
    model.addAttribute("txtTFGComputadores", isSpanish ? "TFG Computadores" : "Computer Engineering Thesis");
    model.addAttribute("txtTFGInformatica", isSpanish ? "TFG Informática" : "Computer Science Thesis");
    model.addAttribute("txtProyectos", isSpanish ? "Proyectos" : "Projects");
    model.addAttribute("txtHobbies", isSpanish ? "Hobbies" : "Hobbies");
    model.addAttribute("txtBaile", isSpanish ? "Baile" : "Dance");
    model.addAttribute("txtIngles", isSpanish ? "Inglés" : "English");
    model.addAttribute("txtPatinaje", isSpanish ? "Patinaje" : "Skating");
    model.addAttribute("txtExperiencia", isSpanish ? "Experiencia" : "Experience");
    model.addAttribute("txtMonitora", isSpanish ? "Monitora" : "Monitor");
    model.addAttribute("txtProfe", isSpanish ? "Profe" : "Teacher");

    return "home";
}
}
