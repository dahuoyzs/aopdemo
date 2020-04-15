package cn.bigfire.aop.example5;

import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.proxy.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @IDE ：IntelliJ IDEA.
 * @Author ：dahuo
 * @Date ：2020/3/29  17:59
 * @Desc ：
 */
public class CglibAop {
    //当前项目目录
    private static String userDir = System.getProperty("user.dir");
    //当前包
    private static String pack = CglibAop.class.getPackage().getName();
    private static String packPath = pack.replace(".","/");
    //环境目录
    private static String envDir = "/src/main/java/";
    //当前包的绝对路径
    private static String currPackPath = userDir  + envDir + packPath;

    public static void main(String[] args) throws Exception {
        //运行时将生成的class文件保存到当前目录下
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, currPackPath);
//        ArrayList<String> aoplist = (ArrayList) new Log(new ArrayList<>()).newProxyInstance();
//        aoplist.add("1");
//        aoplist.add("2");
//        aoplist.add("3");
//        aoplist.remove("3");

//        Integer five = (Integer) new Log(new Integer(5)).newProxyInstance();
//        five.compareTo(10);

//        Person aopPerson = (Person) new Log(new Person()).newProxyInstance();
//        aopPerson.say();

//        List list = (List) Proxy.newProxyInstance(null,new Class[]{List.class},new LogList(new ArrayList<>()));
//        list.add("a");

        Person aopPerson = (Person) new Log(new Person()).newProxyInstance();
        aopPerson.say();

    }

    static class LogList implements InvocationHandler {
        Object target;//要代理的对象

        public LogList(Object obj){
            this.target=obj;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            //打印方法名
            System.out.print(method.getName()+":");
            //打印参数
            for (Object object : objects) {
                System.out.print(object);
            }
            //换行
            System.out.println();
            //调用原对象的方法
            Object object=method.invoke(target, objects);
            //这里也可以在方法调用完之后插入一些逻辑
            return object;
//            return null;
        }
    }
    static class Log implements MethodInterceptor {

        Object target;//要代理的对象

        public Log(Object obj){
            this.target=obj;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            //打印方法名
            System.out.print(method.getName()+":");
            //打印参数
            for (Object object : objects) {
                System.out.print(object);
            }
            //换行
            System.out.println();
            //调用原对象的方法
            Object object=method.invoke(target, objects);
            //这里也可以在方法调用完之后插入一些逻辑
            return object;
        }

        public Object newProxyInstance(){
            Enhancer enhancer = new Enhancer();
            //设置父类,因为Cglib是针对指定的类生成一个子类，所以需要指定父类
            enhancer.setSuperclass(target.getClass());
            enhancer.setCallback(this);// 设置回调
            Object result = enhancer.create();//创建并返回代理对象
            return result;
        }
    }
}
