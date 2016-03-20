package ist.meic.pa;



import javassist.CannotCompileException;
import javassist.NotFoundException;
import javassist.Loader;
import javassist.ClassPool;



public class BoxingProfiler {

	public static void main(String[] args) {
		try {
			MyProfiler profiler = new MyProfiler();			
			ClassPool pool= ClassPool.getDefault();
			Loader classloader = new Loader(pool);
			classloader.addTranslator(pool, profiler);
			String[] restargs= new String[args.length-2];
			System.arraycopy(args, 1, restargs, 0, restargs.length);
			classloader.run(args[0], restargs);
		} 
		
		
		
		catch (NotFoundException e) {
			e.printStackTrace();
		} 
		catch (CannotCompileException e) {
			e.printStackTrace();
		} 
		catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

}
