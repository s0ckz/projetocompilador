package compilador.sintatico;

import static compilador.sintatico.AnalisadorSintaticoException.getMensagemErro;

import java.util.List;

import compilador.lexico.AnalisadorLexico;
import compilador.lexico.AnalisadorLexicoException;
import compilador.semantico.AnalisadorSemantico;
import compilador.semantico.AnalisadorSemanticoException;
import compilador.tratamentoDeErros.TabelaPrimeirosESeguidores;
import compilador.tratamentoDeErros.TratadorDeErros;
import compilador.util.Simbolo;

public class AnalisadorSintatico {

	private AnalisadorLexico lexico;
	
	private AnalisadorSemantico semantico = new AnalisadorSemantico();
	
	private TratadorDeErros tratadorDeErros = new TratadorDeErros();

	private Simbolo simbolo;

	private Simbolo simboloAnterior;

	public AnalisadorSintatico(AnalisadorLexico lexico) {
		this.lexico = lexico;
	}
	
	public String getProximoErro() {
		return tratadorDeErros.popErro();
	}

	public void analyse() throws AnalisadorSintaticoException {
		lerProximoSimbolo();
		programa();
	}

	private void programa() throws AnalisadorSintaticoException {
		declaracoes();
		subprogramas();
		bloco();
		checarFinal();
	}

	private void declaracoes() throws AnalisadorSintaticoException {
		if (declaracao()) {
			requiredSymbol(";");
			declaracoes();
		}
	}

	private boolean declaracao() throws AnalisadorSintaticoException {
		if (optionalSymbol("const")) {
			dec_const();
			return true;
		}
		return dec_normal();
	}

	private boolean dec_normal() throws AnalisadorSintaticoException {
		if (tipo()) {
			Simbolo tipo = simboloAnterior;
			boolean ehVetor = eh_vetor();
			semantico.asEmpilharTipo(tipo, ehVetor);
			valor_inicial();
			dec_resto(tipo, ehVetor);
			return true;
		}
		return false;
	}

	private void dec_const() throws AnalisadorSintaticoException {
		requiredTipo();
		Simbolo tipo = simboloAnterior;
		boolean ehVetor = eh_vetor();
		semantico.asEmpilharTipo(tipo, ehVetor);
		valor_inicial_const();
		dec_resto_const(tipo, ehVetor);
	}

	private boolean dec_resto_const(Simbolo tipo, boolean ehVetor) throws AnalisadorSintaticoException {
		if (optionalSymbol(",")) {
			semantico.asEmpilharTipo(tipo, ehVetor);
			valor_inicial_const();
			dec_resto_const(tipo, ehVetor);
			return true;
		}
		return false;
	}

	private void valor_inicial_const() throws AnalisadorSintaticoException {
		requiredIdentificador();
		
		try {
			semantico.asDeclararXptoConstante(simboloAnterior);
		} catch (AnalisadorSemanticoException e) {
			tratarExcecaoSemantico(e);
		}
		
		requiredSymbol("=");
		valor();
	}

	private void valor_inicial() throws AnalisadorSintaticoException {
		requiredIdentificador();
		
		try {
			semantico.asDeclararXptoVariavel(simboloAnterior);
		} catch (AnalisadorSemanticoException e) {
			tratarExcecaoSemantico(e);
		}
		
		valor_aux();
	}

	private void valor_aux() throws AnalisadorSintaticoException {
		if (optionalSymbol("=")) {
			valor();
		}
	}

	private void valor() throws AnalisadorSintaticoException {
		if (cadeia()) {
		} else if (vetor()) {
		} else {
			expressao();
		}
	}

	private boolean vetor() throws AnalisadorSintaticoException {
		if (optionalSymbol("{")) {
			valores();
			requiredSymbol("}");
			return true;
		}
		return false;
	}

	private void valores() throws AnalisadorSintaticoException {
		var_exp();
		mais_valores();
	}

	private void mais_valores() throws AnalisadorSintaticoException {
		if (optionalSymbol(",")) {
			var_exp();
			mais_valores();
		}
	}

	private void var_exp() throws AnalisadorSintaticoException {
		if (!cadeia()) 
			expressao();
		
	}

	private void dec_resto(Simbolo tipo, boolean ehVetor) throws AnalisadorSintaticoException {
		if (optionalSymbol(",")) {
			semantico.asEmpilharTipo(tipo, ehVetor);
			valor_inicial();
			dec_resto(tipo, ehVetor);
		}
	}

	private void requiredTipo() throws AnalisadorSintaticoException {
		if (!tipo())
			lancarExcecaoEsperada("TIPO");
	}

	private boolean tipo() throws AnalisadorSintaticoException {
		return optionalSymbol("int") || optionalSymbol("string");
	}

	private boolean eh_vetor() throws AnalisadorSintaticoException {
		if (optionalSymbol("[")) {
			expressao();
			requiredSymbol("]");
			return true;
		}
		return false;
	}

