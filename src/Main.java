import com.sun.javafx.tk.Toolkit;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaException;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.floor;
import static java.lang.String.format;

public class Main extends Application {

    private Media pick = null;
    private MediaPlayer player = null;

    private String style = "resource/style2.css";

    private Button playButton = null;
    private Button fileSelector = null;
    private Button fileSelectorAdd = null;
    private Button nextSong = null;
    private Button previousSong = null;
    private Slider slider = null;

    private TableView<PlaylistElement> playlistView = null;
    private ObservableList<PlaylistElement> playlistObservableData = null;

    private Label time;
    private Label title;
    private Duration duration;

    private int currentSongIndex = 0;

    Scanner in;
    PrintWriter pw = null;

    FileChooser fc = null;
    String path;

    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    private ProgressBar pb;
    private ProgressBar volumeb;

    private Slider volumeSlider;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("MP3Nano");
/*

        //////////////INIT///////////////
*/
        initializeGui();
        initializeFileChooser();
        initializePlaylistObservableData();
        initializePlaylistView();
        setInitialSongs();

        //////////////////////////////////////////////

        setCurrentSong();

        if(playlistObservableData.size() != 0)
            setPlayerListeners();

        setGuiListeners();

        /////////////////////////////////////////////

        initializeWindow(primaryStage);

