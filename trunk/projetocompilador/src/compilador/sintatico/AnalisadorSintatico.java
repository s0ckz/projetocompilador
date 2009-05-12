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

	private void checarFinal() throws AnalisadorSintaticoException {
		if (simbolo != null)
			throw new AnalisadorSintaticoException("'" + simbolo.getCadeia() + "' não esperado!");
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
			identificador();
			dec_resto();
			return true;
		}
		return false;
	}

	private void eh_const() throws AnalisadorLexicoException {
		optionalSymbol("const");
	}

	private void dec_resto() throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (optionalSymbol(",")) {
			identificador();
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

	private void identificador() throws AnalisadorSintaticoException, AnalisadorLexicoException {
		if (!simbolo.isIdentificador())
			throw new AnalisadorSintaticoException("Esperava: 'IDENTIFICADOR'");
		lerProximoSimbolo();
	}

	private void numero() {
		// TODO Auto-generated method stub

	}

	private void requiredSymbol(String required)
			throws AnalisadorLexicoException, AnalisadorSintaticoException {
		if (simbolo != null && simbolo.getCadeia().equals(required)) {
			lerProximoSimbolo();
		} else {
			throw new AnalisadorSintaticoException("Esperava: '" + required
					+ "'");
		}
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

}
