package br.mycontacts.app;

import br.mycontacts.app.model.Contato;
import br.mycontacts.app.model.ContatoComercial;
import br.mycontacts.app.utils.ValidadorEmail;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ContatoFormController {

    @FXML
    private TextField campoNome;

    @FXML
    private TextField campoTelefone;

    @FXML
    private TextField campoEmail;

    @FXML
    private ComboBox<TipoContato> comboTipo;

    @FXML
    private TextField campoEmpresa;

    private String mensagemErro;

    @FXML
    private void initialize() {
        comboTipo.getItems().setAll(TipoContato.values());
        comboTipo.valueProperty().addListener((observable, anterior, atual) -> atualizarEmpresa());
        comboTipo.setValue(TipoContato.PESSOAL);
    }

    public void configurar(Contato contato) {
        if (contato == null) {
            return;
        }

        campoNome.setText(contato.getNome());
        campoTelefone.setText(contato.getTelefone());
        campoEmail.setText(contato.getEmail());
        if (contato instanceof ContatoComercial comercial) {
            comboTipo.setValue(TipoContato.COMERCIAL);
            campoEmpresa.setText(comercial.getEmpresa());
        }
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public boolean validarFormulario() {
        return validar();
    }

    public Contato criarContato() {
        if (!validar()) {
            return null;
        }

        String nome = campoNome.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String email = campoEmail.getText().trim();
        if (comboTipo.getValue() == TipoContato.COMERCIAL) {
            return new ContatoComercial(nome, telefone, email, campoEmpresa.getText().trim());
        }
        return new Contato(nome, telefone, email);
    }

    private boolean validar() {
        mensagemErro = null;
        if (campoNome.getText() == null || campoNome.getText().isBlank()) {
            mensagemErro = "Informe o nome do contato.";
            return false;
        }
        if (campoNome.getText().trim().length() > 100) {
            mensagemErro = "O nome deve ter no máximo 100 caracteres.";
            return false;
        }
        if (campoTelefone.getText() == null || campoTelefone.getText().isBlank()) {
            mensagemErro = "Informe o telefone do contato.";
            return false;
        }
        if (campoTelefone.getText().trim().length() > 20) {
            mensagemErro = "O telefone deve ter no máximo 20 caracteres.";
            return false;
        }
        if (!ValidadorEmail.isValido(campoEmail.getText())) {
            mensagemErro = "Informe um endereço de e-mail válido.";
            return false;
        }
        if (campoEmail.getText().trim().length() > 100) {
            mensagemErro = "O e-mail deve ter no máximo 100 caracteres.";
            return false;
        }
        if (comboTipo.getValue() == null) {
            mensagemErro = "Selecione o tipo de contato.";
            return false;
        }
        if (comboTipo.getValue() == TipoContato.COMERCIAL
                && (campoEmpresa.getText() == null || campoEmpresa.getText().isBlank())) {
            mensagemErro = "Informe a empresa do contato comercial.";
            return false;
        }
        if (comboTipo.getValue() == TipoContato.COMERCIAL
                && campoEmpresa.getText().trim().length() > 100) {
            mensagemErro = "A empresa deve ter no máximo 100 caracteres.";
            return false;
        }
        return true;
    }

    private void atualizarEmpresa() {
        boolean comercial = comboTipo.getValue() == TipoContato.COMERCIAL;
        campoEmpresa.setDisable(!comercial);
        if (!comercial) {
            campoEmpresa.clear();
        }
    }

    private enum TipoContato {
        PESSOAL("Pessoal"),
        COMERCIAL("Comercial");

        private final String descricao;

        TipoContato(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
