package compilador.semantico;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import compilador.util.Simbolo;

public class AnalisadorSemantico {
	
	private Map<String, SimboloAS> tabelaSimbolos = new HashMap<String, SimboloAS>();
	
	private LinkedList<TipoAS> pilhaControleTipos = new LinkedList<TipoAS>();
	
	private boolean declarandoConstantes = false;

	public void asEmpilharTipo(Simbolo tipo, boolean vetor) {
		pushTipo(new TipoAS(tipo, vetor));
	}
	
	public void asAtivarDeclaracaoConstantes() {
		declarandoConstantes = true;
	}
	
	public void asDesativarDeclaracaoConstantes() {
		declarandoConstantes = false;
	}

	public void asDeclararXpto(Simbolo simbolo) throws AnalisadorSemanticoException {
		try {
			TipoAS tipo = popTipo();
			String identificador = simbolo.getCadeia();
			SimboloAS simboloAS = new SimboloXptoAS(identificador, tipo, declarandoConstantes);
			inserir(identificador, simboloAS);
		} catch (NoSuchElementException e) {
			throw new AnalisadorSemanticoException("Fudeu!");
		}
	}

	private boolean contem(String identificador) {
		return tabelaSimbolos.containsKey(identificador);
	}
	
	private void pushTipo(TipoAS tipo) {
		pilhaControleTipos.push(tipo);
	}
	
	private TipoAS popTipo() {
		return pilhaControleTipos.pop();
	}
	
	private void inserir(String identificador, SimboloAS simbolo) throws AnalisadorSemanticoException {
		if (contem(identificador))
			throw new AnalisadorSemanticoException("Identificador '" + identificador + "' já foi declarado!");
		
		tabelaSimbolos.put(identificador, simbolo);
	}

}
