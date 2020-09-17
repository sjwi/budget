package com.sjwi.budget.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sjwi.budget.model.Budget;

@Component
public class BudgetService {

	@Autowired
	BudgetDao budgetDao;

	public void createTemplate(Budget budget) {
		budgetDao.createTemplate(budget);
	}

	public List<Budget> getAllTemplates() {
		return budgetDao.getAllTemplates();
	}

	public List<Budget> getAllBudgets() {
		return budgetDao.getAllBudgets();
	}

	public Budget getBudget(int id) {
		return budgetDao.getBudgetById(id);
	}

	public void editBudget(Budget budget) {
		budgetDao.editBudget(budget);
	}

	public void disableBudget(int id) {
		budgetDao.deleteBudget(id);
	}

	public void createBudget(Optional<Integer> template, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		String year = Integer.toString(cal.get(Calendar.YEAR));
		if (!template.isPresent()) {
			budgetDao.createEmptyBudget(getMonthFromInteger(month) + " " + year);
		} else {
			budgetDao.createBudgetFromTemplate(template.get(), getMonthFromInteger(month) + " " + year);
		}
	}
	
	private String getMonthFromInteger(int month) {
		switch(month){
			case 0:
				return "January";
			case 1:
				return "February";
			case 2:
				return "March";
			case 3:
				return "April";
			case 4:
				return "May";
			case 5:
				return "June";
			case 6:
				return "July";
			case 7:
				return "August";
			case 8:
				return "September";
			case 9:
				return "October";
			case 10:
				return "November";
			case 11:
				return "December";
			default:
				return "January";
		} 
	}

}
