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

/**
 * The GUI class represents the graphical user interface for a Domino game.
 * It extends the JavaFX Application class and provides the layout and functionality
 * for the game, including player interactions, game logic, and visual representation
 * of the game state.
 *
 * <p>This class handles the distribution of dominoes, player turns, and game-over conditions.
 * It also manages the visual components such as the player's tray, the middle play area,
 * and the boneyard.</p>
 *
 * <p>In this version, any domino that has a 0 on either side is treated as a wildcard,
 * meaning it matches with any domino end.</p>
 *
 * <p>
 * Additionally, this version supports an optional integer command line argument that
 * specifies the maximum number of dots on a domino. If no argument is provided or if the
 * value is 6, the default domino set (with up to 6 dots) is used. If the argument is any
 * value between 3 and 9, that value is used. Values outside that range will cause an error.
 *
 * @author Krishna Sedhain
 * </p>
 */
public class GUI extends Application {

    // Static field for maximum dots. Default is 6.
    public static int MAX_DOTS = 6;

    private final Board board = new Board();
    private final Player human = new Player(Players.Human);
    private final Player computer = new Player(Players.Computer);
    private final VBox vBoxToIncludeLabelAndDominos = new VBox();
    private final HBox humanPlayAreaDown = new HBox();
    private final HBox numOfDicesUpdateHbox = new HBox();
    private final HBox middlePlayArea = new HBox();
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

