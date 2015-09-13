package com.tchepannou.blog.service.url;

import com.tchepannou.blog.service.UrlService;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlServiceImplTest {
    UrlService service = new UrlServiceImpl();

    @Test
    public void testUnknown() throws Exception {
        assertThat(service.embedUrl("https://foo.bar")).isNull();
    }

    @Test
    public void testYouTube() throws Exception {
        assertThat(service.embedUrl("https://www.youtube.com/watch?v=sOkitpK-wKI&list=UU1yBKRuGpC1tSM73A0ZjYjQ&index=16"))
                .isEqualTo("https://youtube.com/embed/sOkitpK-wKI");

        assertThat(service.embedUrl("http://www.youtube.com/watch?v=0zM4nApSvMg"))
                .isEqualTo("https://youtube.com/embed/0zM4nApSvMg");

        assertThat(service.embedUrl("http://www.youtube.com/watch?v=0zM4nApSvMg"))
                .isEqualTo("https://youtube.com/embed/0zM4nApSvMg");

        assertThat(service.embedUrl("http://www.youtube.com/embed/0zM4nApSvMg?rel=0"))
                .isEqualTo("https://youtube.com/embed/0zM4nApSvMg");
    }

    @Test
    public void testVimeo() throws Exception {
        assertThat(service.embedUrl("https://vimeo.com/132024990"))
                .isEqualTo("https://player.vimeo.com/video/132024990");
    }

    @Test
    public void testInsidesoccer() throws Exception {
        assertThat(service.embedUrl("http://www.insidesoccer.com/?isf=video&id=753202"))
                .isEqualTo("https://www.insidesoccer.com/is-oembed-web/video/iframe?id=753202");
    }
}
