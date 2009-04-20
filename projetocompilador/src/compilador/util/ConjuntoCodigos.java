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

	public static final int OP_MAIOR_QUE = 16;
	public static final int OP_ATRIBUICAO = 17;

	public static final int PARENTISADOR_ABRE_PARENTESES = 21;
	public static final int PARENTISADOR_FECHA_PARENTESES = 22;
	public static final int PARENTISADOR_ABRE_CHAVES = 23;
	public static final int PARENTISADOR_FECHA_CHAVES = 24;
	public static final int PARENTISADOR_ABRE_COLCHETE = 25;
	public static final int PARENTISADOR_FECHA_COLCHETE = 26;
	
	public static final int DELIMITADOR_COMANDO = 40;

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
				'+', '-', '*', '/', '>', '<', '=', '!'
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

		simbolos = new HashMap<String, Integer>();
		simbolos.put("+", OP_SOMA);
		simbolos.put("-", OP_SUBTRACAO);
		simbolos.put("*", OP_MULTIPLICACAO);
		simbolos.put("==", OP_IGUAL);
		simbolos.put("=", OP_ATRIBUICAO);
		simbolos.put("**", OP_POTENCIA);
		simbolos.put(">", OP_MAIOR_QUE);
	}
	
	/**
	 * Retorna o código para a palavra reservada. Se não for palavra reservada, retorna o código para IDENTIFICADOR.
	 * @param cadeia Cadeia a ser verificada.
	 * @return O código de identificador se a cadeia enviada não for uma palavra reservada.
	 */
	public static int getCodigoParaIdentificador(String cadeia) {
		Integer codigo = palavrasReservadas.get(cadeia);
		if (codigo == null)
			return IDENTIFICADOR;
		return codigo;
	}
	
	/**
	 * Retorna o código para um parentisador. Se não for parentisador, retorna -1.
	 * @param cadeia Cadeia a ser verificada.
	 * @return -1 se a cadeia enviada não for um parentisador.
	 */
	public static int getCodigoParaParentisador(String cadeia) {
		Integer codigo = parentisadores.get(cadeia);
		if (codigo == null)
			return -1;
		return codigo;
	}
	/**
	 * Retorna o código para um símbolo. Se não for símbolo, retorna -1.
	 * @param cadeia Cadeia a ser verificada.
	 * @return -1 se a cadeia enviada não for um símbolo.
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
