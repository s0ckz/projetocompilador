package compilador.semantico;

public abstract class AbstractSimboloAS implements SimboloAS {

	private String nome;
	
	public AbstractSimboloAS(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

}
