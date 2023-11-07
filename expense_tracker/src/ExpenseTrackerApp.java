import javax.swing.JOptionPane;
import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;

import java.util.ArrayList;
import java.util.List;

public class ExpenseTrackerApp {

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    // Create MVC components
    ExpenseTrackerModel model = new ExpenseTrackerModel();
    ExpenseTrackerView view = new ExpenseTrackerView();
    ExpenseTrackerController controller = new ExpenseTrackerController(model, view);
    

    // Initialize view
    view.setVisible(true);



    // Handle add transaction button clicks
    view.getAddTransactionBtn().addActionListener(e -> {
      // Get transaction data from view
      double amount = view.getAmountField();
      String category = view.getCategoryField();
      
      // Call controller to add transaction
      boolean added = controller.addTransaction(amount, category);
      
      if (!added) {
        JOptionPane.showMessageDialog(view, "Invalid amount or category entered");
        view.toFront();
      }
    });

      // Add action listener to the "Apply Category Filter" button
    view.addApplyCategoryFilterListener(e -> {
      try{
      String categoryFilterInput = view.getCategoryFilterInput();
      CategoryFilter categoryFilter = new CategoryFilter(categoryFilterInput);
      if (categoryFilterInput != null) {
          // controller.applyCategoryFilter(categoryFilterInput);
          controller.setFilter(categoryFilter);
          controller.applyFilter();
      }
     }catch(IllegalArgumentException exception) {
    JOptionPane.showMessageDialog(view, exception.getMessage());
    view.toFront();
   }});


    // Add action listener to the "Apply Amount Filter" button
    view.addApplyAmountFilterListener(e -> {
      try{
      double amountFilterInput = view.getAmountFilterInput();
      AmountFilter amountFilter = new AmountFilter(amountFilterInput);
      if (amountFilterInput != 0.0) {
          controller.setFilter(amountFilter);
          controller.applyFilter();
      }
    }catch(IllegalArgumentException exception) {
    JOptionPane.showMessageDialog(view,exception.getMessage());
    view.toFront();
   }});


    view.getUndoTransactionBtn().addActionListener(e -> {
        int[] selectedRows = view.getTransactionsTable().getSelectedRows();
        List<Transaction> transactionsToDelete = new ArrayList<>();
        int dialogResult = JOptionPane.showConfirmDialog(null,
                  "Are you sure you want to delete the selected transactions?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane. YES_OPTION) {
            for (int selectedRow : selectedRows) {
                if (selectedRow != -1) {
                    Transaction selectedTransaction = model.getTransactions().get(selectedRow);
                    transactionsToDelete.add(selectedTransaction);
                }
            }
            controller.undoTransaction(transactionsToDelete);
        }
    });
    

  }
}
