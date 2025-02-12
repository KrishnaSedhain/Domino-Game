package domino;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class GUI extends Application {

    private BorderPane root;
    private final HBox middlePlayArea = new HBox();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Domino Game Board");


        root = new BorderPane();
        root.setStyle("-fx-background-color: red;");


        middlePlayArea.setAlignment(Pos.CENTER);
        middlePlayArea.setPrefSize(500, 500);


        root.setCenter(middlePlayArea);


        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

     public static void main(String[] args) {
        launch(args);
    }
}
