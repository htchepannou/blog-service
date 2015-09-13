package com.tchepannou.blog.mapper;

import com.tchepannou.blog.client.v1.AttachmentResponse;
import com.tchepannou.blog.domain.Attachment;
import com.tchepannou.blog.service.UrlService;

public class AttachmentResponseMapper {
    private Attachment attachment;
    private UrlService urlService;

    public AttachmentResponse build (){
        AttachmentResponse response = new AttachmentResponse();
        response.setId(attachment.getId());
        response.setContentLength(attachment.getContentLength());
        response.setContentType(attachment.getContentType());
        response.setDescription(attachment.getDescription());
        response.setDurationSeconds(attachment.getDurationSeconds());
        response.setHeight(attachment.getHeight());
        response.setName(attachment.getName());
        response.setOembed(attachment.getOembed());
        response.setThumbnailUrl(attachment.getThumbnailUrl());
        response.setUrl(attachment.getUrl());
        response.setWidth(attachment.getWidth());
        response.setXvideoId(attachment.getXvideoId());

        if (Boolean.TRUE.equals(response.getOembed())){
            response.setEmbedUrl(urlService.embedUrl(attachment.getUrl()));
        }
        return response;
    }

    public AttachmentResponseMapper withAttachment (Attachment attachment){
        this.attachment = attachment;
        return this;
    }

    public AttachmentResponseMapper withUrlService(UrlService urlService) {
        this.urlService = urlService;
        return this;
    }
}
