package es.codeurjc.practica1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.practica1.model.Post;
import es.codeurjc.practica1.model.PostImage;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    // Buscar por título
    List<Post> findById(int id);
}
