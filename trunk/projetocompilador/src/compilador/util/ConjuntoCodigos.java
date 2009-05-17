package compilador.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConjuntoCodigos {
	
	public static final int IDENTIFICADOR = 1;
	public static final int NUMERO = 2;
	public static final int CADEIA = 3;
	
	public static final int OP_IGUAL = 11;
	public static final int OP_SOMA = 12;
	public static final int OP_SUBTRACAO = 13;
	public static final int OP_MULTIPLICACAO = 14;
	public static final int OP_POTENCIA = 15;
	public static final int OP_DIVISAO = 24;

	public static final int OP_MAIOR_QUE = 16;
	public static final int OP_ATRIBUICAO = 17;
	public static final int OP_MENOR_QUE = 18;
	public static final int OP_MAIOR_OU_IGUAL_A = 19;
	public static final int OP_MENOR_OU_IGUAL_A = 20;
	public static final int OP_DIFERENCA = 21;
	public static final int OP_AND = 22;
	public static final int OP_OR = 23;

	public static final int PARENTISADOR_ABRE_PARENTESES = 31;
	public static final int PARENTISADOR_FECHA_PARENTESES = 32;
	public static final int PARENTISADOR_ABRE_CHAVES = 33;
	public static final int PARENTISADOR_FECHA_CHAVES = 34;
	public static final int PARENTISADOR_ABRE_COLCHETE = 35;
	public static final int PARENTISADOR_FECHA_COLCHETE = 36;
	
	public static final int DELIMITADOR_COMANDO = 40;
	public static final int DELIMITADOR_VARIAVEL = 41;

	public static final int CONST = 51;
	public static final int STRING = 52;
	public static final int INTEGER = 53;
	public static final int DEF = 54;
	public static final int IF = 55;
	public static final int THEN = 56;
	public static final int ELSE = 57;
	public static final int WHILE = 58;
	public static final int READ = 59;
	public static final int WRITE = 60;
	public static final int VOID = 61;
	public static final int TRUE = 62;
	public static final int FALSE = 63;
	
	private static Map<String, Integer> palavrasReservadas;
	
	private static Map<String, Integer> parentisadores;
	
	private static Map<String, Integer> simbolos;
	
	private static Set<Character> conjuntoSimbolos =
		new HashSet<Character>(
			Arrays.asList(
				'+', '-', '*', '/', '>', '<', '=', '!', '&', '|'
			)
		);
	
	static {
		palavrasReservadas = new HashMap<String, Integer>();
		palavrasReservadas.put("const", CONST);
		palavrasReservadas.put("string", STRING);
		palavrasReservadas.put("int", INTEGER);
		palavrasReservadas.put("def", DEF);
		palavrasReservadas.put("if", IF);
		palavrasReservadas.put("then", THEN);
		palavrasReservadas.put("else", ELSE);
		palavrasReservadas.put("while", WHILE);
		palavrasReservadas.put("read", READ);
		palavrasReservadas.put("write", WRITE);
		palavrasReservadas.put("void", VOID);
		palavrasReservadas.put("true", TRUE);
		palavrasReservadas.put("false", FALSE);

		parentisadores = new HashMap<String, Integer>();
		parentisadores.put("(", PARENTISADOR_ABRE_PARENTESES);
		parentisadores.put(")", PARENTISADOR_FECHA_PARENTESES);
		parentisadores.put("[", PARENTISADOR_ABRE_COLCHETE);
		parentisadores.put("]", PARENTISADOR_FECHA_COLCHETE);
		parentisadores.put("{", PARENTISADOR_ABRE_CHAVES);
		parentisadores.put("}", PARENTISADOR_FECHA_CHAVES);

		
		// se for colocar simbolos como > e >= (onde os dois come�am)
		// com >, coloque o que tiver maior tamanho primeiro, ou seja:
		// >= depois > .
		simbolos = new HashMap<String, Integer>();
		simbolos.put("+", OP_SOMA);
		simbolos.put("-", OP_SUBTRACAO);
		simbolos.put("*", OP_MULTIPLICACAO);
		simbolos.put("/", OP_DIVISAO);
		simbolos.put("==", OP_IGUAL);
		simbolos.put("=", OP_ATRIBUICAO);
		simbolos.put("**", OP_POTENCIA);
		simbolos.put(">=", OP_MAIOR_OU_IGUAL_A);
		simbolos.put(">", OP_MAIOR_QUE);
		simbolos.put("<=", OP_MENOR_OU_IGUAL_A);
		simbolos.put("<", OP_MENOR_QUE);
		simbolos.put("!=", OP_DIFERENCA);
		simbolos.put("&&", OP_AND);
		simbolos.put("||", OP_OR);
	}
	
	/**
	 * Retorna o c�digo para a palavra reservada. Se n�o for palavra reservada, retorna o c�digo para IDENTIFICADOR.
	 * @param cadeia Cadeia a ser verificada.
	 * @return O c�digo de identificador se a cadeia enviada n�o for uma palavra reservada.
	 */
	public static int getCodigoParaIdentificador(String cadeia) {
		Integer codigo = palavrasReservadas.get(cadeia);
		if (codigo == null)
			return IDENTIFICADOR;
		return codigo;
	}
	
	/**
	 * Retorna o c�digo para um parentisador. Se n�o for parentisador, retorna -1.
	 * @param cadeia Cadeia a ser verificada.
	 * @return -1 se a cadeia enviada n�o for um parentisador.
	 */
	public static int getCodigoParaParentisador(String cadeia) {
		Integer codigo = parentisadores.get(cadeia);
		if (codigo == null)
			return -1;
		return codigo;
	}
	/**
	 * Retorna o c�digo para um s�mbolo. Se n�o for s�mbolo, retorna -1.
	 * @param cadeia Cadeia a ser verificada.
	 * @return -1 se a cadeia enviada n�o for um s�mbolo.
	 */
	public static int getCodigoParaSimbolo(String cadeia) {
		Integer codigo = simbolos.get(cadeia);
		if (codigo == null)
			return -1;
		return codigo;
	}

	public static boolean isSimbolo(char caractere) {
		return conjuntoSimbolos.contains(caractere);
	}

}
