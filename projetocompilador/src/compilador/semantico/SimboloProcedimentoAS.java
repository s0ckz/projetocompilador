package compilador.semantico;

public class SimboloProcedimentoAS extends AbstractSimboloAS {

	public SimboloProcedimentoAS(String nome) {
		super(nome);
	}

	public SimboloASEnum getSimboloASEnum() {
		return SimboloASEnum.PROCEDIMENTO;
	}

}