	private void subprogramas() throws AnalisadorSintaticoException {
		if (optionalSymbol("def")) {
			requiredSymbol("void");
			requiredIdentificador();
			
			try {
				semantico.asDeclararProcedimento(simboloAnterior);
			} catch (AnalisadorSemanticoException e) {
				tratarExcecaoSemantico(e);
			}
			
			requiredSymbol("(");
			requiredSymbol(")");
			requiredSymbol("{");
			bloco();
			requiredSymbol("}");
			subprogramas();
		}
	}

	private void expressao() throws AnalisadorSintaticoException {
		termo();
		expressaoLinha();
	}

	private void expressaoLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("+") || optionalSymbol("-")) {
			expressao();
			try {
				semantico.asVerificarTipo();
			} catch (AnalisadorSemanticoException e) {
				tratarExcecaoSemantico(e);
			}
		}
	}

	private void termo() throws AnalisadorSintaticoException {
		fator();
		termoLinha();
	}

	private void termoLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("*") || optionalSymbol("/")) {
			termo();
			try {
				semantico.asVerificarTipo();
			} catch (AnalisadorSemanticoException e) {
				tratarExcecaoSemantico(e);
			}
		}
	}

	private void fator() throws AnalisadorSintaticoException {
		pred();
		fatorLinha();
	}

	private void fatorLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("**")) {
			fator();
		}
	}

	private void pred() throws AnalisadorSintaticoException {
		if (numero()) {
			semantico.asEmpilharTipoNumero();
		} else if (expressaoParentisada()) {
			// nao precisa fazer nada.
		} else if (escalar()) {
			// nao precisa fazer nada.
		} else if (tratarErro("pred", "Esperava: numero, (expressão) ou identificador")) {
			pred();
		} else {
			semantico.asEmpilharNulo(); // para o semantico saber quando deve empilhar um elemento de novo.
		}
	}

	// retorna um booleano pq se contiver o primeiro, eu posso ignorar o que veio
	// antes e tentar de novo a mesma regra: fiz assim, mas achei estranho...
	private boolean tratarErro(String regra, String msgErro) throws AnalisadorSintaticoException {
		List<Integer> primeiros = TabelaPrimeirosESeguidores.getPrimeiros(regra);
		List<Integer> seguidores = TabelaPrimeirosESeguidores.getSeguidores(regra);
		if (!primeiros.contains(simbolo.getCodigo())) {
			tratadorDeErros.addMensagemDeErro(getMensagemErro(lexico.getLinhaAtual(), lexico.getConteudoLinhaAtual(), msgErro));
			while (!primeiros.contains(simbolo.getCodigo()) && !seguidores.contains(simbolo.getCodigo())) {
				lerProximoSimbolo();
			}
			return primeiros.contains(simbolo.getCodigo());
		}
		// acho que aqui lanca excecao de lascou foi tudo, pq nao conseguiu recuperar o erro.
		return false;
	}

	private boolean expressaoParentisada() throws AnalisadorSintaticoException {
		if (optionalSymbol("(")) {
			expressao();
			requiredSymbol(")");
			return true;
		}
		return false;
	}
	
	private boolean escalar() throws AnalisadorSintaticoException {
		if (identificador()) {
			try {
				semantico.asEmpilharTipoBaseadoEmIdentificador(simboloAnterior, eh_vetor());
			} catch (AnalisadorSemanticoException e) {
				tratarExcecaoSemantico(e);
			}
			return true;
		}
		return false;
	}
	
	private void expressaoLogica() throws AnalisadorSintaticoException {
		termoRelacional();
		expressaoLogicaLinha();
	}

	private void expressaoLogicaLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("||")) {
			termoRelacional();
			expressaoLogica();
		}
	}

	private void termoRelacional() throws AnalisadorSintaticoException {
		fatorRelacional();
		termoRelacionalLinha();
	}

	private void termoRelacionalLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("&&")) {
			fatorRelacional();
			termoRelacionalLinha();
		}
	}

	private void fatorRelacional() throws AnalisadorSintaticoException {
		if (!expressaoLogicaParentisada()) {
			expressao();
			requiredOperadorRelacional();
			expressao();
			try {
				semantico.asVerificarTipo();
			} catch (AnalisadorSemanticoException e) {
				tratarExcecaoSemantico(e);
			}
		}
	}

	private boolean expressaoLogicaParentisada() throws AnalisadorSintaticoException {
		if (optionalSymbol("[")) {
			expressaoLogica();
			requiredSymbol("]");
			return true;
		}
		return false;
	}

	private boolean operadorRelacional() throws AnalisadorSintaticoException {
		if (simbolo == null || !simbolo.isOperadorRelacional()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private void bloco() throws AnalisadorSintaticoException {
		if (comando())
			bloco();
	}

	private boolean comando() throws AnalisadorSintaticoException {
		if (optionalSymbol("if")) {
			requiredSymbol("(");
			expressaoLogica();
			requiredSymbol(")");
			requiredSymbol("{");
			bloco();
			requiredSymbol("}");
			cmd_decisao_else();
			optionalSymbol(";");
			return true;
		} else if (optionalSymbol("while")) {
			requiredSymbol("(");
			expressaoLogica();
			requiredSymbol(")");
			requiredSymbol("{");
			bloco();
			requiredSymbol("}");
			optionalSymbol(";");
			return true;
		} else if (optionalSymbol("read")) {
			escalar();
			requiredSymbol(";");
			return true;
		} else if (optionalSymbol("write")) {
			valor();
			requiredSymbol(";");
			return true;
		} else if (identificador()) {
			Simbolo identificador = simboloAnterior;
			if (optionalSymbol("(")) {
				chamadaProcedimento(identificador);
			} else {
				atribuicao();
			}
			requiredSymbol(";");
			return true;
		}
		return false;
	}

	private void chamadaProcedimento(Simbolo identificador)
			throws AnalisadorSintaticoException {
		requiredSymbol(")");
		try {
			semantico.asVerificarExistenciaProcedimento(identificador);
		} catch (AnalisadorSemanticoException e) {
			tratarExcecaoSemantico(e);
		}
	}

	private void atribuicao() throws AnalisadorSintaticoException {
		try {
			semantico.asVerificarSeEhTipoConstante(simboloAnterior);
			semantico.asEmpilharTipoBaseadoEmIdentificador(simboloAnterior, eh_vetor());
			requiredSymbol("=");
			if (cadeia()) {
				semantico.asEmpilharTipoCadeia();
			} else {
				expressao();
			}
			semantico.asVerificarTipo();
		} catch (AnalisadorSemanticoException e) {
			tratarExcecaoSemantico(e);
		}
	}

	private void cmd_decisao_else() throws AnalisadorSintaticoException {
		if (optionalSymbol("else")) {
			requiredSymbol("{");
			bloco();
			requiredSymbol("}");
		}
	}

	private boolean cadeia() throws AnalisadorSintaticoException {
		if (simbolo == null || !simbolo.isCadeia()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private boolean identificador() throws AnalisadorSintaticoException {
		if (simbolo == null || !simbolo.isIdentificador()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private boolean numero() throws AnalisadorSintaticoException {
		if (simbolo == null || !simbolo.isNumero()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private void requiredIdentificador() throws AnalisadorSintaticoException {
		if (!identificador()) 
			lancarExcecaoEsperada("IDENTIFICADOR");
	}

//	private void requiredNumero() throws AnalisadorSintaticoException, AnalisadorLexicoException {
//		if (!numero()) lancarExcecaoEsperada("NUMERO");
//	}

	private void requiredOperadorRelacional() throws AnalisadorSintaticoException {
		if (!operadorRelacional()) lancarExcecaoEsperada("OPERADOR RELACIONAL");
	}


	private void requiredSymbol(String required)
			throws AnalisadorSintaticoException {
		if (simbolo != null && simbolo.getCadeia().equals(required)) {
			lerProximoSimbolo();
		} else {
			lancarExcecaoEsperada(required);
		}
	}

	private void tratarExcecaoSemantico(AnalisadorSemanticoException e) throws AnalisadorSintaticoException {
		throw new AnalisadorSintaticoException(lexico.getLinhaAtual(), lexico.getConteudoLinhaAtual(), e.getMessage());
	}
	
	private void lancarExcecaoEsperada(String symbol) throws AnalisadorSintaticoException {
		throw new AnalisadorSintaticoException(lexico.getLinhaAtual(), lexico.getConteudoLinhaAtual(), "Esperava: '" + symbol + "'!");
	}

	private void lancarExcecaoNaoEsperada(String symbol) throws AnalisadorSintaticoException {
		throw new AnalisadorSintaticoException(lexico.getLinhaAtual(), lexico.getConteudoLinhaAtual(), "Não esperava: '" + symbol + "'!");
	}
	
	private boolean optionalSymbol(String optional) throws AnalisadorSintaticoException {
		if (simbolo == null)
			return false;
		
		if (simbolo.getCadeia().equals(optional)) {
			lerProximoSimbolo();
			return true;
		}
		return false;
	}

	private void lerProximoSimbolo() throws AnalisadorSintaticoException {
		try {
			simboloAnterior = simbolo;
			simbolo = lexico.proximoSimbolo();
		} catch (AnalisadorLexicoException e) {
			lancarExcecaoEsperada(e.getMessage());
		}
	}

	private void checarFinal() throws AnalisadorSintaticoException {
		if (simbolo != null) 
			lancarExcecaoNaoEsperada(simbolo.getCadeia());
	}


}
