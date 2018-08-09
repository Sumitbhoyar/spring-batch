package com.home.step;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class Reader {

    @Bean
    ItemReader<List<Object>> readerDB(DataSource dbSource) {
        JdbcCursorItemReader<List<Object>> databaseReader = new JdbcCursorItemReader<>();
        databaseReader.setFetchSize(500);
        databaseReader.setDataSource(dbSource);
        databaseReader.setRowMapper((rs, rowNum) -> {
            ArrayList<Object> list = new ArrayList<>();
            list.add(rs.getString(1));
            list.add(rs.getString(2));
            return list;
        });
        databaseReader.setSql("select name,type from dummy");

        return databaseReader;
    }

}