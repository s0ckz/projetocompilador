package compilador.lexico;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import compilador.util.Simbolo;

public class LexicoEasyAcceptFacade {
	
	private AnalisadorLexico analisadorLexico;
	
	public LexicoEasyAcceptFacade() {
		reset();
	}
	
	public void initialize(String fileName) throws FileNotFoundException {
		this.analisadorLexico.initialize(new BufferedReader(new FileReader(fileName)));
	}
	
	public Simbolo proximoSimbolo() throws AnalisadorLexicoException {
		return analisadorLexico.proximoSimbolo();	
	}
	
	public void reset() {
		this.analisadorLexico  = new AnalisadorLexico();
	}
}
