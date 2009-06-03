package compilador.lexico;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import compilador.util.ConjuntoCodigos;
import compilador.util.Simbolo;

public class AnalisadorLexico {
	
	public static final int TAMANHO_MAXIMO_NUMERO = 32;
	
	public static final int TAMANHO_MAXIMO_IDENTIFICADOR = 64;

	private static final int TAMANHO_MAXIMO_CADEIA = 512;
	
	private BufferedReader bufferedReader;
	
	private int caractereAtual;
	
	private boolean obteveSimbolo;
	
	private Simbolo simboloAtual;
	
	private List<String> advertencias = new LinkedList<String>();

	private boolean caractereAMaisLido = false;
	
	private int linhaAtual = 1;
	
	public AnalisadorLexico() {
	}

	public AnalisadorLexico(BufferedReader bufferedReader) {
		initialize(bufferedReader);
	}

	public void initialize(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	public Simbolo proximoSimbolo() throws AnalisadorLexicoException {
		if (bufferedReader == null) {
			throw new AnalisadorLexicoException("Analisador Lexico nao foi inicializado.");
		}

		obteveSimbolo = false;
		while ( !obteveSimbolo ) {
			
			if (!caractereAMaisLido)
				proximoCaractereNaoBranco();
			else
				caractereAMaisLido = false;
			
			if (caractereAtual == -1)
				return null;
			
			simboloAtual = null;
			if (Character.isLetter(caractereAtual))
				identificador();
			else if (Character.isDigit(caractereAtual))
				numero();
			else
				operadorParentisadorOuPontuacao();
		}
		
		return simboloAtual;
	}
	
	public int getLinhaAtual() {
		return linhaAtual;
	}
	
	private void operadorParentisadorOuPontuacao() throws AnalisadorLexicoException {
		if (!parentisador() && !pontuacao() && !cadeia())
			operador();
	}

	private boolean cadeia() throws AnalisadorLexicoException {
		if (caractereAtual == '"') {
			StringBuilder cadeia = new StringBuilder();

			while (proximoCaractere() != -1 && caractereAtual != '"') {
				cadeia.append((char) caractereAtual);
			}
			
			if (caractereAtual != -1) {
				if (cadeia.length() > TAMANHO_MAXIMO_CADEIA) {
					advertencias.add("Tamanho máximo das cadeias de caracteres deve ser " + TAMANHO_MAXIMO_CADEIA);
					cadeia.setLength(TAMANHO_MAXIMO_CADEIA);
				}
				simboloAtual = new Simbolo(ConjuntoCodigos.CADEIA, '"' + cadeia.toString() + '"');
				obteveSimbolo = true;
			} else {
				obteveSimbolo = false;
				throw new AnalisadorLexicoException("Cadeias de caracteres devem conter aspas no final delas.");
			}
		}
		return obteveSimbolo;
	}

	private boolean pontuacao() {
		if (caractereAtual == ';') {
			simboloAtual = new Simbolo(ConjuntoCodigos.DELIMITADOR_COMANDO, caractereAtual);
			obteveSimbolo = true;
		} else if (caractereAtual == ',') {
			simboloAtual = new Simbolo(ConjuntoCodigos.DELIMITADOR_VARIAVEL, caractereAtual);
			obteveSimbolo = true;
		}
		return obteveSimbolo;
	}

	private boolean parentisador() {
		int codigo = ConjuntoCodigos.getCodigoParaParentisador((char) caractereAtual + "");
		if (codigo != -1) {
			simboloAtual = new Simbolo(codigo, caractereAtual);
			obteveSimbolo = true;
		}
		return obteveSimbolo;
	}

	private void numero() {
		StringBuilder cadeia = new StringBuilder();
		while (Character.isDigit(caractereAtual)) {
			cadeia.append((char) caractereAtual);
			proximoCaractere();
			atualizarCaractereAMaisLido();
		}
		
		if (cadeia.length() > TAMANHO_MAXIMO_NUMERO) {
			cadeia.setLength(TAMANHO_MAXIMO_NUMERO);
			advertencias.add("Tamanho máximo para números é " + TAMANHO_MAXIMO_NUMERO);
		}
		simboloAtual = new Simbolo(ConjuntoCodigos.NUMERO, cadeia.toString());
		obteveSimbolo = true;
	}

	private void identificador() {
		StringBuilder cadeia = new StringBuilder();
		while (Character.isLetterOrDigit(caractereAtual)) {
			cadeia.append((char) caractereAtual);
			proximoCaractere();
			atualizarCaractereAMaisLido();
		}
		
		if (cadeia.length() > TAMANHO_MAXIMO_IDENTIFICADOR) {
			cadeia.setLength(TAMANHO_MAXIMO_IDENTIFICADOR);
			advertencias.add("Tamanho máximo para identificador é " + TAMANHO_MAXIMO_IDENTIFICADOR);
		}
		simboloAtual = new Simbolo(ConjuntoCodigos.getCodigoParaIdentificador(cadeia.toString()), cadeia.toString());
		obteveSimbolo = true;
	}

	private void operador() {
		obteveSimbolo = true;
		StringBuilder cadeia = new StringBuilder();
		while (ConjuntoCodigos.isSimbolo((char) caractereAtual)) {
			cadeia.append((char) caractereAtual);
			proximoCaractere();
			atualizarCaractereAMaisLido();
		}

		int codigo = ConjuntoCodigos.getCodigoParaSimbolo(cadeia.toString());
		if (codigo != -1) {
			simboloAtual = new Simbolo(codigo, cadeia.toString());
			obteveSimbolo = true;
		} else {
			obteveSimbolo = false;
			advertencias.add("Não foi possível identificar o símbolo.");
		}
	}

	private int proximoCaractere() {
		try {
			caractereAtual = bufferedReader.read();
			if (caractereAtual == '\n')
				linhaAtual++;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return caractereAtual;
	}

	private void proximoCaractereNaoBranco() {
		while (proximoCaractere() == ' '
				|| caractereAtual == '\n'
				|| caractereAtual == '\r'
				|| caractereAtual == '\t');
	}

	private void atualizarCaractereAMaisLido() {
		caractereAMaisLido = caractereAtual != ' ';
	}
	
	public List<String> getAdvertencias() {
		return advertencias;
	}
}
