import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Transaction Record Class
class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private double amount;
    private double balanceAfter;
    private String timestamp;
    
    public Transaction(String type, double amount, double balanceAfter) {
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public double getBalanceAfter() { return balanceAfter; }
    public String getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("%s | %s | Amount: ₹%.2f | Balance: ₹%.2f", 
            timestamp, type, amount, balanceAfter);
    }
}

// Bank Account Class
class BankAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private String accountNumber;
    private String accountHolderName;
    private int pin;
    private double balance;
    private List<Transaction> transactionHistory;
    private static final double MAX_WITHDRAWAL = 50000.0;
    private static final double MAX_DEPOSIT = 100000.0;
    private static final double MIN_BALANCE = 500.0;
    
    public BankAccount(String accountNumber, String accountHolderName, int pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }
    
    public String getAccountNumber() { return accountNumber; }
    public String getAccountHolderName() { return accountHolderName; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }
    
    public boolean verifyPin(int enteredPin) {
        return pin == enteredPin;
    }
    
    public boolean deposit(double amount) {
        if (amount <= 0) {
            System.out.println("❌ Invalid amount! Amount must be greater than zero.");
            return false;
        }
        if (amount > MAX_DEPOSIT) {
            System.out.println("❌ Deposit limit exceeded! Maximum deposit per transaction: ₹" + MAX_DEPOSIT);
            return false;
        }
        balance += amount;
        transactionHistory.add(new Transaction("DEPOSIT", amount, balance));
        System.out.println("✅ Amount Deposited Successfully!");
        System.out.println("💰 New Balance: ₹" + String.format("%.2f", balance));
        return true;
    }
    
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("❌ Invalid amount! Amount must be greater than zero.");
            return false;
        }
        if (amount > balance) {
            System.out.println("❌ Insufficient Balance!");
            System.out.println("💰 Available Balance: ₹" + String.format("%.2f", balance));
            return false;
        }
        if (balance - amount < MIN_BALANCE) {
            System.out.println("❌ Minimum balance requirement! You must maintain at least ₹" + MIN_BALANCE);
            return false;
        }
        if (amount > MAX_WITHDRAWAL) {
            System.out.println("❌ Withdrawal limit exceeded! Maximum withdrawal per transaction: ₹" + MAX_WITHDRAWAL);
            return false;
        }
            balance -= amount;
        transactionHistory.add(new Transaction("WITHDRAWAL", amount, balance));
            System.out.println("✅ Withdrawal Successful!");
        System.out.println("💰 Remaining Balance: ₹" + String.format("%.2f", balance));
        return true;
    }
    
    public void addTransaction(String type, double amount) {
        transactionHistory.add(new Transaction(type, amount, balance));
    }
}

// Account Manager Class for File Persistence
class AccountManager {
    private static final String DATA_FILE = "atm_accounts.dat";
    private Map<String, BankAccount> accounts;
    private static final String BANK_CODE = "1234"; // Bank identifier
    
    public AccountManager() {
        accounts = new HashMap<>();
        loadAccounts();
    }
    
    public void addAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
        saveAccounts();
    }
    
    public BankAccount getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
    
    public boolean accountExists(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }
    
    // Generate a realistic 12-digit account number
    public String generateAccountNumber() {
        Random random = new Random();
        // Format: BANK_CODE (4 digits) + Branch (2 digits) + Account (6 digits) = 12 digits
        String branchCode = String.format("%02d", random.nextInt(100)); // 00-99
        String accountSeq = String.format("%06d", random.nextInt(1000000)); // 000000-999999
        String accountNumber = BANK_CODE + branchCode + accountSeq;
        
        // Ensure uniqueness
        while (accountExists(accountNumber)) {
            branchCode = String.format("%02d", random.nextInt(100));
            accountSeq = String.format("%06d", random.nextInt(1000000));
            accountNumber = BANK_CODE + branchCode + accountSeq;
        }
        
        return accountNumber;
    }
    
    public boolean validateAccountNumber(String accountNumber) {
        // Account number must be exactly 12 digits
        return accountNumber != null && accountNumber.matches("\\d{12}");
    }
    
    @SuppressWarnings("unchecked")
    private void loadAccounts() {
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                accounts = (Map<String, BankAccount>) ois.readObject();
                ois.close();
                fis.close();
                System.out.println("✅ Account data loaded successfully!");
            } else {
                // Create default account for demo with realistic account number
                BankAccount defaultAccount = new BankAccount("123456789012", "Rajesh Kumar", 1234, 50000.0);
                accounts.put(defaultAccount.getAccountNumber(), defaultAccount);
                saveAccounts();
                System.out.println("✅ Default account created!");
                System.out.println("   Account Number: 123456789012");
                System.out.println("   Account Holder: Rajesh Kumar");
                System.out.println("   PIN: 1234");
                System.out.println("   Initial Balance: ₹50,000");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading accounts. Creating default account...");
            BankAccount defaultAccount = new BankAccount("123456789012", "Rajesh Kumar", 1234, 50000.0);
            accounts.put(defaultAccount.getAccountNumber(), defaultAccount);
            saveAccounts();
        }
    }
    
    private void saveAccounts() {
        try {
            FileOutputStream fos = new FileOutputStream(DATA_FILE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(accounts);
            oos.close();
            fos.close();
        } catch (Exception e) {
            System.out.println("❌ Error saving account data: " + e.getMessage());
        }
    }
    
    public void saveAccount(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
        saveAccounts();
    }
}

