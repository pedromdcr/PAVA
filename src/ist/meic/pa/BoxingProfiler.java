package ist.meic.pa;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LineNumberAttribute;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;



public class BoxingProfiler {

	public static String[] getCounterType(String methodName, String className, String lineNumber) {

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

	public static void memoize(CtClass ctClass, CtMethod ctMethod, List<String[]> order) throws CannotCompileException, NotFoundException {

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
		try {
			ctClass.writeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ctClass.defrost();
	}

	public static void main(String[] args) throws NotFoundException, CannotCompileException {

		ClassPool pool = ClassPool.getDefault();
		final CtClass ctClass = pool.getCtClass(args[0]);

		//final HashMap<String, Integer> counts = new HashMap<String, Integer>();

		for (final CtMethod method : ctClass.getDeclaredMethods()) {
			System.out.println("\n\n\n\n\n");
			System.out.println(method.getName());
			System.out.println("\n");

			final List<String[]> order = new ArrayList<String[]>();
			
			method.instrument(
					new ExprEditor() {
						public void edit(MethodCall m)
								throws CannotCompileException
						{
							String[] counterType = new String[3]; //counterType[2] is the line where the counter is to be inserted
							
							
							try {
								String name = m.getMethod().getName();
								if(name.contains("valueOf") || name.contains("Value")) {
									counterType = getCounterType(m.getMethodName(), m.getClassName(), String.valueOf(m.getLineNumber()));
									order.add(0, counterType);
								}
							} catch (NotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	
							try {
								memoize(ctClass, method, order);
							} catch (NotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					});

		}

	} 

}


