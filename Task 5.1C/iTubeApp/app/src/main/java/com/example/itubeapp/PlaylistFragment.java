package com.example.itubeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.playlistRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        UserEntity currUser = getArguments().getParcelable("CURR_USER");

        PlaylistAdapter adapter = new PlaylistAdapter(currUser.getPlaylist(), (url, pos) -> {
            FragmentManager fm = getParentFragmentManager();
            Bundle res = new Bundle();
            res.putString("YOUTUBE_URL", url);
            fm.setFragmentResult("YOUTUBE_URL", res);
            fm.popBackStackImmediate();
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}
