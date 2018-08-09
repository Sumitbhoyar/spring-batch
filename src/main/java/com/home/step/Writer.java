package com.home.step;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
public class Writer {

	@Bean
	ItemWriter<List<Object>> writerDb(DataSource dbSource) {
		JdbcBatchItemWriter<List<Object>> databaseItemWriter = new JdbcBatchItemWriter<>();
		databaseItemWriter.setDataSource(dbSource);

		databaseItemWriter.setSql("insert into dummy2 (name,type) values (?,?)");
		databaseItemWriter.setItemPreparedStatementSetter(new ItemPreparedStatementSetter<List<Object>>() {
			@Override
			public void setValues(List<Object> dummy, PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setObject(1, dummy.get(0));
                preparedStatement.setObject(2, dummy.get(1));
			}
		});
		return databaseItemWriter;
	}

}