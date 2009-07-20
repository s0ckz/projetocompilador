package compilador.sintatico;

import java.util.List;

import compilador.geradorDeCodigo.GeradorDeCodigo;
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

	private GeradorDeCodigo geradorDeCodigo;
	
	public AnalisadorSintatico(AnalisadorLexico lexico, AnalisadorSemantico semantico, GeradorDeCodigo geradorDeCodigo) {
		this.lexico = lexico;
		this.semantico = semantico;
		this.geradorDeCodigo = geradorDeCodigo;
	}
	
	public void analyse() {
		lerProximoSimbolo();
		programa();
		this.geradorDeCodigo.finalizar();
	}

	private void programa() {
		declaracoes();
		subprogramas();
		bloco();
		checarFinal();
	}

	private void declaracoes() {
		if (declaracao()) {
			tratarSimboloRequerido(";", "declaracoes-;");
			declaracoes();
		}
	}

	private boolean declaracao() {
		if (optionalSymbol("const")) {
			dec_const();
			return true;
		}
		return dec_normal();
	}

	private boolean dec_normal() {
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

	private void dec_const() {
		tratarTipoRequerido("dec_const");
		Simbolo tipo = simboloAnterior;
		boolean ehVetor = eh_vetor();
		semantico.asEmpilharTipo(tipo, ehVetor);
		valor_inicial_const();
		dec_resto_const(tipo, ehVetor);
	}

	private boolean dec_resto_const(Simbolo tipo, boolean ehVetor) {
		if (optionalSymbol(",")) {
			semantico.asEmpilharTipo(tipo, ehVetor);
			valor_inicial_const();
			dec_resto_const(tipo, ehVetor);
			return true;
		}
		return false;
	}

	private void valor_inicial_const() {
		tratarIdentificadorRequerido("valor_inicial_const");
		semantico.asDeclararXptoConstante(simboloAnterior);
		geradorDeCodigo.resetarAtribuicao(simboloAnterior.getCadeia());
		tratarSimboloRequerido("=", "valor_inicial_const-=");
		valor();
		geradorDeCodigo.gerarAtribuicao();
	}

	private void valor_inicial() {
		tratarIdentificadorRequerido("valor_inicial");
		semantico.asDeclararXptoVariavel(simboloAnterior);
		geradorDeCodigo.resetarAtribuicao(simboloAnterior.getCadeia());
		valor_aux();
	}

	private void valor_aux() {
		if (optionalSymbol("=")) {
			valor();
			geradorDeCodigo.gerarAtribuicao();
		}
	}

	private void valor() {
		if (tratarErro("valor", getStringEsperado("cadeia, vetor ou expressão"))); {
			if (cadeia()) {
				geradorDeCodigo.empilharOperando(simboloAnterior.getCadeia());
			} else if (vetor()) {
				geradorDeCodigo.empilharVetorOperando();
			} else {
				optionalExpressao();
			}
		}
	}

	private boolean vetor() {
		if (optionalSymbol("{")) {
			geradorDeCodigo.resetarOperandoVetor();
			geradorDeCodigo.addOperandoVetor(simboloAnterior.getCadeia());
			valores();
			tratarSimboloRequerido("}", "vetor-}");
			geradorDeCodigo.addOperandoVetor(simboloAnterior.getCadeia());
			return true;
		}
		return false;
	}

	private void valores() {
		var_exp();
		geradorDeCodigo.addOperandoVetor(simboloAnterior.getCadeia());
		mais_valores();
	}

	private void mais_valores() {
		if (optionalSymbol(",")) {
			geradorDeCodigo.addOperandoVetor(simboloAnterior.getCadeia());
			var_exp();
			geradorDeCodigo.addOperandoVetor(simboloAnterior.getCadeia());
			mais_valores();
		}
	}

	private void var_exp() {
		if (!cadeia()) 
			optionalExpressao();
		
	}

	private void dec_resto(Simbolo tipo, boolean ehVetor) {
		if (optionalSymbol(",")) {
			semantico.asEmpilharTipo(tipo, ehVetor);
			valor_inicial();
			dec_resto(tipo, ehVetor);
		}
	}

	private boolean tipo() {
		return optionalSymbol("int") || optionalSymbol("string");
	}

	private boolean eh_vetor() {
		String ident = simboloAnterior.getCadeia();
		if (optionalSymbol("[")) {
			geradorDeCodigo.resetarOperandoVetor();
			geradorDeCodigo.addOperandoVetor(ident);
			geradorDeCodigo.addOperandoVetor(simboloAnterior.getCadeia());
			expressao();
			geradorDeCodigo.addOperandoVetor();
			tratarSimboloRequerido("]", "eh_vetor-]");
			geradorDeCodigo.addOperandoVetor(simboloAnterior.getCadeia());
			return true;
		}
		return false;
	}

	private void subprogramas() {
		if (optionalSymbol("def")) {
			tratarSimboloRequerido("void", "subprogramas-void");
			tratarIdentificadorRequerido("subprogramas-identificador");
			semantico.asDeclararProcedimento(simboloAnterior);
			
			geradorDeCodigo.gerarInicioSubPrograma(simboloAnterior.getCadeia());
			
			tratarSimboloRequerido("(", "subprogramas-(");
			tratarSimboloRequerido(")", "subprogramas-)");
			tratarSimboloRequerido("{", "subprogramas-{");
			bloco();
			tratarSimboloRequerido("}", "subprogramas-}");
			
			geradorDeCodigo.gerarFimSubPrograma();
			
			subprogramas();
		}
	}
	
	private void optionalExpressao() {
		if (simbolo != null && 
				(simbolo.isIdentificador() || 
						simbolo.isNumero() || 
						simbolo.isAbreParenteses())) {
			expressao();
		}
	}

	private void expressao() {
		termo();
		expressaoLinha();
	}
	
	private void expressaoLinha() {
		if (optionalSymbol("+") || optionalSymbol("-")) {
			String operador = simboloAnterior.getCadeia();
			expressao();			
			semantico.asVerificarTipo();
			geradorDeCodigo.gerarOperacaoAritmetica(operador);
		}
	}

	private void termo() {
		fator();
		termoLinha();
	}

	private void termoLinha() {
		if (optionalSymbol("*") || optionalSymbol("/")) {
			String operador = simboloAnterior.getCadeia();
			termo();
			semantico.asVerificarTipo();
			geradorDeCodigo.gerarOperacaoAritmetica(operador);
		}
	}

	private void fator() {
		pred();
		fatorLinha();
	}

	private void fatorLinha() {
		if (optionalSymbol("**")) {
			fator();
			geradorDeCodigo.gerarOperacaoAritmetica("**");
		}
	}

	private void pred() {
		if (tratarErro("pred", getStringEsperado("numero, (expressão) ou identificador"))) {
			if (numero()) {
				semantico.asEmpilharTipoNumero();
				geradorDeCodigo.empilharOperando(simboloAnterior.getCadeia());
			} else if (expressaoParentisada()) {
				// nao precisa fazer nada.
			} else if (escalar()) {
				// nao precisa fazer nada.
			}
		} else {
			semantico.asEmpilharNulo();
		}
	}

	private boolean tratarOperadorRelacionalRequerido(String regra)
			 {
		boolean tratou = tratarErro(regra, getStringEsperado("OPERADOR RELACIONAL"));
		operadorRelacional();
		return tratou;
	}

	private boolean tratarTipoRequerido(String regra)
			 {
		boolean tratou = tratarErro(regra, getStringEsperado("Tipo STRING ou INT"));
		tipo();
		return tratou;
	}

	private boolean tratarIdentificadorRequerido(String regra) {
		boolean tratou = tratarErro(regra, getStringEsperado("IDENTIFICADOR"));
		identificador();
		return tratou;
	}

	private boolean tratarSimboloRequerido(String symbol, String regra) {
		if (tratarErro(regra, getStringEsperado(symbol))) {
			lerProximoSimbolo();
			return true;
		}
		return false;
	}

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

	private boolean expressaoParentisada() {
		if (optionalSymbol("(")) {
			expressao();
			tratarSimboloRequerido(")", "expressaoParentisada-)");
			return true;
		}
		return false;
	}
	
	private boolean escalar() {
		if (identificador()) {
			Simbolo ant = simboloAnterior;
			boolean ehVetor = eh_vetor();
			semantico.asEmpilharTipoBaseadoEmIdentificador(ant, ehVetor);
			if (ehVetor) geradorDeCodigo.empilharVetorOperando();
			else geradorDeCodigo.empilharOperando(ant.getCadeia());
			return true;
		}
		return false;
	}
	
	private void expressaoLogica() {
		termoRelacional();
		expressaoLogicaLinha();
	}

	private void expressaoLogicaLinha() {
		if (optionalSymbol("||")) {
			expressaoLogica();
			geradorDeCodigo.salvarOperadorRelacional("||");
			geradorDeCodigo.empilharExpressaoLogica();
		}
	}

	private void termoRelacional() {
		fatorRelacional();
		termoRelacionalLinha();
	}

	private void termoRelacionalLinha() {
		if (optionalSymbol("&&")) {
			fatorRelacional();
			termoRelacionalLinha();
			geradorDeCodigo.salvarOperadorRelacional("&&");
			geradorDeCodigo.empilharExpressaoLogica();
		}
	}

	private void fatorRelacional() {
		if (!expressaoLogicaParentisada()) {
			expressao();
			tratarOperadorRelacionalRequerido("fatorRelacional");
			geradorDeCodigo.salvarOperadorRelacional(simboloAnterior.getCadeia());
			expressao();
			semantico.asVerificarTipo();
			geradorDeCodigo.empilharExpressaoLogica();
		}
	}

	private boolean expressaoLogicaParentisada() {
		if (optionalSymbol("[")) {
			expressaoLogica();
			tratarSimboloRequerido("]", "expressaoLogicaParentisada");
			return true;
		}
		return false;
	}

	private boolean operadorRelacional() {
		if (simbolo == null || !simbolo.isOperadorRelacional()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private void bloco() {
		if (comando())
			bloco();
	}

	private boolean comando() {
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
		return false;
	}

	private boolean procedimentoOuAtribuicao()
			 {
		Simbolo identificador = simboloAnterior;
		if (optionalSymbol("(")) {
			chamadaProcedimento(identificador);
		} else {
			geradorDeCodigo.resetarAtribuicao(identificador.getCadeia());
			atribuicao();
			geradorDeCodigo.gerarAtribuicao();
		}
		tratarSimboloRequerido(";", "comando-;");
		return true;
	}

	private boolean comandoWrite() {
		valor();
		tratarSimboloRequerido(";", "comando-;");
		return true;
	}

	private boolean comandoRead() {
		// soh fala com o semantico se conseguiu tratar belamente.
		if (tratarIdentificadorRequerido("comandoRead")) {
			semantico.asEmpilharTipoBaseadoEmIdentificador(simboloAnterior, eh_vetor());
		}
		tratarSimboloRequerido(";", "comando-;");
		return true;
	}

	private boolean comandoWhile() {
		tratarSimboloRequerido("(", "comandoCondicional-(");
		geradorDeCodigo.resetTemp();
		geradorDeCodigo.gerarInicioWhile();
		expressaoLogica();
		geradorDeCodigo.gerarDesvioCondicional();
		tratarSimboloRequerido(")", "comandoCondicional-)");
		tratarSimboloRequerido("{", "comandoCondicional-{");
		bloco();
		tratarSimboloRequerido("}", "comandoCondicional-}");
		geradorDeCodigo.geraFimWhile();
		optionalSymbol(";");
		return true;
	}

	private boolean comandoIf() {
		tratarSimboloRequerido("(", "comandoCondicional-(");
		geradorDeCodigo.resetTemp();
		expressaoLogica();
		geradorDeCodigo.gerarDesvioCondicional();
		tratarSimboloRequerido(")", "comandoCondicional-)");
		tratarSimboloRequerido("{", "comandoCondicional-{");
		bloco();
		geradorDeCodigo.gerarInicioIf();
		tratarSimboloRequerido("}", "comandoCondicional-}");
		cmd_decisao_else();
		geradorDeCodigo.gerarFimIf();
		optionalSymbol(";");
		return true;
	}

	private void chamadaProcedimento(Simbolo identificador)
			 {
		tratarSimboloRequerido(")", "procedimento");
		semantico.asVerificarExistenciaProcedimento(identificador);
		geradorDeCodigo.gerarChamadaSubPrograma(identificador.getCadeia());
	}

	private void atribuicao() {
		semantico.asVerificarSeEhTipoConstante(simboloAnterior);
		Simbolo ant = simboloAnterior;
		boolean ehVetor = eh_vetor();
		semantico.asEmpilharTipoBaseadoEmIdentificador(ant, ehVetor);
		if (ehVetor) {
			geradorDeCodigo.setLadoEsqVetor();
		}
		tratarSimboloRequerido("=", "atribuicao-=");
		tratarErro("atribuicao", getStringEsperado("cadeia ou expressão"));
		if (cadeia()) {
			semantico.asEmpilharTipoCadeia();
			geradorDeCodigo.empilharOperando(simboloAnterior.getCadeia());
		} else {
			optionalExpressao();
		}
		semantico.asVerificarTipo();
	}

	private void cmd_decisao_else() {
		if (optionalSymbol("else")) {
			tratarSimboloRequerido("{", "comandoCondicional-{");
			bloco();
			tratarSimboloRequerido("}", "comandoCondicional-}");
		}
	}

	private boolean cadeia() {
		if (simbolo == null || !simbolo.isCadeia()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private boolean identificador() {
		if (simbolo == null || !simbolo.isIdentificador()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
	}

	private boolean numero() {
		if (simbolo == null || !simbolo.isNumero()) {
			return false;
		} else {
			lerProximoSimbolo();
			return true;
		}
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

	private void checarFinal() {
		if (simbolo != null) {
			ListaDeErros.getInstance().addMensagemDeErro(getStringNaoEsperado(simbolo.getCadeia()));
		}
	}

}
