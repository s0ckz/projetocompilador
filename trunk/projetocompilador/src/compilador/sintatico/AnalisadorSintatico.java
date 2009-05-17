package compilador.sintatico;

import compilador.lexico.AnalisadorLexico;
import compilador.lexico.AnalisadorLexicoException;
import compilador.util.Simbolo;

public class AnalisadorSintatico {

	private AnalisadorLexico lexico;

	private Simbolo simbolo;

	public AnalisadorSintatico(AnalisadorLexico lexico) {
		this.lexico = lexico;
	}

	public void analyse() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		lerProximoSimbolo();
		programa();
	}

	private void programa() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		declaracoes();
		subprogramas();
		bloco();
		checarFinal();
	}

	private void declaracoes() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (declaracao()) {
			requiredSymbol(";");
			declaracoes();
		}
	}

	private boolean declaracao() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (optionalSymbol("const")) {
			dec_const();
			return true;
		}
		return dec_normal();
	}

	private boolean dec_normal() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (tipo()) {
			eh_vetor();
			valor_inicial();
			dec_resto();
			return true;
		}
		return false;
	}

	private void dec_const() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		requiredTipo();
		eh_vetor();
		valor_inicial_const();
		dec_resto_const();
	}

	private boolean dec_resto_const() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol(",")) {
			valor_inicial_const();
			dec_resto_const();
			return true;
		}
		return false;
	}

	private void valor_inicial_const() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		requiredIdentificador();
		requiredSymbol("=");
		valor();
	}

	private void valor_inicial() throws AnalisadorSintaticoException, AnalisadorLexicoException {
		requiredIdentificador();
		valor_aux();
	}

	private void valor_aux() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol("=")) {
			valor();
		}
	}

	private void valor() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (cadeia()) {
		} else if (vetor()) {
		} else {
			expressao();
		}
	}

	private boolean vetor() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol("{")) {
			valores();
			requiredSymbol("}");
			return true;
		}
		return false;
	}

	private void valores() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		var_exp();
		mais_valores();
	}

	private void mais_valores() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol(",")) {
			var_exp();
			mais_valores();
		}
	}

	private void var_exp() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (!cadeia()) 
			expressao();
		
	}

	private void dec_resto() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol(",")) {
			requiredIdentificador();
			dec_resto();
		}
	}

	private void requiredTipo() throws AnalisadorSintaticoException, AnalisadorLexicoException {
		if (!tipo())
			lancarExcecaoEsperada("TIPO");
	}

	private boolean tipo() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		return optionalSymbol("int") || optionalSymbol("string");
	}

	private void eh_vetor() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (optionalSymbol("[")) {
			expressao();
			requiredSymbol("]");
		}
	}

	private void subprogramas() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (optionalSymbol("def")) {
			tipo_retorno();
			requiredIdentificador();
			requiredSymbol("(");
			requiredSymbol(")");
			requiredSymbol("{");
			bloco();
			requiredSymbol("}");
			subprogramas();
		}
	}

	private void tipo_retorno() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (!optionalSymbol("void") && !tipo())
			lancarExcecaoEsperada("void, int ou string");
	}
	
	private void expressao() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		termo();
		expressaoLinha();
	}

	private void expressaoLinha() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol("+") || optionalSymbol("-")) {
			expressao();
		}
	}

	private void termo() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		fator();
		termoLinha();
	}

	private void termoLinha() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol("*") || optionalSymbol("/")) {
			termo();
		}
	}

	private void fator() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		pred();
		fatorLinha();
	}

	private void fatorLinha() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol("**")) {
			fator();
		}
	}

	private void pred() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (!numero() && !expressaoParentisada() && !escalar())
			lancarExcecaoEsperada("numero, (expressão) ou identificador");
	}

	private boolean expressaoParentisada() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol("(")) {
			expressao();
			requiredSymbol(")");
			return true;
		}
		return false;
	}
	
	private boolean escalar() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (identificador()) {
			eh_vetor();
			return true;
		}
		return false;
	}
	
	private void expressaoLogica() throws AnalisadorSintaticoException, AnalisadorLexicoException {
		termoRelacional();
		expressaoLogicaLinha();
	}

	private void expressaoLogicaLinha() throws AnalisadorSintaticoException, AnalisadorLexicoException {
		if (optionalSymbol("||")) {
			termoRelacional();
			expressaoLogica();
		}
	}

	private void termoRelacional() throws AnalisadorSintaticoException, AnalisadorLexicoException {
		fatorRelacional();
		termoRelacionalLinha();
	}

	private void termoRelacionalLinha() throws AnalisadorSintaticoException, AnalisadorLexicoException {
		if (optionalSymbol("&&")) {
			fatorRelacional();
			termoRelacionalLinha();
		}
	}

	private void fatorRelacional() throws AnalisadorSintaticoException, AnalisadorLexicoException {
		if (!expressaoLogicaParentisada()) {
			expressao();
			requiredOperadorRelacional();
			expressao();
		}
	}

	private boolean expressaoLogicaParentisada() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol("[")) {
			expressaoLogica();
			requiredSymbol("]");
			return true;
		}
		return false;
	}

	private boolean operadorRelacional() throws AnalisadorLexicoException {
		if (simbolo == null || !simbolo.isOperadorRelacional()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private void bloco() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		comando();
		outros_cmdos();
	}
	
	private void outros_cmdos() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol(";")) {
			comando();
			outros_cmdos();
		}
	}

	private void comando() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol("if")) {
			requiredSymbol("(");
			expressaoLogica();
			requiredSymbol(")");
			requiredSymbol("{");
			bloco();
			requiredSymbol("}");
			cmd_decisao_else();
		} else if (optionalSymbol("while")) {
			requiredSymbol("(");
			expressaoLogica();
			requiredSymbol(")");
			requiredSymbol("{");
			bloco();
			requiredSymbol("}");
		} else if (optionalSymbol("read")) {
			escalar();
		} else if (optionalSymbol("write")) {
			expressao();
		} else if (escalar()) {
			requiredSymbol("=");
			var_exp();
		}
	}

	private void cmd_decisao_else() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol("else")) {
			requiredSymbol("{");
			bloco();
			requiredSymbol("}");
		}
	}

	private boolean cadeia() throws AnalisadorLexicoException {
		if (simbolo == null || !simbolo.isCadeia()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private boolean identificador() throws AnalisadorLexicoException {
		if (simbolo == null || !simbolo.isIdentificador()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private boolean numero() throws AnalisadorLexicoException {
		if (simbolo == null || !simbolo.isNumero()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private void requiredIdentificador() throws AnalisadorSintaticoException, AnalisadorLexicoException {
		if (!identificador()) lancarExcecaoEsperada("IDENTIFICADOR");
	}

	private void requiredNumero() throws AnalisadorSintaticoException, AnalisadorLexicoException {
		if (!numero()) lancarExcecaoEsperada("NUMERO");
	}

	private void requiredOperadorRelacional() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (!operadorRelacional()) lancarExcecaoEsperada("OPERADOR RELACIONAL");
	}


	private void requiredSymbol(String required)
			throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (simbolo != null && simbolo.getCadeia().equals(required)) {
			lerProximoSimbolo();
		} else {
			lancarExcecaoEsperada(required);
		}
	}
	
	private void lancarExcecaoEsperada(String symbol) throws AnalisadorSintaticoException {
		throw new AnalisadorSintaticoException(lexico.getLinhaAtual(), "Esperava: '" + symbol + "'!");
	}

	private void lancarExcecaoNaoEsperada(String symbol) throws AnalisadorSintaticoException {
		throw new AnalisadorSintaticoException(lexico.getLinhaAtual(), "Não esperava: '" + symbol + "'!");
	}
	
	private boolean optionalSymbol(String optional) throws AnalisadorLexicoException {
		if (simbolo == null)
			return false;
		
		if (simbolo.getCadeia().equals(optional)) {
			lerProximoSimbolo();
			return true;
		}
		return false;
	}

	private void lerProximoSimbolo() throws AnalisadorLexicoException {
		simbolo = lexico.proximoSimbolo();
	}

	private void checarFinal() throws AnalisadorSintaticoException {
		if (simbolo != null) 
			lancarExcecaoNaoEsperada(simbolo.getCadeia());
	}


}
