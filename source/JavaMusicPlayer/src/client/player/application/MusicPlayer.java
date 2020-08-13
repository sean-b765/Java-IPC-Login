package client.player.application;

import client.player.controller.Controller;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.*;

public class MusicPlayer extends Thread {

    public MediaPlayer mediaPlayer;
    Audio currentlyPlaying;
    Playlist selectedPlaylist;
    LinkedList<Playlist> playlistLibrary = new LinkedList<>();

    public PlayQueue queue = new PlayQueue();
    public Stack<Audio> songHistory = new Stack<Audio>();
    public Controller controller;

    public boolean repeat  = false;
    public boolean shuffle = false;

    public boolean exit = false;

    double lastTime = 0.0;

    public MusicPlayer(Controller controller) {
        // in constructor, get the client.player.controller object so we can access UI elements
        //          from this MusicPlayer thread
        this.controller = controller;

        // send client.player.controller to our custom audio spectrum listener as well
//        mediaPlayer.setAudioSpectrumListener(new SpectrumListener(client.player.controller, mediaPlayer));

    }

    @Override
    public void run() {
        // set name of thread
        this.setName("Player");

        // thread loop
        while (!exit) {

            if (controller == null) {
                this.interrupt();
            }

            // check for null mediaPlayer
            if (mediaPlayer != null) {

                // if the mediaplayer is playing, update progress
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {

                    // set songInfo, window Title according to song playing
                    if (currentlyPlaying != null) {
                        double duration = currentlyPlaying.getDuration();
                        int min = (int)(duration / 60);
                        int s = (int)(duration) - (min*60);
                        controller.songInfo.setText(
                                currentlyPlaying.getName() + "\n\n" +
                                min + "m" + s + "s\n\n" +
                                "by " + currentlyPlaying.getArtist()
                        );
                    }

                    mediaPlayer.volumeProperty().set(controller.getVolume());

                    double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                    double endTime = mediaPlayer.getMedia().getDuration().toSeconds();

                    // display time progress in system.out, only every second
                    if ((int)currentTime != (int)lastTime) {
                        System.out.print((int)currentTime + "s / ");
                        System.out.println((int)endTime + "s");
                        lastTime = currentTime;
                    }
                    // send song progress as decimal (0.0-1.0) for progressBar
                    // update UI
                    controller.setProgress(currentTime/endTime);

                    mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.stop());
                }


                // when the mediaPlayer stops, check if there is a song in the queue
                //      the mediaPlayer should load via the queue.pop() method
                if (mediaPlayer.getStatus() == javafx.scene.media.MediaPlayer.Status.STOPPED) {
                    System.out.println("Stopped");// push current Audio to song history,
                    songHistory.push(currentlyPlaying);

                    System.out.println("Stopped. Attempting to load from Queue(" + queue.size() + ")");
                    if (queue.size() > 0) {
                        play(queue.pop());
                    } else {
                        if (repeat) {
                            // if the repeat button is activated, play the next song from the playlist
                            System.out.println("Queue empty.");

                            // shuffle the songs using Random class
                            if (shuffle) {
                                // SHUFFLE
                                // get random int from 0 - playlistSize, play that
                                Random random = new Random();
                                int choice = random.nextInt(controller.currentPlaylist.getItems().size());
                                Audio songChoice = getPlaylist().getSongAt(choice);
                                play(songChoice);
                            } else {
                                // REPEAT
                                // find selectedIndex of song currentlyPlaying in the listview, then select the song which comes after
                                int index = controller.currentPlaylist.getItems().indexOf(currentlyPlaying);
                                if (index == controller.currentPlaylist.getItems().size() - 1) {
                                    play(controller.currentPlaylist.getItems().get(0));
                                } else {
                                    play(controller.currentPlaylist.getItems().get(index + 1));
                                }
                            }
                        } else {
                            System.out.println("Queue empty, repeat disabled.");
                            // mediaPlayer shouldn't be playing
                            mediaPlayer = null;
                        }
                    }
                }

            } else {
                // mediaplayer is null, most likely the start of the client.player.application lifecycle
                try {
                    synchronized (this) {
                        // lock the thread, and allow the user to play a song
                        System.out.println("Locking client.player thread.");
                        // wait for user to play song
                        this.wait();
                        System.out.println("Player thread resumed.");
                        Thread.sleep(50);
                    }
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }

        } // while true
    }

    // play an audio object
    // update currentlyPlaying, send the previous song to our songHistory stack
    public void play(Audio audio) {
        try {
            // check if mediaPlayer is playing, if so, stop
            if (mediaPlayer != null) {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.stop();
                }
            }
            // then set current Audio to song we are going to play.
            currentlyPlaying = audio;

            Media media = new Media(audio.getSong().toURI().toString());
            mediaPlayer = new MediaPlayer(media);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        play();
    }

    // quick play method
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    // pause method
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    // seek to a certain position within the song
    // takes a double (fraction of the song) and will set the duration as the songDuration * progressFraction
    public void seek(double progress) {
        double songDuration = mediaPlayer.getMedia().getDuration().toSeconds();
        // set the seek duration as a total of the song duration
        Duration duration = Duration.seconds(songDuration * progress);
        mediaPlayer.seek(duration);
    }

    public void setPlaylist(Playlist p) {
        selectedPlaylist = p;
    }

    public Playlist getPlaylist() {
        return selectedPlaylist;
    }

    public Playlist getPlaylistByName(String s) {
        for (Playlist playlist : playlistLibrary) {
            if (playlist.getListName().equals(s)) {
                return playlist;
            }
        }
        // return null if the list name was not found
        return null;
    }

    public LinkedList<Playlist> getPlaylistLib() {
        return playlistLibrary;
    }

    public void setPlaylistLibrary(LinkedList<Playlist> lib) {
        this.playlistLibrary = lib;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Audio getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public void setCurrentlyPlaying(Audio playing) {
        play(currentlyPlaying);
    }

    public void enqueue(Audio audio) {
        queue.add(audio);
    }

    public void dequeue(Audio audio) {
        queue.remove(audio);
    }

    public PlayQueue getQueue() {
        return queue;
    }
}
