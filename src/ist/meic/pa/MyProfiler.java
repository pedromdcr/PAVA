package ist.meic.pa;

//import java.util.ArrayList;
//import java.util.List;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class MyProfiler implements Translator {
	private CtClass _ctClass;
	// private List<String> names;
	public TreeMap<Long, String> dictionary = new TreeMap<Long, String>();

	public void start(ClassPool pool) throws NotFoundException,
			CannotCompileException {
	}

	public void onLoad(ClassPool pool, String className)
			throws NotFoundException, CannotCompileException {
		_ctClass = pool.get(className);
		make(pool);
	}

	Long globalID = 0L;

	protected void make(ClassPool pool) throws NotFoundException,
			CannotCompileException {
		for (CtMethod ctMethod : _ctClass.getDeclaredMethods()) {

			// names = new ArrayList<String>();
			final String id = "BoxingProfiler";
			final String prefix = ctMethod.getLongName();
			// para distinguir metodos com o mesmo nome e assinaturas diferentes
			ctMethod.instrument(new ExprEditor() {
				public void edit(MethodCall m) throws CannotCompileException {
					if (checkClass(m.getMethodName())) {
						String[] tokens = m.getClassName().split("\\.");
						String name = id + "_" + m.getMethodName() + "_"
								+ tokens[2] + "_" + prefix;
						String injection = null;
						if (!dictionary.containsValue(name)) {
							dictionary.put(globalID, name);
							globalID = globalID + 1;
							injection = id + "_" + globalID;
							
						} else {
							for (Map.Entry<Long, String> entry : dictionary.entrySet()) {
								if(entry.getValue().equals(name)) {
									injection = id + "_" + entry.getKey();
								} 
								
							}
						}

						createField(injection);

						// System.out.println(globalID);

						// System.out.println("New Variable Name:\t" + name);
						// System.out.println("to string: "+globalID.toString());

					//	 System.out.println(injection);
						m.replace("{ $_ = $proceed($$);" + injection + "++;}");
					}
				}
			});

		}
		processFinalFields();
	}

	protected void processFinalFields() throws CannotCompileException,
			NotFoundException {
		TreeMap<String, String> storage = new TreeMap<String, String>();
		for (CtField ctm : _ctClass.getFields()) {

			if (ctm.getName().contains("BoxingProfiler")) {

				String[] tokens = ctm.getName().split("_");
				Long _NUM = Long.parseLong(tokens[1]);
				//System.out.println(ctm.getName());
				String truetype = null;
				if (dictionary.containsKey(_NUM)) {
					truetype = dictionary.get(_NUM);
					String[] tokenTrue = truetype.split("_");
					// System.out.println("TOKEN TRUE :" +tokenTrue[3]);
					
					String classname = tokenTrue[3];
					
					String type = null;
					String varType = " java.lang." + tokenTrue[2];
					String aspa = "\"";
					String plus = "+";
					if (tokenTrue[1].matches("(.*)Value")) {
						type = " unboxed ";
					} else if (tokenTrue[1].matches("(.*)valueOf")) {
						type = " boxed ";
					}
					// Printer / String formater
					String all = "{System.err.println(" + aspa + classname + aspa
							+ plus + aspa + type + aspa + plus + ctm.getName()
							+ plus + aspa + varType + aspa + ");}";
					
					String key = classname + " " + varType + " " + type;
					storage.put(key, all);
//					System.out.println(truetype);
				} 
			}
		}
		CtMethod main = _ctClass.getDeclaredMethod("main");
		for (Map.Entry<String, String> entry : storage.entrySet()) {
			main.insertAfter(entry.getValue());
		}
	}

	protected void createField(String var) throws CannotCompileException {
		for (CtField ctm : _ctClass.getFields())
			if (ctm.getName().equals(var)) {
				return;
			}

		CtField f = CtField.make("public static int " + var + "= 0;", _ctClass);
		_ctClass.addField(f);
	}

	protected boolean checkClass(String className) {
		// System.out.println(className);
		if (className.matches("(.*)Value") || className.matches("(.*)valueOf")) {
			return true;
		} else
			return false;
	}
}
