package com.sjwi.budget.model;

import java.util.List;

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
}