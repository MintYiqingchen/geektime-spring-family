package geektime.spring.data.declarativetransactiondemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement(mode = AdviceMode.PROXY) // 一定要开启这个注解才能通过@Transaction注解配置事务，否则要通过applicationContext.xml文件进行配置
@Slf4j
public class DeclarativeTransactionDemoApplication implements CommandLineRunner {
	@Autowired
	private FooService fooService;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		SpringApplication.run(DeclarativeTransactionDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fooService.insertRecord();
		log.info("AAA {}",
				jdbcTemplate
						.queryForObject("SELECT COUNT(*) FROM FOO WHERE BAR='AAA'", Long.class));
		try {
			fooService.insertThenRollback();
		} catch (Exception e) {
			log.info("BBB {}",
					jdbcTemplate
							.queryForObject("SELECT COUNT(*) FROM FOO WHERE BAR='BBB'", Long.class));
		}

		try {
			fooService.invokeInsertThenRollback();

		} catch (Exception e) {
		} finally {
			log.info("BBB {}",
					jdbcTemplate
							.queryForObject("SELECT COUNT(*) FROM FOO WHERE BAR='BBB'", Long.class));
		}
	}
}

