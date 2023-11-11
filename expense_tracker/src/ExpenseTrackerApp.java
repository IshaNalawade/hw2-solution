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

      view.refreshHighlightRows();
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

    // Action Listener to the "Undo Transaction" button
    view.getUndoTransactionBtn().addActionListener(e -> {
        int[] selectedRows = view.getRows();
        List<Transaction> transactionsToDelete = new ArrayList<>();
        view.refreshHighlightRows();
        // Confirm undo transaction action
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

      // Add Action listener on table to enable undo button when rows are selected
      view.getTransactionsTable().getSelectionModel().addListSelectionListener(e->  {

          int rowCount = view.getTransactionsTable().getRowCount();
          int[] selectedRows = view.getRows();
          // Check if any rows are selected
          if(!e.getValueIsAdjusting() && view.getTransactionsTable().getSelectedRowCount() > 0 && !(selectedRows[selectedRows.length - 1] == rowCount - 1)) {
              view.getUndoTransactionBtn().setEnabled(true);
          } else {
              view.getUndoTransactionBtn().setEnabled(false);
          }
      });




  }
}
