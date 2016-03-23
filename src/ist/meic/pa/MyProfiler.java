package ist.meic.pa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LineNumberAttribute;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;



public class MyProfiler implements Translator {

	@Override
	public void onLoad(ClassPool pool, String ClassName) throws NotFoundException, CannotCompileException {
		CtClass ctclass = pool.get(ClassName);
		try {
			try {
				addprofile(pool, ctclass);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (BadBytecode e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
		// do nothing

	}

	public String[] getCounterType(String methodName, String className, String lineNumber) {
		
		System.out.println("LINENUMBER: " + lineNumber + " CLASSNAME " + className + " METHODNAME " + methodName);
		
		//counterType is [boxed_or_unboxed, wrapperType] e.g. [boxed, Integer]  
		String[] counterType = new String[3];

		//parse classname to get wrapper type
		String[] tokens = className.split("\\.");
		if(tokens.length >= 3) {
			
			//store it as the appropriate counter type
			if (tokens[2].equals("Integer") || tokens[2].equals("Double") || tokens[2].equals("Float") || 
					tokens[2].equals("Short") || tokens[2].equals("Long")) {
				counterType[1] = tokens[2];
//				for(String si : counterType) {
//					System.out.println("->>>COUNTERTYPE: " + si);
//				}
				System.out.println("METHOD NAME: " + methodName);
				if (methodName.equals("valueOf")) {
					counterType[0] = "boxed";
				} else if (methodName.contains("Value")){ //falta verificar se ha mais metodos que n sejam de unboxing que contenham "value"
					counterType[0] = "unboxed";
				}
				counterType[2] = lineNumber;
//				for(String si : counterType) {
//					System.out.println("->>>COUNTERTYPE2: " + si);
//				}
			}
		}
		return counterType;
	}
	
	public static void memoize(CtClass ctClass, CtMethod ctMethod, List<String[]> order) throws CannotCompileException, NotFoundException, IOException {
		
		// creates a field to store the counters for autoboxing and unboxing operations for the specified class
		CtField ctField = CtField.make("static java.util.Hashtable " + ctMethod.getName() + "Count" + " = " + 
											"new java.util.Hashtable();", 
												ctClass);
		
		// adds the created field to the manipulated ctClass
		ctClass.addField(ctField);
		for(CtField cfield : ctClass.getFields()) {
			System.out.println("FIELD NAME: " + cfield.getName().toString());
		}
		String key;
		List<String[]> noDuplicates = new ArrayList<String[]>();
		
		// gets rid of duplicates before initializing the countResults hashmap
		for(String[] i : order) {
			if(!noDuplicates.isEmpty())	
				for(String[] j : noDuplicates) {
					if(i[0].equals(j[0]) && i[1].equals(j[1]))
						break;
					noDuplicates.add(i);
				}
		}
		
		// initializes necessary counters in the ctField
		for(String[] op1 : noDuplicates) {
			key = op1[0] + "." + op1[1];
			/*ctMethod.insertBefore("if(countResults.containsKey(" + key + ")) {" +
										"countResults.put(" + key + ", countResults.get(" + key + ") + 1);" +
								  "} else {" + 
										"countResults.put(" + key + ", 1);" + 
								  "}");*/
			ctMethod.insertBefore("System.out.println(\"BANANAS\");");
			ctMethod.insertBefore("System.out.println(\"YOOHOO " + ctMethod.getName() + "Count.containsKey(" + key + ")\");");
			ctMethod.insertBefore(ctMethod.getName() + "Count.put(" + key + ", 0);");
		}
		
		// inserts a counter for autoboxing and unboxing ocurrences of each wrapper type in the corresponding lineNumber
		for(String[] op2 : order) {
			key = op2[0] + "." + op2[1];
			ctMethod.insertAt(Integer.parseInt(op2[2]), ctMethod.getName() + "Count.put(" + key + ", " + ctMethod.getName() + "Count.get(" + key + ") + 1)");	
		}
		
		// writes the changes to the ctClass file
		ctClass.writeFile();
		ctClass.defrost();
	}
	
	public void addprofile(ClassPool pool, CtClass ctClass) throws BadBytecode, ClassNotFoundException, CannotCompileException, NotFoundException {
		final HashMap<String, Integer> counts = new HashMap<String, Integer>();
		
		if (!ctClass.getName().equals("ist.meic.pa.BoxingProfiler") && !ctClass.getName().contains("javassist")) {
			for (final CtMethod method : ctClass.getDeclaredMethods()) {
				//printCounter(method);
				
				System.out.println("\n\n\n\n\n");
				System.out.println(method.getName());
				System.out.println("\n");
				
				final List<String[]> order = new ArrayList<String[]>();
				
				
				try {
					method.instrument(
					        new ExprEditor() {
					            public void edit(MethodCall m)
					                          throws CannotCompileException
					            {
					            	String[] counterType = new String[3]; //counterType[2] is the line where the counter is to be inserted
					            	CodeAttribute ca = method.getMethodInfo().getCodeAttribute();
					            	LineNumberAttribute lna = (LineNumberAttribute) ca.getAttribute(LineNumberAttribute.tag);
					            	//lna n funciona

					            	counterType = getCounterType(m.getMethodName(), m.getClassName(), String.valueOf(method.getMethodInfo().getLineNumber(0)));

					            	//String key = counterType[0] + "." + counterType[1];
					            	
//					            	try {
//										Field f = Class.forName(ctClass.getName()).getDeclaredField("hastable name in class");
//										f.setAccessible(true);
//										Object counts2 = f.get(null);
//									} catch (NoSuchFieldException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} catch (SecurityException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} catch (ClassNotFoundException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} catch (IllegalArgumentException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									} catch (IllegalAccessException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
					            	
//					            	method.ins
//					            	String ms = m.getClassName() + "." + m.getMethodName();
//					            	System.out.println(ms);
					            	
					            	
					            }
					        });
					
					try {
						memoize(ctClass, method, order);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

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
