package com.tchepannou.blog.service.impl;

import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.rr.PostResponse;
import com.tchepannou.blog.service.GetPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class GetPostServiceImpl implements GetPostService{
    //-- Attributes
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;


    //-- GetPostService overrides
    @Override
    public PostResponse execute(Long id) {
        Post post = postDao.findById(id);
        List<Tag> tags = tagDao.findByPost(id);

        return new PostResponseMapper()
                .withPost(post)
                .withTags(tags)
                .map()
        ;
    }
}
