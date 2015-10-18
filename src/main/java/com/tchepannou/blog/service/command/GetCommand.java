package com.tchepannou.blog.service.command;

import com.google.common.collect.Multimap;
import com.tchepannou.blog.client.v1.PostResponse;
import com.tchepannou.blog.dao.AttachmentDao;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostResponseMapper;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class GetCommand extends AbstractCommand<Long, PostResponse> {
    //-- Attributes
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private AttachmentDao attachmentDao;

    //-- AbstractCommand overrides
    @Override
    public PostResponse doExecute(Long id, CommandContext context) {
        Post post = postDao.findByIdByBlog(id, context.getBlogId());
        List<Tag> tags = tagDao.findByPost(id);
        Multimap<Long, Long> attachmentIds = attachmentDao.findIdsByPosts(Collections.singleton(id));

        return new PostResponseMapper()
                .withPost(post)
                .withTags(tags)
                .withAttachmentIds(attachmentIds.get(id))
                .map()
        ;
    }
}
