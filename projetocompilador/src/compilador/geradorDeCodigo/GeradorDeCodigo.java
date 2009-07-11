package compilador.geradorDeCodigo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GeradorDeCodigo {

	private static final String NEW_LINE = System.getProperty("line.separator");

	private BufferedWriter out;

	private String ladoEsquerdo;

	private int proxTemp;

	private int proxRotulo;

	private List<String> pilhaControleOperandos;

	private List<String> pilhaRotulos;

	private Map<String, Integer> tabelaRotulos;

	private boolean verbose;

	private String operadorRelacional;

	private int tripla_atual;

	public GeradorDeCodigo() {
		pilhaControleOperandos = new LinkedList<String>();
		pilhaRotulos = new LinkedList<String>();
		tabelaRotulos = new HashMap<String, Integer>();
		proxRotulo = 1;
		tripla_atual = 1;
	}

	public void initialize(BufferedWriter out) {
		this.verbose = out == null ? false : true;
		this.out = out;
	}

	public void resetAtribuicao(String ladoEsq) {
		this.ladoEsquerdo = ladoEsq;
		resetTemp();
	}

	public void resetTemp() {
		proxTemp = 1;
	}

	public void geraAtribuicao() {
		String oper_1 = pop(pilhaControleOperandos);
		emitir(ladoEsquerdo, ":=", oper_1); // TODO: mudar para codigo
	}

	public void gerarOperacaoAritmetica(String operacao) {
		String oper_2 = pop(pilhaControleOperandos);
		String oper_1 = pop(pilhaControleOperandos);
		String result = makeTemp();
		push(pilhaControleOperandos, result);
		emitir(result, operacao, oper_1, oper_2); // TODO: mudar para codigo
	}

	public void iniciaExprRelacional() {
		ladoEsquerdo = pop(pilhaControleOperandos);
	}

	public void empilharOperando(String operando) {
		push(pilhaControleOperandos, operando);
	}

	public void salvaOperadorRelacional(String op_rel) {
		this.operadorRelacional = op_rel;
	}

	public void gerarDesvioCondicional() {
		emitir("", operadorRelacional, ladoEsquerdo,
				pop(pilhaControleOperandos));
		String label = makeRotulo();
		push(pilhaRotulos, label);
		emitir(label, "jif", "", "");
	}

	public void geraInicioIf() {
		String label_aux = pop(pilhaRotulos);
		String label = makeRotulo();
		push(pilhaRotulos, label);
		emitir(label, "jmp", "", "");
		insere(tabelaRotulos, label_aux, tripla_atual);
	}

	public void geraFimIf() {
		String label = pop(pilhaRotulos);
		insere(tabelaRotulos, label, tripla_atual);
	}

	private void insere(Map<String, Integer> tabela, String key, int value) {
		tabela.put(key, value);
	}

	private String makeRotulo() {
		String rotulo = ":L";
		rotulo += proxRotulo < 100 ? "0" : "";
		rotulo += proxRotulo < 10 ? "0" : "";
		return rotulo + proxRotulo++;
	}

	private String pop(List<String> pilha) {
		return pilha.remove(0);
	}

	private void push(List<String> pilha, String simbolo) {
		pilha.add(0, simbolo);
	}

	private void emitir(String resultado, String operacao, String param) {
		emitir(resultado, operacao, param, "");
	}

	private void emitir(String cmd) {
		if (!verbose)
			return;
		tripla_atual++;
		try {
			out.write(cmd + NEW_LINE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void emitir(String resultado, String operacao, String param1,
			String param2) {
		emitir(makeCmd(resultado, operacao, param1, param2));
	}

	private String makeCmd(String resultado, String operacao, String param1,
			String param2) {
		return resultado + " " + operacao + " " + param1 + " " + param2;
	}

	private String makeTemp() {
		return "t" + proxTemp++;
	}

	public void finalize() {
		if (verbose)
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}