package compilador.util;

public class Simbolo {
	
	private int codigo;
	
	private String cadeia;
	
	public Simbolo(int codigo, String cadeia) {
		this.codigo = codigo;
		this.cadeia = cadeia;
	}

	public Simbolo(int codigo, int caractere) {
		this(codigo, (char)caractere + "");
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getCadeia() {
		return cadeia;
	}

	public void setCadeia(String cadeia) {
		this.cadeia = cadeia;
	}
	
	public String toString() {
		return codigo + ":"+cadeia;
	}

	public boolean isIdentificador() {
		return getCodigo() == ConjuntoCodigos.IDENTIFICADOR;
	}

	public boolean isNumero() {
		return getCodigo() == ConjuntoCodigos.NUMERO;
	}

	public boolean isCadeia() {
		return getCodigo() == ConjuntoCodigos.CADEIA;
	}

	public boolean isOperadorRelacional() {
		return 	getCodigo() == ConjuntoCodigos.OP_MAIOR_OU_IGUAL_A || 
				getCodigo() == ConjuntoCodigos.OP_MAIOR_QUE ||
				getCodigo() == ConjuntoCodigos.OP_IGUAL ||
				getCodigo() == ConjuntoCodigos.OP_DIFERENCA ||
				getCodigo() == ConjuntoCodigos.OP_MENOR_OU_IGUAL_A ||
				getCodigo() == ConjuntoCodigos.OP_MENOR_QUE
			;
	}
	
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Simbolo))
			return false;
		Simbolo outro = (Simbolo) o;
		return getCodigo() == outro.getCodigo() &&
				getCadeia().equals(outro.getCadeia());
	}

	public static Simbolo createTipoNumero() {
		return new Simbolo(ConjuntoCodigos.INTEGER, "int");
	}

	public static Simbolo createTipoCadeia() {
		return new Simbolo(ConjuntoCodigos.STRING, "string");
	}

}
