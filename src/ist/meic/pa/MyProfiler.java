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

		make(pool, _ctClass);
	}

	void make(ClassPool pool, CtClass ctClass) throws NotFoundException, CannotCompileException {
		//System.out.println("-> MyProfiler.make");

		for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			names = new ArrayList<String>();
			final String prefix = ctMethod.getName(); // para distinguir metodos
														// com o mesmo nome e
														// assinaturas
														// diferentes

		//	System.out.println("> Method Name:\t" + ctMethod.getName() + "\n");

			ctMethod.instrument(new ExprEditor() {
				public void edit(MethodCall m) throws CannotCompileException {
				//	System.out.println("Class Name:\t" + m.getClassName());
				//	System.out.println("Method Name:\t" + m.getMethodName());
				//	System.out.println("Line Number:\t" + m.getLineNumber());

					String[] tokens = m.getClassName().split("\\.");
					if (tokens.length == 3
							&& (m.getClassName().contains("java.lang.") || m.getMethodName().contains("alue"))) {
						//System.out.println(tokens[2]);
						String name = prefix + "_" + tokens[2] + "_" + m.getMethodName();
						names.add(name); // adicionar name ao ARRAYLIST
					//	System.out.println("New Variable Name:\t" + name);
						// cria um contador não final (ou seja, que pode ser
						// incrementado) com o nome especificado
						createField(name);
						m.replace("{ $_ = $proceed($$); " + name + "++;  }");
					}
				//	System.out.println("");
					
				}
			});
			
		
			
			processFinalFields(ctMethod);
		}

	}
	// }

	private void processFinalFields(CtMethod ctMethod) throws CannotCompileException {

		for (CtField ctm : _ctClass.getFields()) {
			if (ctm.getName().contains(ctMethod.getName())) {
				String[] tokens = ctm.getName().split("_");
				String type=new String();
			
				if(ctm.getName().matches("(.*)Value")){
					type=" boxed ";
				}
				else if(ctm.getName().matches("(.*)valueOf")){type=" unboxed ";
				}
				String classname = ctMethod.getLongName();
				
				
				
				String varType = " java.lang."+tokens[1];
				
				
				String aspa = "\"";
				String space=" "; 
				String plus = "+";
				String all = "{System.out.println("+aspa+classname+aspa+plus+
													aspa+type+aspa+plus+
													ctm.getName()+plus+
													aspa+varType+aspa+");}";
				
				//System.out.println(all);
				
				ctMethod.insertAfter(all);
			}

		}


	}

	protected void createField(String name) throws CannotCompileException {
		for (CtField ctm : _ctClass.getFields())
			if (ctm.getName().equals(name)) {
				return;
			}
		CtField f = CtField.make("public static int " + name + "= 0;", _ctClass);
		_ctClass.addField(f);
	}

}
