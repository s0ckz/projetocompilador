package compilador.runner;

import java.util.LinkedList;
import java.util.List;

import compilador.lexico.LexicoEasyAcceptFacade;

import easyaccept.EasyAcceptFacade;

public class TestRunner {
	
	private static final String SEP = System.getProperty("file.separator");

	public static void main(String[] args) {
		LexicoEasyAcceptFacade facade = new LexicoEasyAcceptFacade();
		List<String> files = new LinkedList<String>();
		files.add("testes"+SEP+"easyaccept"+SEP+"teste1.txt");
		EasyAcceptFacade eaFacade = new EasyAcceptFacade(facade,files);
		eaFacade.executeTests();
		System.out.println(eaFacade.getCompleteResults());
	}

}
