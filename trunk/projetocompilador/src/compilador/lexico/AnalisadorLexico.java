package compilador.lexico;

import java.io.BufferedReader;

import compilador.util.Simbolo;

public class AnalisadorLexico {
	
	private BufferedReader bufferedReader;
	
	public AnalisadorLexico(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}

	public Simbolo proximoSimbolo() throws AnalisadorLexicoException {
		if (bufferedReader == null) {
			throw new AnalisadorLexicoException("Analisador Lexico nao foi inicializado.");
		}
		return new Simbolo(51,"int");
	}
}
