package domino;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class GUI extends Application {

    private BorderPane root;
    private HBox middlePlayArea;
    private HBox humanPlayArea;
    private HBox computerPlayArea;
    private VBox menuPanel;
    private Board board = new Board();
    private Player human = new Player(Players.Human);
    private Player computer = new Player(Players.Computer);
    private Main gameManager = new Main();
    private String currentRadioButton = "";
    private int comboBoxSelection = 0;
    private String rotateOptionSelection = "";
    private boolean endGame;
    private String lastPlayedPlayerForWinnerSelection = "human";
    private List<ImageView> listToRemoveHumanTrayAfterSelection = new ArrayList<>();
    private List<Domino> listToRemoveHumanTrayTrack = new ArrayList<>();
    private boolean isPopUpRequired = false;
    private Deque<Domino> latestAddedDice = new LinkedList<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Domino Game Board");

        initializeLayout();
        setupScene(primaryStage);

        gameManager.distributeDominos();
        setupMenuPanel();
        setupAnimationTimer();
    }

    private void initializeLayout() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #8B4513;");

        middlePlayArea = createHBox(Pos.CENTER, "-fx-background-color: #D3D3D3; -fx-border-color: black;");

        humanPlayArea = createHBox(Pos.CENTER, "-fx-background-color: #4B0082; -fx-border-color: white;");
        VBox humanTray = createLabeledVBox("Player's Tray", humanPlayArea);

        computerPlayArea = createHBox(Pos.CENTER, "-fx-background-color: #4B0082; -fx-border-color: white;");
        VBox computerTray = createLabeledVBox("Computer's Tray", computerPlayArea);

        menuPanel = new VBox(10);
        menuPanel.setPadding(new Insets(10));
        menuPanel.setStyle("-fx-background-color: #A9A9A9; -fx-border-color: black;");

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

    private void setupMenuPanel() {
        Label menuLabel = new Label("Game Menu");
        menuLabel.setTextFill(Color.BLACK);

        Button playButton = new Button("Play");
        playButton.setOnAction(event -> handlePlayButton());

        Button drawButton = new Button("Draw");
        drawButton.setOnAction(event -> handleDrawButton());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> System.exit(0));

        menuPanel.getChildren().addAll(menuLabel, playButton, drawButton, exitButton);

        setupDominoSelectionComboBox();
        setupWhichSideToPlay();
        setupRotateToggleBox();
    }

    private void setupDominoSelectionComboBox() {
        VBox dominoSelectionVbox = new VBox();
        Label dominoSelectionLabel = new Label("Please select which domino to play");
        dominoSelectionLabel.setTextFill(Color.WHITE);
        ComboBox<Integer> dominoSelectionOptions = new ComboBox<>();

        for (int i = 0; i < gameManager.getBoard().getAvailableDice().size(); i++) {
            dominoSelectionOptions.getItems().add(i);
        }
        dominoSelectionOptions.setOnAction(event -> comboBoxSelection = dominoSelectionOptions.getValue());

        dominoSelectionVbox.getChildren().addAll(dominoSelectionLabel, dominoSelectionOptions);
        menuPanel.getChildren().add(dominoSelectionVbox);
    }

    private void setupWhichSideToPlay() {
        VBox leftOrRightOption = new VBox();
        Label comboBoxLabel = new Label("Select which side to play the domino");
        comboBoxLabel.setTextFill(Color.WHITE);
        ToggleGroup locationGroup = new ToggleGroup();
        RadioButton rbLeft = new RadioButton("Left");
        RadioButton rbRight = new RadioButton("Right");
        leftOrRightOption.setSpacing(3);
        rbLeft.setToggleGroup(locationGroup);
        rbRight.setToggleGroup(locationGroup);
        leftOrRightOption.getChildren().addAll(comboBoxLabel, rbLeft, rbRight);
        locationGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> {
                    RadioButton locationSelected = (RadioButton) locationGroup.getSelectedToggle();
                    if (locationSelected != null) {
                        if (locationSelected.getText().equals("Left")) {
                            currentRadioButton = "l";
                        } else if (locationSelected.getText().equals("Right")) {
                            currentRadioButton = "r";
                        }
                    }
                }
        );
        menuPanel.getChildren().add(leftOrRightOption);
    }

    private void setupRotateToggleBox() {
        VBox rotateVbox = new VBox();
        Label rotateLabel = new Label("Rotate Domino");
        rotateLabel.setTextFill(Color.WHITE);
        ToggleGroup rotateGroup = new ToggleGroup();
        RadioButton yesButton = new RadioButton("Yes");
        RadioButton noButton = new RadioButton("No");
        yesButton.setToggleGroup(rotateGroup);
        noButton.setToggleGroup(rotateGroup);
        rotateVbox.getChildren().addAll(rotateLabel, yesButton, noButton);
        rotateGroup.selectedToggleProperty().addListener(
                (observable, oldValue, newValue) -> {
                    RadioButton selectedButton = (RadioButton) rotateGroup.getSelectedToggle();
                    if (selectedButton != null) {
                        if (selectedButton.getText().equals("Yes")) {
                            rotateOptionSelection = "y";
                        } else if (selectedButton.getText().equals("No")) {
                            rotateOptionSelection = "n";
                        }
                    }
                }
        );
        menuPanel.getChildren().add(rotateVbox);
    }

    private void handlePlayButton() {
        if ((board.getAvailableDice().size() >= 0 || gameManager.hasValidMove(human.getTray(), board)) &&
                board.getAvailableDice() != null) {
            if (comboBoxSelection >= human.getTray().size()) {
                isPopUpRequired = true;
            }
            if (isPopUpRequired) {
                showPopupWindow("Selected dice is empty!!!", "Invalid Play. Try Again!");
                isPopUpRequired = false;
                return;
            }
            Domino dice = human.getDiceFromTray(comboBoxSelection);
            if (!isValidMove(dice)) {
                isPopUpRequired = true;
            }
            if (isPopUpRequired) {
                showPopupWindow("Incorrect Play", "Invalid Play. Try Again!");
                isPopUpRequired = false;
                return;
            }
            if (currentRadioButton.equals("l")) {
                if (rotateOptionSelection.equals("y")) {
                    dice.rotateDomino();
                }
                board.placeOnLeft(dice, Players.Human);
            } else if (currentRadioButton.equals("r")) {
                if (rotateOptionSelection.equals("y")) {
                    dice.rotateDomino();
                }
                board.placeOnRight(dice, Players.Human);
            }
            human.removedDiceFromPlayerTray(comboBoxSelection);
            addDiceImageToPlayersTray(human, humanPlayArea, comboBoxSelection);
        }
        if (gameEndStatus(human)) {
            gameOverGUI(human);
        }
        computerPlay();
        if (gameEndStatus(computer)) {
            gameOverGUI(computer);
        }
    }

    private void handleDrawButton() {
        for (Domino dice : human.getTray()) {
            if (isValidMove(dice)) {
                showPopupWindow("Valid Play Exists", "You have a valid play. Please check again!");
                return;
            }
        }
        if (board.getAvailableDice() == null) {
            showPopupWindow("Boneyard is empty", "Cannot draw. Boneyard is empty!!!");
            gameOverGUI(computer);
            return;
        }
        Domino pickedDice = board.drawFromBoneyard();
        human.addDiceToPlayerTray(pickedDice);
        ImageView pickedDomino = getImage(pickedDice.getLeftNumDots(), pickedDice.getRightNumDots(), false);
        humanPlayArea.getChildren().add(pickedDomino);
    }

    private void setupAnimationTimer() {
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateLabels();
            }
        };
        animationTimer.start();
    }

    private void updateLabels() {
        Label boneyardCount = new Label("Boneyard has " + board.getAvailableDice().size() + " pieces.");
        Label computerCount = new Label("Computer has " + computer.getTray().size() + " pieces.");
        Label humanCount = new Label("Human has " + human.getTray().size() + " pieces.");
        boneyardCount.setTextFill(Color.WHITE);
        computerCount.setTextFill(Color.WHITE);
        humanCount.setTextFill(Color.WHITE);

        VBox topBox = (VBox) root.getTop();
        topBox.getChildren().clear();
        topBox.getChildren().addAll(boneyardCount, computerCount, humanCount);
    }

    private void addDiceImageToPlayersTray(Player currPlayer, HBox playArea, int removeIndex) {
        for (int i = 0; i < currPlayer.getTray().size(); i++) {
            Domino dice = currPlayer.getDiceFromTray(i);
            ImageView domino = getImage(dice.getLeftNumDots(), dice.getRightNumDots(), false);
            if (removeIndex >= 0 && i == removeIndex) {
                playArea.getChildren().remove(domino);
            } else {
                playArea.getChildren().add(domino);
            }
        }
    }

    private boolean isValidMove(Domino dice) {
        if (board.getAvailableDice().size() == 0) {
            return true;
        }
        int leftNum = dice.getLeftNumDots();
        int rightNum = dice.getRightNumDots();
        int playedLeftNum = board.getAvailableDice().getFirst().getLeftNumDots();
        int playedRightNum = board.getAvailableDice().getLast().getRightNumDots();
        if (rotateOptionSelection.equals("n")) {
            return leftNum == playedRightNum || rightNum == playedLeftNum;
        } else if (rotateOptionSelection.equals("y")) {
            return leftNum == playedLeftNum || rightNum == playedRightNum;
        }
        return false;
    }

    private ImageView getImage(int m, int n, boolean rotate) {
        if (rotate) {
            int temp = m;
            m = n;
            n = temp;
        }
        String imageName = "/resources/" + m + "," + n + ".png";
        InputStream inputStream = getClass().getResourceAsStream(imageName);
        if (inputStream == null) {
            System.err.println("Resource not found: " + imageName);
            return null;
        }
        Image dominoPNG = new Image(inputStream);
        ImageView domino = new ImageView(dominoPNG);
        listToRemoveHumanTrayAfterSelection.add(domino);
        return domino;
    }

    private void computerPlay() {
        ArrayList<Domino> computerTray = computer.getTray();
        for (int i = 0; i < computerTray.size(); i++) {
            Domino computerDice = computerTray.get(i);
            int computerLeftPlay = computerDice.getLeftNumDots();
            int computerRightPlay = computerDice.getRightNumDots();
            int leftEnd = board.getAvailableDice().getFirst().getLeftNumDots();
            int rightEnd = board.getAvailableDice().getLast().getRightNumDots();
            if (computerLeftPlay == leftEnd) {
                computerDice.rotateDomino();
                board.placeOnLeft(computerDice, Players.Computer);
                middlePlayArea.getChildren().add(0, getImage(computerLeftPlay, computerRightPlay, true));
                computerTray.remove(i);
                return;
            } else if (computerRightPlay == rightEnd) {
                computerDice.rotateDomino();
                board.placeOnRight(computerDice, Players.Computer);
                middlePlayArea.getChildren().add(getImage(computerLeftPlay, computerRightPlay, true));
                computerTray.remove(i);
                return;
            } else if (computerLeftPlay == rightEnd) {
                board.placeOnRight(computerDice, Players.Computer);
                middlePlayArea.getChildren().add(getImage(computerLeftPlay, computerRightPlay, false));
                computerTray.remove(i);
                return;
            } else if (computerRightPlay == leftEnd) {
                board.placeOnLeft(computerDice, Players.Computer);
                middlePlayArea.getChildren().add(0, getImage(computerLeftPlay, computerRightPlay, false));
                computerTray.remove(i);
                return;
            }
        }
        while (board.getAvailableDice() != null) {
            Domino diceFromBoneyard = board.drawFromBoneyard();
            computerTray.add(diceFromBoneyard);
            if (gameManager.doesPickedDiceMatchEitherEnd(diceFromBoneyard, board)) {
                break;
            }
        }
        if (board.getAvailableDice() == null) {
            return;
        }
        computerPlay();
    }

    private void gameOverGUI(Player playedLast) {
        String playerWins = "You win the game!";
        String computerWins = "The computer wins!";

        Stage gameOverWindow = new Stage();
        gameOverWindow.setTitle("Game Over!");
        VBox gameOverLayout = new VBox(10);
        Scene gameOverScene = new Scene(gameOverLayout, 350, 100);
        Label winnerMessage = new Label(playedLast.equals(human) ? playerWins : computerWins);
        winnerMessage.setTextFill(Color.WHITE);
        Button closeButton = new Button("Ok");
        closeButton.setOnAction(e -> gameOverWindow.close());
        gameOverWindow.initModality(Modality.APPLICATION_MODAL);
        gameOverLayout.getChildren().addAll(winnerMessage, closeButton);
        gameOverLayout.setAlignment(Pos.CENTER);
        gameOverWindow.setScene(gameOverScene);
        gameOverWindow.showAndWait();
        System.exit(0);
    }

    private boolean gameEndStatus(Player lastPlayedPlayer) {
        boolean validPlayForHuman = gameManager.hasValidMove(human.getTray(), board);
        boolean validPlayForComputer = gameManager.hasValidMove(computer.getTray(), board);
        if (!validPlayForHuman && !validPlayForComputer) {
            endGame = true;
            gameOverGUI(lastPlayedPlayer);
            return true;
        }
        return false;
    }

    private void showPopupWindow(String title, String message) {
        Stage popupWindow = new Stage();
        popupWindow.setTitle(title);
        VBox popupLayout = new VBox(10);
        Scene popupScene = new Scene(popupLayout, 350, 100);
        Label errorMessage = new Label(message);
        errorMessage.setTextFill(Color.WHITE);
        Button closeButton = new Button("Ok");
        closeButton.setOnAction(e -> popupWindow.close());
        popupWindow.initModality(Modality.APPLICATION_MODAL);
        popupLayout.getChildren().addAll(errorMessage, closeButton);
        popupLayout.setAlignment(Pos.CENTER);
        popupWindow.setScene(popupScene);
        popupWindow.showAndWait();
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