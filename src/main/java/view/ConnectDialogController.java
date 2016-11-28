package view;

import controller.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.FTPManager;
import model.FTPManagerResponse;

public class ConnectDialogController {
    private Main mainApp;

    @FXML
    private Button cancelButton;

    @FXML
    private PasswordField password;

    @FXML
    private TextField address;

    @FXML
    private Button connectButton;

    @FXML
    private TextField username;

    @FXML
    private Label errorLabel;

    private Stage dialogStage;

    @FXML
    void initialize() {
        errorLabel.setVisible(false);
        // TODO remove that
        address.setText("ftp.mccme.ru");
        username.setText("anonymous");
        password.setText("anonymous");
    }

    public void setStage(Stage stage) {
        dialogStage = stage;
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void handleOk() {
        FTPManager manager = mainApp.getFtpManager();
        FTPManagerResponse<Boolean> response = manager.connect(address.getText(), username.getText(), password.getText());
        if (response.getErrors() != null) {
            errorLabel.setText("Unable to connect. Please check address & credentials");
            System.err.println("Connection error:"+response.getErrors());
            errorLabel.setVisible(true);
        } else {
            errorLabel.setVisible(false);
            dialogStage.close();
        }
    }

    @FXML
    public void handleCancel() {
        dialogStage.close();
    }
}
