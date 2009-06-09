package compilador.tratamentoDeErros;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static compilador.util.ConjuntoCodigos.*;

public class TabelaPrimeirosESeguidores {
	
	private static Map<String, List<Integer>> primeiros = new HashMap<String, List<Integer>>();
	
	private static Map<String, List<Integer>> seguidores = new HashMap<String, List<Integer>>();
	
	static {
		primeiros.put("pred", makeList(PARENTISADOR_ABRE_PARENTESES, IDENTIFICADOR, NUMERO));
		seguidores.put("pred", makeList(OP_POTENCIA, OP_MULTIPLICACAO, OP_DIVISAO, OP_SOMA, 
				OP_SUBTRACAO, DELIMITADOR_COMANDO, DELIMITADOR_VARIAVEL, OP_MAIOR_QUE, OP_MAIOR_OU_IGUAL_A,
				OP_IGUAL, OP_DIFERENCA, OP_MENOR_QUE, OP_MENOR_OU_IGUAL_A, PARENTISADOR_FECHA_PARENTESES, 
				PARENTISADOR_FECHA_COLCHETE));
		
		primeiros.put("expressaoParentisada-)", makeList(PARENTISADOR_FECHA_PARENTESES));
		seguidores.put("expressaoParentisada-)", makeList(OP_POTENCIA, OP_MULTIPLICACAO, OP_DIVISAO, OP_SOMA, 
				OP_SUBTRACAO, DELIMITADOR_COMANDO, DELIMITADOR_VARIAVEL, OP_MAIOR_QUE, OP_MAIOR_OU_IGUAL_A,
				OP_IGUAL, OP_DIFERENCA, OP_MENOR_QUE, OP_MENOR_OU_IGUAL_A, PARENTISADOR_FECHA_PARENTESES, 
				PARENTISADOR_FECHA_COLCHETE));
		
		primeiros.put("declaracoes-;", makeList(DELIMITADOR_COMANDO));
		seguidores.put("declaracoes-;", makeList(CONST, STRING, INTEGER, DEF, IF, WHILE, READ, WRITE, IDENTIFICADOR));
		
		primeiros.put("valor_inicial", makeList(IDENTIFICADOR));
		seguidores.put("valor_inicial", makeList(OP_ATRIBUICAO, DELIMITADOR_VARIAVEL, DELIMITADOR_COMANDO));
		
		primeiros.put("valor_inicial_const", makeList(IDENTIFICADOR));
		seguidores.put("valor_inicial_const", makeList(OP_ATRIBUICAO));
		
		primeiros.put("valor_inicial_const-=", makeList(OP_ATRIBUICAO));
		seguidores.put("valor_inicial_const-=", makeList(CADEIA, PARENTISADOR_ABRE_PARENTESES, IDENTIFICADOR, NUMERO, PARENTISADOR_ABRE_CHAVES));
		
		primeiros.put("valor", makeList(CADEIA, PARENTISADOR_ABRE_PARENTESES, IDENTIFICADOR, NUMERO, PARENTISADOR_ABRE_CHAVES));
		seguidores.put("valor", makeList(DELIMITADOR_COMANDO, DELIMITADOR_VARIAVEL));
		
		primeiros.put("vetor-}", makeList(PARENTISADOR_FECHA_CHAVES));
		seguidores.put("vetor-}", makeList(DELIMITADOR_COMANDO, DELIMITADOR_VARIAVEL));
		
		primeiros.put("subprogramas-void", makeList(VOID));
		seguidores.put("subprogramas-void", makeList(IDENTIFICADOR));
		
		primeiros.put("subprogramas-identificador", makeList(IDENTIFICADOR));
		seguidores.put("subprogramas-identificador", makeList(PARENTISADOR_ABRE_CHAVES));
		
		primeiros.put("subprogramas-(", makeList(PARENTISADOR_ABRE_PARENTESES));
		seguidores.put("subprogramas-(", makeList(PARENTISADOR_FECHA_PARENTESES));
		
		primeiros.put("subprogramas-)", makeList(PARENTISADOR_FECHA_PARENTESES));
		seguidores.put("subprogramas-)", makeList(PARENTISADOR_ABRE_CHAVES));
		
		primeiros.put("subprogramas-{", makeList(PARENTISADOR_ABRE_CHAVES));
		seguidores.put("subprogramas-{", makeList(IF, WHILE, READ, WRITE, IDENTIFICADOR));
		
		primeiros.put("subprogramas-}", makeList(PARENTISADOR_FECHA_CHAVES));
		seguidores.put("subprogramas-}", makeList(DEF, IF, WHILE, READ, WRITE, IDENTIFICADOR));
		
		primeiros.put("comando-;", makeList(DELIMITADOR_COMANDO));
		seguidores.put("comando-;", makeList(IF, WHILE, READ, WRITE, IDENTIFICADOR));
		
		primeiros.put("comandoCondicional-(", makeList(PARENTISADOR_ABRE_PARENTESES));
		seguidores.put("comandoCondicional-(", makeList(IDENTIFICADOR, NUMERO, PARENTISADOR_ABRE_PARENTESES, PARENTISADOR_ABRE_COLCHETE));
		
		primeiros.put("comandoCondicional-)", makeList(PARENTISADOR_FECHA_PARENTESES));
		seguidores.put("comandoCondicional-)", makeList(PARENTISADOR_ABRE_CHAVES));
		
		primeiros.put("comandoCondicional-{", makeList(PARENTISADOR_ABRE_CHAVES));
		seguidores.put("comandoCondicional-{", makeList(IF, WHILE, READ, WRITE, IDENTIFICADOR));
		
		primeiros.put("comandoCondicional-}", makeList(PARENTISADOR_FECHA_CHAVES));
		seguidores.put("comandoCondicional-}", makeList(IF, WHILE, READ, WRITE, IDENTIFICADOR));
		
		primeiros.put("comandoRead", makeList(IDENTIFICADOR));
		seguidores.put("comandoRead", makeList(PARENTISADOR_ABRE_COLCHETE, DELIMITADOR_COMANDO));
		
		primeiros.put("fatorRelacional", makeList(OP_MAIOR_QUE, OP_MAIOR_OU_IGUAL_A, OP_IGUAL, OP_DIFERENCA, OP_MENOR_QUE, OP_MENOR_OU_IGUAL_A));
		seguidores.put("fatorRelacional", makeList(IDENTIFICADOR, NUMERO, PARENTISADOR_ABRE_PARENTESES));
		
		primeiros.put("eh_vetor-]", makeList(PARENTISADOR_FECHA_COLCHETE));
		seguidores.put("eh_vetor-]", makeList(IDENTIFICADOR, OP_ATRIBUICAO, DELIMITADOR_COMANDO, OP_POTENCIA, OP_MULTIPLICACAO, OP_DIVISAO, OP_SOMA, 
				OP_SUBTRACAO, DELIMITADOR_VARIAVEL, OP_MAIOR_QUE, OP_MAIOR_OU_IGUAL_A,
				OP_IGUAL, OP_DIFERENCA, OP_MENOR_QUE, OP_MENOR_OU_IGUAL_A, PARENTISADOR_FECHA_PARENTESES));
	}
	
	private static List<Integer> makeList(Integer... values) {
		List<Integer> list = new LinkedList<Integer>();
		for (Integer value : values) {
			list.add(value);
		}
		return list;
	}
	
	public static List<Integer> getPrimeiros(String nonTerminal) {
		return primeiros.get(nonTerminal);
	}

	public static List<Integer> getSeguidores(String nonTerminal) {
		return seguidores.get(nonTerminal);
	}
}
