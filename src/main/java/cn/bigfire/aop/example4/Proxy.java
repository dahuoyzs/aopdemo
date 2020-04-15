package cn.bigfire.aop.example4;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * @ IDE    ：IntelliJ IDEA.
 * @ Author ：dahuo
 * @ Date   ：2020/3/15  20:41
 * @ Desc   ：
 */
public class Proxy {

    //当前项目目录
    private static String userDir = System.getProperty("user.dir");
    //当前包
    private static String pack = Proxy.class.getPackage().getName();
    private static String packPath = pack.replace(".","/");
    //环境目录
    private static String envDir = "/src/main/java/";
    //当前包的绝对路径
    private static String currPackPath = userDir  + envDir + packPath;

    // 所有代理类名的前缀(模拟JDK中的命名)
    private static final String proxyClassNamePrefix = "$Proxy";
    // 下一个用于生成唯一代理类名的数字，用于生成代码类名 $Proxy  + 自增号(模拟JDK中的命名)
    private static final AtomicLong nextUniqueClassNumber = new AtomicLong();
    // 在生成代码过程中，由于会生成大量参数，命名较为繁琐，使用(arg + 自增号)命名
    private static final AtomicLong nextUniqueArgsNumber = new AtomicLong();

    /**
     * 生成代码类(仅支持接口类型)
     *
     * @param clazz   需要代理的接口类
     * @param handler 已经实现了InvocationHandler接口的实体类
     */
    public static Object newProxyInstance(Class clazz, InvocationHandler handler) throws Exception {
        //换行
        String rt = "\r\n";
        //接口名称
        String interfaceName = clazz.getName();
        //生成代码类名      $Proxy  + 自增号
        String className = proxyClassNamePrefix + nextUniqueClassNumber.getAndIncrement();
        //类所在包
        String packCode = "package " + pack + ";" + rt + rt;
        //导包代码
        String importMethod = "import java.lang.reflect.Method;" + rt + rt;
        //类的关系------------------------------------------------------------
        String relation = (clazz.isInterface() ? " implements " : " extends ") + interfaceName;
//        String relation = clazz.isInterface() ? " implements " + interfaceName : " ";
        //class类定义代码---------------------------------------------------------
        String clazzCode = "public class " + className + relation + "{" + rt;
        //属性
        String fieldCode = "    InvocationHandler handler;" + rt;
        //构造方法
        String constructorCode = "    public  " + className + "(InvocationHandler handler){" + rt;
        //构造方法内的代码
        String innerCode1 = "        this.handler = handler;" + rt;
        //构造方法结束
        String right = "    }" + rt;
        //拼接 前面的代码
        String header = packCode
                + importMethod
                + clazzCode
                + rt
                + fieldCode
                + rt
                + constructorCode
                + innerCode1
                + right
                + rt;
        //用于拼接代理类的方法代码
        StringBuilder builder = new StringBuilder(header);
        //通过反射拿到代理类接口的所有方法
        Method[] methods = clazz.getMethods();
        //便利每一个方法，并代理每一个方法
        Stream.of(methods).forEach(method -> {
            //方法名
            String methodName = typeOf(method.getName());
            int modifier = method.getModifiers();
            if (Modifier.isNative(modifier)||Modifier.isFinal(modifier)) {
                //如果是Native方法则不代理。
            } else {
                //方法返回值类型
                String returnName = typeOf(method.getReturnType().getName());
                //方法参数列表
                Class<?>[] parameterTypes = method.getParameterTypes();
                //用于拼接形参列表
                String formalArgs = "";
                //用于拼接实参列表
                String realArgs = "";
                //用于拼接实参Class类型的字符串
                String realArgsClass = "";
                //便利参数列表，并拼接出上面三个参数
                for (int i = 0; i < parameterTypes.length; i++) {
                    //参数类型
                    String parameterTypeName = typeOf(parameterTypes[i].getName());

                    //参数名 arg0,arg1,arg2,arg3  ...
                    String argName = "arg" + nextUniqueArgsNumber.getAndIncrement();
                    //形参   Object arg0,String arg1,Integer arg2  ...
                    formalArgs += (parameterTypeName + " " + argName);
                    realArgs += argName;
                    realArgsClass += parameterTypeName + ".class";
                    //如果不是最后一个都加上个","  如果最后一个就不再添加","
                    if (i != (parameterTypes.length - 1)) {
                        formalArgs += ",";
                        realArgs += ",";
                        realArgsClass += ",";
                    }
                }
                //如果参数长度为0，那么传null
                String arg = parameterTypes.length > 0 ? realArgs : "new Object()";
                //拼接方法体代码
                String methodStr = clazz.isInterface() ?   "    @Override" + rt : "";
                methodStr += ("    public " + returnName + " " + methodName + "(" + formalArgs + "){" + rt);
                methodStr += ("        try{ " + rt);
                methodStr += ("            Method method = " + interfaceName + ".class.getMethod(\"" + methodName + "\"");
                //根据参数长度，确定是否添加参数
                if (parameterTypes.length > 0) {
                    methodStr += ("," + realArgsClass);
                }
                methodStr += (");" + rt);
                //返回值 如果为"void"，则生成没有返回值的方法调用
                if (returnName.equals("void")) {
                    methodStr += ("            handler.invoke(method," + arg + ");" + rt);
                } else {
                    methodStr += ("            return (" + returnName + ")handler.invoke(method," + arg + ");" + rt);
                }
//            System.out.println(methodStr);
                //捕获所有异常，转换成RuntimeException异常
                methodStr += ("        }catch (Throwable e){e.printStackTrace();throw new RuntimeException(e.getMessage());}" + rt + right + rt);
                //代理对象的一个方法代码拼接完成，与原有代码拼接在一起
                builder.append(methodStr);
            }
        });
        builder.append("}");
        //代理类的所有代码拼接完成
        String code = builder.toString();

        String javaFileName = userDir + envDir + packPath + "/" + className + ".java";
        File javaFile = new File(javaFileName);
        String classFileName = userDir + envDir + packPath + "/" + className + ".class";
        File classFile = new File(classFileName);

        //Java源文件源代码写入文件
        FileWriter fileWriter = new FileWriter(new File(javaFileName));
        fileWriter.write(code);
        fileWriter.close();

        //运行时编译器  编译代理类的Java源代码代码->class文件
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjects(javaFileName);
        JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, null, null, null, javaFileObjects);
        task.call();
        fileManager.close();

