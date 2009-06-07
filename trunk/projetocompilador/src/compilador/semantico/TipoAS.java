package compilador.semantico;

import compilador.util.Simbolo;

public class TipoAS {
	
	private Simbolo simbolo;
	
	private boolean vetor;

	public TipoAS(Simbolo simbolo, boolean vetor) {
		this.simbolo = simbolo;
		this.vetor = vetor;
	}

	public Simbolo getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(Simbolo simbolo) {
		this.simbolo = simbolo;
	}

	public boolean isVetor() {
		return vetor;
	}

	public void setVetor(boolean vetor) {
		this.vetor = vetor;
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof TipoAS))
			return false;
		TipoAS outro = (TipoAS) o;
		return getSimbolo().equals(outro.getSimbolo()) &&
				isVetor() == outro.isVetor();
	}
	
	public String toString() {
		return simbolo.getCadeia() + ((vetor) ? "[]" : "");
	}

}
