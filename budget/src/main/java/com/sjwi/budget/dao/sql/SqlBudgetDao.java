package com.sjwi.budget.dao.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sjwi.budget.dao.BudgetDao;
import com.sjwi.budget.model.Budget;
import com.sjwi.budget.model.Item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SqlBudgetDao implements BudgetDao {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	Map<String,String> queryStore;

	@Override
	public void createTemplate(Budget budget) {
		int templateId = createTemplateMaster(budget.getName());
		insertBudgetItems(templateId,budget.getItems());
	}
	@Override
	public synchronized void editBudget(Budget budget) {
		deleteBudgetItems(budget.getId());
		updateBudget(budget);
		insertBudgetItems(budget.getId(),budget.getItems());
	}
	@Override
	public void deleteBudget(int id) {
		deleteBudgetItems(id);
		jdbcTemplate.update(queryStore.get("disableBudgetById"), new Object[] {id});
	}
	@Override
	public List<Budget> getAllTemplates() {
		return jdbcTemplate.query(queryStore.get("getAllTemplates"), r -> {
			List<Budget> templates = new ArrayList<>();
			while (r.next()){
				templates.add(getBudgetById(r.getInt("ID")));
			}
			return templates;
		});
	}
	@Override
	public List<Budget> getAllBudgets() {
		return jdbcTemplate.query(queryStore.get("getAllBudgets"), r -> {
			List<Budget> templates = new ArrayList<>();
			while (r.next()){
				templates.add(getBudgetById(r.getInt("ID")));
			}
			return templates;
		});
	}
	@Override
	public Budget getBudgetById(int budgetId) {
		return jdbcTemplate.query(queryStore.get("getBudgetById"), new Object[] {budgetId}, r -> {
			if (r.next()) {
				return new Budget(r.getInt("ID"), r.getString("NAME"),getItemsForBudgetById(r.getInt("ID")));
			} else {
				return null;
			}
		});
	}
	
	@Override
	public int createEmptyBudget(String month) {
		jdbcTemplate.update(queryStore.get("createEmptyBudget"), new Object[] {month});
		return jdbcTemplate.query(queryStore.get("getLatestBudget"), r -> {
			r.next();
			return r.getInt("ID");
		});
	}
	@Override
	public void createBudgetFromTemplate(Integer templateId, String month) {
		jdbcTemplate.update(queryStore.get("cloanBudgetItems"), new Object[] {createEmptyBudget(month),templateId});
	}

	private void updateBudget(Budget budget) {
		jdbcTemplate.update(queryStore.get("updateBudget"),new Object[] {budget.getName(), budget.getId()});
	}

	private void deleteBudgetItems(int id) {
		jdbcTemplate.update(queryStore.get("deleteBudgetItemsByBudgetId"), new Object[] {id});
		
	}
	
	private void insertBudgetItems(int templateId, List<Item> items) {
		jdbcTemplate.batchUpdate(queryStore.get("insertBudgetItems"), new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setInt(1,templateId);
				ps.setString(2,items.get(i).getName());
				ps.setDouble(3,items.get(i).getAmount());
				ps.setInt(4,items.get(i).getMaxDenomination());
			}
			public int getBatchSize() {
				return items.size();
			}
		});
	}
	
	private int createTemplateMaster(String templateName) {
		jdbcTemplate.update(queryStore.get("createNewTemplate"), new Object[] {templateName});
		return jdbcTemplate.query(queryStore.get("getLatestTemplate"), r -> {
			r.next();
			return r.getInt("ID");
		});
	}
	
	private List<Item> getItemsForBudgetById(int budgetId) {
		return jdbcTemplate.query(queryStore.get("getItemsForBudgetById"), new Object[] {budgetId}, r -> {
			List<Item> items = new ArrayList<>();
			while (r.next()) {
				items.add(new Item(r.getInt("ID"),r.getString("NAME"),r.getDouble("AMOUNT"),r.getInt("MAX_DENOM")));
			}
			return items;
		});
	}
}
