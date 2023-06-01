package com.example.SpringBatchTutorial.job;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.SpringBatchTutorial.SpringBatchTestConfig;
import com.example.SpringBatchTutorial.core.domain.accounts.AccountRepository;
import com.example.SpringBatchTutorial.core.domain.orders.OrderRepository;
import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import com.example.SpringBatchTutorial.job.DbDataReadWrite.TriMigrationConfig;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest(classes = { SpringBatchTestConfig.class, TriMigrationConfig.class })
public class TriMigrationConfigTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private AccountRepository accountRepository;

	@AfterEach
	public void cleanUpEach() {
		orderRepository.deleteAll();
		accountRepository.deleteAll();
	}

	@Test()
	public void success() throws Exception {
		JobExecution execution = jobLauncherTestUtils.launchJob();

		Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
		Assertions.assertEquals(0, accountRepository.count());
	}

	@Test()
	public void success_existData() throws Exception {

		Orders orders1 = new Orders(null, "kakao gift", 15000, new Date());
		Orders orders2 = new Orders(null, "naver gift", 15000, new Date());

		orderRepository.save(orders1);
		orderRepository.save(orders2);

		JobExecution execution = jobLauncherTestUtils.launchJob();

		Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
		Assertions.assertEquals(2, accountRepository.count());
	}

}
