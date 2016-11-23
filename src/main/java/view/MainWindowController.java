package view;

import controller.Main;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;
import model.FTPManager;
import model.FTPManagerResponse;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController {
    private FTPManager ftpManager;
    private Main mainApp;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private MenuBar menuBar;

    @FXML
    private ImageView preview;

    @FXML
    private MenuItem itemDisconnect;

    @FXML
    private MenuItem itemConnect;

    @FXML
    private MenuItem itemExit;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private TextFlow info;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
        ftpManager = mainApp.getFtpManager();
    }

    @FXML
    void initialize() {
        assert menuBar != null : "fx:id=\"menuBar\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert preview != null : "fx:id=\"preview\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert treeView != null : "fx:id=\"treeView\" was not injected: check your FXML file 'MainWindow.fxml'.";
        assert info != null : "fx:id=\"info\" was not injected: check your FXML file 'MainWindow.fxml'.";

    }

    @FXML
    void handleConnect() {
        mainApp.showConnectDialog();
        FTPManagerResponse<List<String>> response = ftpManager.fileList();
        if (response.getErrors() != null) {
            showErrorAlert(response.getErrors().toString());
            ftpManager.disconnect();
        } else {
            treeView.setRoot(new TreeItem<String>("/"));
            List<String> dirs = response.getContent();
            setChildrenDirs(treeView.getRoot(), dirs);
            System.out.println(response.getContent());
            treeView.refresh();
        }
    }

    @FXML
    void handleDisconnect() {
        ftpManager.disconnect();
    }

    @FXML
    void handleExit() {
        ftpManager.disconnect();
        Platform.exit();
    }

    @FXML
    void onTreeMouseClicked(MouseEvent me) {
        if (me.getButton() == MouseButton.PRIMARY) {
            if (me.getClickCount() > 1) {
                TreeItem<String> treeItem = treeView.getSelectionModel().getSelectedItem();
                ftpManager.changeDirectory(treeItem.getValue());
                List<String> list = ftpManager.fileList().getContent();
                setChildrenDirs(treeItem, list);
                treeItem.setExpanded(true);
            }
        }
    }

    private void setChildrenDirs(TreeItem<String> parent, List<String> dirNames) {
        ObservableList<TreeItem<String>> list = parent.getChildren();

        for (String name : dirNames) {
            list.add(new TreeItem<>(name));
        }
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Some error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
