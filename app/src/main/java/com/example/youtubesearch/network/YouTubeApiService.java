package com.example.youtubesearch.network;

import com.example.youtubesearch.model.YouTubeResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YouTubeApiService {

    @GET("youtube/v3/search")
    Call<YouTubeResponse> searchVideos(
            @Query("part")       String part,
            @Query("type")       String type,
            @Query("q")          String query,
            @Query("maxResults") int    maxResults,
            @Query("order")      String order,
            @Query("key")        String apiKey
    );
}
