package br.mycontacts.app.controller;

import br.mycontacts.app.exceptions.ContatoNaoEncontradoException;
import br.mycontacts.app.interfaces.Buscavel;
import br.mycontacts.app.dao.ContatoDAO;
import br.mycontacts.app.model.Contato;
import java.util.List;
 
public class Agenda implements Buscavel<Contato> {
 
    private final ContatoDAO contatoDAO;
 
    public Agenda() {
        this.contatoDAO = new ContatoDAO();
    }

    public void inicializarBanco() {
        contatoDAO.criarTabela();
    }
 
    public void adicionarContato(Contato contato) {
        contatoDAO.inserir(contato);
    }
 
    public List<Contato> listarContatos() {
        return contatoDAO.listarTodos();
    }
 
    @Override
    public Contato buscarPorNome(String nome) throws ContatoNaoEncontradoException {
        return contatoDAO.buscarPorNome(nome);
    }
 
    public void removerContato(String nome) throws ContatoNaoEncontradoException {
        Contato contato = contatoDAO.buscarPorNome(nome);
        if (!contatoDAO.removerPorId(contato.getId())) {
            throw new ContatoNaoEncontradoException(
                    "Contato \"" + nome + "\" não encontrado.");
        }
    }

    public boolean removerContato(Contato contato) {
        return contatoDAO.removerPorId(contato.getId());
    }

    public boolean atualizarContato(Contato contatoAntigo, Contato contatoAtualizado) {
        contatoAtualizado.setId(contatoAntigo.getId());
        return contatoDAO.atualizar(contatoAtualizado);
    }
}