// ATM Machine Class
class ATM {
    private AccountManager accountManager;
    private BankAccount currentAccount;
    private Scanner scanner;
    private int loginAttempts;
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    public ATM() {
        this.accountManager = new AccountManager();
        this.scanner = new Scanner(System.in);
        this.loginAttempts = 0;
    }
    
    public void start() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         WELCOME TO ATM SYSTEM");
        System.out.println("=".repeat(50));
        
        while (true) {
            System.out.println("\n1. Login to Existing Account");
            System.out.println("2. Create New Account");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1:
                        if (authenticate()) {
                            showMenu();
                            return;
                        } else {
                            System.out.println("\n❌ Maximum login attempts exceeded. System locked!");
                            System.out.println("Please contact your bank for assistance.");
                            return;
                        }
                    case 2:
                        createNewAccount();
                        break;
                    case 3:
                        System.out.println("\n👋 Thank you for using the ATM System!");
                        return;
                    default:
                        System.out.println("❌ Invalid option! Please choose 1-3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }
    
    private void createNewAccount() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         CREATE NEW ACCOUNT");
        System.out.println("=".repeat(50));
        
        try {
            System.out.print("Enter Account Holder Name: ");
            String name = scanner.nextLine().trim();
            
            if (name.isEmpty()) {
                System.out.println("❌ Name cannot be empty!");
                return;
            }
            
            System.out.print("Enter 4-digit PIN: ");
            String pinStr = scanner.nextLine().trim();
            
            if (!pinStr.matches("\\d{4}")) {
                System.out.println("❌ PIN must be exactly 4 digits!");
                return;
            }
            
            int pin = Integer.parseInt(pinStr);
            
            System.out.print("Enter Initial Deposit Amount: ₹");
            double initialBalance = Double.parseDouble(scanner.nextLine().trim());
            
            if (initialBalance < 0) {
                System.out.println("❌ Initial balance cannot be negative!");
                return;
            }
            
            // Generate realistic account number
            String accountNumber = accountManager.generateAccountNumber();
            
            // Create new account
            BankAccount newAccount = new BankAccount(accountNumber, name, pin, initialBalance);
            accountManager.addAccount(newAccount);
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("✅ ACCOUNT CREATED SUCCESSFULLY!");
            System.out.println("=".repeat(50));
            System.out.println("Account Number: " + accountNumber);
            System.out.println("Account Holder: " + name);
            System.out.println("Initial Balance: ₹" + String.format("%.2f", initialBalance));
            System.out.println("=".repeat(50));
            System.out.println("\n⚠️ Please remember your Account Number and PIN!");
            System.out.println("You can now login with your credentials.");
            
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input! Please enter valid numeric values.");
        } catch (Exception e) {
            System.out.println("❌ Error creating account: " + e.getMessage());
        }
    }
    
    private boolean authenticate() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              LOGIN");
        System.out.println("=".repeat(50));
        
        while (loginAttempts < MAX_LOGIN_ATTEMPTS) {
            try {
                System.out.print("\nEnter Account Number (12 digits): ");
                String accountNumber = scanner.nextLine().trim();
                
                // Validate account number format
                if (!accountManager.validateAccountNumber(accountNumber)) {
                    System.out.println("❌ Invalid account number format! Account number must be 12 digits.");
                    loginAttempts++;
                    if (loginAttempts < MAX_LOGIN_ATTEMPTS) {
                        System.out.println("⚠️ Attempts remaining: " + (MAX_LOGIN_ATTEMPTS - loginAttempts));
                    }
                    continue;
                }
                
                if (!accountManager.accountExists(accountNumber)) {
                    System.out.println("❌ Account not found! Please check your account number.");
                    loginAttempts++;
                    if (loginAttempts < MAX_LOGIN_ATTEMPTS) {
                        System.out.println("⚠️ Attempts remaining: " + (MAX_LOGIN_ATTEMPTS - loginAttempts));
                    }
                    continue;
                }
                
                System.out.print("Enter PIN: ");
                int pin = Integer.parseInt(scanner.nextLine().trim());
                
                currentAccount = accountManager.getAccount(accountNumber);
                if (currentAccount.verifyPin(pin)) {
                    System.out.println("\n✅ Login Successful!");
                    System.out.println("Welcome, " + currentAccount.getAccountHolderName() + "!");
                    loginAttempts = 0;
                    return true;
                } else {
                    System.out.println("❌ Invalid PIN!");
                    loginAttempts++;
                    if (loginAttempts < MAX_LOGIN_ATTEMPTS) {
                        System.out.println("⚠️ Attempts remaining: " + (MAX_LOGIN_ATTEMPTS - loginAttempts));
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter numeric values.");
                loginAttempts++;
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                loginAttempts++;
            }
        }
        return false;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("              ATM MENU");
            System.out.println("=".repeat(50));
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transaction History");
            System.out.println("5. Change PIN");
            System.out.println("6. Mini Statement");
            System.out.println("7. Exit");
            System.out.println("=".repeat(50));
            System.out.print("Choose an option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

            switch (choice) {
                case 1:
                    checkBalance();
                    break;
                case 2:
                    deposit();
                    break;
                case 3:
                    withdraw();
                    break;
                case 4:
                        showTransactionHistory();
                        break;
                    case 5:
                        changePin();
                        break;
                    case 6:
                        miniStatement();
                        break;
                    case 7:
                        logout();
                    return;
                default:
                        System.out.println("❌ Invalid Option! Please choose 1-7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number between 1-7.");
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
        }
    }

    private void checkBalance() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("           ACCOUNT BALANCE");
        System.out.println("-".repeat(50));
        System.out.println("Account Number: " + currentAccount.getAccountNumber());
        System.out.println("Account Holder: " + currentAccount.getAccountHolderName());
        System.out.println("💰 Current Balance: ₹" + String.format("%.2f", currentAccount.getBalance()));
        System.out.println("-".repeat(50));
    }

    private void deposit() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("           DEPOSIT MONEY");
        System.out.println("-".repeat(50));
        try {
            System.out.print("Enter amount to deposit: ₹");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            
            if (currentAccount.deposit(amount)) {
                accountManager.saveAccount(currentAccount);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount! Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void withdraw() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("          WITHDRAW MONEY");
        System.out.println("-".repeat(50));
        try {
            System.out.print("Enter amount to withdraw: ₹");
            double amount = Double.parseDouble(scanner.nextLine().trim());
            
            if (currentAccount.withdraw(amount)) {
                accountManager.saveAccount(currentAccount);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount! Please enter a valid number.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void showTransactionHistory() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("        TRANSACTION HISTORY");
        System.out.println("=".repeat(50));
        List<Transaction> history = currentAccount.getTransactionHistory();
        
        if (history.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.printf("%-20s | %-12s | %-15s | %-15s%n", 
                "Date & Time", "Type", "Amount", "Balance After");
            System.out.println("-".repeat(70));
            for (Transaction t : history) {
                System.out.printf("%-20s | %-12s | ₹%-14.2f | ₹%-14.2f%n",
                    t.getTimestamp(), t.getType(), t.getAmount(), t.getBalanceAfter());
            }
        }
        System.out.println("=".repeat(50));
    }
    
    private void changePin() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("            CHANGE PIN");
        System.out.println("-".repeat(50));
        try {
            System.out.print("Enter current PIN: ");
            int currentPin = Integer.parseInt(scanner.nextLine().trim());
            
            if (!currentAccount.verifyPin(currentPin)) {
                System.out.println("❌ Incorrect current PIN!");
                return;
            }
            
            System.out.print("Enter new PIN (4 digits): ");
            String newPinStr = scanner.nextLine().trim();
            
            if (newPinStr.length() != 4 || !newPinStr.matches("\\d+")) {
                System.out.println("❌ PIN must be exactly 4 digits!");
                return;
            }
            
            int newPin = Integer.parseInt(newPinStr);
            System.out.print("Confirm new PIN: ");
            int confirmPin = Integer.parseInt(scanner.nextLine().trim());
            
            if (newPin != confirmPin) {
                System.out.println("❌ PINs do not match!");
                return;
            }
            
            // Update PIN (in real system, this would be encrypted)
            System.out.println("✅ PIN changed successfully!");
            System.out.println("⚠️ Note: In production, PIN would be encrypted and stored securely.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input! Please enter numeric values.");
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void miniStatement() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           MINI STATEMENT");
        System.out.println("=".repeat(50));
        System.out.println("Account Number: " + currentAccount.getAccountNumber());
        System.out.println("Account Holder: " + currentAccount.getAccountHolderName());
        System.out.println("Current Balance: ₹" + String.format("%.2f", currentAccount.getBalance()));
        System.out.println("-".repeat(50));
        
        List<Transaction> history = currentAccount.getTransactionHistory();
        int recentCount = Math.min(5, history.size());
        
        if (recentCount > 0) {
            System.out.println("Last " + recentCount + " transactions:");
            System.out.println("-".repeat(50));
            List<Transaction> recent = history.subList(history.size() - recentCount, history.size());
            for (Transaction t : recent) {
                System.out.println(t);
            }
        } else {
            System.out.println("No transactions found.");
        }
        System.out.println("=".repeat(50));
    }
    
    private void logout() {
        accountManager.saveAccount(currentAccount);
        System.out.println("\n✅ Account data saved successfully!");
        System.out.println("👋 Thank you for using the ATM!");
        System.out.println("Have a great day!");
    }
}

// Main Class
public class ATMInterface {
    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.start();
    }
}
