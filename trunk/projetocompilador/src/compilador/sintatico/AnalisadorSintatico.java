package compilador.sintatico;

import java.util.List;

import compilador.lexico.AnalisadorLexico;
import compilador.lexico.AnalisadorLexicoException;
import compilador.semantico.AnalisadorSemantico;
import compilador.tratamentoDeErros.ListaDeErros;
import compilador.tratamentoDeErros.TabelaPrimeirosESeguidores;
import compilador.util.Simbolo;

public class AnalisadorSintatico {

	private AnalisadorLexico lexico;
	
	private AnalisadorSemantico semantico;
	
	private Simbolo simbolo;

	private Simbolo simboloAnterior;

	public AnalisadorSintatico(AnalisadorLexico lexico, AnalisadorSemantico semantico) {
		this.lexico = lexico;
		this.semantico = semantico;
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
			tratarSimboloRequerido(";", "declaracoes-;");
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
		semantico.asDeclararXptoConstante(simboloAnterior);
		requiredSymbol("=");
		valor();
	}

	private void valor_inicial() throws AnalisadorSintaticoException {
		tratarIdentificadorRequerido("subprogramas-identificador");
		semantico.asDeclararXptoVariavel(simboloAnterior);
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
			tratarSimboloRequerido("void", "subprogramas-void");
			tratarIdentificadorRequerido("subprogramas-identificador");
			semantico.asDeclararProcedimento(simboloAnterior);
			tratarSimboloRequerido("(", "subprogramas-(");
			tratarSimboloRequerido(")", "subprogramas-)");
			tratarSimboloRequerido("{", "subprogramas-{");
			bloco();
			tratarSimboloRequerido("}", "subprogramas-}");
			
			subprogramas();
		}
	}

	private void tratarIdentificadorRequerido(String regra)
			throws AnalisadorSintaticoException {
		tratarErro(regra, getStringEsperado("IDENTIFICADOR"));
		identificador();
	}

	private void expressao() throws AnalisadorSintaticoException {
		termo();
		expressaoLinha();
	}

	private void expressaoLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("+") || optionalSymbol("-")) {
			expressao();
			semantico.asVerificarTipo();
		}
	}

	private void termo() throws AnalisadorSintaticoException {
		fator();
		termoLinha();
	}

	private void termoLinha() throws AnalisadorSintaticoException {
		if (optionalSymbol("*") || optionalSymbol("/")) {
			termo();
			semantico.asVerificarTipo();
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
		if (tratarErro("pred", getStringEsperado("numero, (expressão) ou identificador"))) {
			if (numero()) {
				semantico.asEmpilharTipoNumero();
			} else if (expressaoParentisada()) {
				// nao precisa fazer nada.
			} else if (escalar()) {
				// nao precisa fazer nada.
			}
		} else {
			semantico.asEmpilharNulo();
		}
	}

	// retorna um booleano pq se contiver o primeiro, eu posso ignorar o que veio
	// antes e tentar de novo a mesma regra: fiz assim, mas achei estranho...
	private boolean tratarErro(String regra, String msgErro) {
		List<Integer> primeiros = TabelaPrimeirosESeguidores.getPrimeiros(regra);
		List<Integer> seguidores = TabelaPrimeirosESeguidores.getSeguidores(regra);
		if (simbolo == null || !primeiros.contains(simbolo.getCodigo())) {
			ListaDeErros.getInstance().addMensagemDeErro(msgErro);
			while (simbolo != null && !primeiros.contains(simbolo.getCodigo()) && !seguidores.contains(simbolo.getCodigo())) {
				lerProximoSimbolo();
			}
			return simbolo != null && primeiros.contains(simbolo.getCodigo());
		}
		return simbolo != null;
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
			semantico.asEmpilharTipoBaseadoEmIdentificador(simboloAnterior, eh_vetor());
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
			semantico.asVerificarTipo();
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

	private boolean comando() {
		try {
			if (optionalSymbol("if")) {
				return comandoIf();
			} else if (optionalSymbol("while")) {
				return comandoWhile();
			} else if (optionalSymbol("read")) {
				return comandoRead();
			} else if (optionalSymbol("write")) {
				return comandoWrite();
			} else if (identificador()) {
				return procedimentoOuAtribuicao();
			}
		} catch (AnalisadorSintaticoException e) {
//			if (tratarErro("comando", e.getMessage()))
//				return comando();
//			else
//				return false;
		}
		
		return false;
	}

	private boolean procedimentoOuAtribuicao()
			throws AnalisadorSintaticoException {
		Simbolo identificador = simboloAnterior;
		if (optionalSymbol("(")) {
			chamadaProcedimento(identificador);
		} else {
			atribuicao();
		}
		requiredSymbol(";");
		return true;
	}

	private boolean comandoWrite() throws AnalisadorSintaticoException {
		valor();
		tratarSimboloRequerido(";", "comando-;");
		return true;
	}

	private boolean comandoRead() throws AnalisadorSintaticoException {
		escalar();
		tratarSimboloRequerido(";", "comando-;");
		return true;
	}

	private boolean comandoWhile() throws AnalisadorSintaticoException {
		requiredSymbol("(");
		expressaoLogica();
		requiredSymbol(")");
		requiredSymbol("{");
		bloco();
		requiredSymbol("}");
		optionalSymbol(";");
		return true;
	}

	private boolean comandoIf() throws AnalisadorSintaticoException {
		requiredSymbol("(");
		expressaoLogica();
		requiredSymbol(")");
		requiredSymbol("{");
		bloco();
		requiredSymbol("}");
		cmd_decisao_else();
		optionalSymbol(";");
		return true;
	}

	private void chamadaProcedimento(Simbolo identificador)
			throws AnalisadorSintaticoException {
		requiredSymbol(")");
		semantico.asVerificarExistenciaProcedimento(identificador);
	}

	private void atribuicao() throws AnalisadorSintaticoException {
		semantico.asVerificarSeEhTipoConstante(simboloAnterior);
		semantico.asEmpilharTipoBaseadoEmIdentificador(simboloAnterior, eh_vetor());
		requiredSymbol("=");
		if (cadeia()) {
			semantico.asEmpilharTipoCadeia();
		} else {
			expressao();
		}
		semantico.asVerificarTipo();
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



	private boolean tratarSimboloRequerido(String symbol, String regra) {
		if (tratarErro(regra, getStringEsperado(symbol))) {
			lerProximoSimbolo();
			return true;
		}
		return false;
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
		throw new AnalisadorSintaticoException(getStringEsperado(symbol));
	}

	private String getStringEsperado(String symbol) {
		return "Esperava: '" + symbol + "'!";
	}

	private String getStringNaoEsperado(String symbol) {
		return "Não esperava: '" + symbol + "'!";
	}

	private boolean optionalSymbol(String optional) {
		if (simbolo == null)
			return false;
		
		if (simbolo.getCadeia().equals(optional)) {
			lerProximoSimbolo();
			return true;
		}
		return false;
	}

	private void lerProximoSimbolo() {
		try {
			simboloAnterior = simbolo;
			simbolo = lexico.proximoSimbolo();
		} catch (AnalisadorLexicoException e) {
			ListaDeErros.getInstance().addMensagemDeErro(e.getMessage());
		}
	}

	private void checarFinal() throws AnalisadorSintaticoException {
		if (simbolo != null) {
			ListaDeErros.getInstance().addMensagemDeErro(getStringNaoEsperado(simbolo.getCadeia()));
		}
	}

}
