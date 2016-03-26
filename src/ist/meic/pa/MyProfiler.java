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

	
	void make(ClassPool pool, CtClass ctClass)
			throws NotFoundException, CannotCompileException {
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
		for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			names = new ArrayList<String>();
			System.out.println("------Name:\t"+ctMethod.getName());
			final String prefix = ctMethod.getName(); // para distinguir metodos com o mesmo nome e assinaturas diferentes
			ctMethod.instrument(new ExprEditor() {
				public void edit(MethodCall m) throws CannotCompileException {
					System.out.println("Class Name:\t"+ m.getClassName());
					System.out.println("Method Name:\t"+ m.getMethodName());
					System.out.println("Line Number:\t"+ m.getLineNumber());
					String[] tokens = m.getClassName().split("\\."); 
					if(tokens.length == 3 && (m.getClassName().contains("java.lang.") || m.getMethodName().contains("alue"))) {
						String name = prefix + "_" + tokens[2] + "_" + m.getMethodName();
						names.add(name);
						
						System.out.println(">>>>>>NAME: " + name);
						
						
						//cria um contador não final (ou seja, que pode ser incrementado) com o nome especificado
						createField(name);
						
						m.replace("{ $_ = $proceed($$); " + name + "++; }");
						
					}
					System.out.println("");
					
				}

			});
			
//			for (String s : names) {
//				CtField f = CtField.make("public static final int " + s + "_final = " + s + ";", _ctClass);
//				_ctClass.addField(f);
//			}
			
			//processFinalFields();
		}

		//ver a variavel z introduzida acima
		System.out.println("PRINT FIELDS");
		for (CtField ctm : ctClass.getFields()) {
			System.out.println(ctm.getName());
			//System.out.println(ctm.getFieldInfo().getName());
			//System.out.println(ctm.getConstantValue());
		}


	}

	private void processFinalFields() {
		
		for (CtField ctm : _ctClass.getFields()) {
			if(ctm.getName().contains("_final")) {
				System.out.println("----->COUNTER NAME: " + ctm.getName() + " = " + ctm.getConstantValue());
			}
		}
		
	}

	protected void createField(String name) throws CannotCompileException {
		
		for (CtField ctm : _ctClass.getFields()) {
			if(ctm.getName().equals(name)) {
				return;
			}
		}
		
		CtField f = CtField.make("public int " + name + "= 0;", _ctClass);
		_ctClass.addField(f);
		
	}
	
}
		

