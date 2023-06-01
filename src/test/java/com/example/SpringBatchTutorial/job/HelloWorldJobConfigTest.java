package com.example.SpringBatchTutorial.job;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.SpringBatchTutorial.SpringBatchTestConfig;
import com.example.SpringBatchTutorial.job.HelloWorld.HelloWorldJobConfig;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes = { SpringBatchTestConfig.class, HelloWorldJobConfig.class })
public class HelloWorldJobConfigTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void success() throws Exception {
		JobExecution execution = jobLauncherTestUtils.launchJob();

		Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
	}

}
