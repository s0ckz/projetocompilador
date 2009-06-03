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
			eh_vetor();
			valor_inicial();
			dec_resto();
			return true;
		}
		return false;
	}

	private void dec_const() throws AnalisadorSintaticoException {
		requiredTipo();
		eh_vetor();
		valor_inicial_const();
		dec_resto_const();
	}

	private boolean dec_resto_const() throws AnalisadorSintaticoException {
		if (optionalSymbol(",")) {
			valor_inicial_const();
			dec_resto_const();
			return true;
		}
		return false;
	}

	private void valor_inicial_const() throws AnalisadorSintaticoException {
		requiredIdentificador();
		requiredSymbol("=");
		valor();
	}

	private void valor_inicial() throws AnalisadorSintaticoException {
		requiredIdentificador();
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

	private void dec_resto() throws AnalisadorSintaticoException {
		if (optionalSymbol(",")) {
			valor_inicial();
			dec_resto();
		}
	}

	private void requiredTipo() throws AnalisadorSintaticoException {
		if (!tipo())
			lancarExcecaoEsperada("TIPO");
	}

	private boolean tipo() throws AnalisadorSintaticoException {
		return optionalSymbol("int") || optionalSymbol("string");
	}

	private void eh_vetor() throws AnalisadorSintaticoException {
		if (optionalSymbol("[")) {
			expressao();
			requiredSymbol("]");
		}
	}

	private void subprogramas() throws AnalisadorSintaticoException {
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

	private void tipo_retorno() throws AnalisadorSintaticoException {
		if (!optionalSymbol("void") && !tipo())
			lancarExcecaoEsperada("void, int ou string");
	}
	
	private void expressao() throws AnalisadorSintaticoException {
		termo();
		expressaoLinha();
	}

	private void expressaoLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("+") || optionalSymbol("-")) {
			expressao();
		}
	}

	private void termo() throws AnalisadorSintaticoException {
		fator();
		termoLinha();
	}

	private void termoLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("*") || optionalSymbol("/")) {
			termo();
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
		if (!numero() && !expressaoParentisada() && !escalar())
			lancarExcecaoEsperada("numero, (expressão) ou identificador");
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
			eh_vetor();
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
			if (optionalSymbol("(")) {
				requiredSymbol(")");
			} else {
				eh_vetor();
				requiredSymbol("=");
				var_exp();
			}
			requiredSymbol(";");
			return true;
		}
		return false;
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
		try {
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