        /////////////////////////////////
    }

    //////////////FUNCTIONS///////////////

    private void initializeGui(){
        time = new Label();
        title = new Label();

        time.getStylesheets().add(style);
        title.getStylesheets().add(style);

        title.setStyle(
                "-fx-font: 20px Tahoma;"
        );

        slider = new Slider();
        playButton = new Button();

        setPlayButton(true);

        playButton.setLayoutX((WIDTH/2-64/2));
        playButton.setLayoutY(50);

        fileSelector = new Button();

        fileSelector.setStyle(
                "-fx-background-image: url('resource/music.png');"+
                        "-fx-background-size: 16px;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-radius: 32em; " +
                        "-fx-min-width: 32px; " +
                        "-fx-min-height: 32px; " +
                        "-fx-max-width: 32px; " +
                        "-fx-max-height: 32px;"
        );


        fileSelector.setLayoutX(60);
        fileSelector.setLayoutY(15);

        fileSelectorAdd = new Button();

        fileSelectorAdd.setStyle(
                "-fx-background-image: url('resource/musicAdd.png');"+
                        "-fx-background-size: 16px;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-radius: 32em; " +
                        "-fx-min-width: 32px; " +
                        "-fx-min-height: 32px; " +
                        "-fx-max-width: 32px; " +
                        "-fx-max-height: 32px;"
        );


        fileSelectorAdd.setLayoutX(20);
        fileSelectorAdd.setLayoutY(15);

        previousSong = new Button();

        previousSong.setStyle(
                "-fx-background-image: url('resource/previous.png');"+
                        "-fx-background-size: 20px;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-radius: 40em; " +
                        "-fx-min-width: 40px; " +
                        "-fx-min-height: 40px; " +
                        "-fx-max-width: 40px; " +
                        "-fx-max-height: 40px;"
        );

        previousSong.setLayoutX(playButton.getLayoutX() - 50);
        previousSong.setLayoutY(playButton.getLayoutY() + 50);

        nextSong = new Button();

        nextSong.setStyle(
                "-fx-background-image: url('resource/next.png');"+
                        "-fx-background-size: 20px;" +
                        "-fx-background-position: center center;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-radius: 40em; " +
                        "-fx-min-width: 40px; " +
                        "-fx-min-height: 40px; " +
                        "-fx-max-width: 40px; " +
                        "-fx-max-height: 40px;"
        );


        nextSong.setLayoutX(playButton.getLayoutX() + 50 + 20);
        nextSong.setLayoutY(playButton.getLayoutY() + 50);

        slider.setMinSize(WIDTH-50, 20);
        slider.setMaxSize(WIDTH-50, 20 );
        slider.setLayoutX(WIDTH/2-250/2);
        slider.setLayoutY(150);

        slider.getStylesheets().add(style);

        time.setTranslateY(slider.getLayoutY() + 20);
        title.setTranslateY(slider.getLayoutY() + 30);

        pb = new ProgressBar(0);
        pb.setMinWidth(WIDTH-50 - 6);
        pb.setMaxWidth(WIDTH-50 - 6);
        pb.setMinHeight(8);
        pb.setMaxHeight(8);

        pb.getStylesheets().add(style);
        pb.setLayoutX(WIDTH/2-250/2 + 3);
        pb.setLayoutY(150+6.5);

        volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setMinWidth(8);
        volumeSlider.setMaxWidth(8);
        volumeSlider.setMinHeight(100);
        volumeSlider.setMaxHeight(100);

        volumeSlider.setLayoutX(WIDTH - 30);
        volumeSlider.setLayoutY(30);

        volumeb = new ProgressBar(0.5);
        volumeb.setMinWidth(94);
        volumeb.setMaxWidth(94);
        volumeb.setMinHeight(8);
        volumeb.setMaxHeight(8);
        volumeb.getTransforms().setAll(
                new Translate(0, 100),
                new Rotate(-90, 0, 0));

        volumeb.getStylesheets().add(style);
        volumeb.setLayoutX(WIDTH - 30);
        volumeb.setLayoutY(30 - 3);

        volumeSlider.setOrientation(Orientation.VERTICAL);
        volumeSlider.getStylesheets().add(style);
    }

    private boolean setCurrentSong(){
        try {
            if (playlistObservableData == null) {
                throw new Exception("Cannot assing current song, Playlist initialization error");
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

        if(playlistObservableData.size() == 0)
            return false;

        if(currentSongIndex == -1)
            currentSongIndex = 0;
        else if(currentSongIndex == playlistObservableData.size())
            currentSongIndex--;

        String path = playlistObservableData.get(currentSongIndex).getSongPath();

        try{
            pick = new Media(path);



        }catch( MediaException e){
            e.printStackTrace();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Wrong format!");
                    alert.setHeaderText("Data format not supported!");
                    alert.setContentText("Path: " + path + "\nI am deeply sorry...");

                    alert.showAndWait();
                }
            });

            return setNextSong();
        }

        if(player != null)
            player.stop();

        player = null;
        player = new MediaPlayer(pick);

        try {
            player = new MediaPlayer(pick);
            player.stop();

            if (player == null) {
                throw new Exception("Cannot assing current song, Cannot read file");
            }
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        setPlayerListeners();
        return true;
     //   player.play();
    }

    private boolean setNextSong(){
        System.out.println("setNextSong");
        try {
            if (playlistObservableData == null) {
                throw new Exception("Cannot assing current song, Playlist initialization error");
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

        currentSongIndex++;

        if(currentSongIndex >= playlistObservableData.size()){
            updateToNone();
            currentSongIndex = playlistObservableData.size();
            return false;
        }

        if( setCurrentSong() == true){
            player.play();
            setPlayButton(false);
            return true;
        }
        else
            return false;
    }

    private boolean setPreviousSong(){
        try {
            if (playlistObservableData == null) {
                throw new Exception("Cannot assing current song, Playlist initialization error");
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

        currentSongIndex--;

        if(currentSongIndex < 0){
            updateToNone();
            currentSongIndex = -1;
            return false;
        }

        if( setCurrentSong() == true){
            player.play();
            setPlayButton(false);
            return true;
        }
        else
            return false;
    }

    private void updateToNone(){

        if(player != null)
           player.stop();

        player = null;
        time.setText("");
        title.setText("");
        slider.setDisable(false);
        slider.setValue(0);

        setPlayButton(true);
    }

    private void setPlayButton(boolean val){
        if(val){
            playButton.setStyle(
                    "-fx-background-image: url('resource/play.png');" +
                            "-fx-background-size: 32px;" +
                            "-fx-background-position: center center;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 64em; " +
                            "-fx-min-width: 64px; " +
                            "-fx-min-height: 64px; " +
                            "-fx-max-width: 64px; " +
                            "-fx-max-height: 64px;"
            );
        }

        else{
            playButton.setStyle(
                    "-fx-background-image: url('resource/stop.png');" +
                            "-fx-background-size: 32px;" +
                            "-fx-background-position: center center;" +
                            "-fx-background-repeat: no-repeat;" +
                            "-fx-background-radius: 64em; " +
                            "-fx-min-width: 64px; " +
                            "-fx-min-height: 64px; " +
                            "-fx-max-width: 64px; " +
                            "-fx-max-height: 64px;"
            );
        }
    }

    private boolean setInitialSongs(){

        try{
            in = new Scanner(new FileReader("src/resource/last.txt"));
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

        try {
            if (playlistObservableData == null) {
                throw new Exception("Playlist initialization error");
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

        currentSongIndex = 0;
        playlistObservableData.clear();

        if(in != null){
            try{
                while(in.hasNextLine()) {
                    path = in.nextLine();
                    path.trim();
                    playlistObservableData.add(new PlaylistElement(path));
                }
            }catch(Exception e){
                e.printStackTrace();
                path = null;
                return false;
            }
        }

      //  setPlayerListeners();

        return true;
    }

    private void initializeFileChooser(){
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio" , "*.mp3"));
    }

    private void initializePlaylistObservableData(){
        playlistObservableData = FXCollections.observableArrayList();
    }

    private void initializePlaylistView(){
        playlistView = new TableView<PlaylistElement>();
        playlistView.setEditable(false);

        playlistView.setLayoutX(WIDTH + 10);
        playlistView.setLayoutY(10);

        playlistView.setPrefWidth(WIDTH - 20);
        playlistView.setPrefHeight(HEIGHT - 20);

        TableColumn playIndicator = new TableColumn("Play");
        playIndicator.setResizable(false);
        playIndicator.setPrefWidth(30);
        playIndicator.setMaxWidth(30);
        playIndicator.setSortable(false);

        playlistView.getColumns().add(playIndicator);

        TableColumn songTitle = new TableColumn("Title");
        songTitle.setResizable(false);
        songTitle.setPrefWidth(playlistView.getPrefWidth()-53);
        songTitle.setMaxWidth(playlistView.getPrefWidth()-53);
        songTitle.setSortable(false);


        songTitle.setCellValueFactory(
                new PropertyValueFactory<PlaylistElement, String>("songTitle")
        );

        playlistView.getColumns().add(songTitle);

        try {
            if (playlistObservableData == null) {
                throw new Exception("Playlist initialization error; PlayListView Initialization failed");
            }
        } catch(Exception e){
            e.printStackTrace();
            return;
        }

        playlistView.getStylesheets().add(style);
        playlistView.setPlaceholder(new Label("Set songs to play"));

        PseudoClass current = PseudoClass.getPseudoClass("current");
        PseudoClass notCurrent = PseudoClass.getPseudoClass("notCurrent");



        playlistView.setItems(playlistObservableData);
    }

    private boolean setNewPlaylist(){
        initializePlaylistObservableData();

        boolean val = addSongs();

        playlistView.setItems(playlistObservableData);

        return val;
    }

    private boolean addSongs(){

        if(fc == null){
            initializeFileChooser();
        }

        try {
            if (playlistObservableData == null) {
                throw new Exception("Playlist initialization error");
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

        List<File> songListPrev = fc.showOpenMultipleDialog(null);

        for(File file : songListPrev){
            String newSong = file.getAbsolutePath();
            newSong = newSong.replace("\\", "/");
            newSong = Paths.get(newSong).toUri().toString();
            playlistObservableData.add(new PlaylistElement(newSong));
        }

        if(songListPrev.size() > 0 ){

            return true;
        }

        return false;
    }

    private boolean addSongs(List<File> list){

       try {
            if (playlistObservableData == null) {
                throw new Exception("Playlist initialization error");
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

           for(File file : list){
            String newSong = file.getAbsolutePath();
            newSong = newSong.replace("\\", "/");
            newSong = Paths.get(newSong).toUri().toString();
            playlistObservableData.add(new PlaylistElement(newSong));
        }

        if(list.size() > 0 ){

            return true;
        }

        return false;
    }

    private void initializeWindow(Stage primaryStage){
        Pane root = new Pane();
        root.getStylesheets().add(style);
        root.getChildren().add(playButton);
        root.getChildren().addAll(pb, slider);
        root.getChildren().add(time);
        root.getChildren().add(title);
        root.getChildren().add(fileSelector);
        root.getChildren().add(fileSelectorAdd);
        root.getChildren().addAll(volumeb, volumeSlider);
        root.getChildren().add(playlistView);
        root.getChildren().add(previousSong);
        root.getChildren().add(nextSong);

        Scene mainScene = new Scene(root, WIDTH*2, HEIGHT, Color.GRAY);

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);


        mainScene.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();

                if(db.hasFiles()){
                    event.acceptTransferModes(TransferMode.COPY);
                }
                else{
                    event.consume();
                }
            }
        });

        mainScene.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();

                boolean success = true;

                if(db.hasFiles()){
                    addSongs(db.getFiles());
                    success = true;
                }

                event.setDropCompleted(success);
                event.consume();
            }
        });

        primaryStage.show();
    }

    ////////////////////

    protected void updateValues(){

        if(time != null && slider != null && duration != null && player != null){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Duration currentTime = player.getCurrentTime();
                    slider.setDisable(true);
                    time.setText(formatTime(currentTime, duration));
                    time.setLayoutX(WIDTH/2 - Toolkit.getToolkit().getFontLoader().computeStringWidth(time.getText(), time.getFont())/2);

                    String temp = pick.getSource().toString();
                    int pos = temp.lastIndexOf('/');
                    int pos2 = temp.lastIndexOf('.');
                    temp = pick.getSource().toString().substring(pos+1, pos2);
                    temp = temp.replace("%20", " ");
                    if(temp.length() > 28){
                        temp = temp.substring(0,28);
                        temp += "...";
                    }

                    title.setText(temp);
                    title.setLayoutX(WIDTH/2 - Toolkit.getToolkit().getFontLoader().computeStringWidth(title.getText(), title.getFont())/2);
                    slider.setDisable(false);
                    if(!slider.isDisable() && duration.greaterThan(Duration.ZERO) && !slider.isValueChanging()){
                        slider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                    }
                }
            });
        }

    }

    private void setGuiListeners(){

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if(playlistObservableData.size() == 0)
                    return;

                Thread one = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        updateValues();

                        if(player == null){

                            if( setCurrentSong() == false)
                                return;
                        }


                        MediaPlayer.Status status = player.getStatus();

                        while(status == MediaPlayer.Status.UNKNOWN){
                            status = player.getStatus();
                            try{
                                Thread.sleep(1);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }


                        if(status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY
                                || status == MediaPlayer.Status.STOPPED){
                            player.play();
                            setPlayButton(false);
                        }
                        else{
                            player.pause();
                            setPlayButton(true);
                        }

                    }
                });

                one.start();
            }
        });

        fileSelector.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                setNewPlaylist();
                setPlayerListeners();

                currentSongIndex = 0;
                setCurrentSong();

                player.play();
            }
        });

        fileSelectorAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addSongs();
                setPlayerListeners();
            }
        });

        previousSong.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                setPreviousSong();
            }
        });

        nextSong.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

               setNextSong();
            }
        });

        slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {

                if( slider.isValueChanging()){
                    player.seek(duration.multiply(slider.getValue() / 100.0));
                    updateValues();
                }
            }
        });

        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                pb.setProgress(new_val.doubleValue() / 100.0);
                updateValues();
            }
        });

        slider.setOnMousePressed( (value) ->{
            double val = ((double)value.getX() / (double)(pb.getWidth()));
            val *= 100;
            double val2 = val;

            if(val2 < 45)
                val2--;
            if(val2 > 70)
                val2++;

            if(val2 < 10)
                val2--;

            if(val < 0)
                val = 0;
            if(val < 100)
                val = 100;

            if(val >= slider.getMin() && val <= slider.getMax() )
            {
                slider.valueProperty().set(val2);

                player.seek(duration.multiply(slider.getValue() / 100.0));
                updateValues();
            }

        });

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                volumeb.setProgress(newValue.doubleValue());
            }
        });
    }

    private void setPlayerListeners(){

        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                updateValues();
            }
        });

        player.currentTimeProperty().addListener((Observable ov) -> {
            updateValues();
        });

        player.setOnReady(() -> {
            duration = player.getMedia().getDuration();
            updateValues();
        });

        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                setNextSong();
                }
            });


        player.volumeProperty().bindBidirectional(volumeSlider.valueProperty());
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
}
