package es.codeurjc.practica1.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;

import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImageUtils {
    public InputStream remoteImageToInputStream(String imageUrl) {
        try {
            Resource image = new UrlResource(imageUrl);
            return image.getInputStream();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al procesar la imagen remota");
        }
    }

    public InputStream localImageToInputStream(String localFilePath) {
        try {
            if (Files.exists(Paths.get(localFilePath))) {
                return Files.newInputStream(Paths.get(localFilePath));
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo no encontrado");
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al procesar la imagen local");
        }
    }

    public InputStream multiPartFileToInputStream(MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            try {
                return image.getInputStream();
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error al procesar la imagen");
            }
        }
        return null;
    }

    public Blob localImageToBlob(String localFilePath){

		File imageFile = new File(localFilePath);
		
		if (imageFile.exists()) {

			try {
				System.out.println("LA IMAGEN EXISTE");
				return BlobProxy.generateProxy(imageFile.toURI().toURL().openStream(), imageFile.length());
			} catch (IOException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ERROR at processing the IMAGE");
			}
		}
		System.out.println("LA IMAGEN NO EXISTE");

		return null;
	}

}
