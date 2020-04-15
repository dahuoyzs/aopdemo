package cn.bigfire.aop.example3;

import cn.bigfire.aop.example1.Log;
import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @IDE ：IntelliJ IDEA.
 * @Author ：dahuo
 * @Date ：2020/3/22  20:24
 * @Desc ：
 */
public class JDKAop {

    //当前项目目录
    private static String userDir = System.getProperty("user.dir");
    //当前包
    private static String pack = JDKAop.class.getPackage().getName();
    private static String packPath = pack.replace(".","/");
    //环境目录
    private static String envDir = "/src/main/java/";
    //当前包的绝对路径
    private static String currPackPath = userDir  + envDir + packPath;

    public static void main(String[] args)throws Throwable {

        List<String> aoplist = (List)Proxy.newProxyInstance(null, new Class[]{List.class}, new Log(new ArrayList<>()));

        byte[] classFile = ProxyGenerator.generateProxyClass("$Proxy0", new Class[]{List.class});
        FileOutputStream fos = new FileOutputStream(currPackPath + "/$Proxy0.class");
        fos.write(classFile);
        fos.flush();

    }

}
