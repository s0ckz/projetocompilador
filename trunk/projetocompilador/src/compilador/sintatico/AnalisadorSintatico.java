package compilador.sintatico;

import compilador.lexico.AnalisadorLexico;
import compilador.lexico.AnalisadorLexicoException;
import compilador.util.Simbolo;

public class AnalisadorSintatico {

	private AnalisadorLexico lexico;

	private Simbolo simbolo;

	public AnalisadorSintatico() {
		lexico = new AnalisadorLexico();
	}

	private void programa() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		declaracoes();
		subprogramas();
		bloco();
	}

	private void declaracoes() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (declaracao())
			mais_dec();
	}

	private void mais_dec() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (optionalSymbol(";")) {
			declaracao();
			mais_dec();
		}
	}

	private boolean declaracao() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		eh_const();
		tipo();
		eh_vetor();
		identificador();
		dec_resto();
		return true;
	}

	private void eh_const() throws AnalisadorLexicoException {
		optionalSymbol("const");
	}

	private void dec_resto() throws AnalisadorLexicoException {
		if (optionalSymbol(",")) {
			identificador();
			dec_resto();
		}
	}

	private void tipo() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (!optionalSymbol("int") && !optionalSymbol("string")) {
			throw new AnalisadorSintaticoException("Tipo invalido");
		}
	}

	private void eh_vetor() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (optionalSymbol("[")) {
			numero();
			requiredSymbol("]");
		}
	}

	private void subprogramas() throws AnalisadorLexicoException,
			AnalisadorSintaticoException {
		if (optionalSymbol("def")) {
			tipo_retorno();
			identificador();
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
		if (!optionalSymbol("void")) tipo();
	}

	private void bloco() {
		// TODO Auto-generated method stub

	}

	private void identificador() {
		// TODO Auto-generated method stub

	}

	private void numero() {
		// TODO Auto-generated method stub

	}

	private void requiredSymbol(String required)
			throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (simbolo.getCadeia().equals(required)) {
			simbolo = lexico.proximoSimbolo();
		} else {
			throw new AnalisadorSintaticoException("Esperava: '" + required
					+ "'");
		}
	}
	
	private boolean optionalSymbol(String optional) throws AnalisadorLexicoException {
		if (simbolo.getCadeia().equals(optional)) {
			simbolo = lexico.proximoSimbolo();
			return true;
		}
		return false;
	}

}
