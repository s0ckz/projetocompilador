package compilador.tratamentoDeErros;

import java.util.LinkedList;

public class ListaDeErros {
	
	private LinkedList<String> mensagens;
	
	private static ListaDeErros instance;
	
	private ListaDeErros() {
		mensagens = new LinkedList<String>();
	}
	
	public void addMensagemDeErro(String msgErro) {
		mensagens.push(msgErro);
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

}
