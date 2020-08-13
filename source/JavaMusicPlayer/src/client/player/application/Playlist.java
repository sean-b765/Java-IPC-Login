package client.player.application;

import java.io.Serializable;
import java.util.*;

public class Playlist implements Serializable {

    // use java.util.TreeSet -> red-black tree which is self-balancing

    private TreeSet<Audio> songList = new TreeSet<>();

    private int id;
    private String listName;
    private String description;

    private static int count = 0;

    public Playlist(String name) {
        this.id = count;
        this.listName = name;

        count++;
    }

    // restoration constructor
    public Playlist(int id, String name, String description, TreeSet<Audio> songList) {
        this.id = id;
        this.listName = name;
        this.description = description;
        this.songList = songList;
    }

    public int putSongs(Audio[] audios) {
        // if a song already exists, skip duplicate

        for (int i = 0; i < audios.length; i++) {
            songList.add(audios[i]);
        }
        return audios.length - 1;
    }

    public void putSong(Audio audio) {
        System.out.println("Putting " + songList.size());

        songList.add(audio);
    }
    // returns true if the Audio is contained,
    // false if not
    public boolean contains(Audio audio) {
        for (Audio value : songList) {
            if (audio.equals(value)) {
                return true;
            }
            if (audio.getName().equals(value.getName())) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(Audio audio) {
        int i = 0;
        for (Audio a : songList) {
            if (a.equals(audio)) {
                System.out.println("Index of " + audio.getName() + ": " + i);
                return i;
            }
            i++;
        }
        // no audio obj found
        return -1;
    }

    // return all Audio objects contained in this List
    public Audio[] getSongs() {
        Audio[] songs = new Audio[songList.size()];
        int i = 0;

        // iterate through songList, add to songs array, then return it
        for (Audio audio : songList) {
            songs[i] = audio;
            i++;
        }

        return songs;
    }

    public Audio getSongAt(int index) {
        Audio returnA = (Audio) songList.toArray()[index];
        return returnA;
    }

    public void remove(Audio audio) {
        System.out.println("Remove " + audio.getName());
        songList.remove(audio);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getListName() {
        return this.listName;
    }

    public TreeSet<Audio> getSongList() {
        return songList;
    }

    public String getDescription() {
        return this.description;
    }

    public int getCount() {
        return this.count;
    }

    public static void setCount(int c) {
        count = c;
    }

}
