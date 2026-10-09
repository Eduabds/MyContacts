package br.mycontacts.app.dao;

import br.mycontacts.app.database.Conexao;
import br.mycontacts.app.exceptions.ContatoNaoEncontradoException;
import br.mycontacts.app.exceptions.ErroPersistenciaException;
import br.mycontacts.app.model.Contato;
import br.mycontacts.app.model.ContatoComercial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContatoDAO {

    private final String jdbcUrl;

    public ContatoDAO() {
        this.jdbcUrl = null;
    }

    public ContatoDAO(String jdbcUrl) {
        this.jdbcUrl = Objects.requireNonNull(jdbcUrl, "A URL JDBC não pode ser nula.");
    }

    public void criarTabela() {
        String sql = """
                CREATE TABLE IF NOT EXISTS contatos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nome VARCHAR(100),
                    telefone VARCHAR(20),
                    email VARCHAR(100),
                    empresa VARCHAR(100)
                )
                """;

        try (Connection conexao = conectar();
             Statement instrucao = conexao.createStatement()) {
            instrucao.execute(sql);
        } catch (SQLException e) {
            throw new ErroPersistenciaException("Não foi possível criar a tabela de contatos.", e);
        }
    }

    public void inserir(Contato contato) {
        Objects.requireNonNull(contato, "O contato não pode ser nulo.");
        String sql = """
                INSERT INTO contatos (nome, telefone, email, empresa)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexao = conectar();
             PreparedStatement instrucao = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherParametros(instrucao, contato);
            instrucao.executeUpdate();
            try (ResultSet chaves = instrucao.getGeneratedKeys()) {
                if (chaves.next()) {
                    contato.setId(chaves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new ErroPersistenciaException("Não foi possível cadastrar o contato.", e);
        }
    }

    public List<Contato> listarTodos() {
        String sql = """
                SELECT id, nome, telefone, email, empresa
                FROM contatos
                ORDER BY id
                """;
        List<Contato> contatos = new ArrayList<>();

        try (Connection conexao = conectar();
             PreparedStatement instrucao = conexao.prepareStatement(sql);
             ResultSet resultado = instrucao.executeQuery()) {
            while (resultado.next()) {
                contatos.add(mapearContato(resultado));
            }
            return contatos;
        } catch (SQLException e) {
            throw new ErroPersistenciaException("Não foi possível listar os contatos.", e);
        }
    }

    public Contato buscarPorId(long id) throws ContatoNaoEncontradoException {
        String sql = """
                SELECT id, nome, telefone, email, empresa
                FROM contatos
                WHERE id = ?
                """;

        try (Connection conexao = conectar();
             PreparedStatement instrucao = conexao.prepareStatement(sql)) {
            instrucao.setLong(1, id);
            try (ResultSet resultado = instrucao.executeQuery()) {
                if (resultado.next()) {
                    return mapearContato(resultado);
                }
            }
        } catch (SQLException e) {
            throw new ErroPersistenciaException("Não foi possível buscar o contato.", e);
        }
        throw new ContatoNaoEncontradoException("Contato com ID " + id + " não encontrado.");
    }

    public Contato buscarPorNome(String nome) throws ContatoNaoEncontradoException {
        String sql = """
                SELECT id, nome, telefone, email, empresa
                FROM contatos
                WHERE nome = ? COLLATE NOCASE
                ORDER BY id
                LIMIT 1
                """;

        try (Connection conexao = conectar();
             PreparedStatement instrucao = conexao.prepareStatement(sql)) {
            instrucao.setString(1, nome);
            try (ResultSet resultado = instrucao.executeQuery()) {
                if (resultado.next()) {
                    return mapearContato(resultado);
                }
            }
        } catch (SQLException e) {
            throw new ErroPersistenciaException("Não foi possível buscar o contato.", e);
        }
        throw new ContatoNaoEncontradoException("Contato \"" + nome + "\" não encontrado.");
    }

    public boolean atualizar(Contato contato) {
        Objects.requireNonNull(contato, "O contato não pode ser nulo.");
        String sql = """
                UPDATE contatos
                SET nome = ?, telefone = ?, email = ?, empresa = ?
                WHERE id = ?
                """;

        try (Connection conexao = conectar();
             PreparedStatement instrucao = conexao.prepareStatement(sql)) {
            preencherParametros(instrucao, contato);
            instrucao.setLong(5, contato.getId());
            return instrucao.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ErroPersistenciaException("Não foi possível atualizar o contato.", e);
        }
    }

    public boolean removerPorId(long id) {
        String sql = "DELETE FROM contatos WHERE id = ?";
        try (Connection conexao = conectar();
             PreparedStatement instrucao = conexao.prepareStatement(sql)) {
            instrucao.setLong(1, id);
            return instrucao.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ErroPersistenciaException("Não foi possível remover o contato.", e);
        }
    }

    private void preencherParametros(PreparedStatement instrucao, Contato contato)
            throws SQLException {
        instrucao.setString(1, contato.getNome());
        instrucao.setString(2, contato.getTelefone());
        instrucao.setString(3, contato.getEmail());
        if (contato instanceof ContatoComercial comercial) {
            instrucao.setString(4, comercial.getEmpresa());
        } else {
            instrucao.setNull(4, Types.VARCHAR);
        }
    }

    private Connection conectar() throws SQLException {
        return jdbcUrl == null ? Conexao.conectar() : Conexao.conectar(jdbcUrl);
    }

    private Contato mapearContato(ResultSet resultado) throws SQLException {
        long id = resultado.getLong("id");
        String nome = resultado.getString("nome");
        String telefone = resultado.getString("telefone");
        String email = resultado.getString("email");
        String empresa = resultado.getString("empresa");
        if (empresa == null) {
            return new Contato(id, nome, telefone, email);
        }
        return new ContatoComercial(id, nome, telefone, email, empresa);
    }
}
