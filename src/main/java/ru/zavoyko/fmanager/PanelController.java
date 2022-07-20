package ru.zavoyko.fmanager;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import ru.zavoyko.fmanager.fileInfo.FileInfo;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class PanelController implements Initializable {

    public final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private TableView<FileInfo> fileTable;

    @FXML
    private ComboBox<String> diskBox;

    @FXML
    private TextField pathField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final var fileTypeColumn = new TableColumn<FileInfo, String>();
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getDesc()));
        fileTypeColumn.setPrefWidth(24d);

        final var fileNameColumn = new TableColumn<FileInfo, String>("Name");
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
        fileNameColumn.setPrefWidth(240d);

        final var fileSizeColumn = new TableColumn<FileInfo, Long>("Size");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize()));
        fileSizeColumn.setPrefWidth(120d);
        fileSizeColumn.setCellFactory(param -> new TableCell<FileInfo, Long >() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item == -1l) {
                        setText("[DIR]");
                        return;
                    }
                    setText(String.format("%,d bytes", item));
                }
            }
        });

        final var fileDateColumn = new TableColumn<FileInfo, String>("Last modified");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(FORMATTER)));
        fileDateColumn.setPrefWidth(120d);

        fileTable.getColumns().addAll(fileTypeColumn, fileNameColumn, fileSizeColumn, fileDateColumn);
        fileTable.getSortOrder().add(fileTypeColumn);

        diskBox.getItems().clear();
        for (Path path : FileSystems.getDefault().getRootDirectories()) {
            diskBox.getItems().add(path.toString());
        }
        diskBox.getSelectionModel().select(0);

        fileTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                final var path =  Paths.get(pathField.getText())
                        .resolve(fileTable.getSelectionModel().getSelectedItem().getName());
                if (Files.isDirectory(path)) {
                    updateList(path);
                }
            }
        });

        updateList(Paths.get("."));
    }

    public void updateList(Path path) {
        fileTable.getItems().clear();
        final var fileInfo = fileTable.getItems();
        try {
            pathField.setText(path.normalize().toAbsolutePath().toString());
            final var fileInfoList = Files.list(path).map(FileInfo::new).collect(Collectors.toList());
            fileInfo.addAll(fileInfoList);
        } catch (IOException e) {
            final  var alert = new Alert(Alert.AlertType.WARNING, "Failed to update directory", ButtonType.OK);
            alert.showAndWait();
        }
        fileTable.sort();
    }

    public void btnUpAction(ActionEvent actionEvent) {
        var upperPath = Paths.get(pathField.getText()).getParent();
        if (upperPath != null) {
            updateList(upperPath);
        }
    }

    public void selectDiskAction(ActionEvent actionEvent) {
        var comboBox = (ComboBox<String>) actionEvent.getSource();
        updateList(Paths.get(comboBox.getSelectionModel().getSelectedItem()));
    }

    public String getSelectedFileName() {
        if (!fileTable.isFocused()) {
            return null;
        }
        return fileTable.getSelectionModel().getSelectedItem().getName();
    }

    public String getCurrentPath() {
        return pathField.getText();
    }

}