package br.mycontacts.app.model;


 // Classe que estabelce os atributos de um Contato comum
 // Encapsulamento: Atributos são privados e acessados através de getters e setters.
public class Contato {
 
    private long id;
    private String nome;
    private String telefone;
    private String email;
 
    public Contato(String nome, String telefone, String email) {
        this(-1, nome, telefone, email);
    }

    public Contato(long id, String nome, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
 
    public String getNome() {
        return nome;
    }
 
    public void setNome(String nome) {
        this.nome = nome;
    }
 
    public String getTelefone() {
        return telefone;
    }
 
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    @Override
    public String toString() {
        return "Nome: " + nome + " | Telefone: " + telefone + " | Email: " + email;
    }
}