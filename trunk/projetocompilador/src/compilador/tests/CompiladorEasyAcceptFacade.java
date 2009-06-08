package compilador.tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import compilador.lexico.AnalisadorLexico;
import compilador.lexico.AnalisadorLexicoException;
import compilador.semantico.AnalisadorSemantico;
import compilador.sintatico.AnalisadorSintatico;
import compilador.sintatico.AnalisadorSintaticoException;
import compilador.tratamentoDeErros.ListaDeErros;
import compilador.util.Simbolo;

public class CompiladorEasyAcceptFacade {
	
	private AnalisadorLexico analisadorLexico;
	
	private AnalisadorSintatico analisadorSintatico;

	private AnalisadorSemantico analisadorSemantico;
	
	public CompiladorEasyAcceptFacade() {
		reset();
	}
	
	public void initialize(String fileName) throws FileNotFoundException {
		this.analisadorLexico.initialize(new BufferedReader(new FileReader(fileName)));
	}
	
	public void analyse(String fileName) throws AnalisadorSintaticoException, AnalisadorLexicoException, FileNotFoundException {
		reset();
		this.analisadorLexico.initialize(new BufferedReader(new FileReader(fileName)));
		this.analisadorSintatico.analyse();
	}
	
	// para evitar a mudanca dos testes do semantico por enquanto.
	public void analyseSematico(String fileName) throws Exception {
		analyse(fileName);
		throw new Exception(getProximoErro());
	}
	
	public Simbolo proximoSimbolo() throws AnalisadorLexicoException {
		return analisadorLexico.proximoSimbolo();	
	}
	
	public String getProximoErro() {
		return ListaDeErros.getInstance().popErro();
	}
	
	public void reset() {
		this.analisadorLexico  = new AnalisadorLexico();
		this.analisadorSemantico  = new AnalisadorSemantico();
		this.analisadorSintatico =  new AnalisadorSintatico(analisadorLexico, analisadorSemantico);
		ListaDeErros.getInstance().clear();
	}
}
