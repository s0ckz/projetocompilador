package compilador.sintatico;

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

	private Simbolo simbolo;

	private Simbolo simboloAnterior;

	public AnalisadorSintatico(AnalisadorLexico lexico) {
		this.lexico = lexico;
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
		System.out.println("E");
		termo();
		expressaoLinha();
	}

	private void expressaoLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("+") || optionalSymbol("-")) {
			System.out.println("E'");
			expressao();
			try {
				semantico.asVerificarTipo();
			} catch (AnalisadorSemanticoException e) {
				tratarExcecaoSemantico(e);
			}
		}
	}

	private void termo() throws AnalisadorSintaticoException {
		System.out.println("T");
		fator();
		termoLinha();
	}

	private void termoLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("*") || optionalSymbol("/")) {
			System.out.println("T'");
			termo();
			try {
				semantico.asVerificarTipo();
			} catch (AnalisadorSemanticoException e) {
				tratarExcecaoSemantico(e);
			}
		}
	}

	private void fator() throws AnalisadorSintaticoException {
		System.out.println("F");
		pred();
		fatorLinha();
	}

	private void fatorLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("**")) {
			System.out.println("F'");
			fator();
		}
	}

	private void pred() throws AnalisadorSintaticoException {
		System.out.println("Pred");
		if (numero()) {
			semantico.asEmpilharTipoNumero();
		} else if (expressaoParentisada()) {
			// nao precisa fazer nada.
		} else if (escalar()) {
			// nao precisa fazer nada.
		} else {
			trataErro(TabelaPrimeirosESeguidores.getPrimeiros("pred"), 
					  TabelaPrimeirosESeguidores.getSeguidores("pred"),
					  "Esperava: numero, (expressão) ou identificador");
			pred();
//			lancarExcecaoEsperada("numero, (expressão) ou identificador");
		}
	}

	private void trataErro(List<Integer> primeiros, List<Integer> seguidores, String msgErro) throws AnalisadorSintaticoException {
		if (!primeiros.contains(simbolo.getCodigo())) {
			TratadorDeErros.getInstance().addMensagemDeErro(msgErro);
			while (!primeiros.contains(simbolo.getCodigo()) && !seguidores.contains(simbolo.getCodigo())) {
				lerProximoSimbolo();
			}
		}
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
		throw new AnalisadorSintaticoException(lexico.getLinhaAtual(), e.getMessage());
	}
	
	private void lancarExcecaoEsperada(String symbol) throws AnalisadorSintaticoException {
		throw new AnalisadorSintaticoException(lexico.getLinhaAtual(), "Esperava: '" + symbol + "'!");
	}

	private void lancarExcecaoNaoEsperada(String symbol) throws AnalisadorSintaticoException {
		throw new AnalisadorSintaticoException(lexico.getLinhaAtual(), "Não esperava: '" + symbol + "'!");
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
		System.out.println(simbolo);
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
