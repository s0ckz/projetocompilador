package compilador.tratamentoDeErros;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import compilador.util.ConjuntoCodigos;

public class TabelaPrimeirosESeguidores {
	
	private static Map<String, List<Integer>> primeiros = new HashMap<String, List<Integer>>();
	
	private static Map<String, List<Integer>> seguidores = new HashMap<String, List<Integer>>();
	
	static {
		primeiros.put("pred", makeList(ConjuntoCodigos.PARENTISADOR_ABRE_PARENTESES, ConjuntoCodigos.IDENTIFICADOR, ConjuntoCodigos.NUMERO));
		seguidores.put("pred", makeList());
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
