package cn.bigfire.aop.example4;

import java.lang.reflect.Method;

/**
 * @ IDE    ：IntelliJ IDEA.
 * @ Author ：dahuo
 * @ Date   ：2020/3/13  19:12
 * @ Desc   ：
 */
public interface InvocationHandler {

    Object invoke(Method method, Object... args)throws Throwable;
}
