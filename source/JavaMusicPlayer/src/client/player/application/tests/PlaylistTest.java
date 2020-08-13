package client.player.application.tests;

import client.player.application.Audio;
import client.player.application.Playlist;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class PlaylistTest {

    static Playlist myList = new Playlist("My List");

    static Audio a = new Audio(new File("E:\\Music\\$B\\song.mp3"));
    static Audio b = new Audio(new File("E:\\Music\\$B\\song2.mp3"));
    static Audio c = new Audio(new File("E:\\Music\\$B\\song3.mp3"));

    static Audio[] audios = null;

    @BeforeAll
    static void setUp() {
        // Before we perform tests, add Audio to playlist
        audios = new Audio[]{a, b, c};

        myList.putSongs(audios);
    }

    @Test
    @DisplayName("Test contains()")
    void contains() {
        assertTrue(myList.contains(a));
    }

    @Test
    @DisplayName("Test getSongs()")
    void getSongs() {
        Audio[] returnAudio = myList.getSongs();
        assertArrayEquals(returnAudio,  audios);
    }
}