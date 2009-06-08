package compilador.tratamentoDeErros;

import java.util.LinkedList;

public class ListaDeErros {
	
	private LinkedList<String> mensagens;
	
	private static ListaDeErros instance;
	
	private int linhaAtual;
	
	private String conteudoLinhaAtual;

	private ListaDeErros() {
		mensagens = new LinkedList<String>();
	}
	
	public void addMensagemDeErro(String msgErro) {
		mensagens.push("Erro na linha: " + getLinhaAtual() + " perto de: '" + getConteudoLinhaAtual() +  "' - " + msgErro);
	}

	public String popErro() {
		return mensagens.removeLast();
	}

	public static ListaDeErros getInstance() {
		if (instance == null) {
			instance =  new ListaDeErros();
		}
		return instance;
	}

	public void clear() {
		mensagens.clear();
	}
	
	public int getLinhaAtual() {
		return linhaAtual;
	}

	public void setLinhaAtual(int linhaAtual) {
		this.linhaAtual = linhaAtual;
	}

	public String getConteudoLinhaAtual() {
		return conteudoLinhaAtual;
	}

	public void setConteudoLinhaAtual(String conteudoLinhaAtual) {
		this.conteudoLinhaAtual = conteudoLinhaAtual;
	}

}
