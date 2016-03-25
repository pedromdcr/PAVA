package ist.meic.pa;

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
	public void start(ClassPool pool) 
			throws NotFoundException, CannotCompileException {
	}

	public void onLoad(ClassPool pool, String className)
			throws NotFoundException, CannotCompileException {
		CtClass ctClass = pool.get(className);
		make(pool,ctClass);
	}

	void make(ClassPool pool, CtClass ctClass)
			throws NotFoundException, CannotCompileException {
		System.out.println("---------\n\tMake\n");

		// tentar enfiar int z para a class..

		CtField f = new CtField(CtClass.intType, "z", ctClass);
		ctClass.addField(f, "0");

	
		//ver metodos
		for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			ctMethod.instrument(new ExprEditor() {
				public void edit(MethodCall m)
						throws CannotCompileException {


					if(m.getClassName().equals("java.lang.Integer")||
							m.getClassName().equals("java.lang.Long")||
							m.getClassName().equals("java.lang.Short")||
							m.getClassName().equals("java.lang.Byte")){
						if(m.getMethodName().equals("valueOf")){
							System.out.println("Class Name:\t"+m.getClassName());
							System.out.println("Method Name:\t"+m.getMethodName());
							System.out.println("Line Number:\t"+m.getLineNumber());
							System.out.println("");

						}
					}
				



				}

			});
		}

		//ver a variavel z introduzida acima
		System.out.println("PRINT FIELDS");
		for (CtField ctm : ctClass.getFields()) {
			System.out.println(ctm.getName());
			System.out.println(ctm.getFieldInfo().getName());
		}


	}

}
