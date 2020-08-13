package client.player.application.tests;

import client.player.application.Audio;
import client.player.application.algorithms.MergeSort;
import client.player.application.comparators.SortByDuration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Random;
import java.util.TreeSet;

public class main {
    public static TreeSet<Audio> playlist = new TreeSet<>();

    public static void main(String[] args) {

        // generate random audios
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            double dur = random.nextDouble();

            Audio audio = null;
            if (i % 2 == 0) {
                audio = new Audio(new File("E:\\Music\\$B\\song.mp3"));
            } else {
                audio = new Audio(new File("E:\\Music\\$B\\song3.mp3"));
            }
            audio.setDuration(dur * 1000);

            playlist.add(audio);
        }

        // example listView to reflect the real client.player.application of this test
        ObservableList<Comparable> fxlistView = FXCollections.observableArrayList();

        for (int i = 0; i < playlist.size(); i++) {
            fxlistView.add( (Audio) playlist.toArray()[i] );
        }

        // testing MERGE SORT method on Audio obj
        MergeSort<Audio> ms = new MergeSort<>();

        Audio[] arr = new Audio[fxlistView.size()];
        for (int i = 0; i < fxlistView.size(); i++) {
            arr[i] = (Audio) fxlistView.get(i);
        }

        ms.sort(arr, new SortByDuration(), true);

        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i].getName() + "\tDuration: " + arr[i].getDuration() + "s");
        }

        // search tests
    }
}
