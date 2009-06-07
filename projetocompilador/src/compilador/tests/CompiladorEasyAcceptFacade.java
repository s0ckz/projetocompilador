package compilador.tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import compilador.lexico.AnalisadorLexico;
import compilador.lexico.AnalisadorLexicoException;
import compilador.sintatico.AnalisadorSintatico;
import compilador.sintatico.AnalisadorSintaticoException;
import compilador.util.Simbolo;

public class CompiladorEasyAcceptFacade {
	
	private AnalisadorLexico analisadorLexico;
	
	private AnalisadorSintatico analisadorSintatico;
	
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
	
	public Simbolo proximoSimbolo() throws AnalisadorLexicoException {
		return analisadorLexico.proximoSimbolo();	
	}
	
	public String getProximoErro() {
		return analisadorSintatico.getProximoErro();
	}
	
	public void reset() {
		this.analisadorLexico  = new AnalisadorLexico();
		this.analisadorSintatico =  new AnalisadorSintatico(analisadorLexico);
	}
}
