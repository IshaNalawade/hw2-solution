# hw1- Manual Review

The homework will be based on this project named "Expense Tracker",where users will be able to add/remove daily transaction. 

## Compile

To compile the code from terminal, use the following command:
```
cd src
javac ExpenseTrackerApp.java
java ExpenseTracker
```

You should be able to view the GUI of the project upon successful compilation. 

## Java Version
This code is compiled with ```openjdk 17.0.7 2023-04-18```. Please update your JDK accordingly if you face any incompatibility issue.

## New Functionality added
- Provided with Undo functionality using undo functionality button to undo any transaction that has been added and thereby reflect the changes in the total cost.
- Provided with the logic as to when the Undo button should be enabled and disabled
- Provided with Undo functionality button and method for the same.
- Wrote unit test cases for all the following 6 cases:
    - Add Transaction
    - Invalid Input Handling
    - Filter by Amount
    - Filter by Category
    - Undo Disallowed
    - Undo Allowed