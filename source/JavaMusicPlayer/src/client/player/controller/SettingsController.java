package client.player.controller;

import client.player.application.Audio;
import client.player.application.Playlist;
import com.opencsv.CSVWriter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    RadioButton mergeSort = new RadioButton();
    @FXML
    RadioButton quickSort = new RadioButton();
    @FXML
    Button help = new Button();
    @FXML
    Button export = new Button();

    public boolean merge = true;
    File helpFile = new File("help.txt");

    Controller controller;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // setting the help text area
        try (FileWriter fw = new FileWriter(helpFile)) {
            if (helpFile.createNewFile() || helpFile.exists()) {
                // the file was created
                fw.write("\n\n\t\t\tTUTORIAL\n\n\n" +
                        "Adding a Playlist\n\tThis can be done by locating the top-left most text box, \n\tand typing in the playlist name. Click '+ Add' to make the playlist.\n\n" +
                        "Adding Songs\n\tLocate the 'Add Songs' button near the top-right. This will open a file browser, \n\twhere you can select multiple mp3 files to add.\n\n" +
                        "Removing Songs\n\tRight-click on a song in the playlist. Click 'Remove from [playlist]'.\n\n" +
                        "Queueing Songs\n\tRight-click a song, and add or remove from the song queue. \n\tThe queue can be viewed by clicking 'Show Queue' button on the right. Clicking this again will change back to Song Info.\n\n" +
                        "Sorting the Playlist\n\tCan be done by selecting a playlist and clicking 'Sort' at the bottom.\n\n" +
                        "Searching for Song\n\tCan be done by typing in the text field at the top of the playlist.\n\tThis will search your song library (the title must be exact!)\n\n"
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        help.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().edit(helpFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mergeSort.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                quickSort.selectedProperty().set(false);
                merge = true;
            } else {
                quickSort.selectedProperty().set(true);
            }
        });
        quickSort.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                mergeSort.selectedProperty().set(false);
                merge = false;
            } else {
                mergeSort.selectedProperty().set(true);
            }
        });

        export.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                exportCSV();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void exportCSV() throws IOException {
        File listFile = new File("playlists.csv");
        File audioFile = new File("audios.csv");

        if (listFile.createNewFile() || listFile.exists()) {
            int n = controller.player.getPlaylistLib().size();
            // string[] of playlist records
            List<String[]> playlists = new LinkedList<>();
            List<String[]> audios = new LinkedList<>();

            // string[] of playlist headers
            String[] row = new String[3];
            // 3 fields : ID, name, AmountSongs
            row[0] = "ID";
            row[1] = "ListName";
            row[2] = "#Songs";
            // add the headers
            playlists.add(row);

            // string[] of audio headers
            row = new String[6];
            // 6 fields : id, title, duration, artist, album, playlistID
            row[0] = "ID";
            row[1] = "PlaylistID";
            row[2] = "Title";
            row[3] = "Duration";
            row[4] = "Artist";
            row[5] = "Album";
            audios.add(row);

            // loop through playlists, add their info to row[]
            for (int i = 0; i < n; i++) {
                Playlist currentPlaylist = controller.player.getPlaylistLib().get(i);
                row = new String[3];

                row[0] = currentPlaylist.getId() + "";
                row[1] = currentPlaylist.getListName();
                row[2] = currentPlaylist.getSongs().length + "";

                // add row[] to our list<String[]>
                playlists.add(row);
            }

            // loop through playlists and audio, to write to audio.csv
            for (int i = 0; i < controller.player.getPlaylistLib().size(); i++) {
                Playlist currentPlaylist = controller.player.getPlaylistLib().get(i);
                // now do the same for audios
                for (int j = 0; j < currentPlaylist.getSongs().length; j++) {
                    Audio currentAudio = currentPlaylist.getSongAt(j);
                    row = new String[6];

                    row[0] = currentAudio.getId() + "";
                    row[1] = currentPlaylist.getId() + "";
                    row[2] = currentAudio.getName();
                    row[3] = currentAudio.getDuration() + "";
                    row[4] = currentAudio.getArtist();
                    row[5] = currentAudio.getAlbum();

                    audios.add(row);
                }
            }

            // write the generated lists to their files
            CSVWriter csvWriter = new CSVWriter(new FileWriter(listFile));
            csvWriter.writeAll(playlists);
            csvWriter.close();

            csvWriter = new CSVWriter((new FileWriter(audioFile)));
            csvWriter.writeAll(audios);
            csvWriter.close();
        }
    }

    public void setMainController(Controller controller) {
        this.controller = controller;
    }
}
