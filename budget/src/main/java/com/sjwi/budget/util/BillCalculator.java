package com.sjwi.budget.util;

import java.util.Arrays;
import java.util.Iterator;
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
    itemAmounts.forEach(itemAmt -> {
	  Iterator<Map.Entry<Integer, Integer>> itr = billMap.entrySet().iterator(); 
      while (itemAmt > 0) {
	    Map.Entry<Integer, Integer> billEntry = itr.next();
	    billMap.put(billEntry.getKey(), billEntry.getValue() + itemAmt / billEntry.getKey());
	    itemAmt = itemAmt % billEntry.getKey();
	  }
    });
    return billMap;
  }
}