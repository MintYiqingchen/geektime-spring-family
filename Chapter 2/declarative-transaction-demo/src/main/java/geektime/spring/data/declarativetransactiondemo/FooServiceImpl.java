package geektime.spring.data.declarativetransactiondemo;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
// 在类上加@Transacational注解，则类里的public方法都会带上事务。而且属性都是用同一个。
public class FooServiceImpl implements FooService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /*
    如果带事务的方法A,循环调用带事务的方法B, 并且在方法B中有try catch 代码, 当B中出现异常,并且被捕获,会报事务嵌套异常. 这个时候我应该怎么选择事务的传播级别呢? 对应业务需求是: 方法B中出现异常,方法B要回滚, 并且把异常返回方法A, 但是不能影响方法A的循环调用. 解决方案应该是在方法B使用NEXTED 事务级别吗?
    作者回复: B可以是NESTED也可以是REQUIRES_NEW，要看你需要什么样的行为。那B正常的，但A回滚了，这时B要回滚么？如果要回滚就是前者，不然后者
     */
    @Override
    @Transactional //  没有@Transactional注解，就是没加事务，大部分情况下就是各条SQL单独的事务，比如一条upate语句。假设你用JdbcTemplate来做操作，就会从DataSource里获取连接。有事务的时候，就会用当前事务的连接来做操作。
    public void insertRecord() {
        jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('AAA')");
    }

    @Override
    @Transactional(rollbackFor = RollbackException.class)
    public void insertThenRollback() throws RollbackException {
        jdbcTemplate.execute("INSERT INTO FOO (BAR) VALUES ('BBB')");
        throw new RollbackException();
    }

//    @Autowired
//    FooService fooService; // 优雅版 1

    @Override
    // @Transactional(rollbackFor = RollbackException.class) // 如果不用这一行的话，相当于对此方法的调用没有经过事务代理类，内部的insertThenRollback()调用只是一个普通的调用，所以不会回滚
    // 不过这个方法非常不优雅，更好的方法是，通过对当前类注入FooServiceImpl，通过被注入的Bean调用方法
    public void invokeInsertThenRollback() throws RollbackException {
        // 不生效版
        // insertThenRollback();
        // 优雅版 1
        // fooService.insertRecord();
        // 优雅版 2
        ((FooService)AopContext.currentProxy()).insertThenRollback();
    }
    /*
    最近遇到了类似的问题，重新回顾了一下，我有一个问题，为什么这边如果注入类本身不会遇到循环依赖的问题呢，但是比如@Async就会有出现循环依赖呢
作者回复: 这时在你依赖的地方加个@Lazy延时注入这个依赖应该就可以了。@EnableAsync实际是通过AsyncAnnotationBeanPostProcessor对Bean做后置处理的，在执行它对Bean做增强时发现这个Bean已经被依赖了就会报错。
     */
}
