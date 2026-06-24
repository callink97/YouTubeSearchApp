package com.example.youtubesearch;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.youtubesearch.adapter.VideoAdapter;
import com.example.youtubesearch.databinding.ActivityMainBinding;
import com.example.youtubesearch.model.YouTubeResponse;
import com.example.youtubesearch.network.YouTubeApiClient;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private VideoAdapter        videoAdapter;

    // Sort options — labels match positions
    private final String[] sortLabels  = {"Relevance", "Date", "View Count", "Rating", "Title"};
    private final String[] sortOrders  = {"relevance", "date", "viewCount", "rating", "title"};
    private       String   currentOrder = "relevance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupSortSpinner();
        setupSearchListeners();
    }

    // ── Setup ────────────────────────────────────────────────────────────────

    private void setupRecyclerView() {
        videoAdapter = new VideoAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(videoAdapter);
        binding.recyclerView.setHasFixedSize(true);
    }

    private void setupSortSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, sortLabels);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSort.setAdapter(spinnerAdapter);

        binding.spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                currentOrder = sortOrders[pos];
                // Re-search if query exists
                String query = getQuery();
                if (!query.isEmpty()) performSearch(query);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupSearchListeners() {
        // Search button
        binding.btnSearch.setOnClickListener(v -> triggerSearch());

        // Keyboard "Search" / Enter key
        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                triggerSearch();
                return true;
            }
            return false;
        });
    }

    // ── Search ───────────────────────────────────────────────────────────────

    private void triggerSearch() {
        String query = getQuery();
        if (query.isEmpty()) {
            binding.etSearch.setError(getString(R.string.error_empty_query));
            return;
        }
        hideKeyboard();
        performSearch(query);
    }

    private void performSearch(String query) {
        showLoading(true);
        clearState();

        Call<YouTubeResponse> call = YouTubeApiClient.getInstance().searchVideos(
                "snippet",
                "video",
                query,
                25,
                currentOrder,
                YouTubeApiClient.API_KEY
        );

        call.enqueue(new Callback<YouTubeResponse>() {
            @Override
            public void onResponse(Call<YouTubeResponse> call, Response<YouTubeResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    YouTubeResponse body = response.body();

                    // API-level error (quota, bad key, etc.)
                    if (body.getError() != null) {
                        showError(body.getError().getMessage());
                        return;
                    }

                    List<com.example.youtubesearch.model.VideoItem> items = body.getItems();
                    if (items == null || items.isEmpty()) {
                        showEmpty();
                    } else {
                        videoAdapter.setVideos(items);
                        binding.tvResultCount.setText(
                                getString(R.string.result_count, items.size()));
                        binding.tvResultCount.setVisibility(View.VISIBLE);
                    }

                } else {
                    String msg;
                    switch (response.code()) {
                        case 400: msg = getString(R.string.error_bad_request); break;
                        case 403: msg = getString(R.string.error_api_key);     break;
                        case 404: msg = getString(R.string.error_not_found);   break;
                        default:  msg = getString(R.string.error_server, response.code());
                    }
                    showError(msg);
                }
            }

            @Override
            public void onFailure(Call<YouTubeResponse> call, Throwable t) {
                showLoading(false);
                if (t instanceof IOException) {
                    showError(getString(R.string.error_network));
                } else {
                    showError(getString(R.string.error_unexpected));
                }
            }
        });
    }

    // ── UI helpers ───────────────────────────────────────────────────────────

    private String getQuery() {
        String q = binding.etSearch.getText() != null
                ? binding.etSearch.getText().toString().trim() : "";
        return q;
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSearch.setEnabled(!isLoading);
    }

    private void clearState() {
        videoAdapter.setVideos(null);
        binding.tvEmpty.setVisibility(View.GONE);
        binding.tvResultCount.setVisibility(View.GONE);
    }

    private void showEmpty() {
        binding.tvEmpty.setText(getString(R.string.empty_results));
        binding.tvEmpty.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
        binding.tvEmpty.setText(message);
        binding.tvEmpty.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
    }
}
