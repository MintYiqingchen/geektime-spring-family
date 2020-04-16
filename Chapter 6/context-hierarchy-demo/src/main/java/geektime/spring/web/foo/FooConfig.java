package geektime.spring.web.foo;

import geektime.spring.web.context.TestBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class FooConfig {
    @Bean
    public TestBean testBeanX() {
        return new TestBean("foo");
    }

    @Bean
    public TestBean testBeanY() {
        return new TestBean("foo");
    }

    @Bean
    public FooAspect fooAspect() {
        return new FooAspect();
    }
}
/*
Glossory

FooConfig.java：父上下文（parent application context）。
applicationContext.xml：子上下文（child application context）。

FooConfig.java 中定义两个 testBean，分别为 testBeanX(foo) 和 testBeanY(foo)。
applicationContext.xml 定义了一个 testBeanX(bar)。

委托机制：在自己的 context 中找不到 bean，会委托父 context 查找该 bean。

----------

代码解释：
fooContext.getBean("testBeanX")，在父上下文查找 testBeanX，命中直接返回 testBeanX(foo)。
barContext.getBean("testBeanX")，在子上下文查找 testBeanX，命中直接返回 testBeanX(bar)。
barContext.getBean("testBeanY")，在子上下文查找 testBeanY，未命中；委托父上下文查找，命中，返回 testBeanY(foo)。

----------

场景一：
父上下文开启 @EnableAspectJAutoProxy 的支持
子上下文未开启 <aop: aspectj-autoproxy />
切面 fooAspect 在 FooConfig.java 定义（父上下文增强）

输出结果：
testBeanX(foo) 和 testBeanY(foo) 均被增强。
testBeanX(bar) 未被增强。

结论：
在父上下文开启了增强，父的 bean 均被增强，而子的 bean 未被增强。

----------

场景二：
父上下文开启 @EnableAspectJAutoProxy 的支持
子上下文开启 <aop: aspectj-autoproxy />
切面 fooAspect 在 applicationContext.xml 定义（子上下文增加）

输出结果：
testBeanX(foo) 和 testBeanY(foo) 未被增强。
testBeanX(bar) 被增强。

结论：
在子上下文开启增强，父的 bean 未被增强，子的 bean 被增强。

----------

根据场景一和场景二的结果，有结论：“各个 context 相互独立，每个 context 的 aop 增强只对本 context 的 bean 生效”。如果想将切面配置成通用的，对父和子上下文的 bean 均支持增强，则：
1. 切面 fooAspect 定义在父上下文。
2. 父上下文和子上下文，均要开启 aop 的增加，即 @EnableAspectJAutoProxy 或<aop: aspectj-autoproxy /> 的支持。
 */