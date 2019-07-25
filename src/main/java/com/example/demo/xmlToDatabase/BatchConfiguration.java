package com.example.demo.xmlToDatabase;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public StaxEventItemReader<Person> reader(){
        StaxEventItemReader<Person> reader = new StaxEventItemReader<Person>();
        reader.setResource(new ClassPathResource("test.xml"));
        reader.setFragmentRootElementName("Person");

        Map<String, String> aliases = new HashMap<String, String>();
        aliases.put("Person", "com.example.demo.xmlToDatabase");

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(aliases);

        reader.setUnmarshaller(xStreamMarshaller);

        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource){
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
        writer.setDataSource(dataSource);
        writer.setSql("insert into Person(firstName, lastName) values(?, ?)");
        writer.setItemPreparedStatementSetter(new PersonItemPreparedStmSetter());
        return writer;
    }

    private class PersonItemPreparedStmSetter implements ItemPreparedStatementSetter<Person>{

        @Override
        public void setValues(Person person, PreparedStatement ps) throws SQLException {
            ps.setString(1, person.getFirstName());
            ps.setString(2, person.getLastName());
        }

    }

    @Bean
    public Step stepXML(JdbcBatchItemWriter<Person> writer) {
        return stepBuilderFactory.get("stepXML")
                .<Person, Person> chunk(10)
                .reader(reader())
                .writer(writer)
                .build();
    }

    @Bean
    public Job importPersonJob(Step stepXML) {
        return jobBuilderFactory.get("importPersonJob")
                .incrementer(new RunIdIncrementer())
                .flow(stepXML)
                .end()
                .build();

    }
}
