package com.example.youtubesearch.model;

import com.google.gson.annotations.SerializedName;

public class VideoItem {

    @SerializedName("id")
    private VideoId id;

    @SerializedName("snippet")
    private Snippet snippet;

    public VideoId  getId()      { return id; }
    public Snippet  getSnippet() { return snippet; }

    // ── VideoId ──────────────────────────────────────────────────────────────
    public static class VideoId {
        @SerializedName("videoId")
        private String videoId;

        public String getVideoId() { return videoId != null ? videoId : ""; }
    }

    // ── Snippet ──────────────────────────────────────────────────────────────
    public static class Snippet {
        @SerializedName("title")        private String     title;
        @SerializedName("description")  private String     description;
        @SerializedName("publishedAt")  private String     publishedAt;
        @SerializedName("channelTitle") private String     channelTitle;
        @SerializedName("thumbnails")   private Thumbnails thumbnails;

        public String     getTitle()        { return title        != null ? title        : ""; }
        public String     getDescription()  { return description  != null ? description  : ""; }
        public String     getPublishedAt()  { return publishedAt  != null ? publishedAt  : ""; }
        public String     getChannelTitle() { return channelTitle != null ? channelTitle : ""; }
        public Thumbnails getThumbnails()   { return thumbnails; }
    }

    // ── Thumbnails ───────────────────────────────────────────────────────────
    public static class Thumbnails {
        @SerializedName("medium")  private Thumbnail medium;
        @SerializedName("default") private Thumbnail defaultThumb;

        /** Returns the best available thumbnail URL. */
        public String getBestUrl() {
            if (medium != null && medium.getUrl() != null) return medium.getUrl();
            if (defaultThumb != null && defaultThumb.getUrl() != null) return defaultThumb.getUrl();
            return "";
        }
    }

    // ── Single thumbnail ─────────────────────────────────────────────────────
    public static class Thumbnail {
        @SerializedName("url")
        private String url;

        public String getUrl() { return url; }
    }
}
