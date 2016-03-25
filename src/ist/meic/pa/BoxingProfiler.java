package ist.meic.pa;

import javassist.ClassPool;
import javassist.Loader;
import javassist.Translator;


public class BoxingProfiler {

	public static void main(String[] args) {
		try{
			Translator  translator = new MyProfiler();
			ClassPool pool = ClassPool.getDefault();
			Loader classLoader = new Loader();
			classLoader.addTranslator(pool,translator);
			String[] restArgs = new String[args.length - 1];
			System.arraycopy(args, 1, restArgs, 0, restArgs.length);
			classLoader.run(args[0], restArgs);
		} catch (Throwable e) {
			
		}

	}

}
