package compilador.tests;

import java.util.LinkedList;
import java.util.List;

import easyaccept.EasyAcceptFacade;

public class SintaticoTestRunner {
	
	private static final String SEP = System.getProperty("file.separator");

	public static void main(String[] args) {
		CompiladorEasyAcceptFacade facade = new CompiladorEasyAcceptFacade();
		List<String> files = new LinkedList<String>();
		String folder = "testes"+SEP+"easyaccept"+SEP+"sintatico"+SEP;
//		files.add(folder + "teste1.txt");
		files.add(folder + "teste2.txt");
//		files.add(folder + "testeErro1.txt");
//		files.add(folder + "testeErro2.txt");
		EasyAcceptFacade eaFacade = new EasyAcceptFacade(facade,files);
		eaFacade.executeTests();
		System.out.println(eaFacade.getCompleteResults());
	}

}
