package com.tchepannou.blog.service;

import com.tchepannou.blog.rr.PostCollectionResponse;

public interface GetPostListService extends Command<GetPostListService.Request, PostCollectionResponse> {

    //-- Inner class
    class Request {
        private long blogId;
        private int limit;
        private int offset;

        public Request(long blogId, int limit, int offset) {
            this.blogId = blogId;
            this.limit = limit;
            this.offset = offset;
        }

        public long getBlogId() {
            return blogId;
        }

        public int getLimit() {
            return limit;
        }

        public int getOffset() {
            return offset;
        }
    }
}
