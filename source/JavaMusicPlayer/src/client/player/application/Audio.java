package client.player.application;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Audio implements Comparable<Audio>, Serializable {
    private int id;
    private static int count = 0;

    private String name;
    private double duration;
    private String artist;
    private String album;
    private byte[] albumImage;


    private File song;

    public Audio(File song) {
        this.song = song;
        this.id = count;
        setName();
        //setDuration();
        setInfo();

        count++;
    }

    // restoration constructor
    public Audio(File song, int id) {
        this.id = id;
        this.song = song;
        setName();
        setInfo();
        setDuration();
    }

    // Using a maven third-party library, get mp3 data tags for album, artist and artist image attributes.
    //  may be useful in future iterations
    void setInfo() {
        Mp3File mp3MetaDataReader = null;
        try {
            mp3MetaDataReader = new Mp3File(this.song.getPath());
        } catch (IOException e) {

        } catch (UnsupportedTagException e){

        } catch (InvalidDataException e) {

        }
        var songInfo = mp3MetaDataReader.getId3v2Tag();
        album = songInfo.getAlbum();
        artist = songInfo.getArtist();
        albumImage = songInfo.getAlbumImage();
    }

    void setDuration() {
        Media media = new Media(song.toURI().toString());
        MediaPlayer mp = new MediaPlayer(media);

        mp.setOnReady(() -> {
            duration = mp.getMedia().getDuration().toSeconds();
            mp.stop();
        });

        System.out.println(duration);
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double d) {
        this.duration = d;
    }

    void setName() {
        String DS = "[/\\\\]"; // directory separator regex
        String[] songName = this.song.getPath().split(DS);

        int index = songName[songName.length - 1].lastIndexOf(".");
        String fileExtension = songName[songName.length - 1].substring(index);

        this.name = songName[songName.length - 1].replace(fileExtension, "");
    }

    public String getName() {
        return this.name;
    }

    public String getArtist() {
        return this.artist;
    }

    public int getId() {
        return this.id;
    }

    public int getCount() {
        return count;
    }

    public static void setCount(int c) {
        count = c;
    }

    public File getSong() {
        return song;
    }

    public String getAlbum() {
        return album;
    }

    @Override
    public int compareTo(Audio o) {
        return this.getName().compareTo(o.getName());
    }
}
