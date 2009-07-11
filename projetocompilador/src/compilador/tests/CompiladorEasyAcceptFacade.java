package compilador.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import compilador.geradorDeCodigo.GeradorDeCodigo;
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

	private GeradorDeCodigo geradorDeCodigo;
	
	public CompiladorEasyAcceptFacade() {
		reset();
	}
	
	public void initialize(String fileName) throws FileNotFoundException {
		this.analisadorLexico.initialize(new BufferedReader(new FileReader(fileName)));
	}

	public void analyse(String fileName, String targetFile) throws AnalisadorSintaticoException, AnalisadorLexicoException, IOException {
		defaultAnalyse(new BufferedReader(new FileReader(fileName)), new BufferedWriter(new FileWriter(new File(targetFile))));
	}

	public void analyse(String fileName) throws AnalisadorSintaticoException, AnalisadorLexicoException, FileNotFoundException {
		defaultAnalyse(new BufferedReader(new FileReader(fileName)), null);
	}
	
	private void defaultAnalyse(BufferedReader reader, BufferedWriter writer) throws AnalisadorSintaticoException {
		reset();
		this.analisadorLexico.initialize(reader);
		this.geradorDeCodigo.inicializar(writer);
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
		this.geradorDeCodigo = new GeradorDeCodigo();
		this.analisadorSintatico =  new AnalisadorSintatico(analisadorLexico, analisadorSemantico, geradorDeCodigo);
		ListaDeErros.getInstance().clear();
	}
}
