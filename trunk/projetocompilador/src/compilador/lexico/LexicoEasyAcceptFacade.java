package compilador.lexico;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import compilador.util.Simbolo;

public class LexicoEasyAcceptFacade {
	
	private AnalisadorLexico analisadorLexico;
	
	public void initialize(String fileName) throws FileNotFoundException {
		analisadorLexico = new AnalisadorLexico(new BufferedReader(new FileReader(fileName)));
	}
	
	public Simbolo proximoSimbolo() throws AnalisadorLexicoException {
		return analisadorLexico.proximoSimbolo();	
	}
}
