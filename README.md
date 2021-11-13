# javafx_sbt_minimal

This is a minimal example of a bug I ran into while trying to build a working GraalVM native image of a Scala app with 
JavaFX, sbt, and the `org.scalameta.sbt-native-image` plugin, plus a bit of additional code to make it easy to compare it with what 
happens when we build a native image with the same JavaFX, but with Maven, and the `com.gluonhq.gluonfx-maven-plugin` plugin.

### Setup

- OS: Linux Mint 20.1
- Arch: x86_64
- JDK: GraalVM CE 21.1.0, openjdk version 11.0.13 (JavaFX does not work yet with Java 17)
- Scala: 2.13.7
- SBT: 1.5.5
- JavaFX: 16 (JavaFX 17 does not work yet with SBT because of [this issue](https://github.com/sbt/sbt/issues/6564))
- sbt-native-image: 0.3.1

### Common ground

If you type `sbt run` from the console, it should download JavaFX dependencies, compile the app, and run it.
A small window will open with a text "Hello from Scala!" in it.

If you want to do the same with Maven, make the changes described in comments in `minimalexample.Main.scala` and then
run `mvn javafx:run`. The reason for the changes is that SBT and Maven look for the main class in a bit different way.
SBT gets confused if the class extending the JavaFX `Application` class has the same name as the object which extends
Scala `App`, but Maven used with `scala-maven-plugin` actually expects that. Something to do with Java 9 modules.
Don't ask.

### Native image

Run `sbt nativeImage` and `sbt nativeImageRun`. The native image will be compiled (this part works) and then it will 
crash at initialization with `java.lang.NoClassDefFoundError: Ljava/nio/ByteBuffer`:
```
sbt:javafx_sbt_minimal> nativeImageRun
Nov 13, 2021 5:16:01 PM com.sun.javafx.application.PlatformImpl startup
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @574a89e2'
Exception in thread "main" java.lang.RuntimeException: java.lang.NoClassDefFoundError: Ljava/nio/ByteBuffer;
  | => nat com.sun.javafx.tk.quantum.QuantumToolkit.startup(QuantumToolkit.java:290)
        at com.sun.javafx.application.PlatformImpl.startup(PlatformImpl.java:288)
	at com.sun.javafx.application.PlatformImpl.startup(PlatformImpl.java:160)
	at com.sun.javafx.application.LauncherImpl.startToolkit(LauncherImpl.java:658)
	at com.sun.javafx.application.LauncherImpl.launchApplication1(LauncherImpl.java:678)
	at com.sun.javafx.application.LauncherImpl.lambda$launchApplication$2(LauncherImpl.java:195)
	at java.lang.Thread.run(Thread.java:829)
	at com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:596)
	at com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:192)
Caused by: java.lang.NoClassDefFoundError: Ljava/nio/ByteBuffer;
	at com.oracle.svm.jni.functions.JNIFunctions.FindClass(JNIFunctions.java:346)
	at com.oracle.svm.jni.JNIOnLoadFunctionPointer.invoke(JNILibraryInitializer.java)
	at com.oracle.svm.jni.JNILibraryInitializer.callOnLoadFunction(JNILibraryInitializer.java:72)
	at com.oracle.svm.jni.JNILibraryInitializer.initialize(JNILibraryInitializer.java:129)
	at com.oracle.svm.core.jdk.NativeLibrarySupport.addLibrary(NativeLibrarySupport.java:186)
	at com.oracle.svm.core.jdk.NativeLibrarySupport.loadLibrary0(NativeLibrarySupport.java:142)
	at com.oracle.svm.core.jdk.NativeLibrarySupport.loadLibraryAbsolute(NativeLibrarySupport.java:101)
	at java.lang.ClassLoader.loadLibrary(ClassLoader.java:131)
	at java.lang.Runtime.load0(Runtime.java:768)
	at java.lang.System.load(System.java:1835)
	at com.sun.glass.utils.NativeLibLoader.installLibraryFromResource(NativeLibLoader.java:214)
	at com.sun.glass.utils.NativeLibLoader.loadLibraryFromResource(NativeLibLoader.java:194)
	at com.sun.glass.utils.NativeLibLoader.loadLibraryInternal(NativeLibLoader.java:135)
	at com.sun.glass.utils.NativeLibLoader.loadLibrary(NativeLibLoader.java:53)
	at com.sun.glass.ui.gtk.GtkApplication.lambda$new$6(GtkApplication.java:187)
	at java.security.AccessController.doPrivileged(AccessController.java:87)
	at com.sun.glass.ui.gtk.GtkApplication.<init>(GtkApplication.java:171)
	at com.sun.glass.ui.gtk.GtkPlatformFactory.createApplication(GtkPlatformFactory.java:41)
	at com.sun.glass.ui.Application.run(Application.java:144)
	at com.sun.javafx.tk.quantum.QuantumToolkit.startup(QuantumToolkit.java:280)
	... 8 more
```

Please note that `java.nio.ByteBuffer` is already present in `src/main/resources/jni-config.json` and `reflect-config.json`. 
I tried to modify those files, as well as `predefined-classes-config.json` a bit to put the whole `ByteBuffer` class there,
not just some of its methods, but to no avail. The error is exactly the same.

The JSON files in `src/main/resources` were generated with `nativeImageRunAgent` and copied from `target/native-image-configs`.

I know problems this kind of problems with `ByteBuffer` occur sometimes because of a difference between Java version used
for compilation and for running the app but I don't know how it can apply here - both compilation and execution are on
Java 11, and besides all works well with the standard JDK app, only the native image fails.

### Maven version for comparison

Clean the project, make changes in the `Main` class, and then run `mvn gluonfx:build gluonfx:nativerun`. The app works.
Also you can check that the executable file is much bigger (but that doesn't really need to mean anything).