package sample;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VerseSearchSceneLayout {

    private static ChoiceBox<Translation> translationChoiceBox;
    private static Button searchButton;
    private static Button previousVerseButton;
    private static Button nextVerseButton;
    private static Verse verse;
    private static Label verseLabel;
    private static Label referenceLabel;
    private static Label copyrightLabel;
    private static TextField verseReferenceTextField;
    private static Translation translation;
    private static GridPane buttonsGridPane;
    private static GridPane verseInputGridPane;
    private static final Font ITALIC_FONT =
            Font.font(
                    "Serif",
                    FontPosture.ITALIC,
                    Font.getDefault().getSize()
            );

    private static VBox rootBox;

    private static VerseSearchSceneLayout instance = null;

    private VerseSearchSceneLayout() {
        initializations();
        setupTranslationChoiceBox();
        setupVerseSearchButton();
        setupPreviousVerseButton();
        setupNextVerseButton();
        setupVerseSearchScene();
    }

    public static VerseSearchSceneLayout getInstance() {
        if(instance == null) {
            instance = new VerseSearchSceneLayout();
        }
        return instance;
    }

    public static VerseSearchSceneLayout getInstanceWithVerse(Verse verse){
        VerseSearchSceneLayout.verse = verse;
        setVerseLabelFromVerse();
        return instance;
    }

    public static VBox sceneLayout() {
        return rootBox;
    }

    private static void setupVerseSearchScene() {
        verseInputGridPane.add(verseReferenceTextField, 0, 0);
        verseInputGridPane.add(translationChoiceBox, 1, 0);

        buttonsGridPane.add(previousVerseButton, 0,0);
        buttonsGridPane.add(nextVerseButton, 2, 0);
        buttonsGridPane.add(searchButton, 1, 0);

        rootBox.getChildren().addAll(verseInputGridPane,
                buttonsGridPane, verseLabel,
                referenceLabel, copyrightLabel);
    }

    private static void setupNextVerseButton() {
        nextVerseButton.setOnAction(event -> {
            try {
                verse = verse.getNextVerse();
            } catch (VerseNotFoundException e) {
                e.printStackTrace();
            }
            setVerseLabelFromVerse();
            //unfocusVerseTextField();
        });
    }

    private static void setupPreviousVerseButton() {
        previousVerseButton.setOnAction(event -> {
            try {
                verse = verse.getPreviousVerse();
            } catch (VerseNotFoundException e) {
                e.printStackTrace();
            }
            setVerseLabelFromVerse();
            //unfocusVerseTextField();
        });
    }

    private static void setupVerseSearchButton() {
        searchButton.setOnAction(event -> {
            boolean success = false;
            try {
                createVerseFromTextField();
                success = true;
            } catch (VerseNotFoundException e) {
                verseLabel.setText("Verse not found :(");
                referenceLabel.setText("");
                copyrightLabel.setText("");
                verseLabel.setTextFill(Color.web("#ff2424"));
            }
            if(success) setVerseLabelFromVerse();
        });
    }

    private static void setupTranslationChoiceBox() {
        // translationChoiceBox.getSelectionModel().select(0);
        translationChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends Translation> observable,
                               Translation oldTranslation,
                               Translation newTranslation)
                        -> translation = newTranslation);
    }

    private static void setVerseLabelFromVerse() {
        String newlinedVerse = addNewlineAfterWords(verse.text, 6);
        verseLabel.setText(newlinedVerse);
        verseLabel.setFont(Font.font("Serif", FontWeight.NORMAL, Font.getDefault().getSize() + 12));
        verseLabel.setTextFill(Color.web("#000000"));
        referenceLabel.setText(verse.getFullReference() + " " + verse.translation);
        referenceLabel.setFont(Font.font("Serif", FontWeight.BOLD, Font.getDefault().getSize() + 3));
        String newLinedCopyright = addNewlineAfterWords(verse.getCopyright(), 10);

        copyrightLabel.setText(newLinedCopyright);
        copyrightLabel.setFont(ITALIC_FONT);
    }

    private static void unfocusVerseTextField() {
        verseReferenceTextField.clear();
        rootBox.requestFocus();
    }

    private static void createVerseFromTextField() throws VerseNotFoundException { // TODO refactor without reference to property
        try{
            String[] words = verseReferenceTextField.getText().split("\\s+");
            String book = words[0].toLowerCase();
            book = book.substring(0, 1).toUpperCase() + book.substring(1); // TODO formats book name in order to keep only first letter in capitals


            verse = new Verse(translation,
                    words[0],
                    Integer.parseInt(words[1]),
                    Integer.parseInt(words[2]));

            System.out.println(verse.text);
        } catch(Exception e) {
            throw new VerseNotFoundException("");
        }
    }

    private static void initializations() {
        rootBox = new VBox();
        buttonsGridPane = new GridPane();
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

    private static void unfocusVerseTextFieldAtStart() {
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);

        verseReferenceTextField.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstTime.get()){
                rootBox.requestFocus(); // Delegate the focus to container
                firstTime.setValue(false); // Variable value changed for future references
            }
        });
    }

    private static String addNewlineAfterWords(String source, int numberOfWords){
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
    
}
