package com.sjwi.budget.model;

public class Item {
	
	private final int id;
	private final String name;
	private final Double amount;
	public Item(int id, String name, Double amount) {
		super();
		this.id = id;
		this.name = name;
		this.amount = amount;
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

}
