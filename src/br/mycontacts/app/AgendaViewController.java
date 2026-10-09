package br.mycontacts.app;

import br.mycontacts.app.controller.Agenda;
import br.mycontacts.app.exceptions.ErroPersistenciaException;
import br.mycontacts.app.model.Contato;
import br.mycontacts.app.model.ContatoComercial;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AgendaViewController {

    private Agenda agenda;
    private List<Contato> contatosCadastrados = List.of();
    private final ObservableList<Contato> contatosExibidos = FXCollections.observableArrayList();
    private Stage stage;

    @FXML
    private TextField campoBusca;

    @FXML
    private TableView<Contato> tabelaContatos;

    @FXML
    private TableColumn<Contato, String> colunaNome;

    @FXML
    private TableColumn<Contato, String> colunaTelefone;

    @FXML
    private TableColumn<Contato, String> colunaEmail;

    @FXML
    private TableColumn<Contato, String> colunaTipo;

    @FXML
    private TableColumn<Contato, String> colunaEmpresa;

    @FXML
    private void initialize() {
        tabelaContatos.setItems(contatosExibidos);
        tabelaContatos.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colunaNome.setCellValueFactory(dado ->
                new ReadOnlyStringWrapper(dado.getValue().getNome()));
        colunaTelefone.setCellValueFactory(dado ->
                new ReadOnlyStringWrapper(dado.getValue().getTelefone()));
        colunaEmail.setCellValueFactory(dado ->
                new ReadOnlyStringWrapper(dado.getValue().getEmail()));
        colunaTipo.setCellValueFactory(dado ->
                new ReadOnlyStringWrapper(
                        dado.getValue() instanceof ContatoComercial ? "Comercial" : "Pessoal"));
        colunaEmpresa.setCellValueFactory(dado -> {
            Contato contato = dado.getValue();
            String empresa = contato instanceof ContatoComercial comercial
                    ? comercial.getEmpresa()
                    : "";
            return new ReadOnlyStringWrapper(empresa);
        });
        campoBusca.textProperty().addListener(
                (observable, anterior, atual) -> atualizarTabela());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
        try {
            contatosCadastrados = agenda.listarContatos();
            atualizarTabela();
        } catch (ErroPersistenciaException e) {
            mostrarErro(e.getMessage());
            Platform.exit();
        }
    }

    @FXML
    private void adicionarContato() {
        abrirFormulario(null);
    }

    @FXML
    private void editarContatoSelecionado() {
        Contato selecionado = tabelaContatos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Selecione um contato para editar.");
            return;
        }
        abrirFormulario(selecionado);
    }

    @FXML
    private void removerContatoSelecionado() {
        Contato selecionado = tabelaContatos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Selecione um contato para remover.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.initOwner(stage);
        confirmacao.setTitle("Remover contato");
        confirmacao.setHeaderText("Deseja remover " + selecionado.getNome() + "?");
        confirmacao.setContentText("Esta ação não pode ser desfeita.");
        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            try {
                if (!agenda.removerContato(selecionado)) {
                    mostrarErro("O contato selecionado não está mais na agenda.");
                    return;
                }
                recarregarContatos();
            } catch (ErroPersistenciaException e) {
                mostrarErro(e.getMessage());
            }
        }
    }

    private void abrirFormulario(Contato contatoExistente) {
        boolean editando = contatoExistente != null;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/br/mycontacts/app/view/contato-form.fxml"));
            GridPane formulario = loader.load();
            ContatoFormController formController = loader.getController();
            formController.configurar(contatoExistente);

            Dialog<Contato> dialogo = new Dialog<>();
            dialogo.initOwner(stage);
            dialogo.setTitle(editando ? "Editar contato" : "Novo contato");
            dialogo.setHeaderText(editando
                    ? "Atualize os dados do contato."
                    : "Preencha os dados para cadastrar um contato.");
            dialogo.getDialogPane().setContent(formulario);

            ButtonType salvar = new ButtonType(
                    editando ? "Salvar alterações" : "Cadastrar", ButtonBar.ButtonData.OK_DONE);
            dialogo.getDialogPane().getButtonTypes().addAll(salvar, ButtonType.CANCEL);
            dialogo.getDialogPane().getStylesheets().add(
                    getClass().getResource("/br/mycontacts/app/view/styles.css").toExternalForm());

            Button botaoSalvar = (Button) dialogo.getDialogPane().lookupButton(salvar);
            botaoSalvar.addEventFilter(ActionEvent.ACTION, event -> {
                if (!formController.validarFormulario()) {
                    mostrarErro(formController.getMensagemErro());
                    event.consume();
                    return;
                }

                Contato contato = formController.criarContato();
                try {
                    if (editando) {
                        if (!agenda.atualizarContato(contatoExistente, contato)) {
                            mostrarErro("Não foi possível atualizar o contato na agenda.");
                            event.consume();
                        }
                    } else {
                        agenda.adicionarContato(contato);
                    }
                } catch (ErroPersistenciaException e) {
                    mostrarErro(e.getMessage());
                    event.consume();
                }
            });
            dialogo.setResultConverter(botao ->
                    botao == salvar ? formController.criarContato() : null);

            dialogo.showAndWait().ifPresent(contato -> recarregarContatos());
        } catch (IOException e) {
            mostrarErro("Não foi possível abrir o formulário de contato: " + e.getMessage());
        }
    }

    private void recarregarContatos() {
        try {
            contatosCadastrados = agenda.listarContatos();
            atualizarTabela();
        } catch (ErroPersistenciaException e) {
            mostrarErro(e.getMessage());
        }
    }

    private void atualizarTabela() {
        String busca = campoBusca.getText() == null
                ? ""
                : campoBusca.getText().trim().toLowerCase(Locale.ROOT);
        contatosExibidos.setAll(contatosCadastrados.stream()
                .filter(contato -> contato.getNome().toLowerCase(Locale.ROOT).contains(busca))
                .toList());
    }

    private void mostrarErro(String mensagem) {
        mostrarAlerta(Alert.AlertType.ERROR, mensagem);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensagem) {
        Alert alerta = new Alert(tipo, mensagem, ButtonType.OK);
        alerta.initOwner(stage);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
