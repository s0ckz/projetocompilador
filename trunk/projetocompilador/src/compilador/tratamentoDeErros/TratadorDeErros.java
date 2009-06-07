package compilador.tratamentoDeErros;

import java.util.LinkedList;
import java.util.List;

public class TratadorDeErros {
	
	private List<String> mensagens;
	
	private static TratadorDeErros instance;
	
	private TratadorDeErros() {
		mensagens = new LinkedList<String>();
	}
	
	public static TratadorDeErros getInstance() {
		if (instance == null) {
			instance = new TratadorDeErros();
		}
		return instance;
	}
	
	public void addMensagemDeErro(String msgErro) {
		System.out.println(msgErro);
		mensagens.add(msgErro);
	}

}
