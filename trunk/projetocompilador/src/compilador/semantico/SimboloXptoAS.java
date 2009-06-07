package compilador.semantico;


public class SimboloXptoAS extends AbstractSimboloAS {

	private boolean constante;
	
	private TipoAS tipo;
	
	public SimboloXptoAS(String nome, TipoAS tipo, boolean constante) {
		super(nome);
		this.tipo = tipo;
		this.constante = constante;
	}

	public SimboloASEnum getSimboloASEnum() {
		return (constante) ? SimboloASEnum.CONSTANTE : SimboloASEnum.VARIAVEL;
	}

	public TipoAS getTipo() {
		return tipo;
	}

	public void setTipo(TipoAS tipo) {
		this.tipo = tipo;
	}

	public String toString() {
		return super.toString() + " - " + getSimboloASEnum() + " - Tipo: " + getTipo();  
	}

}
