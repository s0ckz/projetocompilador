package compilador.semantico;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import compilador.tratamentoDeErros.ListaDeErros;
import compilador.util.Simbolo;

public class AnalisadorSemantico {
	
	private Map<String, SimboloAS> tabelaSimbolos = new HashMap<String, SimboloAS>();
	
	private LinkedList<TipoAS> pilhaControleTipos = new LinkedList<TipoAS>();
	
	public void asEmpilharTipo(Simbolo tipo, boolean vetor) {
		pushTipo(new TipoAS(tipo, vetor));
	}

	public void asEmpilharNulo() {
		pushTipo(null);
		
	}

	public void asEmpilharTipoNumero() {
		asEmpilharTipo(Simbolo.createTipoNumero(), false);
	}

	public void asEmpilharTipoCadeia() {
		asEmpilharTipo(Simbolo.createTipoCadeia(), false);
	}

	public void asVerificarSeEhTipoConstante(Simbolo identificador) {
		SimboloXptoAS simboloAS = getSimboloXptoAS(identificador.getCadeia());
		if (simboloAS == null)
			return;
		if (simboloAS.isConstante())
			ListaDeErros.getInstance().addMensagemDeErro("'" + identificador.getCadeia() +"' é constante!");
	}

	public void asEmpilharTipoBaseadoEmIdentificador(Simbolo identificador, boolean ehVetor) {
		SimboloXptoAS simboloAS = getSimboloXptoAS(identificador.getCadeia());
		
		if (simboloAS == null)
			return;
		
		TipoAS tipoAS = simboloAS.getTipo();
		
		if (tipoAS.isVetor())
			asEmpilharTipo(tipoAS.getSimbolo(), !ehVetor);
		else if (!ehVetor)
			asEmpilharTipo(tipoAS.getSimbolo(), false);
		else // tipoAS.isVetor() && ehVetor
			ListaDeErros.getInstance().addMensagemDeErro("'" + identificador.getCadeia() +"' não é um vetor!");
	}
	
	public void asVerificarExistenciaProcedimento(Simbolo identificador) {
		if (!contem(identificador.getCadeia()))
			ListaDeErros.getInstance().addMensagemDeErro("Procedimento '" + identificador.getCadeia() + "' não declarado!");
	}
	
	public void asDeclararXptoConstante(Simbolo simbolo) {
		asDeclararXpto(simbolo, true);
	}
	
	public void asDeclararXptoVariavel(Simbolo simbolo) {
		asDeclararXpto(simbolo, false);
	}
	
	private void asDeclararXpto(Simbolo simbolo, boolean declarandoConstantes) {
		try {
			TipoAS tipo = popTipo();
			String identificador = simbolo.getCadeia();
			SimboloAS simboloAS = new SimboloXptoAS(identificador, tipo, declarandoConstantes);
			inserir(identificador, simboloAS);
		} catch (NoSuchElementException e) {
			ListaDeErros.getInstance().addMensagemDeErro("Erro na pilha!");
		}
	}

	public void asDeclararProcedimento(Simbolo simbolo) {
		inserir(simbolo.getCadeia(), new SimboloProcedimentoAS(simbolo.getCadeia()));;
	}

	public void asVerificarTipo()  {
		TipoAS tipo1 = popTipo();
		if (tipo1 == null)
			return;
		TipoAS tipo2 = popTipo();
		if (tipo2 == null) {
			pushTipo(tipo1);
			return;
		}
		
		if (tipo1.equals(tipo2)) {
			pushTipo(tipo1);
		} else {
			ListaDeErros.getInstance().addMensagemDeErro("Tipos incompatíveis: " + tipo1 + " com " + tipo2 + "!");
		}
	}
	
	private SimboloXptoAS getSimboloXptoAS(String identificador) {
		SimboloAS simboloAS = getSimboloAS(identificador);
		if (simboloAS == null)
			return null;
		
		if (simboloAS.getSimboloASEnum() == SimboloASEnum.CONSTANTE 
				|| simboloAS.getSimboloASEnum() == SimboloASEnum.VARIAVEL)
			return (SimboloXptoAS) simboloAS;
		
		ListaDeErros.getInstance().addMensagemDeErro("Identificador esperado não é " + SimboloASEnum.PROCEDIMENTO + "!");
		return null;
	}

	private SimboloAS getSimboloAS(String identificador) {
		if (!contem(identificador)) {
			ListaDeErros.getInstance().addMensagemDeErro("Identificador '" + identificador + "' não foi declarado!");
			return null;
		}

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
//		System.out.println("desempilhando " + pilhaControleTipos.peek());
		return pilhaControleTipos.pop();
	}
	
	private void inserir(String identificador, SimboloAS simbolo) {
		if (contem(identificador)) {
			ListaDeErros.getInstance().addMensagemDeErro("Identificador '" + identificador + "' já foi declarado!");
			return;
		}

		tabelaSimbolos.put(identificador, simbolo);
	}

}
