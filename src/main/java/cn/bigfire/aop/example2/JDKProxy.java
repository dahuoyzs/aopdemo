package cn.bigfire.aop.example2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @IDE ：IntelliJ IDEA.
 * @Author ：dahuo
 * @Date ：2020/3/28  22:59
 * @Desc ：
 */
public class JDKProxy {

    public static void main(String[] args) throws Exception {

        List<String> aoplist = (List) Proxy.newProxyInstance(null, new Class[]{List.class}, new Log(new ArrayList<String>()));
        aoplist.add("1");
        aoplist.add("2");
        aoplist.add("3");
        aoplist.remove("3");

    }

    static class Log implements InvocationHandler {

        Object target;//要代理的对象

        public Log(Object obj){
            this.target=obj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //打印方法名
            System.out.print(method.getName()+":");
            //打印参数
            for (Object object : args) {
                System.out.print(object);
            }
            //换行
            System.out.println();
            //调用原对象的方法
            Object object=method.invoke(target, args);
            //这里也可以在方法调用完之后插入一些逻辑
            return object;
        }

    }
}
