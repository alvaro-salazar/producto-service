package com.denkitronik.productoservice.infrastructure.minio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.IOException;
import java.util.UUID;

/**
 * Encapsula todas las operaciones con MinIO (subir y eliminar objetos).
 *
 * Responsabilidades:
 * - Crear el bucket si no existe al iniciar.
 * - Generar un nombre único por archivo para evitar colisiones.
 * - Devolver la URL pública del objeto subido.
 */
@Service
public class MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);

    private final S3Client s3Client;

    @Value("${minio.bucket}")
    private String bucket;

    @Value("${minio.endpoint}")
    private String endpoint;

    public MinioService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Sube un archivo a MinIO y devuelve la URL pública del objeto.
     *
     * @param archivo MultipartFile recibido del controlador REST
     * @return URL del objeto: http://&lt;host&gt;:9000/&lt;bucket&gt;/&lt;objectKey&gt;
     */
    public String subirImagen(MultipartFile archivo) {
        asegurarBucket();

        String objectKey = UUID.randomUUID() + "-" + archivo.getOriginalFilename();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(archivo.getContentType())
                    .build();

            s3Client.putObject(request,
                    RequestBody.fromInputStream(archivo.getInputStream(), archivo.getSize()));

            String url = endpoint + "/" + bucket + "/" + objectKey;
            log.info("Imagen subida a MinIO: {}", url);
            return url;

        } catch (IOException ex) {
            throw new MinioUploadException("No se pudo leer el archivo: " + archivo.getOriginalFilename(), ex);
        } catch (S3Exception ex) {
            throw new MinioUploadException("Error al subir la imagen a MinIO", ex);
        }
    }

    /**
     * Elimina un objeto de MinIO dado su objectKey (último segmento de la URL).
     */
    public void eliminarImagen(String objectKey) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build());
            log.info("Imagen eliminada de MinIO: {}", objectKey);
        } catch (S3Exception ex) {
            log.warn("No se pudo eliminar la imagen de MinIO: {}", objectKey, ex);
        }
    }

    private void asegurarBucket() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            log.info("Bucket creado: {}", bucket);
        }
    }
}
