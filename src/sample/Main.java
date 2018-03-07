package sample;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private VBox verseSearchRootBox;
    private VBox wordSearchRootBox;
    private ChoiceBox<Translation> translationChoiceBox;
    private Button searchButton;
    private Button previousVerseButton;
    private Button nextVerseButton;
    private Verse verse;
    private Label verseLabel;
    private Label referenceLabel;
    private Label copyrightLabel;
    private TextField verseReferenceTextField;
    private Translation translation;
    private GridPane buttonsGridPane;
    private static Stage primaryStage;
    private BorderPane border;
    private HBox hbox;
    private GridPane verseInputGridPane;
    private static final Font ITALIC_FONT =
            Font.font(
                    "Serif",
                    FontPosture.ITALIC,
                    Font.getDefault().getSize()
            );

    @Override
    public void start(Stage primaryStage) throws IOException {
        setPrimaryStage(primaryStage);
        initializations();

       // translationChoiceBox.getSelectionModel().select(0);

        translationChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends Translation> observable,
                               Translation oldTranslation,
                               Translation newTranslation)
                        -> translation = newTranslation);


        searchButton.setOnAction(event -> {
            createVerseFromTextField();
            setVerseLabelFromVerse();

            System.out.println(verse.getNextVerse().text);
            System.out.println(verse.getPreviousVerse().text);

           // unfocusVerseTextField();
        });

        previousVerseButton.setOnAction(event -> {
            verse = verse.getPreviousVerse();
            setVerseLabelFromVerse();
            //unfocusVerseTextField();
        });

        nextVerseButton.setOnAction(event -> {
            verse = verse.getNextVerse();
            setVerseLabelFromVerse();
            //unfocusVerseTextField();
        });

        verseInputGridPane.add(verseReferenceTextField, 0, 0);
        verseInputGridPane.add(translationChoiceBox, 1, 0);

        buttonsGridPane.add(previousVerseButton, 0,0);
        buttonsGridPane.add(nextVerseButton, 2, 0);
        buttonsGridPane.add(searchButton, 1, 0);

        verseSearchRootBox.getChildren().addAll(verseInputGridPane,
                                                buttonsGridPane, verseLabel,
                                                referenceLabel, copyrightLabel);

        hbox = addHBox();
        border.setTop(hbox);
        border.setLeft(verseSearchRootBox);

        Scene testScene = new Scene(border);
        primaryStage.setTitle("BibleCompi");
        primaryStage.setScene(testScene);
        primaryStage.show();
    }

    private void setPrimaryStage(Stage stage) {
        Main.primaryStage = stage;
    }

    static public Stage getPrimaryStage() {
        return Main.primaryStage;
    }

    private void setVerseLabelFromVerse() {
        String newlinedVerse = addNewlineAfterWords(verse.text, 6);
        verseLabel.setText(newlinedVerse);
        verseLabel.setFont(Font.font("Serif", FontWeight.NORMAL, Font.getDefault().getSize() + 12));
        referenceLabel.setText(verse.getFullReference() + " " + verse.translation);
        referenceLabel.setFont(Font.font("Serif", FontWeight.BOLD, Font.getDefault().getSize() + 3));
        String newLinedCopyright = addNewlineAfterWords(verse.getCopyright(), 10);

        copyrightLabel.setText(newLinedCopyright);
        copyrightLabel.setFont(ITALIC_FONT);
    }

    private void unfocusVerseTextField() {
        verseReferenceTextField.clear();
        verseSearchRootBox.requestFocus();
    }

    private void createVerseFromTextField() {
        String[] words = verseReferenceTextField.getText().split("\\s+");

        String book = words[0].toLowerCase();
        book = book.substring(0, 1).toUpperCase() + book.substring(1); //formats book name in order to keep only first letter in capitals

        verse = new Verse(translation,
                words[0],
                Integer.parseInt(words[1]),
                Integer.parseInt(words[2]));
        System.out.println(verse.text);
    }

    private void initializations() {
        border = new BorderPane();
        border.setPrefSize(1000, 1000);
        verseSearchRootBox = new VBox();
        wordSearchRootBox = new VBox();
        buttonsGridPane = new GridPane();
        Image magnifyingGlassImage = new Image(getClass().getResourceAsStream("glass.png"));
        ImageView magnifyingGlassImageView = new ImageView(magnifyingGlassImage);
        magnifyingGlassImageView.setFitWidth(10);
        magnifyingGlassImageView.setFitHeight(10);
        verseInputGridPane = new GridPane();
        translationChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                Translation.values())
        );
        searchButton = new Button("Search!");
        nextVerseButton = new Button(">");
        previousVerseButton = new Button("<");
        verseLabel = new Label();
        referenceLabel = new Label();
        copyrightLabel = new Label();
        verseReferenceTextField = new PersistentPromptTextField("", "Verse reference");
        verseReferenceTextField.setMaxWidth(200);

        unfocusVerseTextFieldAtStart();
    }

    private void unfocusVerseTextFieldAtStart() {
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);

        verseReferenceTextField.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstTime.get()){
                verseSearchRootBox.requestFocus(); // Delegate the focus to container
                firstTime.setValue(false); // Variable value changed for future references
            }
        });
    }

    private String addNewlineAfterWords(String source, int numberOfWords){
        final String[] tokenedVerse = source.split(" ");
        final StringBuilder newString = new StringBuilder();

        for (int i = 0; i < tokenedVerse.length; i++) {
            if ((i > 0) && (0 == (i % numberOfWords))) {
                newString.append('\n');
            }

            newString.append(tokenedVerse[i]);

            if (i != (tokenedVerse.length - 1)) {
                newString.append(' ');
            }
        }

        return newString.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public HBox addHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        Button verseSearchButton = new Button("Bible");
        verseSearchButton.setPrefSize(100, 20);
        verseSearchButton.setOnAction(event -> border.setLeft(verseSearchRootBox));

        Button wordSearchButton = new Button("Search");
        wordSearchButton.setPrefSize(100, 20);
        wordSearchButton.setOnAction(event -> border.setLeft(wordSearchRootBox));

        hbox.getChildren().addAll(verseSearchButton, wordSearchButton);

        return hbox;
    }
}
