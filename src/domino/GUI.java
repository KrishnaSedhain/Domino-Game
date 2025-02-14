package domino;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GUI extends Application {

    private BorderPane root;
    private HBox middlePlayArea;
    private HBox humanPlayArea;
    private HBox computerPlayArea;
    private VBox menuPanel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Domino Game Board");

        initializeLayout();
        setupScene(primaryStage);
    }

    private void initializeLayout() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #8B4513;");

        middlePlayArea = createHBox(Pos.CENTER, "-fx-background-color: #D3D3D3; -fx-border-color: black;");

        humanPlayArea = createHBox(Pos.CENTER, "-fx-background-color: #4B0082; -fx-border-color: white;");
        VBox humanTray = createLabeledVBox("Player's Tray", humanPlayArea);

        computerPlayArea = createHBox(Pos.CENTER, "-fx-background-color: #4B0082; -fx-border-color: white;");
        VBox computerTray = createLabeledVBox("Computer's Tray", computerPlayArea);

        menuPanel = createMenuPanel();

        root.setCenter(middlePlayArea);
        root.setTop(computerTray);
        root.setBottom(humanTray);
        root.setRight(menuPanel);
    }

    private HBox createHBox(Pos alignment, String style) {
        HBox hbox = new HBox();
        hbox.setAlignment(alignment);
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(10));
        hbox.setStyle(style);
        return hbox;
    }

    private VBox createLabeledVBox(String labelText, HBox hbox) {
        Label label = new Label(labelText);
        label.setTextFill(Color.WHITE);
        VBox vbox = new VBox(label, hbox);
        vbox.setAlignment(Pos.CENTER);
        return vbox;
    }

    private VBox createMenuPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #A9A9A9; -fx-border-color: black;");

        Label menuLabel = new Label("Game Menu");
        menuLabel.setTextFill(Color.BLACK);

        Button playButton = new Button("Play");
        Button drawButton = new Button("Draw");
        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> System.exit(0));

        panel.getChildren().addAll(menuLabel, playButton, drawButton, exitButton);
        return panel;
    }

    private void setupScene(Stage primaryStage) {
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
