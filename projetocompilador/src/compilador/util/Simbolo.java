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

}
