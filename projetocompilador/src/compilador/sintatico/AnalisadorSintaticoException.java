package compilador.sintatico;

public class AnalisadorSintaticoException extends Exception {

	private static final long serialVersionUID = 7522763736860745555L;

	public AnalisadorSintaticoException(int linha, String arg0) {
		super(getMensagemErro(linha, arg0));
	}

	public static String getMensagemErro(int linha, String arg0) {
		return "Linha: " + linha + " - " + arg0;
	}

	
}
