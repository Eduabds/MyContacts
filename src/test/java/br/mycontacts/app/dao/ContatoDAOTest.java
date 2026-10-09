package br.mycontacts.app.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.mycontacts.app.database.Conexao;
import br.mycontacts.app.exceptions.ContatoNaoEncontradoException;
import br.mycontacts.app.model.Contato;
import br.mycontacts.app.model.ContatoComercial;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContatoDAOTest {

    private Connection conexaoDeManutencao;
    private ContatoDAO contatoDAO;

    @BeforeEach
    void configurarBancoEmMemoria() throws SQLException {
        String nomeBanco = "mycontacts-test-" + UUID.randomUUID();
        String url = "jdbc:sqlite:file:" + nomeBanco + "?mode=memory&cache=shared";
        conexaoDeManutencao = Conexao.conectar(url);
        contatoDAO = new ContatoDAO(url);
        contatoDAO.criarTabela();
    }

    @AfterEach
    void fecharBancoEmMemoria() throws SQLException {
        if (conexaoDeManutencao != null) {
            conexaoDeManutencao.close();
        }
    }

    @Test
    void inserirEListarContatosPessoaisEComerciais() {
        Contato pessoal = new Contato("Ana", "111", "ana@example.com");
        ContatoComercial comercial =
                new ContatoComercial("Bruno", "222", "bruno@example.com", "Acme");

        contatoDAO.inserir(pessoal);
        contatoDAO.inserir(comercial);

        assertTrue(pessoal.getId() > 0);
        assertTrue(comercial.getId() > pessoal.getId());
        var contatos = contatoDAO.listarTodos();
        assertEquals(2, contatos.size());
        assertEquals(pessoal.getId(), contatos.get(0).getId());
        assertInstanceOf(Contato.class, contatos.get(0));
        assertEquals(comercial.getId(), contatos.get(1).getId());
        ContatoComercial carregado = assertInstanceOf(ContatoComercial.class, contatos.get(1));
        assertEquals("Acme", carregado.getEmpresa());
    }

    @Test
    void buscarPorNomeIgnoraMaiusculasEMinusculas() throws ContatoNaoEncontradoException {
        Contato contato = new Contato("Camila", "333", "camila@example.com");
        contatoDAO.inserir(contato);

        assertEquals(contato.getId(), contatoDAO.buscarPorNome("cAmIlA").getId());
        assertEquals(contato.getId(), contatoDAO.buscarPorId(contato.getId()).getId());
    }

    @Test
    void buscaInexistenteLancaExcecao() {
        assertThrows(ContatoNaoEncontradoException.class,
                () -> contatoDAO.buscarPorNome("Inexistente"));
        assertThrows(ContatoNaoEncontradoException.class,
                () -> contatoDAO.buscarPorId(999));
    }

    @Test
    void atualizarAlteraCamposEPermiteMudarTipoDeContato() throws ContatoNaoEncontradoException {
        Contato original = new Contato("Daniel", "444", "daniel@example.com");
        contatoDAO.inserir(original);

        ContatoComercial atualizado =
                new ContatoComercial(original.getId(), "Daniel", "555", "d@acme.com", "Acme");

        assertTrue(contatoDAO.atualizar(atualizado));
        ContatoComercial encontrado =
                assertInstanceOf(ContatoComercial.class, contatoDAO.buscarPorId(original.getId()));
        assertEquals("555", encontrado.getTelefone());
        assertEquals("d@acme.com", encontrado.getEmail());
        assertEquals("Acme", encontrado.getEmpresa());
    }

    @Test
    void removerExcluiContatoDoBanco() {
        Contato contato = new Contato("Eduardo", "666", "eduardo@example.com");
        contatoDAO.inserir(contato);

        assertTrue(contatoDAO.removerPorId(contato.getId()));
        assertFalse(contatoDAO.removerPorId(contato.getId()));
        assertTrue(contatoDAO.listarTodos().isEmpty());
    }

    @Test
    void preparedStatementTrataTextoComoDadoENaoComoSql() throws ContatoNaoEncontradoException {
        String nome = "Ana'); DROP TABLE contatos; --";
        Contato contato = new Contato(nome, "777", "ana@example.com");
        contatoDAO.inserir(contato);

        assertEquals(nome, contatoDAO.buscarPorNome(nome).getNome());
        assertNotEquals(0, contato.getId());
        assertEquals(1, contatoDAO.listarTodos().size());
    }
}
