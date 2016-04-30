
public class DoDJNI {
	static {
		System.load(System.getProperty("user.dir")+"/jni/libhello.jnilib");
	}
	
	public native void sayHello();
	
	public static void main(String[] args) {
		new DoDJNI().sayHello();
	}
}
