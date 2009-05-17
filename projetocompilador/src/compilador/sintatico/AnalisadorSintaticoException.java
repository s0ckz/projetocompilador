package compilador.sintatico;

public class AnalisadorSintaticoException extends Exception {

	private static final long serialVersionUID = 7522763736860745555L;

	public AnalisadorSintaticoException(int linha, String arg0) {
		super("Linha: " + linha + " - " + arg0);
	}

	
}
