package com.tchepannou.blog.rr;

import com.wordnik.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

public class PostRequest {
    //-- Attributes
    @NotBlank(message = "missing_title")
    @Max(value = 255)
    private String title;

    @ApiModelProperty(allowableValues = "draft,published")
    @Pattern(regexp = "draft|published", message="bad_value")
    @NotBlank(message = "missing_status")
    private String status;

    private String content;

    @Max(value = 255)
    private String slug;

    private List<String> tags;

    private Date published;

    //-- Getter/Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
