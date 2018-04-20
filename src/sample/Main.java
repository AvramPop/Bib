package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static Stage primaryStage;
    private static BorderPane border;
    private HBox hbox;
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage primaryStage) throws VerseNotFoundException {

        setPrimaryStage(primaryStage);

        border = new BorderPane();
        border.setPrefSize(1000, 1000);

        hbox = addHBox();
        border.setTop(hbox);

        LOGGER.setLevel(Level.INFO);
        LOGGER.info("Logging an INFO-level message");

        border.setLeft(VerseSearchScene.getLayout());

        Scene testScene = new Scene(border);
        primaryStage.setTitle("BibleCompi");
        primaryStage.setScene(testScene);
        primaryStage.show();
    }

    private void setPrimaryStage(Stage stage) {
        Main.primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return Main.primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        Button verseSearchButton = new Button("Bible");
        verseSearchButton.setPrefSize(100, 20);
        setupVerseSearchSceneButton(verseSearchButton);

        Button wordSearchButton = new Button("Search");
        wordSearchButton.setPrefSize(100, 20);
        setupWordSearchSceneButton(wordSearchButton);

        hbox.getChildren().addAll(verseSearchButton, wordSearchButton);

        return hbox;
    }

    private void setupVerseSearchSceneButton(Button verseSearchButton) {
        verseSearchButton.setOnAction(event -> border.setLeft(VerseSearchScene.getLayout()));
    }

    public static BorderPane getMainBox(){
        return border;
    }

    private void setupWordSearchSceneButton(Button wordSearchButton) {
        wordSearchButton.setOnAction(event -> border.setLeft(WordSearchScene.getLayout()));
    }
}
