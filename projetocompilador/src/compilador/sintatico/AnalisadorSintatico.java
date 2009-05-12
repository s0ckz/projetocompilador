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
		eh_const();
		if (tipo()) {
			eh_vetor();
			valor_inicial();
			dec_resto();
			return true;
		}
		return false;
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

	private void valores() throws AnalisadorLexicoException {
		var_exp();
		mais_valores();
	}

	private void mais_valores() throws AnalisadorLexicoException {
		if (optionalSymbol(",")) {
			var_exp();
			mais_valores();
		}
	}

	private void var_exp() {
		// TODO Auto-generated method stub
		
	}

	private boolean cadeia() {
		// TODO Auto-generated method stub
		return false;
	}

	private void eh_const() throws AnalisadorLexicoException {
		optionalSymbol("const");
	}

	private void dec_resto() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol(",")) {
			requiredIdentificador();
			dec_resto();
		}
	}

	private boolean tipo() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		return optionalSymbol("int") || optionalSymbol("string");
	}

	private void eh_vetor() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (optionalSymbol("[")) {
			requiredNumero();
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

	private void bloco() {
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


	private void requiredSymbol(String required)
			throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (simbolo != null && simbolo.getCadeia().equals(required)) {
			lerProximoSimbolo();
		} else {
			lancarExcecaoEsperada(required);
		}
	}
	
	private void lancarExcecaoEsperada(String symbol) throws AnalisadorSintaticoException {
		throw new AnalisadorSintaticoException("Esperava: '" + symbol + "'!");
	}

	private void lancarExcecaoNaoEsperada(String symbol) throws AnalisadorSintaticoException {
		throw new AnalisadorSintaticoException("Não esperava: '" + symbol + "'!");
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
