module ru.zavoyko.fmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    opens ru.zavoyko.fmanager to javafx.fxml;
    exports ru.zavoyko.fmanager;
    exports ru.zavoyko.fmanager.fileInfo;
    opens ru.zavoyko.fmanager.fileInfo to javafx.fxml;
}