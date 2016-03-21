package ist.meic.pa;
import javassist.*;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.analysis.ControlFlow;
import javassist.bytecode.analysis.ControlFlow.Block;
import javassist.bytecode.analysis.FramePrinter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Throwable;
import java.util.Arrays;
public class MyProfiler implements  Translator {


	@Override
	public void onLoad(ClassPool pool, String ClassName) throws NotFoundException,
			CannotCompileException {
		CtClass ctclass= pool.get(ClassName);
		addprofile(pool,ctclass);
	}

	@Override
	public void start(ClassPool pool) throws NotFoundException,
			CannotCompileException {
		// do nothing 
		
	}

	public void addprofile(ClassPool pool, final CtClass ctClass){
		if (!ctClass.getName().equals("ist.meic.pa.BoxingPorfiler")
				&& !ctClass.getName().contains("javassist")) {
			for(CtMethod method : ctClass.getDeclaredMethods()){
				
				/*PrintStream ps = new PrintStream(System.out, true);
				FramePrinter fp = new FramePrinter(ps);
				fp.print(method);
				System.out.println(method.getLongName() + " boxed " + printCounter(method) + " " + ;*/
				printCounter(method);
			}
		}
		
	}

	private String printCounter(CtMethod method) {
		// TODO Auto-generated method stub
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		PrintStream ps = new PrintStream(baos, true);
		FramePrinter fp = new FramePrinter(ps);
		fp.print(method);
		String s = new String();
		try {
			s = new String(baos.toByteArray(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("I'm string s:");
		System.out.println(s);
		
		return null;
	}
}
