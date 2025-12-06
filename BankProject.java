import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * BankProject
 * This class contains the Account Model, the BankManager Logic, and the Swing UI (BankUI).
 * It simulates a simple bank system allowing account creation, deposit, withdrawal, and balance inquiry.
 */

// =========================================================================
// 1. Account Model Class
// =========================================================================
class Account {
    private final String accountNumber;
    private final String accountHolderName;
    private double balance;

    public Account(String accountNumber, String accountHolderName, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialDeposit;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        // Used for displaying the account in the combo boxes
        return accountNumber + " (" + accountHolderName + ")";
    }
}

// =========================================================================
// 2. BankManager Logic Class
// =========================================================================
class BankManager {
    private final Map<String, Account> accounts;

    public BankManager() {
        this.accounts = new HashMap<>();
    }

    public boolean addAccount(String number, String name, double initialDeposit) {
        if (accounts.containsKey(number)) {
            return false; // Account already exists
        }
        Account newAccount = new Account(number, name, initialDeposit);
        accounts.put(number, newAccount);
        return true;
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public boolean deposit(String accountNumber, double amount) {
        Account account = getAccount(accountNumber);
        if (account != null && amount > 0) {
            account.deposit(amount);
            return true;
        }
        return false;
    }

    public boolean withdraw(String accountNumber, double amount) {
        Account account = getAccount(accountNumber);
        if (account != null) {
            return account.withdraw(amount);
        }
        return false; // Account not found
    }

    public double getBalance(String accountNumber) {
        Account account = getAccount(accountNumber);
        return account != null ? account.getBalance() : -1; // -1 to indicate error/not found
    }

    public Account[] getAllAccounts() {
        return accounts.values().toArray(new Account[0]);
    }
}

// =========================================================================
// 3. Main Application Class (UI Setup and Event Handling)
// =========================================================================
public class BankProject extends JFrame {

    private final BankManager bankManager;
    private final DecimalFormat currencyFormat = new DecimalFormat("¤#,##0.00");
    
    // UI Components for common use across tabs - now separated for each panel
    private JComboBox<Account> depositAccountSelector;
    private JComboBox<Account> withdrawAccountSelector;
    private JComboBox<Account> viewBalanceAccountSelector;
    private JLabel balanceDisplayLabel;

