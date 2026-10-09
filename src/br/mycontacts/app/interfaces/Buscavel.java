package br.mycontacts.app.interfaces;


import br.mycontacts.app.model.Contato;
import br.mycontacts.app.exceptions.ContatoNaoEncontradoException;
 
public interface Buscavel<T extends Contato> {
 
    T buscarPorNome(String nome) throws ContatoNaoEncontradoException;
}
