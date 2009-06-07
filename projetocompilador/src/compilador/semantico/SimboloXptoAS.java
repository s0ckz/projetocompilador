package compilador.semantico;


public class SimboloXptoAS extends AbstractSimboloAS {

	private boolean constante;
	
	private TipoAS tipo;
	
	private Object valor;
	
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

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}
	
	public String toString() {
		return super.toString() + " - " + getSimboloASEnum() + " - Tipo: " + getTipo() + " - Valor: " + valor;  
	}

}
