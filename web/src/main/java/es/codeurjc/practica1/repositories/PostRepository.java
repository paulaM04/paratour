package es.codeurjc.practica1.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.practica1.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    // Buscar por título
    List<Post> findByTitle(String title);

    // Buscar por si el post está borrado o no
    List<Post> findByDeletedPost(boolean deletedPost);
}
