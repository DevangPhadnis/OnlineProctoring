package com.example.OnlineProctoring.service;


import com.example.OnlineProctoring.models.Attachment;
import com.example.OnlineProctoring.models.AttachmentDTO;

public interface AttachmentService {

    public Attachment uploadAttachment(Attachment attachment, byte[] fileDetails);

    public AttachmentDTO fetchAttachmentDetails(Long attachmentId);

    public String generatePreSignedUrl(Long attachmentId);
}
