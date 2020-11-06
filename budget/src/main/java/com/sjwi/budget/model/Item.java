package com.sjwi.budget.model;

public class Item {
	
	private final int id;
	private final String name;
	private final Double amount;
	private final int maxDenomination;
	public Item(int id, String name, Double amount, Integer maxDenomination) {
		super();
		this.id = id;
		this.name = name;
		this.amount = amount;
		this.maxDenomination = maxDenomination;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Double getAmount() {
		return amount;
	}
	public Integer getMaxDenomination() {
		return maxDenomination;
	}
}
