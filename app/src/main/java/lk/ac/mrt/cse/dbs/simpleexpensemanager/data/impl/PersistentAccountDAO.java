package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.DataBaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private Context context;
    public PersistentAccountDAO(Context c) {
        context = c;
    }
    @Override
    public List<String> getAccountNumbersList() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        List<String> accountNumberList;
        accountNumberList = dataBaseHelper.get_Acc_Number_List();
        return accountNumberList;
    }

    @Override
    public List<Account> getAccountsList() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        List<Account> accountList;
        accountList = dataBaseHelper.get_Acc_List();
        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        Account account = dataBaseHelper.get_Acc(accountNo);
        if(account.getAccountNo().equals("-1")){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else {
            return account;
        }
    }

    @Override
    public void addAccount(Account account) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        boolean success = dataBaseHelper.add_account(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        boolean success = dataBaseHelper.removeAccount(accountNo);

        if(!success){
            String msg = "Account " + accountNo + " is does not exist.";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        boolean success = dataBaseHelper.updateBalance(accountNo,expenseType,amount);
        if(!success){
            String msg = "Account " + accountNo + " is does not exist.";
            throw new InvalidAccountException(msg);
        }
    }
}
