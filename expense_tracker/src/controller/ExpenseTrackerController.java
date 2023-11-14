package controller;

import org.junit.Test;
import view.ExpenseTrackerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JOptionPane;

import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.TransactionFilter;

import static org.junit.Assert.assertEquals;

public class ExpenseTrackerController {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private int id =1;
  /** 
   * The Controller is applying the Strategy design pattern.
   * This is the has-a relationship with the Strategy class 
   * being used in the applyFilter method.
   */
  private TransactionFilter filter;

  public ExpenseTrackerController(ExpenseTrackerModel model, ExpenseTrackerView view) {
    this.model = model;
    this.view = view;
  }

  public void setFilter(TransactionFilter filter) {
    // Sets the Strategy class being used in the applyFilter method.
    this.filter = filter;
  }

  public void refresh() {
    List<Transaction> transactions = model.getTransactions();
    view.refreshTable(transactions);
  }

  public boolean addTransaction(double amount, String category) {
    if (!InputValidation.isValidAmount(amount)) {
      return false;
    }
    if (!InputValidation.isValidCategory(category)) {
      return false;
    }
    
    Transaction t = new Transaction(amount, category, id++);
    model.addTransaction(t);
    view.getTableModel().addRow(new Object[]{t.getAmount(), t.getCategory(), t.getTimestamp()});
    refresh();
    return true;
  }

  public void undoTransaction(List<Transaction> transactions) {
    if(Objects.isNull(transactions)){
      return;
    }
    for(Transaction t : transactions){
      model.removeTransaction(t.getId());
    }
    refresh();
  }

  public void applyFilter() {
    //null check for filter
    if(filter!=null){
      // Use the Strategy class to perform the desired filtering
      List<Transaction> transactions = model.getTransactions();
      List<Transaction> filteredTransactions = filter.filter(transactions);
      List<Integer> rowIndexes = new ArrayList<>();
      for (Transaction t : filteredTransactions) {
        int rowIndex = transactions.indexOf(t);
        if (rowIndex != -1) {
          rowIndexes.add(rowIndex);
        }
      }
      view.highlightRows(rowIndexes);
    }
    else{
      JOptionPane.showMessageDialog(view, "No filter applied");
      view.toFront();}
  }

  @Test
  public void testAddTransactionAndUpdateView() {

    // Pre-condition: The table is empty
    assertEquals(0, view.getTableModel().getRowCount());

    // Perform the action: Add a transaction
    double amount = 50.0;
    String category = "food";
//        assertTrue(controller.addTransaction(amount, category));

    int rowCountBefore = view.getTableModel().getRowCount();
    view.refreshTable(model.getTransactions());
    int rowCountAfter = view.getTableModel().getRowCount();
    // Post-condition: The table contains the added transaction
    assertEquals(rowCountBefore+1, rowCountAfter);
//        assertEquals(2, view.getTableModel().getRowCount());

    System.out.println(view.getTransactionsTable().getValueAt(0,0));
    System.out.println(view.getTransactionsTable().getValueAt(0,1));
    // Check the contents of the table
    assertEquals(1, view.getTransactionsTable().getValueAt(1, 0)); // Serial
    assertEquals(50.0, view.getTransactionsTable().getValueAt(0, 1)); // Amount
    assertEquals("food", view.getTransactionsTable().getValueAt(0, 2)); // Category

    assertEquals(50.0, view.getTransactionsTable().getValueAt(1,3)); // Total Cost
  }



}
