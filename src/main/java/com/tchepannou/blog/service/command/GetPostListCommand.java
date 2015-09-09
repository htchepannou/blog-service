package com.tchepannou.blog.service.command;

import com.google.common.collect.Multimap;
import com.tchepannou.blog.client.v1.PostCollectionResponse;
import com.tchepannou.blog.dao.AttachmentDao;
import com.tchepannou.blog.dao.PostDao;
import com.tchepannou.blog.dao.TagDao;
import com.tchepannou.blog.domain.Attachment;
import com.tchepannou.blog.domain.Post;
import com.tchepannou.blog.domain.Tag;
import com.tchepannou.blog.mapper.PostCollectionResponseMapper;
import com.tchepannou.blog.service.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class GetPostListCommand extends AbstractCommand<Void, PostCollectionResponse> {
    @Autowired
    private PostDao postDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private AttachmentDao attachmentDao;

    //-- Public
    @Override
    protected PostCollectionResponse doExecute(Void request, CommandContext context) {
        final List<Post> posts = postDao.findByBlog(context.getBlogId(), context.getLimit(), context.getOffset());

        final List<Long> postIds = posts.stream()
                .map(Post::getId)
                .collect(Collectors.toList());

        final Multimap<Long, Tag> tags = tagDao.findByPosts(postIds);

        final Multimap<Long, Attachment> attachments = attachmentDao.findByPosts(postIds);

        return new PostCollectionResponseMapper()
                .withPosts(posts)
                .withTags(tags)
                .withAttachments(attachments)
                .map();
    }

    @Override
    protected String getEventName() {
        return null;
    }
}
