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

	public void asEmpilharTipoNumero() {
		asEmpilharTipo(Simbolo.createTipoNumero(), false);
	}

	public void asEmpilharTipoCadeia() {
		asEmpilharTipo(Simbolo.createTipoCadeia(), false);
	}

	public void asVerificarSeEhTipoConstante(Simbolo identificador) throws AnalisadorSemanticoException {
		SimboloXptoAS simboloAS = getSimboloXptoAS(identificador.getCadeia());
		if (simboloAS.isConstante())
			throw new AnalisadorSemanticoException("'" + identificador.getCadeia() +"' é constante!");
	}

	public void asEmpilharTipoBaseadoEmIdentificador(Simbolo identificador, boolean ehVetor) throws AnalisadorSemanticoException {
		SimboloXptoAS simboloAS = getSimboloXptoAS(identificador.getCadeia());
		TipoAS tipoAS = simboloAS.getTipo();
		
		if (tipoAS.isVetor())
			asEmpilharTipo(tipoAS.getSimbolo(), !ehVetor);
		else if (!ehVetor)
			asEmpilharTipo(tipoAS.getSimbolo(), false);
		else // tipoAS.isVetor() && ehVetor
			throw new AnalisadorSemanticoException("'" + identificador.getCadeia() +"' não é um vetor!");
	}
	
	public void asVerificarExistenciaProcedimento(Simbolo identificador) throws AnalisadorSemanticoException {
		if (!contem(identificador.getCadeia()))
			throw new AnalisadorSemanticoException("Procedimento '" + identificador.getCadeia() + "' não declarado!");
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
			throw new AnalisadorSemanticoException("Erro na pilha!");
		}
	}

	public void asDeclararProcedimento(Simbolo simbolo) throws AnalisadorSemanticoException {
		inserir(simbolo.getCadeia(), new SimboloProcedimentoAS(simbolo.getCadeia()));;
	}

	public void asVerificarTipo() throws AnalisadorSemanticoException {
		TipoAS tipo1 = popTipo();
		TipoAS tipo2 = popTipo();
		
		if (tipo1.equals(tipo2)) {
			pushTipo(tipo1);
		} else {
			throw new AnalisadorSemanticoException("Tipos incompatíveis: " + tipo1 + " com " + tipo2 + "!");
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
//		System.out.println("empilhando " + tipo);
		pilhaControleTipos.push(tipo);
	}
	
	private TipoAS popTipo() {
//		System.out.println("poping " + pilhaControleTipos.getFirst());
		return pilhaControleTipos.pop();
	}
	
	private void inserir(String identificador, SimboloAS simbolo) throws AnalisadorSemanticoException {
		if (contem(identificador))
			throw new AnalisadorSemanticoException("Identificador '" + identificador + "' já foi declarado!");

		tabelaSimbolos.put(identificador, simbolo);
	}

}
