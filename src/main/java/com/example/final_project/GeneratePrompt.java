package com.example.final_project;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;

public class GeneratePrompt {

    // FXML elements
    @FXML
    private Button generateButtonForCode;
    @FXML
    private Button generateButtonForText;
    @FXML
    private Button regenerateButtonForCode;
    @FXML
    private Button regenerateButtonForText;
    @FXML
    private Button copyButtonForCode;
    @FXML
    private Button copyButtonForText;
    @FXML
    private TextField topic;
    @FXML
    private TextArea existingCode;
    @FXML
    private TextArea problemStatement;
    @FXML
    private TextArea assumptions;
    @FXML
    private TextArea constraints;
    @FXML
    private ChoiceBox existingCodeChoice;
    @FXML
    private ChoiceBox languages;
    @FXML
    private TextField languageVersion;
    @FXML
    private TextArea promptBoxForCode;
    private StringBuilder prompt = new StringBuilder();

    @FXML
    private TextField apiKeyForCode;

    @FXML
    private TextField apiKeyForText;

    @FXML
    private ChoiceBox opChoice; //Text or Question

    @FXML
    private TextArea textQuestionBox;

    @FXML
    private ChoiceBox structureMenu;

    @FXML
    private TextField purposeTextBox;

    @FXML
    private CheckBox humanize;

    @FXML
    private TextField maxWords;

    @FXML
    private TextField relatedTopic;

    @FXML
    private CheckBox opinionCheckBox;

    @FXML
    private TextArea promptBoxForText;
    @FXML
    private Clipboard clipboard;
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private void initialize(){
        clipboard = Clipboard.getSystemClipboard();
        progressIndicator.setVisible(false);
    }

    // Copy generated prompt to clipboard
    @FXML
    private void copyToClipboardCode() {
        String promptText = promptBoxForCode.getText();
        ClipboardContent content = new ClipboardContent();
        content.putString(promptText);
        clipboard.setContent(content);
    }
    @FXML
    private void copyToClipboardText() {
        String promptText = promptBoxForText.getText();
        ClipboardContent content = new ClipboardContent();
        content.putString(promptText);
        clipboard.setContent(content);
    }

    // Extract content from the API response
    private String getContent(String response) {
        int start = response.indexOf("content")+ 11;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }
    // Method to make API request to OpenAI's ChatGPT using multithreading
    private void LLMPromptAsync(String prompt, int flag) {
        new Thread(() -> {
            try {
                LLMPrompt(prompt, flag);
            } catch (Exception e) {
                e.printStackTrace(); // Handle exceptions appropriately
            }
        }).start();
    }

    // Set the visibility of UI elements based on the choice
    @FXML
    private void setVisible() {
        String choiceValue = existingCodeChoice.getValue().toString();
        if ("No".equals(choiceValue)) {
            existingCode.setDisable(true);
        } else if ("Yes".equals(choiceValue)) {
            existingCode.setDisable(false);
        }
    }

    @FXML
    private void generatePromptForText()
    {
        String finalPrompt = "";
        String textQuestion = textQuestionBox.getText();
        String promptType = opChoice.getValue() != null ? opChoice.getValue().toString() : "";
        String structure = structureMenu.getValue() != null ? structureMenu.getValue().toString() : "";
        String purpose = purposeTextBox.getText();
        boolean isHumanized = humanize.isSelected();
        String maxWordsCounter = maxWords.getText();
        String otherTopic = relatedTopic.getText();
        boolean mentionOpinion = opinionCheckBox.isSelected();

        try {
            progressIndicator.setVisible(true);
            if (promptType == "Text") {
                checkEmptyFields(promptType, apiKeyForText.getText(), textQuestion, structure, purpose);

                if(isHumanized)
                {
                    prompt.append("Generate a clear prompt, do any assumption and elaborate based on the following data:")
                            .append("Type of prompt: ").append(promptType).append(",")
                            .append("Text: ").append(textQuestion).append(",")
                            .append("Desired Structure: ").append(structure).append(",")
                            .append("Purpose: ").append(purpose).append(",")
                            .append("Write it as if a human wrote it").append(",")
                            .append("Maximum words:").append(maxWordsCounter).append(",")
                            .append("Related it to the topic:").append(otherTopic).append(",");
                    if(mentionOpinion)
                    {
                        prompt.append("Add an opinion as if you were me");
                    }
                }
                else
                {
                    prompt.append("Generate a clear prompt, do any assumption and elaborate based on the following data:")
                            .append("Type of prompt: ").append(promptType).append(",")
                            .append("Text: ").append(textQuestion).append(",")
                            .append("Desired Structure: ").append(structure).append(",")
                            .append("Purpose: ").append(purpose).append(",")
                            .append("Maximum words:").append(maxWordsCounter).append(",")
                            .append("Related it to the topic:").append(otherTopic).append(",");
                    if(mentionOpinion)
                    {
                        prompt.append("Add an opinion as if you were me");
                    }
                }
            } else {
                checkEmptyFields(promptType, apiKeyForText.getText(), textQuestion, structure, purpose);

                if (isHumanized) {
                    prompt.append("Generate a clear prompt, do any assumption and elaborate based on the following data:")
                            .append("Type of prompt: ").append(promptType).append(",")
                            .append("Text: ").append(textQuestion).append(",")
                            .append("Desired Structure: ").append(structure).append(",")
                            .append("Purpose: ").append(purpose).append(",")
                            .append("Write it as if a human wrote it").append(",")
                            .append("Maximum words:").append(maxWordsCounter).append(",")
                            .append("Related it to the topic:").append(otherTopic).append(",");
                    if (mentionOpinion) {
                        prompt.append("Add an opinion as if you were me");
                    }
                } else {
                    prompt.append("Generate a clear prompt, do any assumption and elaborate based on the following data:")
                            .append("Type of prompt: ").append(promptType).append(",")
                            .append("Text: ").append(textQuestion).append(",")
                            .append("Desired Structure: ").append(structure).append(",")
                            .append("Purpose: ").append(purpose).append(",")
                            .append("Maximum words:").append(maxWordsCounter).append(",")
                            .append("Related it to the topic:").append(otherTopic).append(",");
                    if (mentionOpinion) {
                        prompt.append("Add an opinion as if you were me");
                    }
                }
            }

            finalPrompt = prompt.toString();
            finalPrompt = finalPrompt.replace("\\n", " ");
            prompt = new StringBuilder();
            // Perform the API request in a separate thread
            LLMPromptAsync(finalPrompt, 1);
            generateButtonForText.setVisible(false);
            copyButtonForText.setVisible(true);
            regenerateButtonForText.setVisible(true);
            progressIndicator.setVisible(false);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Empty Field", e.getMessage());
        }
    }

