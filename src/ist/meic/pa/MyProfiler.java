package ist.meic.pa;
import javassist.*;
public class MyProfiler implements  Translator {


	@Override
	public void onLoad(ClassPool pool, String ClassName) throws NotFoundException,
			CannotCompileException {
		CtClass ctclass= pool.get(ClassName);
		
	}

	@Override
	public void start(ClassPool pool) throws NotFoundException,
			CannotCompileException {
		// do nothing 
		
	}


}
