package com.example.youtubesearch.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.youtubesearch.R;
import com.example.youtubesearch.model.VideoItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoItem> videoList = new ArrayList<>();

    // Call this to update the full list
    public void setVideos(List<VideoItem> videos) {
        videoList.clear();
        if (videos != null) videoList.addAll(videos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bind(videoList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    // ── ViewHolder ──────────────────────────────────────────────────────────
    static class VideoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivThumbnail;
        private final TextView  tvTitle;
        private final TextView  tvChannel;
        private final TextView  tvPublishTime;
        private final TextView  tvDescription;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail   = itemView.findViewById(R.id.ivThumbnail);
            tvTitle       = itemView.findViewById(R.id.tvTitle);
            tvChannel     = itemView.findViewById(R.id.tvChannel);
            tvPublishTime = itemView.findViewById(R.id.tvPublishTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        void bind(VideoItem item) {
            VideoItem.Snippet snippet = item.getSnippet();

            tvTitle.setText(snippet.getTitle());
            tvChannel.setText(snippet.getChannelTitle());

            String desc = snippet.getDescription().trim();
            tvDescription.setText(desc.isEmpty() ? "No description available." : desc);
            tvPublishTime.setText(formatDate(snippet.getPublishedAt()));

            String thumbUrl = "";
            if (snippet.getThumbnails() != null) {
                thumbUrl = snippet.getThumbnails().getBestUrl();
            }

            Glide.with(itemView.getContext())
                    .load(thumbUrl)
                    .placeholder(R.drawable.ic_video_placeholder)
                    .error(R.drawable.ic_video_placeholder)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(ivThumbnail);
        }

        private String formatDate(String raw) {
            try {
                SimpleDateFormat parser    = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                Date date = parser.parse(raw);
                if (date != null) return formatter.format(date);
            } catch (ParseException ignored) { }
            return raw;
        }
    }
}
