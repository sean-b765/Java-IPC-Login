package client.player.application;

import client.player.controller.Controller;

import java.io.Serializable;
import java.util.LinkedList;

public class ProgramState implements Serializable {
    LinkedList<Playlist> library;
    Audio currentlyPlaying;
    Playlist selectedPlaylist;

    int audioCount;
    int playlistCount;

    public ProgramState(Controller c) {
        MusicPlayer player = c.getPlayer();
        // get the MusicPlayer object from Controller parameter,
        // set currentlyPlaying, selectedPlaylist, etc. from client.player
        currentlyPlaying = player.getCurrentlyPlaying();
        selectedPlaylist = player.getPlaylist();
        library = player.getPlaylistLib();
    }

    public void save() {
        if (library.size() > 0) {
            playlistCount = library.size();
            audioCount = library.get(0).getSongs()[0].getCount();

            System.out.println("Total Playlists: " + playlistCount + "\n" +
                    "Total Audio: " + audioCount);
        } else {
            System.out.println("Nothing was saved.");
        }
    }

    public ProgramState restore() {
        Audio.setCount(audioCount);
        Playlist.setCount(playlistCount);

        System.out.println("Total Playlists: " + playlistCount + "\n" +
                "Total Audio: " + audioCount);

        // return this instance, as we will need to set MusicPlayer fields from deserialize() method
        return this;
    }

    public Audio getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    public LinkedList<Playlist> getLibrary() {
        return library;
    }
}
