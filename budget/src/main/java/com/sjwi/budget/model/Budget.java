package com.sjwi.budget.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Budget {
	private final int id;
	private final String name;
	private final List<Item> items;
	
	public Budget(int id, String name, List<Item> items) {
		super();
		this.id = id;
		this.name = name;
		this.items = items;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public List<Item> getItems() {
		return items;
	}
	public Map<Integer,Integer> getBillMap(){
		Map<Integer, Integer> billMap = initializeBillMap();
		for (Item item: items) {
			Double itemAmount = item.getAmount();
			for (Map.Entry<Integer, Integer> kv: billMap.entrySet()) {
				Map<Integer, Double> data = numberOfBillsInAmount(kv.getKey(),itemAmount);
				billMap.put(kv.getKey(),kv.getValue() + data.entrySet().iterator().next().getKey());
				itemAmount = data.entrySet().iterator().next().getValue();
			}
		}	
		return billMap;
	}
	
	private Map<Integer,Integer> initializeBillMap(){
		Map<Integer,Integer> billMap = new LinkedHashMap<>();
		billMap.put(100, 0);
		billMap.put(50, 0);
		billMap.put(20, 0);
		billMap.put(10, 0);
		billMap.put(5, 0);
		billMap.put(1, 0);
		return billMap;
	}
	private Map<Integer,Double> numberOfBillsInAmount(Integer bill, Double amount){
		Integer billCounter = 0;
		while(amount >= bill){
			billCounter++;
			amount = amount - bill;
		}
		Map<Integer,Double> newAmountWithBillCount = new HashMap<>();
		newAmountWithBillCount.put(billCounter, amount);
		return newAmountWithBillCount;
	}
}