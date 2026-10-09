package br.mycontacts.app.repository;

import br.mycontacts.app.exceptions.ContatoNaoEncontradoException;
import br.mycontacts.app.interfaces.Buscavel;
import br.mycontacts.app.model.Contato;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Repositorio<T extends Contato> implements Buscavel<T> {

    private final List<T> contatos = new ArrayList<>();

    public void adicionar(T contato) {
        contatos.add(Objects.requireNonNull(contato, "O contato não pode ser nulo."));
    }

    public void adicionarTodos(Collection<? extends T> novosContatos) {
        Objects.requireNonNull(novosContatos, "A coleção não pode ser nula.");
        for (T contato : novosContatos) {
            Objects.requireNonNull(contato, "O contato não pode ser nulo.");
        }
        contatos.addAll(novosContatos);
    }

    public List<T> listar() {
        return new ArrayList<>(contatos);
    }

    public void listarEm(Collection<? super T> destino) {
        Objects.requireNonNull(destino, "A coleção de destino não pode ser nula.")
                .addAll(contatos);
    }

    @Override
    public T buscarPorNome(String nome) throws ContatoNaoEncontradoException {
        for (T contato : contatos) {
            if (contato.getNome().equalsIgnoreCase(nome)) {
                return contato;
            }
        }
        throw new ContatoNaoEncontradoException("Contato \"" + nome + "\" não encontrado.");
    }

    public <S extends T> List<S> buscarPorTipo(Class<S> tipo) {
        return filtrarPorTipo(contatos, tipo);
    }

    public T removerPorNome(String nome) throws ContatoNaoEncontradoException {
        T contato = buscarPorNome(nome);
        contatos.remove(contato);
        return contato;
    }

    public boolean remover(T contato) {
        return contatos.remove(contato);
    }

    public boolean substituir(T contatoAntigo, T contatoAtualizado) {
        Objects.requireNonNull(contatoAntigo, "O contato antigo não pode ser nulo.");
        Objects.requireNonNull(contatoAtualizado, "O contato atualizado não pode ser nulo.");

        int indice = contatos.indexOf(contatoAntigo);
        if (indice < 0) {
            return false;
        }
        contatos.set(indice, contatoAtualizado);
        return true;
    }

    public static <S extends Contato> List<S> filtrarPorTipo(
            Collection<?> elementos, Class<S> tipo) {
        Objects.requireNonNull(elementos, "A coleção não pode ser nula.");
        Objects.requireNonNull(tipo, "O tipo não pode ser nulo.");

        List<S> resultado = new ArrayList<>();
        for (Object elemento : elementos) {
            if (tipo.isInstance(elemento)) {
                resultado.add(tipo.cast(elemento));
            }
        }
        return resultado;
    }
}
