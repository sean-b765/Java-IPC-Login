package client.player.controller;

import client.player.application.*;
import client.player.application.algorithms.MergeSort;
import client.player.application.algorithms.QuickSort;
import client.player.application.comparators.SortByDuration;
import client.player.application.comparators.SortByName;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public AnchorPane anchorPane = new AnchorPane();
    @FXML
    AnchorPane audioPane = new AnchorPane();
    @FXML
    ListView playLists = new ListView();
    @FXML
    public ListView<Audio> currentPlaylist = new ListView();
    @FXML
    TextField playlistName = new TextField();
    @FXML
    public TextArea songInfo = new TextArea();
    @FXML
    Button addSongs = new Button();
    @FXML
    Button addPlaylist = new Button();
    @FXML
    ProgressBar progress = new ProgressBar();
    @FXML
    Slider volume = new Slider();
    @FXML
    Button playPause = new Button();
    @FXML
    Button previous = new Button();
    @FXML
    Button next = new Button();
    @FXML
    Button settingsAccess = new Button();
    @FXML
    public ListView<String> pQueue = new ListView<>();
    @FXML
    ToggleButton showQueue = new ToggleButton();
    @FXML
    ToggleButton repeat = new ToggleButton();
    @FXML
    ToggleButton shuffle = new ToggleButton();
    @FXML
    Button sort = new Button();
    @FXML
    Label songLabel = new Label();
    @FXML
    TextField search = new TextField();
    @FXML
    Label currentSong = new Label();

    Stage stage;

    DirectoryChooser directoryChooser = new DirectoryChooser();
    FileChooser fileChooser = new FileChooser();

    //default sorting type is merge sort
    String sortType = "merge";

    MusicPlayer player;
    String selectedPlaylist = "";

    // Local data file
    String serializationFile = "data.bin";

    boolean playToggle = false;

    String appPath = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + "/../..").getPath();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // instantiate thread obj with MusicPlayer class taking this client.player.controller as argument,
        // so we can send data between threads via public methods
        new Thread(player = new MusicPlayer(this)).start();

        // deserialize the saved ProgramState obj,
        // setting relevant current client.player.application variables
        deserialize();

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 (*.mp3)", "*.mp3"));
        pQueue.setVisible(false);

        // Event handlers
        addPlaylist.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> newPlaylist());
        addSongs.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> addSongs());

        sort.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> sort());

        search.setOnKeyPressed((event) -> binarySearch(event));

        // Open the settings window
        settingsAccess.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> openSettingsWindow(event));

        // when the repeat button is toggled, set the client.player.repeat boolean
        repeat.selectedProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (newVal) {
                repeat.setText("Repeating");
                player.repeat = true;
            } else {
                repeat.setText("Repeat");
                player.repeat = false;
            }
        }));
        // when the shuffle button is toggled, set the client.player.shuffle boolean
        shuffle.selectedProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (newVal) {
                shuffle.setText("Shuffling");
                repeat.setSelected(true);
                player.shuffle = true;
            } else {
                shuffle.setText("Shuffle");
                player.shuffle = false;
            }
        }));

        // When user clicks previous, play the last song from songHistory Stack in the MusicPlayer
        previous.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (player.songHistory.size() > 0) {
                player.play(player.songHistory.pop());
            } else {
                System.out.println("There are no songs in history.");
            }
            updateQueue();
        });

        // When user clicks next, we can simply stop the mediaPlayer.
        // the client.player will detect the STOPPED status, and will commence playing from Queue.
        // if the queue is empty, it will attempt to repeat the playlist, if the option is selected
        next.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (player.mediaPlayer != null) {
                player.mediaPlayer.stop();
            }
            updateQueue();
        });

        // Show the queue lineup, or show the song info
        showQueue.selectedProperty().addListener((obv, oldVal, newVal) -> {
            if (newVal) {
                // queue will be showing
                pQueue.setVisible(true);
                showQueue.setText("Song Info");
            } else {
                pQueue.setVisible(false);
                showQueue.setText("Song Queue");
            }
        });

        // pause/play toggle button
        playPause.setText("Pause");
        playPause.setOnMouseClicked(mouseEvent -> {
            if (playToggle) {
                playPause.setText("Pause");
                // play the song
                player.play();
                playToggle = false;
            } else {
                playPause.setText("Play");
                // pause the song
                player.pause();
                playToggle = true;
            }
        });

        // when clicking the progress bar, seek to that point in the song
        progress.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                // get width of the progress bar (pixels)
                // and the clicked position
                double w = progress.widthProperty().getValue();
                double clickedPos = mouseEvent.getX();
                player.seek(clickedPos/w);
            }
        });

        // changing volume property
        volume.valueProperty().addListener((observableValue, oldNum, newNum) -> {
            double vol = newNum.doubleValue() / 100;
            try {
                player.mediaPlayer.setVolume(vol);
            } catch (Exception e) {}
        });

        // ListCell<Audio> cell factory
        currentPlaylist.setCellFactory(param -> {

            ListCell<Audio> cell = new ListCell<>() {
                @Override
                protected void updateItem(Audio item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null || item.getName() == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {

                    // synchronize the client.player thread to start the client.player.
                    synchronized (player) {
                        player.play(cell.getItem());
                        // wake up the client.player, as we are going to play a song.
                        // the client.player is currently waiting for the notification
                        player.notify();
                    }
                }
            });

            ContextMenu menu = new ContextMenu();

            // context menu
            MenuItem addToQueue = new MenuItem("Add " +"to Queue");
            MenuItem delFromQueue = new MenuItem("Remove " + "from Queue");
            delFromQueue.setDisable(true);

            // user clicked addToQueue, enqueue
            addToQueue.setOnAction(event -> {
                addToQueue.setDisable(true);
                delFromQueue.setDisable(false);
                player.enqueue(cell.getItem());
                updateQueue();
            });

            // user clicked delFromQueue, dequeue
            delFromQueue.setOnAction(event -> {
                addToQueue.setDisable(false);
                delFromQueue.setDisable(true);
                player.dequeue(cell.getItem());
                updateQueue();
            });

            // remove button,
            // when clicked, remove from the playlist
            MenuItem remove = new MenuItem("Remove from " + selectedPlaylist);
            remove.setOnAction(event -> {
                int selectedId = cell.getIndex();
                player.getPlaylist().remove(cell.getItem());
                currentPlaylist.getItems().remove(selectedId);
            });
            // add our menuItems to context menu
            menu.getItems().addAll(remove, addToQueue, delFromQueue);

            // listen for changes in addToQueue and deleteFromQueue disabled properties,
            // if one was disabled, the other should be enabled
            addToQueue.disableProperty().addListener((observable, old, newVal) -> {
                if (newVal) {
                    System.out.println("add to q");
                }
            });
            delFromQueue.disableProperty().addListener((observableValue, oldVal, newVal) -> {
                if (newVal) {
                    System.out.println("del from q");
                }
            });

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else
                    cell.setContextMenu(menu);
            });

            return cell;
        });

        // update selectedPlaylist String whenever ListView index changes
        playLists.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            System.out.println("Playlist selection changed from "
                    + oldValue + " to " + newValue);
            selectedPlaylist = newValue;
            // get the current playlist by the ListView selected item
            // since we don't allow duplicate playlist names, this is not an issue
            player.setPlaylist(player.getPlaylistByName(selectedPlaylist));
            setWindowTitle("Playing from " + selectedPlaylist);
            updatePlaylist();
        });

    } // end of initialize

    void serialize() {
        ObjectOutputStream objectOutputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            // File out
            fileOutputStream = new FileOutputStream(serializationFile);
            // obj out
            objectOutputStream = new ObjectOutputStream(fileOutputStream);

            // synchronize this block, as we are accessing data from other thread
            synchronized (player) {
                ProgramState programState = new ProgramState(this);
                programState.save(); // save the program state
                // write programState to data.bin
                objectOutputStream.writeObject(programState);
            }

            // close resources
            fileOutputStream.close();
            objectOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void deserialize() {
        FileInputStream fileIn;
        ObjectInputStream objectInputStream;
        ProgramState savedState = null;

        try {
            fileIn = new FileInputStream(serializationFile);
            objectInputStream = new ObjectInputStream(fileIn);

            synchronized (player) {
                // retrieve the saved object, and restore the program state from this
                try {
                    var state = (ProgramState) objectInputStream.readObject();
                    savedState = state.restore();
                } catch (Exception e) {
                    System.out.println(serializationFile + " detected as empty.");
                }

                if (savedState != null) {
                    // restore the client.player state from the deserialized object
                    player.setPlaylistLibrary(savedState.getLibrary());
                    selectedPlaylist = savedState.getSelectedPlaylist().getListName();
                    player.setPlaylist(savedState.getSelectedPlaylist());
                    if (savedState.getCurrentlyPlaying() != null) {
                        player.setCurrentlyPlaying(savedState.getCurrentlyPlaying());
                    }

                    if (player.getPlaylistLib().size() > 0) {
                        updatePlaylists();
                        if (player.getPlaylist().getCount() > 0) {
                            updatePlaylist();
                        }
                        playLists.getSelectionModel().select(selectedPlaylist);
                    }

                    System.out.println("Player restored.");
                }
            }

            fileIn.close();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("Save file not found ( " + serializationFile + "). Creating new library.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setProgress(double p) {
        progress.setProgress(p);
    }

    public void setWindowTitle(String s) {
        ((Stage) currentPlaylist.getScene().getWindow()).setTitle(s);
    }

    // addSongs triggered by clicking "Add Songs" - top right
    // Will instantiate new Audio objects if files have been selected using
    //  FileChooser. Duplicates will be added if user allows it.
    void addSongs() {

        if (player.getPlaylist() != null) {
            java.util.List<File> imports = fileChooser.showOpenMultipleDialog(anchorPane.getScene().getWindow());

            ObservableList<Audio> songs = currentPlaylist.getItems();

            if (imports != null) {
                for (File file : imports) {
                    Audio audio = new Audio(file);
                    // check if the song is a duplicate
                    boolean exists = player.getPlaylist().contains(audio);
                    if (!exists) {
                        songs.add(audio);
                        player.getPlaylist().putSong(audio);
                    } else {
                        // ask user if they want to add duplicate
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Add duplicate " + audio.getName() + "?", ButtonType.YES, ButtonType.NO);
                        alert.showAndWait();
                        if (alert.getResult() == ButtonType.YES) {
                            songs.add(audio);
                            player.getPlaylist().putSong(audio);
                        }
                    }
                }
                currentPlaylist.setItems(songs);
            }
        } else {
            System.out.println("Please select a playlist.");
        }
    }

    // Create new playlist object using playlistName TextField
    //  as the list name.
    void newPlaylist() {
        String playlistNameStr = playlistName.getText().toString();
        if (!playlistNameStr.isEmpty()) {
            // check if playlist name already exists,
            // but if there are no playlists in library, skip and add new
            if (player.getPlaylistLib().size() > 0) {

                Playlist addAfter = null;
                // playlist object to add after iteration
                // to avoid ConcurrentModificationException:
                for (Iterator<Playlist> playlistIt = player.getPlaylistLib().iterator(); playlistIt.hasNext(); ) {
                    if (playlistIt.next().getListName().equals(playlistNameStr)) {
                        System.out.println("Playlist already exists.");
                    } else {
                        addAfter = new Playlist(playlistNameStr);
                    }
                }
                if (addAfter != null)
                    player.getPlaylistLib().add(addAfter);

            } else {
                player.getPlaylistLib().add(new Playlist(playlistNameStr));
            }
            playlistName.setText("");
            updatePlaylists();
        }
    }

    // Open settings FXML as new Window,
    //  exchanging the controllers to allow data to pass between
    private void openSettingsWindow(MouseEvent event) {
        try {
            URL view = Player.class.getClassLoader().getResource("client/player/view/Settings.fxml");
            FXMLLoader loader = new FXMLLoader(view);

            Parent root = loader.load();
            Scene scene = new Scene(root);

            Stage stage = new Stage();

            // save the settingsController obj in case we need in future
            SettingsController settingsController = loader.getController();

            settingsController.setMainController(this);

            stage.setScene(scene);
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.setTitle("Settings");
            stage.show();
            // detect settings applied during the settingsWindow life-cycle
            // via the onCloseRequest event
            stage.setOnCloseRequest(windowEvent -> {
                if (settingsController.merge) {
                    sortType = "merge";
                } else {
                    sortType = "quick";
                }
                System.out.println(sortType);
            });

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // Add a ComboBox to the window, in place of the sort button
    //  Sorting options contained in the combo box
    private void sort() {
        // get the Audio objects from the current playlist via jfx ListView
        Audio[] sortArr = new Audio[currentPlaylist.getItems().size()];
        for (int i = 0; i < currentPlaylist.getItems().size(); i++) {
            sortArr[i] = currentPlaylist.getItems().get(i);
        }

        // options for a comboBox
        ObservableList<String> options = FXCollections.observableArrayList(
                "Title (Low -> High)",
                "Title (High -> Low)",
                "Duration (Low -> High)",
                "Duration (High -> Low)"
        );

        ComboBox comboBox = new ComboBox(options);
        audioPane.getChildren().add(comboBox);

        // comboBox positioning
        comboBox.setPrefWidth(50);
        comboBox.setPrefHeight(32);

        comboBox.setLayoutX(sort.getLayoutX());
        comboBox.setLayoutY(sort.getLayoutY());
        AnchorPane.setBottomAnchor(comboBox, AnchorPane.getBottomAnchor(sort));
        AnchorPane.setRightAnchor(comboBox, AnchorPane.getRightAnchor(sort));

        // focus on the comboBox after clicking sort button
        comboBox.requestFocus();

        QuickSort<Audio> quickSort = new QuickSort<>();
        MergeSort<Audio> mergeSort = new MergeSort<>();

        // If we have lost focus on the comboBox, remove it.
        comboBox.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                audioPane.getChildren().remove(comboBox);
            }
        });

        // detect merge sort or quick sort
        boolean merge;
        if (sortType == "merge")
            merge = true;
        else
            merge = false;

        // When a sorting option has been selected,
        //  perform either merge or quick sort
        comboBox.valueProperty().addListener((observable, oldItem, newItem) -> {
            if (newItem != null) {
                switch(newItem.toString()) {
                    case "Title (Low -> High)":
                        if (merge)
                            mergeSort.sort(sortArr, new SortByName(), true);
                        else
                            quickSort.sort(sortArr, new SortByName(), true);
                        break;
                    case "Title (High -> Low)":
                        if (merge)
                            mergeSort.sort(sortArr, new SortByName(), false);
                        else
                            quickSort.sort(sortArr, new SortByName(), false);
                        break;
                    case "Duration (Low -> High)":
                        if (merge)
                            mergeSort.sort(sortArr, new SortByDuration(), true);
                        else
                            quickSort.sort(sortArr, new SortByDuration(), true);
                        break;
                    case "Duration (High -> Low)":
                        if (merge)
                            mergeSort.sort(sortArr, new SortByDuration(), false);
                        else
                            quickSort.sort(sortArr, new SortByDuration(), false);
                        break;
                    default:
                        break;
                }

                audioPane.getChildren().remove(comboBox);
            }
            currentPlaylist.getItems().clear();
            currentPlaylist.getItems().addAll(sortArr);
        });
    }

    // Search text field key event
    //  when user presses ENTER, trigger binarySearch(Object[], String) iterative method
    private void binarySearch(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            String searchTerm = search.getText();

            System.out.println("Searching for: " + searchTerm);

            // searchTerm must be of a substantial length
            if (searchTerm.length() > 4) {
                currentPlaylist.getItems().sort(Comparator.naturalOrder());
                // sort the playlist by the natural order

                Audio returnObj = (Audio) binarySearch(currentPlaylist.getItems().toArray(), searchTerm);
                if (returnObj != null) {
                    // select, and scroll to the returned object
                    currentPlaylist.getSelectionModel().select(returnObj);
                    currentPlaylist.scrollTo(returnObj);
                }
                search.clear();
            }
        }
    }

    // Iterative binarySearch algorithm
    private Object binarySearch(Object[] sortedArray, String target) {
        // convert to array of Audio obj, return null if not possible
        Audio[] array = new Audio[sortedArray.length];
        for (int i = 0; i < sortedArray.length; i++) {
            try {
                array[i] = (Audio) sortedArray[i];
            } catch (Exception e) {
                return null;
            }
        }

        int low = 0, high = sortedArray.length;

        while (low <= high) {
            int middle = (low + high) / 2;
            if (middle < array.length) {
                if (array[middle].getName().compareTo(target) == 0) {
                    // found !
                    return array[middle];
                } else if (array[middle].getName().compareTo(target) > 0) {
                    high = middle - 1;
                } else {
                    // array[middle] < target
                    low = middle + 1;
                }
            }
        }
        // if code reaches this point, the song wasn't found
        return null;
    }

    public void updateQueue() {
        pQueue.getItems().clear();
        for (Audio audio : player.queue.playQueue) {
            pQueue.getItems().add(audio.getName());
        }
        if (pQueue.getItems().size() == 0) {
            pQueue.getItems().add("Empty.");
        }
    }

    void updatePlaylist() {
        ObservableList<Audio> songs = FXCollections.observableArrayList();
        if (player.getPlaylist().getSongs().length > 0) {
            for (Audio audio : player.getPlaylist().getSongs()) {
                songs.add(audio);
            }
            currentPlaylist.setItems(songs);
        }
    }

    private void updatePlaylists() {
        ObservableList<String> names = FXCollections.observableArrayList();
        for (Playlist playlist : player.getPlaylistLib()) {
            names.add(playlist.getListName());
        }
        playLists.setItems(names);
    }

    public double getVolume() {
        return volume.getValue() / 100;
    }

    public MusicPlayer getPlayer() {
        return this.player;
    }

    public void shutdown() {
        serialize();
        player.exit = true;
        Platform.exit();
    }
}
