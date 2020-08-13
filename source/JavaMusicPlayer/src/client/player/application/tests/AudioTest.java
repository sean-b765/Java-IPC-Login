package client.player.application.tests;

import client.player.application.Audio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class AudioTest {

    static File expectedSong1 = new File("E:\\Music\\$B\\song.mp3");
    static File expectedSong2 = new File("E:\\Music\\$B\\song2.mp3");

    static Audio a;
    static Audio b;

    @BeforeAll
    static void setUp() {
        a = new Audio(expectedSong1);
        b = new Audio(expectedSong2);
    }

    @Test
    @DisplayName("Test getName()")
    void getName() {
        // The getName() method will return the name of the song
        //  derived from the path, excluding the file extension
        boolean test = false;
        if (a.getName().equals("song") && b.getName().equals("song2")) {
            test = true;
        }

        assertTrue(test);
    }

    @Test
    @DisplayName("Test getSong()")
    void getSong() {
        // Use == operator to check the File
        assertTrue(a.getSong() == expectedSong1);
    }

    @Test
    @DisplayName("Test compareTo()")
    void compareTo() {
        int cmp = a.compareTo(b);
        //         ^ should return -1
        // song | song2
        //  default Audio compare method uses getName()

        // The two songs should not be equal
        assertTrue(cmp != 0);
    }
}