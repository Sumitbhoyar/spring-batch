package com.home.step;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Processor implements ItemProcessor<List<Object>, List<Object>> {

	@Override
	public List<Object> process(List<Object> data) throws Exception {
		return data;
	}

}