        //使用自定义ClassLoader加载Class类
        String packet = pack + "." + className;
        MyClassLoader myClassLoader = new MyClassLoader(classFileName);
        Class<?> targetProxy = myClassLoader.findClass(packet);

        //使用反射创建代理类对象
        Constructor constructor = targetProxy.getConstructor(InvocationHandler.class);
        Object object = constructor.newInstance(handler);
//        $Proxy0 proxy = new $Proxy0();

        //删除操作时生成的Java源代码和Class文件
        if (javaFile.exists()) javaFile.delete();
//        if (classFile.exists())classFile.delete();
        return object;
    }

    public static String typeOf(String className) {
        //如果是数组类型，则转换
        if (className.startsWith("[")) {
            //如果是对象数组
            if (className.startsWith("[L"))
                return className.substring(2, className.length() - 1) + "[]";
            //如果是Java8种基础类型
            if (className.startsWith("[S"))
                return "short[]";
            if (className.startsWith("[B"))
                return "byte[]";
            if (className.startsWith("[C"))
                return "char[]";
            if (className.startsWith("I"))
                return "int[]";
            if (className.startsWith("[J"))
                return "long[]";
            if (className.startsWith("[F"))
                return "float[]";
            if (className.startsWith("[D"))
                return "double[]";
            if (className.startsWith("[Z"))
                return "boolean[]";
        }
        //否则普通类型，不做转换
        return className;
    }

    public static void main(String[] args) throws Exception {
//        ArrayList list = (ArrayList) newProxyInstance(ArrayList.class,new Log(new ArrayList<>()));
//        list.add("a");
        ArrayList arrayList = (ArrayList) newProxyInstance(ArrayList.class,new Log(new ArrayList<>()));
        arrayList.add("b");
//        Integer integer = (Integer) newProxyInstance(Integer.class,new Log(new Integer(10)));
//        integer.compareTo(5);
    }
}
