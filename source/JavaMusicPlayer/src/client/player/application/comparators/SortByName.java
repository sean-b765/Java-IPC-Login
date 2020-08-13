package client.player.application.comparators;

import client.player.application.Audio;

import java.util.Comparator;

public class SortByName implements Comparator<Audio> {
    @Override
    public int compare(Audio o1, Audio o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
