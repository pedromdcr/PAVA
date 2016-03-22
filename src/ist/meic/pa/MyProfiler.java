package ist.meic.pa;

import javassist.*;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.analysis.FramePrinter;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class MyProfiler implements Translator {

	@Override
	public void onLoad(ClassPool pool, String ClassName) throws NotFoundException, CannotCompileException {
		CtClass ctclass = pool.get(ClassName);
		try {
			addprofile(pool, ctclass);
		} catch (BadBytecode e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
		// do nothing

	}

	public void addprofile(ClassPool pool, final CtClass ctClass) throws BadBytecode {
		if (!ctClass.getName().equals("ist.meic.pa.BoxingPorfiler") && !ctClass.getName().contains("javassist")) {
			for (CtMethod method : ctClass.getDeclaredMethods()) {
				//printCounter(method);
				
				System.out.println("\n\n\n\n\n");
				System.out.println(method.getName());
				System.out.println("\n");
				
				try {
					method.instrument(
					        new ExprEditor() {
					            public void edit(MethodCall m)
					                          throws CannotCompileException
					            {
					                System.out.println("Class: " + m.getClassName() + 
					                					";\n" + "Method: " + m.getMethodName() + "\n\n");
					            }
					        });
					

				} catch (CannotCompileException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private String printCounter(CtMethod method) {
		String delims = " |\\(";
		String[] tok;
		//System.out.println("Todos os valueOf's identificados pelo print para o metodo: " + method.getLongName());
		TreeMap<String, Integer> storage = new TreeMap<String, Integer>();

//		// Creates the necessary Streams and Printers to read the ctMethod info
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		PrintStream ps = new PrintStream(baos, true);
//		FramePrinter fp = new FramePrinter(ps);
//
//		// prints the ctMethod information into the ByteArrayOutputStream baos
//		fp.print(method);
//		
//		// converts the OutputStream into a String for easy parsing
//		String s = new String();
//		try {
//			s = new String(baos.toByteArray(), "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		method.instrument(new ExprEditor() {
//		     public void edit(MethodCall m) throws CannotCompileException {
//	             System.out.println(m.getMethodName() + " line: "
//	                                + m.getLineNumber());
//		     }
//		 });
//
//		// reads the string containing the method stack information into a
//		// buffered reader
//		// BufferedReader bufReader = new BufferedReader(new StringReader(s));
//
//		// TODO parses the string to locate boxing and unboxing operations
//		Scanner scanner = new Scanner(s);
//		while (scanner.hasNextLine()) {
//			String line = scanner.nextLine();
//			if (line.contains("valueOf")) {
//				tok = line.split(delims);
//
//				for (int i = 0; i < tok.length; ++i) {
//					if (tok[i].contains("valueOf")) {
//						String cenapaguardar = tok[i];
//
//						cenapaguardar = cenapaguardar.replace(".valueOf", "");
//						if (storage.containsKey(cenapaguardar)) {
//
//							int oldval = storage.get(cenapaguardar);
//							storage.put(cenapaguardar, oldval + 1);
//						} else
//							storage.put(cenapaguardar, 1);
//						// System.out.println(cenapaguardar);
//
//					}
//
//				}
//				// System.out.println("I'm string s:");
//				// System.out.println(line);
//
//			}
//		}
//		/*for (Map.Entry<String, Integer> entry : storage.entrySet()) {
//			System.out.println(method.getLongName() +" boxed " + entry.getValue()+" "+ entry.getKey() );
//		}*/
//		scanner.close();
//		//
//		System.out.println(s);
//
//		// TODO saves counts to a hash map or hash table to print as expected in
//		// main
//
		return null;
	}
}
