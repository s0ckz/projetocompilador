package compilador.sintatico;

public class AnalisadorSintaticoException extends Exception {

	private static final long serialVersionUID = 7522763736860745555L;

	public AnalisadorSintaticoException(int linha, String conteudo, String arg0) {
		super(getMensagemErro(linha, conteudo, arg0));
	}

	public static String getMensagemErro(int linha, String conteudoLinha, String arg0) {
		return "Erro na linha: " + linha + " perto de: '" + conteudoLinha +  "' - " + arg0;
	}

	
}
