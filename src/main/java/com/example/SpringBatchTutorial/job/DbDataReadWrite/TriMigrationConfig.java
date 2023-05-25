package com.example.SpringBatchTutorial.job.DbDataReadWrite;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import com.example.SpringBatchTutorial.core.domain.accounts.AccountRepository;
import com.example.SpringBatchTutorial.core.domain.accounts.Accounts;
import com.example.SpringBatchTutorial.core.domain.orders.OrderRepository;
import com.example.SpringBatchTutorial.core.domain.orders.Orders;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TriMigrationConfig {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job trMigrationJob(Step trMigrationStep) {
		return jobBuilderFactory.get("trMigrationJob").incrementer(new RunIdIncrementer()).start(trMigrationStep)
				.build();
	}

	@JobScope
	@Bean
	public Step trMigrationStep(ItemReader trOrdersReader, ItemProcessor trOrderProcessor, ItemWriter trOrdersWriter) {
		return stepBuilderFactory.get("trMigrationStep").<Orders, Accounts>chunk(5) // 트랜잭션 갯수 지정
				.reader(trOrdersReader)
//				.writer(new ItemWriter() {
//					@Override
//					public void write(List items) throws Exception {
//						items.forEach(System.out::println);
//					}
//				})
				.processor(trOrderProcessor).writer(trOrdersWriter).build();
	}

	@Bean
	@StepScope
	public ItemProcessor<Orders, Accounts> trOrderProcessor() {
		return new ItemProcessor<Orders, Accounts>() {
			@Override
			public Accounts process(Orders item) throws Exception {
				return new Accounts(item);
			}
		};
	}

	@Bean
	@StepScope
	public RepositoryItemReader<Orders> trOrdersReader() {
		return new RepositoryItemReaderBuilder<Orders>().name("trOrdersReader").repository(orderRepository)
				.methodName("findAll").pageSize(5).arguments(Arrays.asList())
				.sorts(Collections.singletonMap("id", Sort.Direction.ASC)).build();
	}

	@Bean
	@StepScope
	public RepositoryItemWriter<Accounts> trOrdersWriter() {
		return new RepositoryItemWriterBuilder<Accounts>().repository(accountRepository).methodName("save").build();
	}

	@StepScope
	@Bean
	public ItemWriter<Accounts> trOrdersWriter2() throws Exception {
		return new ItemWriter<Accounts>() {
			@Override
			public void write(List<? extends Accounts> items) throws Exception {
				items.forEach(item -> accountRepository.save(item));
			}
		};
	}
}
