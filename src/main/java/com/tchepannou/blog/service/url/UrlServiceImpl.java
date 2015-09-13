package com.tchepannou.blog.service.url;

import com.tchepannou.blog.service.UrlService;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlServiceImpl implements UrlService {
    //-- Attribute
    public static final String YOUBUTE_PATTERN1 = "youtube\\.com/watch\\?v=([^#&?]*)";
    public static final String YOUBUTE_PATTERN2 = "youtube\\.com/embed/([^#&?]*)";
    public static final String YOUBUTE_PATTERN3 = "youtu\\.be/([^#&?]*)";
    public static final String YOUBUTE_EMBED_URL = "https://youtube.com/embed/%s";
    public static final String VIMEO_PATTERN = "vimeo\\.com/(.*)";
    public static final String VIMEO_EMBED_URL = "https://player.vimeo.com/video/%s";
    public static final String INSIDESOCCER_PATTERN = "insidesoccer.com/?\\?isf=video\\&id=(.*)";
    public static final String INSIDESOCCER_EMBED_URL = "https://www.insidesoccer.com/is-oembed-web/video/iframe?id=%s";

    private List<Embeder> embeders = new ArrayList<>();

    //-- Constructor
    public UrlServiceImpl (){
        embeders.add(new EmbederImpl(YOUBUTE_PATTERN1, YOUBUTE_EMBED_URL));
        embeders.add(new EmbederImpl(YOUBUTE_PATTERN2, YOUBUTE_EMBED_URL));
        embeders.add(new EmbederImpl(YOUBUTE_PATTERN3, YOUBUTE_EMBED_URL));
        embeders.add(new EmbederImpl(VIMEO_PATTERN, VIMEO_EMBED_URL));
        embeders.add(new EmbederImpl(INSIDESOCCER_PATTERN, INSIDESOCCER_EMBED_URL));
    }

    //-- UrlService overrides
    @Override
    public String embedUrl(String url) {
        for (final Embeder embeder : embeders){
            final String xurl = embeder.embedUrl(url);
            if (xurl != null){
                return xurl;
            }
        }
        return null;
    }

    //-- Innerclasses
    public interface Embeder {
        String embedUrl(String url);
    }

    public static class EmbederImpl implements UrlServiceImpl.Embeder {
        //-- Attributes
        private Pattern pattern;
        private String urlFormat;

        //-- Constructor
        public EmbederImpl(String pattern, String urlFormat){
            this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            this.urlFormat = urlFormat;
        }

        //-- UrlServiceImpl.Embeder
        @Override
        public String embedUrl(String url) {
            Matcher matcher = pattern.matcher(url);
            if(matcher.find()){
                String id = matcher.group(1);
                return String.format(urlFormat, id);
            }
            return null;
        }
    }
}
