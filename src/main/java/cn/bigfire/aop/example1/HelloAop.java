package cn.bigfire.aop.example1;

import org.junit.Test;
import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @IDE ：IntelliJ IDEA.
 * @Author ：dahuo
 * @Date ：2020/3/22  20:24
 * @Desc ：
 */
public class HelloAop {

    public static void main(String[] args) throws Throwable{
        //原来写法
        List<String> list = new ArrayList<>();
        list.add("a");
        System.out.println("add:a");
        list.add("b");
        System.out.println("add:b");
        list.add("c");
        System.out.println("add:c");
        list.remove("c");
        System.out.println("remove:c");

        //经过AOP处理的list
        List<String> aoplist = (List)Proxy.newProxyInstance(null, new Class[]{List.class}, new Log(list));
        aoplist.add("1");
        aoplist.add("2");
        aoplist.add("3");
        aoplist.remove("3");

        System.out.println(aoplist.equals(list));
        //true说明还是原来的list对象，只是把原来的对象进行了增强
    }

}
