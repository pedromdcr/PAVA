package ist.meic.pa;

import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class MyProfiler implements Translator {
	
	private CtClass _ctClass;
	private List<String> names; 
	
	public void start(ClassPool pool) 
			throws NotFoundException, CannotCompileException {
	}

	public void onLoad(ClassPool pool, String className)
			throws NotFoundException, CannotCompileException {
		_ctClass = pool.get(className);

		make(pool,_ctClass);
	}

	
	void make(ClassPool pool, CtClass ctClass) {
		System.out.println("---------\n\tMake\n");

		// tentar enfiar int z para a class..
//
//		CtField f = CtField.make("", ctClass);
//		ctClass.addField(f);

		//
		//		

		//		CtField f = CtField.make("TreeMap<Integer, String> tmap = new TreeMap<Integer, String>();", ctClass);
		//		ctClass.addField(f);
		//ver metodos
//		CtField hashtableField = CtField.make("public static java.util.TreeMap treeMap = new java.util.TreeMap();", ctClass);
//		ctClass.addField(hashtableField);
		
		names = new ArrayList<String>();

		for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			System.out.println("CLASS METHOD TO INSPECT: " + ctMethod.getName());
			final String prefix = ctMethod.getName(); // para distinguir metodos com o mesmo nome e assinaturas diferentes
			try {
				ctMethod.instrument(new ExprEditor() {
					public void edit(MethodCall m) throws CannotCompileException {
//						System.out.println("Class Name:\t"+ m.getClassName());
//						System.out.println("Method Name:\t"+ m.getMethodName());
//						System.out.println("Line Number:\t"+ m.getLineNumber());
						String[] tokens = m.getClassName().split("\\."); 
						if(tokens.length == 3 && (m.getClassName().contains("java.lang.") || m.getMethodName().contains("alue"))) {
							String name = prefix + "_" + tokens[2] + "_" + m.getMethodName();
							names.add(name);
							
							//cria um contador nao final (ou seja, que pode ser incrementado) com o nome especificado
							createField(name);
							
//							System.out.println(">>>>>>NAME: " + name);
							
							
							
							//m.replace("{ $_ = $proceed($$); " + name + "++; }");
							//m.replace("{ $_ = $proceed($$); " + name + "++;int t ="+name+";System.out.println(t); }");
							m.replace("{ $_ = $proceed($$); " + name + "++; System.out.println(\"I'm an injected non-final counter with value: \" + "+name+"); }");
						}
						//System.out.println("");
						
					}

				});
			} catch (CannotCompileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (String s : names) {
				//System.out.println("STRING: " + s);
				CtField f;
				try {
					//<<<>>> E ESTE O PROBLEMA, A ATRIBUICAO NAO FUNCIONA <<<>>>
					f = CtField.make("public static final int " + s + "_final;", _ctClass);
					_ctClass.addField(f, CtField.Initializer.byExpr(s));
				} catch (CannotCompileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			processFinalFields();
			System.out.println("");
		}
		
		/*------------------EXPERIENCIA, N APAGUEM------------------------*/
//		try {
//			_ctClass.instrument(new ExprEditor() {
//				public void edit(FieldAccess f) throws CannotCompileException {
//					
//					String fieldName = f.getFieldName();
//					System.out.println("FIELDNAME: " + fieldName);
//					
//					boolean isNotFinalCounter = (fieldName.contains("Integer") ||
//										fieldName.contains("Double") ||
//										fieldName.contains("Float") ||
//										fieldName.contains("Short") ||
//										fieldName.contains("Long") ||
//										fieldName.contains("Byte") ||
//										fieldName.contains("Character") ||
//										fieldName.contains("Boolean")) 
//									&& fieldName.contains("_") 
//									&& (fieldName.contains("Value") || fieldName.contains("valueOf"))
//									&& !fieldName.contains("_final");
//					
//					if(isNotFinalCounter && f.isWriter()) {
//						System.out.println("IN");
//						f.replace("{ $_ = $proceed($$); " + fieldName + "_final = " + fieldName + "; }");
//					}
//				}
//				
//			});
//		} catch (CannotCompileException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//ver a variavel z introduzida acima
		System.out.println("");
		System.out.println("FIELDS CREATED FOR INSPECTED CLASS:");
		System.out.println("as format CtMethod_Class_MethodCall[_final]");
		System.out.println("");
		for (CtField ctm : ctClass.getFields()) {
			System.out.println(ctm.getName());
			//System.out.println(ctm.getConstantValue());
		}


	}

	private void processFinalFields() {
		System.out.println("");
		System.out.println("COUNTERS INFO:");
		System.out.println("as format CtMethod_Class_MethodCall_final = value");
		System.out.println("");
		for (CtField ctm : _ctClass.getFields()) {
			if(ctm.getName().contains("_final")) {
				System.out.println(ctm.getName() + " = " + ctm.getConstantValue());
			}
		}
		
	}

	private void createField(String name) {
		
		for (CtField ctm : _ctClass.getFields()) {
			if(ctm.getName().equals(name)) {
				return;
			}
		}
		
		CtField f;
		try {
			f = CtField.make("public static int " + name + "= 0;", _ctClass);
			_ctClass.addField(f);
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
		

