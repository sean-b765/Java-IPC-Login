package client.player.application.comparators;

import client.player.application.Audio;

import java.util.Comparator;

public class SortByDuration implements Comparator<Audio> {
    @Override
    public int compare(Audio o1, Audio o2) {
        return (int) (o1.getDuration() - o2.getDuration());
    }
}
