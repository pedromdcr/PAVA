package ist.meic.pa;
import javassist.*;
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
				&& !ctClass.getName().contains("javassist")) {}
		
	}
}
