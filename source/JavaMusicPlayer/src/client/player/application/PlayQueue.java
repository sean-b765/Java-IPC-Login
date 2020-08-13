package client.player.application;

import java.util.LinkedList;
import java.util.Queue;

public class PlayQueue {

    public Queue<Audio> playQueue = new LinkedList<Audio>();

    // push method
    public void add(Audio audio) {
        playQueue.add(audio);
    }

    // pop method, will remove Queue header while
    // also returning the removed Audio object
    public Audio pop() {
        try {
            Audio audio = playQueue.remove();
            return audio;
        } catch (Exception e) { }
        return null;
    }

    public boolean contains(Audio audio) {
        if (playQueue.contains(audio))
            return true;
        else
            return false;
    }

    public void remove(Audio item) {
        // remove from list
        if (contains(item)) {
            playQueue.remove(item);
        }
    }

    public int size() {
        return playQueue.size();
    }
}
