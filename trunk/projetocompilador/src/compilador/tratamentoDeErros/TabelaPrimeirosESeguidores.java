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
				OP_IGUAL, OP_DIFERENCA, OP_MENOR_QUE, OP_MENOR_OU_IGUAL_A, PARENTISADOR_FECHA_PARENTESES));
		
		primeiros.put("declaracoes", makeList(CONST, STRING, INTEGER));
		seguidores.put("declaracoes", makeList(DEF, IF, WHILE, READ, WRITE, IDENTIFICADOR));
		
		primeiros.put("valor_inicial", makeList(IDENTIFICADOR));
		seguidores.put("valor_inicial", makeList(DELIMITADOR_VARIAVEL, DELIMITADOR_COMANDO));
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
