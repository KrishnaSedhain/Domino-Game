package domino;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
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

    private final Board board = new Board();
    private final Player human = new Player(Players.Human);
    private final Player computer = new Player(Players.Computer);
    private final VBox vBoxToIncludeLabelAndDominos = new VBox();
    private final HBox humanPlayAreaDown = new HBox();
    private final HBox numOfDicesUpdateHbox = new HBox();
    private final HBox middlePlayArea = new HBox();
    private Stage primaryStage;
    private BorderPane root;
    // Left–side container for menu options.
    private final VBox leftMenuOptions = new VBox();

    // Button selections
    private String currentRadioButton = "";
    private int comboBoxSelection = 0;
    private String rotateOptionSelection = "";
    private final Main gameManager = new Main();
    private boolean endGame;
    private String lastPlayedPlayerForWinnerSelection = "human";
    private final List<ImageView> listToRemoveHumanTrayAfterSelection = new ArrayList<>();
    private final List<Domino> listToRemoveHumanTrayTrack = new ArrayList<>();
    private boolean isPopUpRequired = false;
    private Deque<Domino> latestAddedDice = new LinkedList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Domino Game");
        // Initialize game objects
        gameManager.distributeDomino(human, computer, board);

        // --- Style and layout the middle play area ---
        middlePlayArea.setAlignment(Pos.CENTER);
        middlePlayArea.setPrefSize(500, 500);
        middlePlayArea.setPadding(new Insets(15));
        middlePlayArea.setBorder(new Border(new BorderStroke(Color.GAINSBORO,
                BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(3))));

        humanPlayAreaDown.setSpacing(10);
        humanPlayAreaDown.setPadding(new Insets(10));

        // --- Set up the root BorderPane ---
        root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        // --- Bottom: Player’s Tray ---
        Label humanTrayLabel = makeLabel("Player's Tray", 20);
        addDiceImageToPlayersTray(human, humanPlayAreaDown, -1);
        vBoxToIncludeLabelAndDominos.getChildren().addAll(humanTrayLabel, humanPlayAreaDown);
        vBoxToIncludeLabelAndDominos.setAlignment(Pos.CENTER);
        HBox bottomRightContainer = new HBox(vBoxToIncludeLabelAndDominos);
        bottomRightContainer.setAlignment(Pos.BOTTOM_RIGHT);
        bottomRightContainer.setPadding(new Insets(10));

        // --- Top: Game Counts ---
        numOfDicesUpdateHbox.setSpacing(20);
        numOfDicesUpdateHbox.setPadding(new Insets(10));
        numOfDicesUpdateHbox.setAlignment(Pos.CENTER);
        Label boneyardCount = makeLabel("Boneyard has " + board.getAvailableDomino().size() + " pieces.", 25);
        Label computerCount = makeLabel("Computer has " + computer.getTray().size() + " pieces.", 25);
        Label humanCount = makeLabel("Human has " + human.getTray().size() + " pieces.", 25);
        numOfDicesUpdateHbox.getChildren().addAll(boneyardCount, computerCount, humanCount);

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                numOfDicesUpdateHbox.getChildren().removeAll(boneyardCount, computerCount, humanCount);
                int boneyardSize = board.getAvailableDomino() == null ? 0 : board.getAvailableDomino().size();
                boneyardCount.setText("Boneyard has " + boneyardSize + " pieces.");
                computerCount.setText("Computer has " + computer.getTray().size() + " pieces.");
                humanCount.setText("Human has " + human.getTray().size() + " pieces.");
                numOfDicesUpdateHbox.getChildren().addAll(boneyardCount, computerCount, humanCount);
            }
        };
        animationTimer.start();

        // --- Left: Menu Options ---
        leftMenuOptions.setSpacing(10);
        leftMenuOptions.setPadding(new Insets(15));
        leftMenuOptions.setAlignment(Pos.TOP_CENTER);
        leftMenuOptions.setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, new CornerRadii(5), Insets.EMPTY)));
        leftMenuOptions.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY,
                BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        leftMenuOptions.getChildren().addAll(
                createDominoSelectionComboBox(),  // "please select which domino to play"
                createWhichSideToPlay(),          // "select which side to play the domino"
                createRotateToggleBox()           // "Rotate Domino"
        );
        root.setLeft(leftMenuOptions);

        // --- Right: Action Buttons ---
        VBox rightMenuOptions = new VBox(10);
        rightMenuOptions.setPadding(new Insets(15));
        rightMenuOptions.setAlignment(Pos.CENTER);
        rightMenuOptions.setBackground(new Background(new BackgroundFill(Color.DARKSLATEBLUE, new CornerRadii(5), Insets.EMPTY)));
        rightMenuOptions.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY,
                BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        Button playButton = createPlayButton();
        Button drawFromBoneyardButton = createDrawFromBoneYardButton();
        Button exitButton = createExitButton();
        rightMenuOptions.getChildren().addAll(playButton, drawFromBoneyardButton, exitButton);
        root.setRight(rightMenuOptions);

        // --- Assemble the BorderPane ---
        root.setPadding(new Insets(10));
        root.setBottom(bottomRightContainer);
        root.setTop(numOfDicesUpdateHbox);
        root.setCenter(middlePlayArea);

        Scene scene = new Scene(root, 1500, 700);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Creates the rotate domino toggle box.
     */
    private VBox createRotateToggleBox() {
        VBox rotateVbox = makeVbox();
        Label rotateLabel = makeLabel("Rotate Domino", 16);
        ToggleGroup rotateGroup = new ToggleGroup();
        RadioButton yesButton = new RadioButton("Yes");
        RadioButton noButton = new RadioButton("No");
        yesButton.setToggleGroup(rotateGroup);
        noButton.setToggleGroup(rotateGroup);
        // Style radio buttons
        yesButton.setTextFill(Color.WHITE);
        noButton.setTextFill(Color.WHITE);
        yesButton.setFont(Font.font("Verdana", 14));
        noButton.setFont(Font.font("Verdana", 14));
        rotateVbox.getChildren().addAll(rotateLabel, yesButton, noButton);
        rotateGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                RadioButton selectedButton = (RadioButton) rotateGroup.getSelectedToggle();
                if (selectedButton != null) {
                    if (selectedButton.getText().equals("Yes")) {
                        rotateOptionSelection = "y";
                    } else if (selectedButton.getText().equals("No")) {
                        rotateOptionSelection = "n";
                    }
                }
            }
        });
        return rotateVbox;
    }

    /**
     * Creates the domino selection combo box with label.
     */
    private VBox createDominoSelectionComboBox() {
        VBox dominoSelectionVbox = makeVbox();
        Label dominoSelectionLabel = makeLabel("please select which domino to play", 16);
        ComboBox<Integer> dominoSelectionOptions = new ComboBox<>();
        for (int i = 0; i < gameManager.getBoard().getBoneyardSize(); i++) {
            dominoSelectionOptions.getItems().add(i);
        }
        dominoSelectionOptions.setOnAction(event -> {
            comboBoxSelection = dominoSelectionOptions.getValue();
        });
        dominoSelectionVbox.getChildren().addAll(dominoSelectionLabel, dominoSelectionOptions);
        return dominoSelectionVbox;
    }

    /**
     * Creates the side selection toggle with label.
     */
    private VBox createWhichSideToPlay() {
        VBox leftOrRightOption = makeVbox();
        Label sideSelectionLabel = makeLabel("select which side to play the domino", 16);
        ToggleGroup locationGroup = new ToggleGroup();
        RadioButton rbLeft = new RadioButton("Left");
        RadioButton rbRight = new RadioButton("Right");
        rbLeft.setToggleGroup(locationGroup);
        rbRight.setToggleGroup(locationGroup);
        rbLeft.setTextFill(Color.WHITE);
        rbRight.setTextFill(Color.WHITE);
        rbLeft.setFont(Font.font("Verdana", 14));
        rbRight.setFont(Font.font("Verdana", 14));
        leftOrRightOption.getChildren().addAll(sideSelectionLabel, rbLeft, rbRight);
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
        return leftOrRightOption;
    }

    /**
     * Creates the Play button.
     */
    private Button createPlayButton() {
        Button playButton = new Button("Play");
        styleButton(playButton);
        Stage popupWindow = new Stage();
        popupWindow.setResizable(false);
        popupWindow.setTitle("Incorrect Play");
        VBox popupLayout = makeVbox();
        popupLayout.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(5), Insets.EMPTY)));
        Scene popupScene = new Scene(popupLayout, 350, 100);
        Label errorMessage = makeLabel("Invalid Play. Try Again!", 20);
        newStageIfInvalidPlayPopUp(popupWindow, popupLayout, popupScene, errorMessage);

        playButton.setOnAction(event -> {
            if ((board.getPlayedDomino().size() >= 0 || gameManager.checkIfValidPlayExists(human.getTray(), board))
                    && board.getAvailableDomino() != null) {
                if (comboBoxSelection >= human.getTray().size()) {
                    isPopUpRequired = true;
                }
                if (isPopUpRequired) {
                    popupWindow.setTitle("Selected dice is empty!!!");
                    popupWindow.showAndWait();
                    isPopUpRequired = false;
                    return;
                }
                Domino dice = human.getDominoFromTray(comboBoxSelection);
                if (!checkIfValidPlay(dice)) {
                    isPopUpRequired = true;
                }
                if (isPopUpRequired) {
                    popupWindow.setTitle("Incorrect Play");
                    popupWindow.showAndWait();
                    isPopUpRequired = false;
                    return;
                }
                if (currentRadioButton.equals("l")) {
                    if (rotateOptionSelection.equals("y")) {
                        human.getDominoFromTray(comboBoxSelection).rotate();
                    }
                    board.placeOnLeft(human.getDominoFromTray(comboBoxSelection));
                } else if (currentRadioButton.equals("r")) {
                    if (rotateOptionSelection.equals("y")) {
                        human.getDominoFromTray(comboBoxSelection).rotate();
                    }
                    board.placeOnRight(human.getDominoFromTray(comboBoxSelection));
                }
                human.removedDominoFromPlayerTray(comboBoxSelection);
                addDiceImageToPlayersTray(human, humanPlayAreaDown, comboBoxSelection);
            }
            if (gameEndStatus(human)) {
                gameOverGUI(human);
            }
            computerPlay();
            if (gameEndStatus(computer)) {
                gameOverGUI(computer);
            }
        });
        return playButton;
    }

    /**
     * Creates the Draw From Boneyard button.
     */
    private Button createDrawFromBoneYardButton() {
        Button drawFromBoneyard = new Button("Draw From Boneyard");
        styleButton(drawFromBoneyard);
        drawFromBoneyard.setOnAction(event -> {
            for (Domino dice : human.getTray()) {
                if (checkIfValidPlay(dice)) {
                    popUpWindow("Valid Play Exists", "You have a valid play. Please check again!");
                    return;
                }
            }
            if (board.getAvailableDomino() == null) {
                popUpWindow("Boneyard is empty", "Cannot draw. Boneyard is empty!!!");
                gameOverGUI(computer);
                return;
            }
            Domino pickedDice = board.drawFromBoneyard();
            int leftNum = pickedDice.getLeftNumDots();
            int rightNum = pickedDice.getRightNumDots();
            human.addDominoToPlayerTray(pickedDice);
            System.out.println("Added " + leftNum + ", " + rightNum);
            ImageView pickedDomino = getImage(leftNum, rightNum, false);
            humanPlayAreaDown.getChildren().add(pickedDomino);
        });
        return drawFromBoneyard;
    }

    /**
     * Creates the Exit button.
     */
    private Button createExitButton() {
        Button exitButton = new Button("Exit");
        styleButton(exitButton);
        exitButton.setOnAction(event -> System.exit(0));
        return exitButton;
    }

    private void addDiceImageToPlayersTray(Player currPlayer, HBox humanPlayAreaDown, int removeIndex) {
        int boardSize = gameManager.getBoard().getBoneyardSize();
        for (int i = 0; i < boardSize; i++) {
            if (currPlayer.getTray().size() == 0) {
                gameOverGUI(currPlayer);
                return;
            }
            int leftNum = currPlayer.getDominoFromTray(i).getLeftNumDots();
            int rightNum = currPlayer.getDominoFromTray(i).getRightNumDots();
            for (int m = 0; m < boardSize; m++) {
                for (int n = m; n < boardSize; n++) {
                    if (leftNum == m && rightNum == n) {
                        if (checkIfNullForAddingToTray(humanPlayAreaDown, middlePlayArea, removeIndex, m, n))
                            return;
                    } else if (leftNum == n && rightNum == m) {
                        if (checkIfNullForAddingToTray(humanPlayAreaDown, middlePlayArea, removeIndex, m, n))
                            return;
                    }
                }
            }
        }
    }

    private boolean checkIfNullForAddingToTray(HBox keepDicesHere, HBox addToThisHbox, int removeIndex, int m, int n) {
        ImageView domino;
        if (removeIndex >= 0) {
            if (removeIndex < listToRemoveHumanTrayAfterSelection.size()) {
                keepDicesHere.getChildren().remove(listToRemoveHumanTrayAfterSelection.get(removeIndex));
                ImageView addPlayedDiceToMiddle = listToRemoveHumanTrayAfterSelection.get(removeIndex);
                if (currentRadioButton.equals("l")) {
                    if (rotateOptionSelection.equals("y")) {
                        int leftNum = board.getPlayedDomino().getFirst().getLeftNumDots();
                        int rightNum = board.getPlayedDomino().getFirst().getRightNumDots();
                        addPlayedDiceToMiddle = getImage(rightNum, leftNum, true);
                        addToThisHbox.getChildren().add(0, addPlayedDiceToMiddle);
                    } else {
                        if (!isNodeInHBox(addToThisHbox, addPlayedDiceToMiddle)) {
                            addToThisHbox.getChildren().add(0, addPlayedDiceToMiddle);
                        }
                    }
                } else if (currentRadioButton.equals("r")) {
                    if (rotateOptionSelection.equals("y")) {
                        int leftNum = board.getPlayedDomino().getLast().getLeftNumDots();
                        int rightNum = board.getPlayedDomino().getLast().getRightNumDots();
                        addPlayedDiceToMiddle = getImage(rightNum, leftNum, true);
                        addToThisHbox.getChildren().add(addPlayedDiceToMiddle);
                    } else if (!isNodeInHBox(addToThisHbox, addPlayedDiceToMiddle)) {
                        addToThisHbox.getChildren().add(addPlayedDiceToMiddle);
                    }
                }
                listToRemoveHumanTrayAfterSelection.remove(removeIndex);
                return true;
            }
        } else {
            domino = getImage(m, n, false);
            keepDicesHere.getChildren().add(domino);
        }
        return false;
    }

    private boolean isNodeInHBox(HBox hbox, Node node) {
        for (Node child : hbox.getChildren()) {
            if (child.equals(node)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfValidPlay(Domino dice) {
        if (board.getPlayedDomino().size() == 0) {
            return true;
        }
        int leftNum = dice.getLeftNumDots();
        int rightNum = dice.getRightNumDots();
        int playedLeftNum = board.getPlayedDomino().getFirst().getLeftNumDots();
        int playedRightNum = board.getPlayedDomino().getLast().getRightNumDots();
        if (rotateOptionSelection.equals("n")) {
            if (leftNum == playedRightNum || rightNum == playedLeftNum) {
                return true;
            }
        } else if (rotateOptionSelection.equals("y")) {
            if (leftNum == playedLeftNum || rightNum == playedRightNum) {
                return true;
            }
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
        InputStream inputStream = GUI.class.getResourceAsStream(imageName);
        if (inputStream == null) {
            System.err.println("Resource not found: " + imageName);
            return null;
        }
        Image dominoPNG = new Image(inputStream);
        ImageView domino = new ImageView(dominoPNG);
        // Add a drop shadow effect
        domino.setEffect(new DropShadow(5, Color.BLACK));
        listToRemoveHumanTrayAfterSelection.add(domino);
        return domino;
    }

    private void computerPlay() {
        ArrayList<Domino> computerTray = computer.getTray();
        ImageView computerDomino;
        int leftEnd = board.getPlayedDomino().getFirst().getLeftNumDots();
        int rightEnd = board.getPlayedDomino().getLast().getRightNumDots();
        for (int i = 0; i < computerTray.size(); i++) {
            Domino computerDice = computerTray.get(i);
            int computerLeftPlay = computerDice.getLeftNumDots();
            int computerRightPlay = computerDice.getRightNumDots();
            if (computerDice.getLeftNumDots() == leftEnd) {
                computerDomino = getImage(computerLeftPlay, computerRightPlay, true);
                computerDice.rotate();
                board.placeOnLeft(computerDice);
                middlePlayArea.getChildren().add(0, computerDomino);
                computerTray.remove(i);
                return;
            } else if (computerDice.getRightNumDots() == rightEnd) {
                computerDomino = getImage(computerLeftPlay, computerRightPlay, true);
                computerDice.rotate();
                board.placeOnRight(computerDice);
                middlePlayArea.getChildren().add(computerDomino);
                computerTray.remove(i);
                return;
            } else if (computerDice.getLeftNumDots() == rightEnd) {
                computerDomino = getImage(computerLeftPlay, computerRightPlay, false);
                board.placeOnRight(computerDice);
                middlePlayArea.getChildren().add(computerDomino);
                computerTray.remove(i);
                return;
            } else if (computerDice.getRightNumDots() == leftEnd) {
                computerDomino = getImage(computerLeftPlay, computerRightPlay, false);
                board.placeOnLeft(computerDice);
                middlePlayArea.getChildren().add(0, computerDomino);
                computerTray.remove(i);
                return;
            }
        }
        while (board.getAvailableDomino() != null) {
            Domino diceFromBoneyard = board.drawFromBoneyard();
            computerTray.add(diceFromBoneyard);
            if (gameManager.doesPickedDiceMatchEitherEnd(diceFromBoneyard, board)) {
                break;
            }
        }
        if (board.getAvailableDomino() == null) {
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
        gameOverLayout.setAlignment(Pos.CENTER);
        gameOverLayout.setPadding(new Insets(15));
        gameOverLayout.setBackground(new Background(new BackgroundFill(Color.DARKRED, new CornerRadii(5), Insets.EMPTY)));
        Scene gameOverScene = new Scene(gameOverLayout, 350, 100);
        Label winnerMessage = new Label();
        winnerMessage.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        winnerMessage.setTextFill(Color.WHITE);
        newStageIfInvalidPlayPopUp(gameOverWindow, gameOverLayout, gameOverScene, winnerMessage);
        if (playedLast.equals(human)) {
            winnerMessage.setText(playerWins);
            gameOverWindow.showAndWait();
            System.exit(0);
        }
        if (playedLast.equals(computer)) {
            winnerMessage.setText(computerWins);
            gameOverWindow.showAndWait();
            System.exit(0);
        }
    }

    private boolean gameEndStatus(Player lastPlayedPlayerForWinnerSelectionPlayer) {
        boolean validPlayForHuman = gameManager.checkIfValidPlayExists(human.getTray(), board);
        boolean validPlayForComputer = gameManager.checkIfValidPlayExists(computer.getTray(), board);
        if (!validPlayForHuman && !validPlayForComputer) {
            endGame = true;
            gameOverGUI(lastPlayedPlayerForWinnerSelectionPlayer);
            if (lastPlayedPlayerForWinnerSelectionPlayer.equals(computer)) {
                lastPlayedPlayerForWinnerSelection = "computer";
            }
            Label winnerLabel = makeLabel(" " + lastPlayedPlayerForWinnerSelection, 25);
            middlePlayArea.getChildren().clear();
            middlePlayArea.getChildren().add(winnerLabel);
            return true;
        }
        return false;
    }

    private void popUpWindow(String title, String label) {
        Stage gameOverWindow = new Stage();
        gameOverWindow.setTitle(title);
        VBox gameOverLayout = new VBox(10);
        gameOverLayout.setAlignment(Pos.CENTER);
        gameOverLayout.setPadding(new Insets(15));
        gameOverLayout.setBackground(new Background(new BackgroundFill(Color.DIMGRAY, new CornerRadii(5), Insets.EMPTY)));
        Scene gameOverScene = new Scene(gameOverLayout, 350, 100);
        Label winnerMessage = new Label(label);
        winnerMessage.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        winnerMessage.setTextFill(Color.WHITE);
        newStageIfInvalidPlayPopUp(gameOverWindow, gameOverLayout, gameOverScene, winnerMessage);
        gameOverWindow.showAndWait();
    }

    private void newStageIfInvalidPlayPopUp(Stage gameOverWindow, VBox gameOverLayout, Scene gameOverScene, Label winnerMessage) {
        Button closeButton = new Button("Ok");
        styleButton(closeButton);
        closeButton.setOnAction(e -> gameOverWindow.close());
        gameOverLayout.getChildren().addAll(winnerMessage, closeButton);
        gameOverWindow.initModality(Modality.APPLICATION_MODAL);
        gameOverWindow.setScene(gameOverScene);
    }

    private Label makeLabel(String text, int size) {
        Label label = new Label(text);
        label.setFont(Font.font("Helvetica,Arial,sans-serif", FontWeight.BOLD, size));
        label.setTextFill(Color.WHITE);
        return label;
    }

    private VBox makeVbox() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        vBox.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID,
                new CornerRadii(5), new BorderWidths(2))));
        vBox.setBackground(new Background(new BackgroundFill(Color.rgb(50, 50, 50, 0.7),
                new CornerRadii(5), Insets.EMPTY)));
        return vBox;
    }

    private void styleButton(Button button) {
        button.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, new CornerRadii(5), Insets.EMPTY)));
        button.setPadding(new Insets(5, 15, 5, 15));
        button.setEffect(new DropShadow(5, Color.BLACK));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
