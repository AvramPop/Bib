package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Logger;

public class Main extends Application {

    private static Stage primaryStage;
    private static BorderPane border;
    private VerseSearchSceneLayout verseSearchSceneLayout;
    private WordSearchSceneLayout wordSearchSceneLayout;
    private HBox hbox;
    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage primaryStage) throws IOException, VerseNotFoundException {

        setPrimaryStage(primaryStage);

        verseSearchSceneLayout = VerseSearchSceneLayout.getInstance();
        wordSearchSceneLayout = WordSearchSceneLayout.getInstance();

        border = new BorderPane();
        border.setPrefSize(1000, 1000);

        hbox = addHBox();
        border.setTop(hbox);

        border.setLeft(verseSearchSceneLayout.sceneLayout());

        /*String testRequestURL = "https://bibles.org/v2/search.js?query=Jesus&version=eng-KJV";

        RESTInvoker invoker = new RESTInvoker(testRequestURL, Constants.bibleOrgKey, Constants.bibleOrgPassword);
        String result;
        try {
            result = invoker.makeGetRequest();
        } catch (Exception e) {
            throw new VerseNotFoundException("");
        }
        System.out.println(result);*/

        Verse verse = new Verse(Translation.KJV, "John", 1, 1);
        VerseDAO verseDAO = new VerseDAO();
        VersePOJO versePOJO = new VersePOJO(verse);

        /*for (VersePOJO versePOJO :
                verseDAO.getAll()) {
            System.out.println(versePOJO.getText());
        }*/



        Scene testScene = new Scene(border);
        primaryStage.setTitle("BibleCompi");
        primaryStage.setScene(testScene);
        primaryStage.show();

        /*int index = 2000;
        versePOJO.setId(index);
        while(true){
            verseDAO.add(versePOJO);
            verse = verse.getNextVerse();
            System.out.println("Verse " + versePOJO.getBook() + " "
                                + versePOJO.getChapter() + " " + versePOJO.getVerse() + " successfully added!");
            versePOJO.setId(index);
            index++;
            versePOJO.setVerse(verse.number);
            versePOJO.setBook(verse.book);
            versePOJO.setText(verse.text);
            versePOJO.setChapter(verse.chapter);
        }*/
    }

    private void setPrimaryStage(Stage stage) {
        Main.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
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
        verseSearchButton.setOnAction(event -> border.setLeft(VerseSearchSceneLayout.sceneLayout()));
    }

    public static BorderPane getMainBox(){
        return border;
    }

    private void setupWordSearchSceneButton(Button wordSearchButton) {
        wordSearchButton.setOnAction(event -> border.setLeft(WordSearchSceneLayout.sceneLayout()));
    }
}
