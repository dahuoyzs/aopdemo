package cn.bigfire.aop.example2;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @ IDE    ：IntelliJ IDEA.
 * @ Author ：dahuo
 * @ Date   ：2020/3/19  19:53
 * @ Desc   ：
 */
public class CglibProxy{

    public static void main(String[] args) throws Exception {

        ArrayList<String> aoplist = (ArrayList) new Log(new ArrayList<>()).newProxyInstance();
        aoplist.add("1");
        aoplist.add("2");
        aoplist.add("3");
        aoplist.remove("3");

    }

    static class Log implements MethodInterceptor{

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
