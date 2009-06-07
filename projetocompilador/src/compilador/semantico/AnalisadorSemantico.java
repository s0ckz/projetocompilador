package compilador.semantico;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import compilador.util.Simbolo;

public class AnalisadorSemantico {
	
	private Map<String, SimboloAS> tabelaSimbolos = new HashMap<String, SimboloAS>();
	
	private LinkedList<TipoAS> pilhaControleTipos = new LinkedList<TipoAS>();
	
	public void asEmpilharTipo(Simbolo tipo, boolean vetor) {
		pushTipo(new TipoAS(tipo, vetor));
	}

	public void asEmpilharTipoBaseadoEmIdentificador(Simbolo identificador) throws AnalisadorSemanticoException {
		SimboloXptoAS simboloAS = getSimboloXptoAS(identificador.getCadeia());
		pushTipo(simboloAS.getTipo());
	}
	
	public void asDeclararXptoConstante(Simbolo simbolo) throws AnalisadorSemanticoException {
		asDeclararXpto(simbolo, true);
	}
	
	public void asDeclararXptoVariavel(Simbolo simbolo) throws AnalisadorSemanticoException {
		asDeclararXpto(simbolo, false);
	}
	
	private void asDeclararXpto(Simbolo simbolo, boolean declarandoConstantes) throws AnalisadorSemanticoException {
		try {
			TipoAS tipo = popTipo();
			String identificador = simbolo.getCadeia();
			SimboloAS simboloAS = new SimboloXptoAS(identificador, tipo, declarandoConstantes);
			inserir(identificador, simboloAS);
		} catch (NoSuchElementException e) {
			throw new AnalisadorSemanticoException("Fudeu!");
		}
	}
	
	private SimboloXptoAS getSimboloXptoAS(String identificador) throws AnalisadorSemanticoException {
		SimboloAS simboloAS = getSimboloAS(identificador);
		
		if (simboloAS.getSimboloASEnum() == SimboloASEnum.CONSTANTE 
				|| simboloAS.getSimboloASEnum() == SimboloASEnum.VARIAVEL)
			return (SimboloXptoAS) simboloAS;
		
		throw new AnalisadorSemanticoException("Identificador esperado não é " + SimboloASEnum.PROCEDIMENTO + "!");
	}

	private SimboloAS getSimboloAS(String identificador)
			throws AnalisadorSemanticoException {
		if (!contem(identificador))
			throw new AnalisadorSemanticoException("Identificador '" + identificador + "' não foi declarado!");

		SimboloAS simboloAS = tabelaSimbolos.get(identificador);
		return simboloAS;
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
