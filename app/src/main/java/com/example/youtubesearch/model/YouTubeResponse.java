package com.example.youtubesearch.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class YouTubeResponse {

    @SerializedName("items")
    private List<VideoItem> items;

    @SerializedName("error")
    private ApiError error;

    public List<VideoItem> getItems() { return items; }
    public ApiError getError()        { return error; }

    // ── Nested: API-level error ──────────────────────────────────────────────
    public static class ApiError {
        @SerializedName("code")    private int    code;
        @SerializedName("message") private String message;

        public int    getCode()    { return code; }
        public String getMessage() { return message; }
    }
}
