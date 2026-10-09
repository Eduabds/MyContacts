package br.mycontacts.app;

import br.mycontacts.app.controller.Agenda;
import br.mycontacts.app.exceptions.ErroPersistenciaException;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

public class AgendaApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Agenda agenda = new Agenda();
        try {
            agenda.inicializarBanco();
        } catch (ErroPersistenciaException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alerta.setHeaderText("Falha ao inicializar o banco de dados");
            alerta.showAndWait();
            Platform.exit();
            return;
        }

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/br/mycontacts/app/view/agenda-view.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 960, 600);
        scene.getStylesheets().add(
                getClass().getResource("/br/mycontacts/app/view/styles.css").toExternalForm());

        stage.setTitle("MyContacts");
        stage.setMinWidth(760);
        stage.setMinHeight(480);
        stage.setScene(scene);
        stage.show();
        AgendaViewController controller = loader.getController();
        controller.setStage(stage);
        controller.setAgenda(agenda);
    }
}
