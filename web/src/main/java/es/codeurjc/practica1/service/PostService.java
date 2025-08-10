package es.codeurjc.practica1.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.practica1.model.Post;
import es.codeurjc.practica1.repositories.PostRepository;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Optional<Post> findById(long id) {
        Optional<Post> post = postRepository.findById(id);
        return post;
    }

    public List<Post> findAllById(List<Long> productIds) {
        return postRepository.findAllById(productIds);
    }

    public List<Post> findByDeletePost(Boolean deletedPost) {
        return postRepository.findByDeletedPost(deletedPost);
    }

    public boolean exist(long id) {
        return postRepository.findById(id).isPresent();
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Post save(Post product) {
        return postRepository.save(product);
    }

    public Post save(Post product, List<Long> selectedUsers) throws IOException {        
        return postRepository.save(product);
    }

    public boolean delete(Post post) {
        if (postRepository.existsById(post.getId())) {
            postRepository.delete(post);
            return true;
        }
        return false;
    }

}
