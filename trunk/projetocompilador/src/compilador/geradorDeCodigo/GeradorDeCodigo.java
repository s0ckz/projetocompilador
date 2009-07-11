package compilador.geradorDeCodigo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import compilador.tratamentoDeErros.ListaDeErros;

public class GeradorDeCodigo {

	private static final String NEW_LINE = System.getProperty("line.separator");

	private BufferedWriter out;

	private String ladoEsquerdo;

	private int proxTemp;

	private int proxRotulo;

	private List<String> pilhaControleOperandos;

	private List<String> pilhaRotulos;

	private Map<String, Integer> tabelaRotulos;

	private Map<String, Integer> tabelaRotulosSubPrograma;
	
	private List<String> triplas;
	
	private List<String> triplasSubPrograma;

	private boolean verbose;
	
	private boolean modoSubPrograma;
	
	private String operadorRelacional;

	private int triplaAtual;

	private int triplaAtualSubPrograma;

	public GeradorDeCodigo() {
		pilhaControleOperandos = new LinkedList<String>();
		triplas = new LinkedList<String>();
		triplasSubPrograma = new LinkedList<String>();
		pilhaRotulos = new LinkedList<String>();
		tabelaRotulos = new HashMap<String, Integer>();
		tabelaRotulosSubPrograma = new HashMap<String, Integer>();
		proxRotulo = 1;
		triplaAtual = 1;
		modoSubPrograma = false;
	}

	public void inicializar(BufferedWriter out) {
		this.verbose = out == null ? false : true;
		this.out = out;
	}

	public void finalizar() {
		if (verbose) {
			try {
				triplas = substituirRotulos(triplas, tabelaRotulos, 1);
				triplasSubPrograma = substituirRotulos(triplasSubPrograma, 
						tabelaRotulosSubPrograma, triplas.size() + 1);
				imprimirTriplas(triplas);
				imprimirTriplas(triplasSubPrograma);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void resetarAtribuicao(String ladoEsq) {
		if (temErros())
			return;
		
		this.ladoEsquerdo = ladoEsq;
		resetTemp();
	}

	public void resetTemp() {
		if (temErros())
			return;
		
		proxTemp = 1;
	}

	public void gerarAtribuicao() {
		if (temErros())
			return;
		
		String oper_1 = pop(pilhaControleOperandos);
		emitir(ladoEsquerdo, ":=", oper_1);
	}

	public void gerarOperacaoAritmetica(String operacao) {
		if (temErros())
			return;
		
		String oper_2 = pop(pilhaControleOperandos);
		String oper_1 = pop(pilhaControleOperandos);
		String result = makeTemp();
		push(pilhaControleOperandos, result);
		emitir(result, operacao, oper_1, oper_2);
	}

	public void empilharOperando(String operando) {
		if (temErros())
			return;
		
		push(pilhaControleOperandos, operando);
	}

	public void salvarOperadorRelacional(String op_rel) {
		if (temErros())
			return;
		
		this.operadorRelacional = op_rel;
	}

	public void empilharExpressaoLogica() {
		if (temErros())
			return;

		String result = makeTemp();
		String t2 = pop(pilhaControleOperandos);
		String t1 = pop(pilhaControleOperandos);
		emitir(result, operadorRelacional, t1, t2);
		push(pilhaControleOperandos, result);
	}

	public void gerarDesvioCondicional() {
		if (temErros())
			return;
		
		pop(pilhaControleOperandos);
		String label = makeRotulo();
		push(pilhaRotulos, label);
		emitir(label, "jif", "", "");
	}

	public void gerarInicioIf() {
		if (temErros())
			return;
		
		String label_aux = pop(pilhaRotulos);
		String label = makeRotulo();
		push(pilhaRotulos, label);
		emitir(label, "jmp", "", "");
		insere(label_aux);
	}

	public void gerarFimIf() {
		if (temErros())
			return;
		
		String label = pop(pilhaRotulos);
		insere(label);
	}

	public void gerarInicioWhile() {
		if(temErros())
			return;
		String label = makeRotulo();
		insere(label);
		push(pilhaRotulos, label);
	}

	public void geraFimWhile() {
		if(temErros())
			return;
		String label_fim = pop(pilhaRotulos);
		String label_inicio = pop(pilhaRotulos);
		emitir(label_inicio, "jmp", "", "");
		insere(label_fim);
	}

	public void gerarInicioSubPrograma() {
		modoSubPrograma = true;
	}

	public void gerarFimSubPrograma() {
		modoSubPrograma = false;
	}

	private void insere(String key) {
		getTabelaRotulos().put(key, getTriplaAtual());
	}

	private Integer getTriplaAtual() {
		return modoSubPrograma ? triplaAtualSubPrograma : triplaAtual;
	}

	private Map<String, Integer> getTabelaRotulos() {
		return modoSubPrograma ? tabelaRotulosSubPrograma : tabelaRotulos;
	}

	private String makeRotulo() {
		return String.format(":L%03d", proxRotulo++);
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
		incrementarTriplaAtual();
		getListaTriplas().add(cmd);
	}

	private void incrementarTriplaAtual() {
		if (modoSubPrograma)
			triplaAtualSubPrograma++;
		else
			triplaAtual++;
	}

	private List<String> getListaTriplas() {
		return modoSubPrograma ? triplasSubPrograma : triplas;
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

	private boolean temErros() {
		return ListaDeErros.getInstance().temErros();
	}

	private void imprimirTriplas(List<String> triplas) throws IOException {
		for (String tripla : triplas) {
			out.write(tripla + NEW_LINE);
		}
	}

	private List<String> substituirRotulos(List<String> triplas,
			Map<String, Integer> tabelaRotulos, int inicio) {
		List<String> novasTriplas = new LinkedList<String>();
		for (String triplaAntiga : triplas) {
			String novaTripla = triplaAntiga;
			for (Entry<String, Integer> entry : tabelaRotulos.entrySet()) {
				String rotulo = entry.getKey();
				int linha = entry.getValue();
				novaTripla = novaTripla.replace(rotulo, linha + "");
			}
			novasTriplas.add(novaTripla);
		}
		return novasTriplas;
	}

}