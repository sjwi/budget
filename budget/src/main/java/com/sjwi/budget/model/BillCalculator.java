package com.sjwi.budget.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BillCalculator {
	
	private final List<Integer> itemAmounts;

	public BillCalculator(List<Integer> itemAmounts) {
		this.itemAmounts = itemAmounts;
	}

	public Map<Integer,Integer> getBillMapForItems(){
		Map<Integer, Integer> billMap = Arrays.stream(new Integer[] {100,50,20,10,5,1})
											.collect(LinkedHashMap::new,(map, denomination) -> map.put(denomination, 0),Map::putAll);
		for (Integer itemAmount: itemAmounts) {
			for (Map.Entry<Integer, Integer> billEntry: billMap.entrySet()) {
				Map<Integer, Integer> numberOfBillsInAmount = getNumberOfBillsInAmount(billEntry.getKey(),itemAmount);
				billMap.put(billEntry.getKey(),billEntry.getValue() + numberOfBillsInAmount.entrySet().iterator().next().getKey());
				itemAmount = numberOfBillsInAmount.entrySet().iterator().next().getValue();
			}
		}	
		return billMap;
	}
	
	private Map<Integer,Integer> getNumberOfBillsInAmount(Integer denomination, Integer amount){
		Integer billCounter = 0;
		while(amount >= denomination){
			billCounter++;
			amount = amount - denomination;
		}
		return Collections.singletonMap(billCounter, amount);
	}
}