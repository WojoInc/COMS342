package definelang;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;

import static definelang.AST.*;
import definelang.parser.*;

public class Reader {	
	
	Program read() throws IOException {
		String programText = readNextProgram();
		return parse(programText);
	}
		
	Program parse(String programText) {
		DefineLangLexer l = new DefineLangLexer(new org.antlr.v4.runtime.ANTLRInputStream(programText));
		DefineLangParser p = new DefineLangParser(new org.antlr.v4.runtime.CommonTokenStream(l));
		return p.program().ast;
	}
	
	private String readNextProgram() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("$ ");
		String programText = br.readLine();
		return runFile(programText);
	}
	
	static String readFile(String fileName) throws IOException {
		try (BufferedReader br = new BufferedReader(
				new FileReader(fileName))) {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			return sb.toString();
		}
	}

	private String runFile(String programText) throws IOException {
		if(programText.startsWith("run ")){
			programText = readFile("build/funclang/examples/" + programText.substring(4));
		}
		return programText; 
	}

}