    @FXML
    private void generatePromptForCode() {
        String finalPrompt = "";
        String topicText = topic.getText();
        String language = languages.getValue() != null ? languages.getValue().toString() : "";
        String languageVar = languageVersion.getText();
        String code = existingCode.getText();
        String prob = problemStatement.getText();
        String assumpts = assumptions.getText();
        String constrs = constraints.getText();

        try {
            progressIndicator.setVisible(true);
            if (existingCode.isDisabled()) {
                checkEmptyFields(topicText, language, languageVar, prob);

                prompt.append("Generate a clear prompt, do any assumption and elaborate based on the following data:")
                        .append("Topic: ").append(topicText).append(",")
                        .append("Programming Language: ").append(language).append(",")
                        .append("Language Version: ").append(languageVar).append(",")
                        .append("I want to: ").append(prob).append(",")
                        .append("Assuming that: ").append(assumpts).append(",")
                        .append("With the following constraints: ").append(constrs).append(",");
            } else {
                checkEmptyFields(topicText, language, languageVar, prob, code);

                prompt.append("Generate a clear prompt, do any assumption and elaborate based on the following data:")
                        .append("Given the following code: ").append(code).append(",")
                        .append("Topic: ").append(topicText).append(",")
                        .append("Programming Language: ").append(language).append(",")
                        .append("version: ").append(languageVar).append(",")
                        .append("I want to: ").append(prob).append(",")
                        .append("Assuming that: ").append(assumpts).append(",")
                        .append("With the following constraints: ").append(constrs).append(",");
            }

            finalPrompt = prompt.toString();
            finalPrompt = finalPrompt.replace("\\n", " ");
            prompt = new StringBuilder();
            // Perform the API request in a separate thread
            LLMPromptAsync(finalPrompt, 0);
            generateButtonForCode.setVisible(false);
            copyButtonForCode.setVisible(true);
            regenerateButtonForCode.setVisible(true);
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(14), event -> {
                        progressIndicator.setVisible(false);
                    })
            );
            timeline.play();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Empty Field", e.getMessage());
        }
    }
    // Check for empty fields in the specified parameters
    private void checkEmptyFields(String... fields) {
        for (String field : fields) {
            if (field.trim().isEmpty()) {
                throw new IllegalArgumentException("Please fill in all required fields.");
            }
        }
    }
    // Show an alert dialog with the specified details
    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    // Method to update UI components in the JavaFX Application Thread
    private void updateUI(String result, int flag) {
        Platform.runLater(() -> {
            if (flag == 0) {
                promptBoxForCode.setText(result);
            } else {
                promptBoxForText.setText(result);
            }
        });
    }

    // Method to make API request to OpenAI's ChatGPT
    private void LLMPrompt(String prompt, int flag) {
        String apiKey = (flag == 0) ? apiKeyForCode.getText() : apiKeyForText.getText();
        String model = "gpt-3.5-turbo";
        String url = "https://api.openai.com/v1/chat/completions";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // The request body
            String body = String.format("{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}]}", model, prompt);
            connection.setDoOutput(true);
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(body);
                writer.flush();
            }

            // Response from ChatGPT
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                String res = getContent(response.toString());
                res = res.replace("\\n", System.lineSeparator());
                // Update the UI with the result
                updateUI(res, flag);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