    public BankProject() {
        super("Simple Bank Management System");
        this.bankManager = new BankManager();

        // Basic Frame Setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());

        // Use a JTabbedPane to organize the different functions
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Create and add individual function panels
        tabbedPane.addTab("Add Account", createAddAccountPanel());
        tabbedPane.addTab("Deposit", createTransactionPanel("Deposit"));
        tabbedPane.addTab("Withdraw", createTransactionPanel("Withdraw"));
        tabbedPane.addTab("View Balance", createViewBalancePanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Pre-populate with a test account
        bankManager.addAccount("1001", "Alice Johnson", 500.00);
        bankManager.addAccount("1002", "Bob Smith", 1250.75);
        
        // Initial setup for all account selectors
        updateAccountSelector();

        // Make the frame visible
        setVisible(true);
    }

    /**
     * Helper method to show messages to the user using JOptionPane.
     */
    private void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    /**
     * Updates the JComboBox models across all tabs whenever a new account is added.
     */
    private void updateAccountSelector() {
        Account[] accounts = bankManager.getAllAccounts();
        
        // Helper to update a single selector
        java.util.function.Consumer<JComboBox<Account>> updateSelector = selector -> {
            if (selector != null) {
                selector.removeAllItems();
                for (Account acc : accounts) {
                    selector.addItem(acc);
                }
            }
        };

        // Update all three selector instances
        updateSelector.accept(depositAccountSelector);
        updateSelector.accept(withdrawAccountSelector);
        updateSelector.accept(viewBalanceAccountSelector);
    }

    // =========================================================================
    // Panel 1: Add Account
    // =========================================================================
    private JPanel createAddAccountPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels
        JLabel nameLabel = new JLabel("Account Holder Name:");
        JLabel numberLabel = new JLabel("Account Number (Unique):");
        JLabel depositLabel = new JLabel("Initial Deposit Amount (>= 0):");

        // Text Fields
        JTextField nameField = new JTextField(15);
        JTextField numberField = new JTextField(15);
        JTextField depositField = new JTextField(15);

        // Add components to the panel
        gbc.gridx = 0; gbc.gridy = 0; panel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panel.add(numberLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(numberField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panel.add(depositLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(depositField, gbc);

        // Button
        JButton addButton = new JButton("Create New Account");
        addButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(addButton, gbc);

        // Action Listener for Add Account Button
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String number = numberField.getText().trim();
            String depositStr = depositField.getText().trim();

            if (name.isEmpty() || number.isEmpty() || depositStr.isEmpty()) {
                showMessage("Input Error", "All fields must be filled out.", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double initialDeposit = Double.parseDouble(depositStr);
                if (initialDeposit < 0) {
                    showMessage("Input Error", "Initial deposit must be zero or positive.", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (bankManager.addAccount(number, name, initialDeposit)) {
                    showMessage("Success", "Account " + number + " for " + name + " created successfully!", JOptionPane.INFORMATION_MESSAGE);
                    // Clear fields
                    nameField.setText("");
                    numberField.setText("");
                    depositField.setText("");
                    // Update account list across all panels
                    updateAccountSelector();
                } else {
                    showMessage("Creation Failed", "Account number already exists.", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                showMessage("Input Error", "Initial deposit must be a valid number.", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // =========================================================================
    // Panel 2 & 3: Deposit and Withdraw (Transaction Panels)
    // =========================================================================
    private JPanel createTransactionPanel(String type) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Determine which specific selector field to use for this panel
        JComboBox<Account> currentSelector;
        
        if (type.equals("Deposit")) {
            if (depositAccountSelector == null) {
                depositAccountSelector = new JComboBox<>();
                depositAccountSelector.setFont(new Font("SansSerif", Font.PLAIN, 12));
            }
            currentSelector = depositAccountSelector;
        } else { // Withdraw
            if (withdrawAccountSelector == null) {
                withdrawAccountSelector = new JComboBox<>();
                withdrawAccountSelector.setFont(new Font("SansSerif", Font.PLAIN, 12));
            }
            currentSelector = withdrawAccountSelector;
        }

        // Account Selector
        JLabel accountLabel = new JLabel("Select Account:");
        
        // Amount Field
        JLabel amountLabel = new JLabel(type + " Amount:");
        JTextField amountField = new JTextField(15);

        // Button
        JButton transactionButton = new JButton(type);
        transactionButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; panel.add(accountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(currentSelector, gbc); // Added the selector here

        gbc.gridx = 0; gbc.gridy = 1; panel.add(amountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(amountField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(transactionButton, gbc);

        // Action Listener for Transaction Button
        transactionButton.addActionListener(e -> {
            Account selectedAccount = (Account) currentSelector.getSelectedItem();
            String amountStr = amountField.getText().trim();

            if (selectedAccount == null) {
                showMessage("Error", "Please add an account first.", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (amountStr.isEmpty()) {
                showMessage("Input Error", "Please enter an amount.", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    showMessage("Input Error", "Amount must be positive.", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String accountNumber = selectedAccount.getAccountNumber();
                boolean success = false;
                
                if (type.equals("Deposit")) {
                    success = bankManager.deposit(accountNumber, amount);
                    if (success) {
                        showMessage("Deposit Successful", currencyFormat.format(amount) + " deposited to account " + accountNumber + ".\nNew Balance: " + currencyFormat.format(bankManager.getBalance(accountNumber)), JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (type.equals("Withdraw")) {
                    success = bankManager.withdraw(accountNumber, amount);
                    if (success) {
                        showMessage("Withdrawal Successful", currencyFormat.format(amount) + " withdrawn from account " + accountNumber + ".\nNew Balance: " + currencyFormat.format(bankManager.getBalance(accountNumber)), JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showMessage("Transaction Failed", "Insufficient funds or invalid amount for withdrawal.", JOptionPane.WARNING_MESSAGE);
                    }
                }
                
                // Clear the amount field
                amountField.setText("");

            } catch (NumberFormatException ex) {
                showMessage("Input Error", "Amount must be a valid number.", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // =========================================================================
    // Panel 4: View Balance
    // =========================================================================
    private JPanel createViewBalancePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Account Selector (Specific to View Balance tab)
        JLabel accountLabel = new JLabel("Select Account:");
        if (viewBalanceAccountSelector == null) {
            viewBalanceAccountSelector = new JComboBox<>();
            viewBalanceAccountSelector.setFont(new Font("SansSerif", Font.PLAIN, 12));
        }
        
        // Balance Display Label
        balanceDisplayLabel = new JLabel("Select an account to view its balance.");
        balanceDisplayLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        balanceDisplayLabel.setForeground(Color.BLUE.darker());

        // Button to trigger the balance check
        JButton checkBalanceButton = new JButton("Check Balance");
        checkBalanceButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0; panel.add(accountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(viewBalanceAccountSelector, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(checkBalanceButton, gbc);
        
        gbc.gridy = 2;
        panel.add(balanceDisplayLabel, gbc);

        // Action Listener for Check Balance Button
        checkBalanceButton.addActionListener(e -> {
            Account selectedAccount = (Account) viewBalanceAccountSelector.getSelectedItem();
            if (selectedAccount == null) {
                balanceDisplayLabel.setText("No accounts available.");
                return;
            }

            String accountNumber = selectedAccount.getAccountNumber();
            double balance = bankManager.getBalance(accountNumber);

            if (balance != -1) {
                balanceDisplayLabel.setText("Current Balance for " + accountNumber + ": " + currencyFormat.format(balance));
            } else {
                balanceDisplayLabel.setText("Error: Account not found.");
            }
        });

        return panel;
    }

    /**
     * Main method to start the application.
     */
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(BankProject::new);
    }
}