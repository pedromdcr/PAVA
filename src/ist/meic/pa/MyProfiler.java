package ist.meic.pa;
import javassist.*;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.analysis.ControlFlow;
import javassist.bytecode.analysis.ControlFlow.Block;
import javassist.bytecode.analysis.FramePrinter;
import java.io.PrintStream;
import java.io.PrintWriter;
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
				//System.out.println("vinho verde");
				PrintStream ps = new PrintStream(System.out, true);
				FramePrinter fp = new FramePrinter(ps);
				fp.print(method);
				
			}
		}
		
	}
}
