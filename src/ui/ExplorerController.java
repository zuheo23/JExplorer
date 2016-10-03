/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import ui.util.DirectoryCollector;

/**
 * FXML Controller class
 *
 * @author oheo
 */
public class ExplorerController implements Initializable {

    @FXML
    private TextField tfPath;
    @FXML
    private Label lblInfo;
    @FXML
    private TreeView<String> tvDirs;

    private UI ui;

    private Path cwd;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cwd = Paths.get(".").toAbsolutePath().normalize();
        lblInfo.setText(cwd.toString());
        getAllDirs();
    }

    @FXML
    public void chgDir() {
        String newDir = tfPath.getText();
        Path newRelPath = cwd.resolve(newDir).normalize();
        Path newAbsPath = cwd.resolve(Paths.get(newDir).normalize());
        if (Files.exists(newRelPath)) {
            lblInfo.setText(String.format("%s: Changing to %s", cwd.toString(), newRelPath.toString()));
            cwd = newRelPath.toAbsolutePath();
        } else if (Files.exists(newAbsPath)) {
            lblInfo.setText(String.format("%s: Changing to %s", cwd.toString(), newAbsPath.toString()));
            cwd = newAbsPath;
        } else {
            lblInfo.setText(String.format("%s: Path is invalid", cwd.toString()));
        }
        getAllDirs();
    }

    @FXML
    public void closeApp() {
        Platform.exit();
    }

    @FXML
    public void showAbout() {
        try {
            Alert about = new Alert(AlertType.INFORMATION);
            about.setTitle("About Java Explorer");
            about.setDialogPane(FXMLLoader.load(getClass().getResource("DlgAbout.fxml")));
            about.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(ExplorerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    public void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            chgDir();
        }
    }

    private void getAllDirs() {
        try {
            DirectoryCollector.PrintDirectories pd = new DirectoryCollector.PrintDirectories();
            System.out.println("CWD: " + cwd);
            Files.walkFileTree(cwd, pd);
            pd.root.setExpanded(true);
            tvDirs.setRoot(pd.root);
        } catch (IOException ex) {
            Logger.getLogger(ExplorerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