    /**
     * The init method is called before start() and processes command line arguments.
     * It checks for an optional integer argument specifying the maximum number of dots.
     */
    @Override
    public void init() throws Exception {
        List<String> args = getParameters().getRaw();
        if (!args.isEmpty()) {
            try {
                int value = Integer.parseInt(args.get(0));
                // Accept value 6 (default) or any value between 3 and 9.
                if (value == 6 || (value >= 3 && value <= 9)) {
                    MAX_DOTS = value;
                } else {
                    System.err.println("Invalid domino set size. Please provide a value between 3 and 9 (or no argument for default 6).");
                    System.exit(1);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid argument. Please provide an integer domino set size.");
                System.exit(1);
            }
        }
    }

    /**
     * The main entry point for the JavaFX application.
     *
     * @param primaryStage The primary stage for this application, onto which
     *                     the application scene can be set.
     * @throws Exception If an error occurs during the initialization of the application.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Domino Game");


        System.out.println("Using domino set with maximum dots: " + MAX_DOTS);


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
        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)));

        // --- Bottom: Player’s Tray ---
        Label humanTrayLabel = makeLabel("Player's Tray", 20);
        addDiceImageToPlayersTray(human, humanPlayAreaDown, -1);
        vBoxToIncludeLabelAndDominos.getChildren().addAll(humanTrayLabel, humanPlayAreaDown);
        vBoxToIncludeLabelAndDominos.setAlignment(Pos.CENTER);
        // Moved tray container to bottom left:
        HBox bottomLeftContainer = new HBox(vBoxToIncludeLabelAndDominos);
        bottomLeftContainer.setAlignment(Pos.BOTTOM_LEFT);
        bottomLeftContainer.setPadding(new Insets(10));

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
        root.setBottom(bottomLeftContainer);
        root.setTop(numOfDicesUpdateHbox);
        root.setCenter(middlePlayArea);

        Scene scene = new Scene(root, 1500, 700);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Creates a VBox containing the rotate domino toggle controls.
     * The toggle controls allow the user to select whether to rotate a domino before playing.
     *
     * @return A VBox containing the label and radio buttons for rotating a domino.
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
     * Creates a VBox containing the domino selection controls.
     * Provides a label and a ComboBox to allow the user to select which domino to play.
     *
     * @return A VBox containing the domino selection label and combo box.
     */
    private VBox createDominoSelectionComboBox() {
        VBox dominoSelectionVbox = makeVbox();
        Label dominoSelectionLabel = makeLabel("please select which domino to play", 16);
        ComboBox<Integer> dominoSelectionOptions = new ComboBox<>();
        // Populate combo box based on the player's tray size
        for (int i = 0; i < human.getTray().size(); i++) {
            dominoSelectionOptions.getItems().add(i);
        }
        dominoSelectionOptions.setOnAction(event -> {
            comboBoxSelection = dominoSelectionOptions.getValue();
        });
        dominoSelectionVbox.getChildren().addAll(dominoSelectionLabel, dominoSelectionOptions);
        return dominoSelectionVbox;
    }

    /**
     * Creates a VBox containing the side selection controls.
     * Allows the user to choose whether to play a domino on the left or right side of the board.
     *
     * @return A VBox containing the side selection label and radio buttons.
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
     * Creates and configures the Play button used to play a selected domino.
     * The button action validates the play and updates the board accordingly.
     *
     * @return A Button configured for playing a domino.
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
                        human.getDominoFromTray(comboBoxSelection).rotateDomino();
                    }
                    board.placeOnLeft(human.getDominoFromTray(comboBoxSelection));
                } else if (currentRadioButton.equals("r")) {
                    if (rotateOptionSelection.equals("y")) {
                        human.getDominoFromTray(comboBoxSelection).rotateDomino();
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
     * Creates and configures the button used to draw a domino from the boneyard.
     * The button action checks if the player has a valid play, and if not, draws a domino.
     *
     * @return A Button configured for drawing a domino from the boneyard.
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
     * Creates and configures the Exit button to terminate the application.
     *
     * @return A Button configured for exiting the game.
     */
    private Button createExitButton() {
        Button exitButton = new Button("Exit");
        styleButton(exitButton);
        exitButton.setOnAction(event -> System.exit(0));
        return exitButton;
    }

    /**
     * Adds domino images to the player's tray displayed in the GUI.
     * If removeIndex is non-negative, it removes the domino image at that index and adds it to the middle play area.
     * Otherwise, it adds new domino images representing the player's current tray.
     *
     * @param currPlayer        The player whose tray is being updated.
     * @param humanPlayAreaDown The HBox representing the player's tray area.
     * @param removeIndex       The index of the domino to remove and update; if negative, no removal occurs.
     */
    private void addDiceImageToPlayersTray(Player currPlayer, HBox humanPlayAreaDown, int removeIndex) {
        int traySize = currPlayer.getTray().size(); // Changed from board size to tray size.
        for (int i = 0; i < traySize; i++) {
            if (currPlayer.getTray().size() == 0) {
                gameOverGUI(currPlayer);
                return;
            }
            int leftNum = currPlayer.getDominoFromTray(i).getLeftNumDots();
            int rightNum = currPlayer.getDominoFromTray(i).getRightNumDots();
            int boardSize = gameManager.getBoard().getBoneyardSize();
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

    /**
     * Checks if the domino image should be added to the player's tray or moved to the middle play area.
     *
     * @param keepDicesHere The HBox containing the player's tray domino images.
     * @param addToThisHbox The HBox representing the middle play area.
     * @param removeIndex   The index of the domino to remove from the player's tray.
     * @param m             The left number of dots on the domino.
     * @param n             The right number of dots on the domino.
     * @return true if a domino was removed and added to the middle play area; false otherwise.
     */
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

    /**
     * Checks if a specific Node is present within the given HBox.
     *
     * @param hbox The HBox to search within.
     * @param node The Node to search for.
     * @return true if the node is found in the HBox; false otherwise.
     */
    private boolean isNodeInHBox(HBox hbox, Node node) {
        for (Node child : hbox.getChildren()) {
            if (child.equals(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the selected domino constitutes a valid play based on the current board state.
     * A play is valid if the domino can be placed on either end of the played domino chain according to game rules.
     * <p>
     * NEW: If the domino being played has a 0 on either side, it is treated as a wildcard and is always valid.
     * </p>
     *
     * @param dice The domino to validate.
     * @return true if the play is valid; false otherwise.
     */
    private boolean checkIfValidPlay(Domino dice) {
        if (board.getPlayedDomino().size() == 0) {
            return true;
        }
        int leftNum = dice.getLeftNumDots();
        int rightNum = dice.getRightNumDots();

        // Wildcard rule: if the domino has a 0 on either side, treat it as matching any end.
        if (leftNum == 0 || rightNum == 0) {
            return true;
        }

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

    /**
     * Retrieves an ImageView representing a domino with the specified left and right dot counts.
     * If the rotate flag is set, the domino image is rotated.
     *
     * @param m      The left number of dots.
     * @param n      The right number of dots.
     * @param rotate If true, rotates the domino image.
     * @return An ImageView of the domino, or null if the resource is not found.
     */
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

    /**
     * Executes the computer player's turn.
     * The computer evaluates its tray and attempts to play a valid domino.
     * If no valid play is available, the computer draws from the boneyard until a valid play is found.
     * <p>
     * NEW: The conditions now also treat a domino side with 0 as a wildcard that matches any end.
     * </p>
     */
    private void computerPlay() {
        ArrayList<Domino> computerTray = computer.getTray();
        ImageView computerDomino;
        int leftEnd = board.getPlayedDomino().getFirst().getLeftNumDots();
        int rightEnd = board.getPlayedDomino().getLast().getRightNumDots();
        for (int i = 0; i < computerTray.size(); i++) {
            Domino computerDice = computerTray.get(i);
            int computerLeftPlay = computerDice.getLeftNumDots();
            int computerRightPlay = computerDice.getRightNumDots();

            if (computerDice.getLeftNumDots() == leftEnd || computerDice.getLeftNumDots() == 0) {
                computerDomino = getImage(computerLeftPlay, computerRightPlay, true);
                computerDice.rotateDomino();
                board.placeOnLeft(computerDice);
                middlePlayArea.getChildren().add(0, computerDomino);
                computerTray.remove(i);
                return;
            } else if (computerDice.getRightNumDots() == rightEnd || computerDice.getRightNumDots() == 0) {
                computerDomino = getImage(computerLeftPlay, computerRightPlay, true);
                computerDice.rotateDomino();
                board.placeOnRight(computerDice);
                middlePlayArea.getChildren().add(computerDomino);
                computerTray.remove(i);
                return;
            } else if (computerDice.getLeftNumDots() == rightEnd || computerDice.getLeftNumDots() == 0) {
                computerDomino = getImage(computerLeftPlay, computerRightPlay, false);
                board.placeOnRight(computerDice);
                middlePlayArea.getChildren().add(computerDomino);
                computerTray.remove(i);
                return;
            } else if (computerDice.getRightNumDots() == leftEnd || computerDice.getRightNumDots() == 0) {
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

    /**
     * Displays the game over GUI indicating the winning player.
     * The method creates a modal window that announces the game outcome and terminates the application upon closure.
     *
     * @param playedLast The player who played last, used to determine the winner.
     */
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

    /**
     * Evaluates if the game has ended based on the availability of valid plays for both players.
     * If no valid plays are available for both players, the game is ended and the winner is declared.
     *
     * @param lastPlayedPlayerForWinnerSelectionPlayer The player who last played a domino.
     * @return true if the game has ended; false otherwise.
     */
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

    /**
     * Displays a modal pop-up window with the specified title and message.
     *
     * @param title The title of the pop-up window.
     * @param label The message to display in the pop-up window.
     */
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

    /**
     * Configures a modal stage with a given layout, scene, and message.
     * Adds an OK button to close the pop-up.
     *
     * @param gameOverWindow The Stage to be configured as a pop-up.
     * @param gameOverLayout The VBox layout of the pop-up window.
     * @param gameOverScene  The Scene of the pop-up window.
     * @param winnerMessage  The Label displaying the message.
     */
    private void newStageIfInvalidPlayPopUp(Stage gameOverWindow, VBox gameOverLayout, Scene gameOverScene, Label winnerMessage) {
        Button closeButton = new Button("Ok");
        styleButton(closeButton);
        closeButton.setOnAction(e -> gameOverWindow.close());
        gameOverLayout.getChildren().addAll(winnerMessage, closeButton);
        gameOverWindow.initModality(Modality.APPLICATION_MODAL);
        gameOverWindow.setScene(gameOverScene);
    }

    /**
     * Creates a styled Label with the specified text and font size.
     *
     * @param text The text to display in the label.
     * @param size The font size of the label.
     * @return A styled Label.
     */
    private Label makeLabel(String text, int size) {
        Label label = new Label(text);
        label.setFont(Font.font("Helvetica,Arial,sans-serif", FontWeight.BOLD, size));
        label.setTextFill(Color.WHITE);
        return label;
    }

    /**
     * Creates a styled VBox with preset spacing, padding, border, and background.
     *
     * @return A styled VBox.
     */
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

    /**
     * Applies a consistent styling to a given Button.
     *
     * @param button The Button to style.
     */
    private void styleButton(Button button) {
        button.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, new CornerRadii(5), Insets.EMPTY)));
        button.setPadding(new Insets(5, 15, 5, 15));
        button.setEffect(new DropShadow(5, Color.BLACK));
    }

    /**
     * The main entry point of the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
