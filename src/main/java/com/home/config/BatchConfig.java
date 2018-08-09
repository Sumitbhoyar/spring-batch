package com.home.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.home.listener.JobCompletionListener;
import com.home.step.Processor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class BatchConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
//
//	@Bean
//	public ResourcelessTransactionManager transactionManager() {
//		return new ResourcelessTransactionManager();
//	}

	@Bean
	public DriverManagerDataSource dbSource(){
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
		driverManagerDataSource.setUrl("jdbc:mysql://localhost/test");
		driverManagerDataSource.setUsername("root");
		driverManagerDataSource.setPassword("root");
//        driverManagerDataSource.setSchema("test");
		return driverManagerDataSource;
	}

	@Bean
	public DataSourceTransactionManager dbTransactionManager(DriverManagerDataSource dbSource){
		return new DataSourceTransactionManager(dbSource);
	}

	@Bean
	public MapJobRepositoryFactoryBean jobRepositoryFactory(DataSourceTransactionManager dbTransactionManager)
			throws Exception {

		MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(dbTransactionManager);

		factory.afterPropertiesSet();

		return factory;
	}

	@Bean
	public JobRepository jobRepository(MapJobRepositoryFactoryBean jobRepositoryFactory) throws Exception {
		return jobRepositoryFactory.getObject();
	}

	@Bean
	public Job processJob(ItemReader readerDB, ItemWriter writerDb, Processor processor) {
		return jobBuilderFactory.get("processJob")
				.incrementer(new RunIdIncrementer()).listener(listener())
				.flow(orderStep1(readerDB, writerDb, processor)).end().build();
	}

	@Bean
	public Step orderStep1(ItemReader readerDB, ItemWriter writerDb, Processor processor) {
		return stepBuilderFactory.get("orderStep1").<String, String> chunk(10000)
				.reader(readerDB).processor(processor)
				.writer(writerDb).build();
	}

	@Bean
	public JobExecutionListener listener() {
		return new JobCompletionListener();
	}

}
