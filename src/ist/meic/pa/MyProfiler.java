package ist.meic.pa;
import javassist.*;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.analysis.ControlFlow;
import javassist.bytecode.analysis.ControlFlow.Block;
import javassist.bytecode.analysis.FramePrinter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.Throwable;
import java.util.Arrays;
public class MyProfiler implements  Translator {


	@Override
	public void onLoad(ClassPool pool, String ClassName) throws NotFoundException,
			CannotCompileException {
		CtClass ctclass= pool.get(ClassName);
		addprofile(pool,ctclass);
	}

	@Override
	public void start(ClassPool pool) throws NotFoundException,
			CannotCompileException {
		// do nothing 
		
	}

	public void addprofile(ClassPool pool, final CtClass ctClass){
		if (!ctClass.getName().equals("ist.meic.pa.BoxingPorfiler")
				&& !ctClass.getName().contains("javassist")) {
			for(CtMethod method : ctClass.getDeclaredMethods()){
				
				/*PrintStream ps = new PrintStream(System.out, true);
				FramePrinter fp = new FramePrinter(ps);
				fp.print(method);
				System.out.println(method.getLongName() + " boxed " + printCounter(method) + " " + ;*/
				printCounter(method);
			}
		}
		
	}

	private String printCounter(CtMethod method) {
		
		//Creates the necessary Streams and Printers to read the ctMethod info
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos, true);
		FramePrinter fp = new FramePrinter(ps);
		
		//prints the ctMethod information into the ByteArrayOutputStream baos
		fp.print(method);
		
		//converts the OutputStream into a String for easy parsing
		String s = new String();
		try {
			s = new String(baos.toByteArray(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("I'm string s:");
		
		//reads the string containing the method stack information into a buffered reader
		BufferedReader bufReader = new BufferedReader(new StringReader(s));
		
		//TODO parses the string to locate boxing and unboxing operations
		
		//TODO saves counts to a hash map or hash table to print as expected in main
		
		return null;
	}
}
