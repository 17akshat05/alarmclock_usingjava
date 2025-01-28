import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AlarmApp extends Application {

    private MediaPlayer mediaPlayer;
    private MediaView mediaView;
    private ImageView backgroundImageView;
    private String alarmTime;
    private File videoFile;
    private File songFile;
    private File backgroundImageFile;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Custom Alarm App");

        // Main layout
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        // Alarm Time Input
        Label timeLabel = new Label("Set Alarm Time (HH:MM):");
        TextField timeField = new TextField();
        root.getChildren().addAll(timeLabel, timeField);

        // Background Image Button
        Button bgImageButton = new Button("Set Background Image");
        bgImageButton.setOnAction(e -> setBackgroundImage());
        root.getChildren().add(bgImageButton);

        // Video Button
        Button videoButton = new Button("Set Video");
        videoButton.setOnAction(e -> setVideo());
        root.getChildren().add(videoButton);

        // Song Button
        Button songButton = new Button("Set Song");
        songButton.setOnAction(e -> setSong());
        root.getChildren().add(songButton);

        // Start Alarm Button
        Button startAlarmButton = new Button("Start Alarm");
        startAlarmButton.setOnAction(e -> startAlarm(timeField.getText()));
        root.getChildren().add(startAlarmButton);

        // Scene
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setBackgroundImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Background Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        backgroundImageFile = fileChooser.showOpenDialog(null);
        if (backgroundImageFile != null) {
            System.out.println("Background image set: " + backgroundImageFile.getAbsolutePath());
        }
    }

    private void setVideo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Video File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi"));
        videoFile = fileChooser.showOpenDialog(null);
        if (videoFile != null) {
            System.out.println("Video set: " + videoFile.getAbsolutePath());
        }
    }

    private void setSong() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Song File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        songFile = fileChooser.showOpenDialog(null);
        if (songFile != null) {
            System.out.println("Song set: " + songFile.getAbsolutePath());
        }
    }

    private void startAlarm(String time) {
        if (time.isEmpty()) {
            System.out.println("Please set the alarm time!");
            return;
        }
        alarmTime = time;

        // Start a thread to monitor the alarm
        Thread alarmThread = new Thread(this::monitorAlarm);
        alarmThread.setDaemon(true);
        alarmThread.start();
    }

    private void monitorAlarm() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        while (true) {
            LocalTime now = LocalTime.now();
            if (now.format(formatter).equals(alarmTime)) {
                javafx.application.Platform.runLater(this::triggerAlarm);
                break;
            }
            try {
                Thread.sleep(1000); // Check every second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void triggerAlarm() {
        Stage alarmStage = new Stage();
        alarmStage.setTitle("Alarm!");

        // Background Image
        if (backgroundImageFile != null) {
            Image backgroundImage = new Image(backgroundImageFile.toURI().toString());
            backgroundImageView = new ImageView(backgroundImage);
            backgroundImageView.setFitWidth(800);
            backgroundImageView.setFitHeight(600);
        }

        // Video
        if (videoFile != null) {
            Media media = new Media(videoFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(800);
            mediaView.setFitHeight(600);
            mediaPlayer.play();
        }

        // Song
        if (songFile != null) {
            Media media = new Media(songFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop the song
            mediaPlayer.play();
        }

        // Stop Button
        Button stopButton = new Button("Stop Alarm");
        stopButton.setOnAction(e -> stopAlarm(alarmStage));

        // Layout
        StackPane alarmLayout = new StackPane();
        if (backgroundImageView != null) {
            alarmLayout.getChildren().add(backgroundImageView);
        }
        if (mediaView != null) {
            alarmLayout.getChildren().add(mediaView);
        }
        alarmLayout.getChildren().add(stopButton);

        // Scene
        Scene alarmScene = new Scene(alarmLayout, 800, 600);
        alarmStage.setScene(alarmScene);
        alarmStage.show();
    }

    private void stopAlarm(Stage alarmStage) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        alarmStage.close();
    }
}
