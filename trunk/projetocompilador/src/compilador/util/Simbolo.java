package compilador.util;

public class Simbolo {
	
	public static final int IDENTIFICADOR = 1;
	public static final int NUMERO = 2;
	
	public static final int OP_IGUAL = 11;
	public static final int OP_SOMA = 12;
	public static final int OP_SUB = 13;
	public static final int OP_MULT = 14;
	
	public static final int OP_MAIOR_QUE = 16;
	
	public static final int DELIMITADOR = 21;
	public static final int PONTUACAO = 22;
	
	public static final int CMD = 41;
	
	public static final int TIPO = 51;
	
	private int codigo;
	
	private String cadeia;
	
	public Simbolo(int codigo, String cadeia) {
		super();
		this.codigo = codigo;
		this.cadeia = cadeia;
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

}
