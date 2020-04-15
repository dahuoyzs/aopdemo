package cn.bigfire.aop.example4;


import java.lang.reflect.Method;

/**
 * @IDE ：IntelliJ IDEA.
 * @Author ：dahuo
 * @Date ：2020/3/22  22:02
 * @Desc ：
 */
public class Log implements InvocationHandler {

    Object target;//要代理的对象

    public Log(Object obj){
        this.target=obj;
    }

    @Override
    public Object invoke(Method method, Object[] args) throws Throwable {
        //打印方法名
        System.out.print(method.getName()+":");
        //打印参数
        for (Object object : args) {
            System.out.print(object);
        }
        //换行
        System.out.println();
        //调用原对象的方法
        Object o = method.invoke(target, args);
        //这里也可以在方法调用完之后插入一些逻辑
        return o;
    }

}
