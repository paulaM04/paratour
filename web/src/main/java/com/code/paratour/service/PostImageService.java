// package es.codeurjc.practica1.service;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import es.codeurjc.practica1.modeljj.PostImage;
// import es.codeurjc.practica1.repositories.PostImageRepository;

// @Service
// public class PostImageService {

//     @Autowired
//     private PostImageRepository postImageRepository;

//     public Optional<PostImage> findById(long id) {
//         Optional<PostImage> post = postImageRepository.findById(id);
//         return post;
//     }

//     public List<PostImage> findAllById(List<Long> productIds) {
//         return postImageRepository.findAllById(productIds);
//     }

//     public boolean exist(long id) {
//         return postImageRepository.findById(id).isPresent();
//     }

//     public List<PostImage> findAll() {
//         return postImageRepository.findAll();
//     }

//     public PostImage save(PostImage product) {
//         return postImageRepository.save(product);
//     }

//     public boolean delete(PostImage post) {
//         if (postImageRepository.existsById(post.getId())) {
//             postImageRepository.delete(post);
//             return true;
//         }
//         return false;
//     }

// }