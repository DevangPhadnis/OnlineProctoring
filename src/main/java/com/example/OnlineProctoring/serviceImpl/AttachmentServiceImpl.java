package com.example.OnlineProctoring.serviceImpl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.example.OnlineProctoring.models.Attachment;
import com.example.OnlineProctoring.models.AttachmentDTO;
import com.example.OnlineProctoring.repository.AttachmentRepository;
import com.example.OnlineProctoring.service.AttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    private final AttachmentRepository attachmentRepository;

    private final AmazonS3 amazonS3;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, AmazonS3 amazonS3) {
        this.attachmentRepository = attachmentRepository;
        this.amazonS3 = amazonS3;
    }

    @Override
    public Attachment uploadAttachment(Attachment attachment, byte[] fileDetails) {
        try {
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + "_" + System.currentTimeMillis() + attachment.getFileExtension();
            attachment.setFileName(fileName);
            attachmentRepository.save(attachment);
            ObjectMetadata metaData = new ObjectMetadata();
            metaData.setContentLength(Long.parseLong(attachment.getContentSize()));
            metaData.setContentType(attachment.getContentType());
            this.amazonS3.putObject(new PutObjectRequest(bucketName, fileName, new ByteArrayInputStream(fileDetails), metaData));

            return attachment;
        } catch (Exception e) {
            logger.error("Error Found", e);
            return null;
        }
    }

    @Override
    public AttachmentDTO fetchAttachmentDetails(Long attachmentId) {
        try {
            if(attachmentId != null) {
                Optional<Attachment> attachment = attachmentRepository.findById(attachmentId);
                if(attachment.isPresent()) {
                    String fileName = attachment.get().getFileName();
                    S3Object s3Object = amazonS3.getObject(bucketName, fileName);
                    S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();

                    byte[] fileContent = IOUtils.toByteArray(s3ObjectInputStream);

                    AttachmentDTO attachmentDTO = new AttachmentDTO();
                    attachmentDTO.setOriginalFileName(attachment.get().getOriginalFileName());
                    attachmentDTO.setContentSize(attachment.get().getContentSize());
                    attachmentDTO.setContentType(attachment.get().getContentType());
                    attachmentDTO.setFileExtension(attachment.get().getFileExtension());
                    attachmentDTO.setCreatedDate(attachment.get().getCreatedDate());
                    attachmentDTO.setFileBytes(fileContent);

                    return attachmentDTO;
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error Found", e);
            return null;
        }
    }

    @Override
    public String generatePreSignedUrl(Long attachmentId) {
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5);
        try {
            if(attachmentId != null) {
                Optional<Attachment> attachment = attachmentRepository.findById(attachmentId);
                if(attachment.isPresent()) {
                    GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName
                            , attachment.get().getFileName())
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);

                    return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        } catch (Exception e) {
            logger.error("Error Found", e);
            return null;
        }
    }
}
