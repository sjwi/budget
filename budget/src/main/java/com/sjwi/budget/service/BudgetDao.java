package com.sjwi.budget.service;

import java.util.List;

import com.sjwi.budget.model.Budget;

public interface BudgetDao {

	void createTemplate(Budget budget);

	List<Budget> getAllTemplates();

	Budget getBudgetById(int budgetId);

	void editBudget(Budget budget);

	void deleteBudget(int id);

	int createEmptyBudget(String month);

	void createBudgetFromTemplate(Integer templateId, String month);

	List<Budget> getAllBudgets();

}
