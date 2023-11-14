// package test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.ParseException;

import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import org.junit.Before;
import org.junit.Test;

import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;

import javax.swing.*;

import static org.junit.Assert.*;


public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;

  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }


    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	double amount = 50.0;
	String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	//                 the added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);
	
	// Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }


    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	double amount = 50.0;
	String category = "food";
        Transaction addedTransaction = new Transaction(amount, category,2);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	//                the added transaction
        assertEquals(1, model.getTransactions().size());
	Transaction firstTransaction = model.getTransactions().get(0);
	checkTransaction(amount, category, firstTransaction);

	assertEquals(amount, getTotalCost(), 0.01);
	
	// Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction.getId());
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }

    @Test
    public void testFilterByAmount() {

        // Add multiple transactions with different amounts
        controller.addTransaction(50.0, "food");
        controller.addTransaction(30.0, "travel");
        controller.addTransaction(75.0, "entertainment");
        controller.addTransaction(30.0, "food");


        // using Filter class
        double filterAmount = 30.0;
        AmountFilter amountFilter = new AmountFilter(filterAmount);
        List<Transaction> filteredTransactions = amountFilter.filter(model.getTransactions());
        // Post-condition: List of transactions contains only the filtered transactions with filter amount 30
        assertEquals(2, filteredTransactions.size());
        // Check the contents of the list
        for (Transaction t : filteredTransactions) {
            assertEquals(30.0, t.getAmount(), 0.01);
        }
    }

    @Test
    public void testFilterByCategory() {

        // Add multiple transactions with different categories
        controller.addTransaction(30.0, "travel");
        controller.addTransaction(50.0, "food");
        controller.addTransaction(90.0, "travel");
        controller.addTransaction(75.0, "entertainment");
        controller.addTransaction(45.0, "travel");

        // Apply filter using Filter class for category
        String filterCategory = "travel";
        CategoryFilter categoryFilter = new CategoryFilter(filterCategory);
        List<Transaction> filteredTransactions = categoryFilter.filter(model.getTransactions());

        // Post-condition: List of transactions contains only the filtered transactions with filter category travel
        assertEquals(3, filteredTransactions.size());
        for (Transaction t : filteredTransactions) {
            assertEquals("travel", t.getCategory());
        }
    }

    @Test
    public void testUndoDisallowed() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Attempt to undo when the transactions list is empty
        view.getUndoTransactionBtn().doClick(); // Simulating a button click

        // Post-condition: List of transactions remains empty
        assertEquals(0, model.getTransactions().size());

        // Check the state of the UI widget (undo button)
        JButton undoButton = view.getUndoTransactionBtn();
        assertFalse("Undo button should be disabled when transactions list is empty", undoButton.isEnabled());
    }

    @Test
    public void testInvalidInputHandling() {

        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Pre-condition: Check the total amount remains unchanged
        assertEquals(0.0, getTotalCost(), 0.01);

        // Perform the action: Attempt to add a transaction with an invalid amount
        assertFalse(controller.addTransaction(-50.0, "food"));

        // Perform the action: Attempt to add a transaction with an invalid category
        assertFalse(controller.addTransaction(50.0, ""));

        // Post-condition: List of transactions and Total Cost remain unchanged
        assertEquals(0, model.getTransactions().size());

        // Check the total amount remains unchanged
        assertEquals(0.0, getTotalCost(), 0.01);

    }

    @Test
    public void testAddTransactionAndUpdateView() {

        // Pre-condition: The table is empty
        assertEquals(0, view.getTableModel().getRowCount());
        // Pre-condition: Get total amount of table before
        assertEquals(0, getTotalCost(), 0.0);
        double amountBefore = getTotalCost();

        // Perform the action: Add a transaction
        double amount = 50.0;
        String category = "food";
        // Post-condition: Transaction will be added
        assertTrue(controller.addTransaction(amount, category));
        double amountAfter = getTotalCost();

        // Post-condition: The total amount has the newly added transaction
        assertEquals(amountBefore+50.0, amountAfter, 0.0);

        // Post-condition: A new transaction is added
        assertEquals(2, view.getTableModel().getRowCount());  //rowcount is 2 as there is an extra row that shows the total of the table

        assertEquals(50.0, view.getTransactionsTable().getValueAt(0,1)); //Checking amount
        assertEquals("food", view.getTransactionsTable().getValueAt(0,2)); //Checking Category
    }
    
}
