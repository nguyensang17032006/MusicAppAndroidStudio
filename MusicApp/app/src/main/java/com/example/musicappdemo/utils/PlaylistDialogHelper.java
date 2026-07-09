package com.example.musicappdemo.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.musicappdemo.model.Playlist;
import com.example.musicappdemo.model.Song;

import java.util.List;

public class PlaylistDialogHelper {

    public static void showAddToPlaylistDialog(Context context, Song song) {
        LibraryManager libraryManager = LibraryManager.getInstance(context);
        List<Playlist> playlists = libraryManager.getPlaylists();

        String[] playlistNames = new String[playlists.size() + 1];
        for (int i = 0; i < playlists.size(); i++) {
            playlistNames[i] = playlists.get(i).getName();
        }
        playlistNames[playlists.size()] = "+ Tạo playlist mới";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thêm vào playlist");
        builder.setItems(playlistNames, (dialog, which) -> {
            if (which == playlists.size()) {
                showCreatePlaylistDialog(context, song);
            } else {
                Playlist selected = playlists.get(which);
                libraryManager.addSongToPlaylist(selected.getId(), song);
                Toast.makeText(context, "Đã thêm vào " + selected.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private static void showCreatePlaylistDialog(Context context, Song song) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tên playlist mới");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                LibraryManager.getInstance(context).createPlaylist(name, song);
                Toast.makeText(context, "Đang tạo playlist...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Tên không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
