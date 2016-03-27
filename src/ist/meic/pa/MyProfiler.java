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

	public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
	}

	public void onLoad(ClassPool pool, String className) throws NotFoundException, CannotCompileException {
		_ctClass = pool.get(className);
		make(pool);
	}

	protected void make(ClassPool pool) throws NotFoundException, CannotCompileException {
		//System.out.println("-> MyProfiler.make");

		for (CtMethod ctMethod : _ctClass.getDeclaredMethods()) {
			names = new ArrayList<String>();
			final String prefix = ctMethod.getName(); 
			// para distinguir metodos
			// com o mesmo nome e
			// assinaturas
			// diferentes

//			System.out.println("> Method Name:\t" + ctMethod.getName() + "\n");
			CtField treeMapField = CtField.make("public java.util.TreeMap treemap_"+ctMethod.getName()+";", _ctClass);
			_ctClass.addField(treeMapField);
			ctMethod.instrument(new ExprEditor() {
				public void edit(MethodCall m) throws CannotCompileException {
					//					System.out.println("Class Name:\t" + m.getClassName());
					//					System.out.println("Method Name:\t" + m.getMethodName());
					//	System.out.println("Line Number:\t" + m.getLineNumber());
					//	tokens.length == 3
					//	&& (m.getClassName().contains("java.lang.") || m.getMethodName().contains("alue"))
					if (checkClass(m.getClassName())) {
						//
						// Eg. Class Name:	java.lang.Boolean
						// token[2] -> Boolean
						//
						//	System.out.println(tokens[2]);
						//						Nao da para ter variaveis com . nos nomes!
						String[] tokens = m.getClassName().split("\\.");
						String name = prefix + "_" + tokens[2] + "_" + m.getMethodName();
						names.add(name); // adicionar name ao ARRAYLIST
						//	System.out.println("New Variable Name:\t" + name);
						// cria um contador nao final (ou seja, que pode ser
						// incrementado) com o nome especificado
//						
						//COMENTEI ISTO PORQUE A PARTIR DE AGORA USAM SE TREEMAPS
//						createField(name,ctMethod.getName());
						
						//ESTAVA A TENTAR METER VALORES DENTRO DA TREEMAP MAS ELE PEDE QUE O CTMETHOD SEJA FINAL
						// SE FOR FINAL NAO POSSO ALTERAR A TREEMAP
						//.....EVERYTHING IS FUCKED
						//O CODIGO FUNCIONARIA EM TEORIA SE CONSEGUISSESMOS PASSAR BEM O NOME DA TREEMAP PARA AQUI EM BAIXO
					
//						m.replace("{ $_ = $proceed($$);"
//								+"if(treemap_"+ctMethod.getName()+".containsKey("+name+")){"
//								+"treemap_"+ctMethod.getName()+".put("+name+",treemap_"+ctMethod.getName()+".get("+name+") + 1);"
//								+"} else{ treemap_" + ctMethod.getName() +".put("+name+",1);}}");
//					
					}
					}
			});

//			for(CtField c : _ctClass.getFields()){
//				System.out.println("find Tree : "+c.getName());
//			}
//			processFinalFields(ctMethod);
		}
	}

	protected void processFinalFields(CtMethod ctMethod) throws CannotCompileException {
		for (CtField ctm : _ctClass.getFields()) {
			if (ctm.getName().contains(ctMethod.getName())) {
				String[] tokens = ctm.getName().split("_");
				String classname = ctMethod.getLongName();
				String type = null;
				String varType = " java.lang."+tokens[1];
				String aspa = "\"";
				String plus = "+";
				if (ctm.getName().matches("(.*)Value")) {
					type = " unboxed ";
				}
				else if (ctm.getName().matches("(.*)valueOf")) {
					type = " boxed ";
				}
				//Printer / String formater
				String all = "{System.out.println("+
						aspa+classname+aspa+plus+
						aspa+type+aspa+plus+
						ctm.getName()+plus+
						aspa+varType+aspa+");}";

				ctMethod.insertAfter(all);
			}
		}
	}

	protected void createField(String name, String methodName) throws CannotCompileException {
//		for (CtField ctm : _ctClass.getFields())
//			if (ctm.getName().equals(name)) {
//				return;
//			}
//		CtField f = CtField.make("public static int " + name + "= 0;", _ctClass);
//		_ctClass.addField(f);
//		
	}

	protected boolean checkClass(String className){
		switch (className) {
		case "java.lang.Integer":
		case "java.lang.Double":
		case "java.lang.Float":
		case "java.lang.Long":
		case "java.lang.Short":
		case "java.lang.Character":
		case "java.lang.Byte":
		case "java.lang.Boolean":
			return true;
		default:
			return false;
		}
	}
}
