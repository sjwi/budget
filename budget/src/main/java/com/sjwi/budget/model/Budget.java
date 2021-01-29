package com.sjwi.budget.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Budget {
	private final int id;
	private final String name;
	private final String description;
	private final List<Item> items;
	
	public Budget(int id, String name, String description, List<Item> items) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.items = items;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public List<Item> getItems() {
		return items;
	}

	/**
	 * @param items - A list of budget items
	 * @return A map representing the total number of 
	 * bills required for each denomination so the cash
	 * withdrawal can be separated out into each item's envelope. 
	 */
    public Map<Integer,Integer> getDenominationMapForItems(){
    	Map<Integer, Integer> billMap = Arrays.stream(new Integer[] {100,50,20,10,5,1})
    										.collect(LinkedHashMap::new,(map, denomination) -> map.put(denomination, 0),Map::putAll);
    	items.stream().forEach(item -> {
    		Integer itemAmt = item.getAmount().intValue();
			Iterator<Map.Entry<Integer, Integer>> itr = billMap.entrySet().iterator(); 
			while (itemAmt > 0) {
				Map.Entry<Integer, Integer> billEntry = itr.next();
				while (billEntry.getKey() > item.getMaxDenomination()) {
					billEntry = itr.next();
				}
				billMap.put(billEntry.getKey(), billEntry.getValue() + itemAmt / billEntry.getKey());
				itemAmt = itemAmt % billEntry.getKey();
			}
		});
		return billMap;
	}
}