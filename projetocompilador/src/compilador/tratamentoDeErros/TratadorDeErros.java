package compilador.tratamentoDeErros;

import java.util.LinkedList;

public class TratadorDeErros {
	
	private LinkedList<String> mensagens;
	
	public TratadorDeErros() {
		mensagens = new LinkedList<String>();
	}
	
	public void addMensagemDeErro(String msgErro) {
		mensagens.push(msgErro);
	}

	public String popErro() {
		return mensagens.removeLast();
	}

}
