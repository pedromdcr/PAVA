package ist.meic.pa;

import javassist.*;

import javassist.bytecode.analysis.FramePrinter;

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
		addprofile(pool, ctclass);
	}

	@Override
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
		// do nothing

	}

	public void addprofile(ClassPool pool, final CtClass ctClass) {
		if (!ctClass.getName().equals("ist.meic.pa.BoxingPorfiler") && !ctClass.getName().contains("javassist")) {
			for (CtMethod method : ctClass.getDeclaredMethods()) {

				/*
				 * PrintStream ps = new PrintStream(System.out, true);
				 * FramePrinter fp = new FramePrinter(ps); fp.print(method);
				 * System.out.println(method.getLongName() + " boxed " +
				 * printCounter(method) + " " + ;
				 */
				printCounter(method);
			}
		}

	}

	private String printCounter(CtMethod method) {
		String delims = " |\\(";
		String[] tok;
		System.out.println("Todos os valueOf's identificados pelo print para o metodo: " + method.getLongName());
		TreeMap<String, Integer> storage = new TreeMap<String, Integer>();

		// Creates the necessary Streams and Printers to read the ctMethod info
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos, true);
		FramePrinter fp = new FramePrinter(ps);

		// prints the ctMethod information into the ByteArrayOutputStream baos
		fp.print(method);
		
		// converts the OutputStream into a String for easy parsing
		String s = new String();
		try {
			s = new String(baos.toByteArray(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// reads the string containing the method stack information into a
		// buffered reader
		// BufferedReader bufReader = new BufferedReader(new StringReader(s));

		// TODO parses the string to locate boxing and unboxing operations
		Scanner scanner = new Scanner(s);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.contains("valueOf")) {
				tok = line.split(delims);

				for (int i = 0; i < tok.length; ++i) {
					if (tok[i].contains("valueOf")) {
						String cenapaguardar = tok[i];

						cenapaguardar = cenapaguardar.replace(".valueOf", "");
						if (storage.containsKey(cenapaguardar)) {

							int oldval = storage.get(cenapaguardar);
							storage.put(cenapaguardar, oldval + 1);
						} else
							storage.put(cenapaguardar, 1);
						// System.out.println(cenapaguardar);

					}

				}
				// System.out.println("I'm string s:");
				// System.out.println(line);

			}
		}
		for (Map.Entry<String, Integer> entry : storage.entrySet()) {
			System.out.println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
		}
		scanner.close();
		System.out.println(s);

		// TODO saves counts to a hash map or hash table to print as expected in
		// main

		return null;
	}
}
