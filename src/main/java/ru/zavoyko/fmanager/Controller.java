package ru.zavoyko.fmanager;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Controller {

    @FXML
    private VBox leftPanel, rightPanel;

    private PanelController leftCtrl, rightCtrl, srcCtrl, dstCtrl;
    private Path srcPath, dstPath;

    public void btnExitAction(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void btnCopyAction(ActionEvent actionEvent) {
        updateControllers();
        try {
            Files.copy(srcPath, dstPath);
            final var path = Paths.get(dstCtrl.getCurrentPath());
            dstCtrl.updateList(path);
        } catch (IOException e) {
            final var alert = new Alert(Alert.AlertType.WARNING, "Unable to copy", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void btnMoveAction(ActionEvent actionEvent) {
        updateControllers();
        try {
            Files.move(srcPath, dstPath);
            final var path = Paths.get(dstCtrl.getCurrentPath());
            dstCtrl.updateList(path);
        } catch (IOException e) {
            final var alert = new Alert(Alert.AlertType.WARNING, "Unable to copy", ButtonType.OK);
            alert.showAndWait();
        }
    }


    public void btnDelAction(ActionEvent actionEvent) {
        updateControllers();
        try {
            if (!Files.isDirectory(srcPath)) {
                Files.delete(srcPath);
            } else {
                Files.walkFileTree(srcPath, new FileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (Files.isSymbolicLink(file) || Files.isRegularFile(file)) {
                            Files.delete(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.TERMINATE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
            final var path = Paths.get(srcCtrl.getCurrentPath());
            srcCtrl.updateList(path);
        } catch (IOException e) {
            final var alert = new Alert(Alert.AlertType.WARNING, "Unable to copy", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void updateControllers() {
        if (leftCtrl == null || rightCtrl == null) {
            leftCtrl = (PanelController) leftPanel.getProperties().get("ctrl");
            rightCtrl = (PanelController) rightPanel.getProperties().get("ctrl");
        }
        if (leftCtrl.getSelectedFileName() == null && rightCtrl.getSelectedFileName() == null) {
            final var alert = new Alert(Alert.AlertType.WARNING, "Select file first", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        srcCtrl = null;
        dstCtrl = null;
        if (leftCtrl.getSelectedFileName() != null) {
            srcCtrl = leftCtrl;
            dstCtrl = rightCtrl;
        } else if (rightCtrl.getSelectedFileName() != null) {
            srcCtrl = rightCtrl;
            dstCtrl = leftCtrl;
        }
        srcPath = Paths.get(srcCtrl.getCurrentPath(), srcCtrl.getSelectedFileName());
        dstPath = Paths.get(dstCtrl.getCurrentPath()).resolve(srcCtrl.getSelectedFileName());
    }

}