package sample;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class Main extends Application {

    private VBox rootBox;
    private ChoiceBox<Translation> translationChoiceBox;
    private Button searchButton;
    private Verse verse;
    private Label verseLabel;
    private Label referenceLabel;
    private Label copyrightLabel;
    private TextField verseReferenceTextField;
    private Translation translation;
    private static final Font ITALIC_FONT =
            Font.font(
                    "Serif",
                    FontPosture.ITALIC,
                    Font.getDefault().getSize()
            );

    @Override
    public void start(Stage primaryStage){

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
            String newlinedVerse = addNewlineAfterWords(verse.text, 6);
            verseLabel.setText(newlinedVerse);
            verseLabel.setFont(Font.font("Serif", FontWeight.NORMAL, Font.getDefault().getSize() + 12));
            referenceLabel.setText(verse.getFullReference() + " " + verse.translation);
            String newLinedCopyright = addNewlineAfterWords(verse.getCopyright(), 10);

            copyrightLabel.setText(newLinedCopyright);
            copyrightLabel.setFont(ITALIC_FONT);

            System.out.println(verse.getNextVerse().text);
            System.out.println(verse.getPreviousVerse().text);

           // unfocusVerseTextField();
        });

        rootBox.getChildren().addAll(verseReferenceTextField, translationChoiceBox,
                                    searchButton, verseLabel,
                                    referenceLabel, copyrightLabel);

        Scene scene = new Scene(rootBox, 1000, 1000);

        primaryStage.setTitle("BibleCompi");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void unfocusVerseTextField() {
        verseReferenceTextField.clear();
        rootBox.requestFocus();
    }

    private void createVerseFromTextField() {
        String[] words = verseReferenceTextField.getText().split("\\s+");

        verse = new Verse(translation,
                words[0],
                Integer.parseInt(words[1]),
                Integer.parseInt(words[2]));
        System.out.println(verse.text);
    }

    private void initializations() {
        rootBox = new VBox();
        translationChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                Translation.values())
        );
        searchButton = new Button("Search!");
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
                rootBox.requestFocus(); // Delegate the focus to container
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
}
