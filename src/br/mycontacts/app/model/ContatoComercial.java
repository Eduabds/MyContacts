package br.mycontacts.app.model;


	// Subclasse que herda de Contato e representa um contato comercial
	// ContatoComercial apresenta um atributo adicional "empresa" e metodo @Override 
public class ContatoComercial extends Contato {
 
    private String empresa;
 
    public ContatoComercial(String nome, String telefone, String email, String empresa) {
        super(nome, telefone, email);
        this.empresa = empresa;
    }

    public ContatoComercial(long id, String nome, String telefone, String email, String empresa) {
        super(id, nome, telefone, email);
        this.empresa = empresa;
    }
 
    public String getEmpresa() {
        return empresa;
    }
 
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
 
    @Override
    public String toString() {
        return super.toString() + " | Empresa: " + empresa;
    }
}