package compilador.tests;

import java.util.LinkedList;
import java.util.List;

import easyaccept.EasyAcceptFacade;

public class SemanticoTestRunner {
	
	private static final String SEP = System.getProperty("file.separator");

	public static void main(String[] args) {
		CompiladorEasyAcceptFacade facade = new CompiladorEasyAcceptFacade();
		List<String> files = new LinkedList<String>();
		String folder = "testes"+SEP+"easyaccept"+SEP+"semantico"+SEP;
		files.add(folder + "testeSemErros.txt");
		files.add(folder + "testeComErros.txt");
		EasyAcceptFacade eaFacade = new EasyAcceptFacade(facade,files);
		eaFacade.executeTests();
		System.out.println(eaFacade.getCompleteResults());
	}

}
