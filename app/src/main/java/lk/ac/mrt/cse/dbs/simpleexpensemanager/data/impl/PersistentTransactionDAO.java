package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import java.util.Date;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.DataBaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private Context context;
    public PersistentTransactionDAO(Context c) {
        context = c;
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        dataBaseHelper.addTransaction(date,accountNo,expenseType,amount);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List <Transaction> transactions;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        transactions = dataBaseHelper.getAllTransactios();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List <Transaction> transactions;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        transactions = dataBaseHelper.getAllTransactios();
        int size = transactions.size();
        if(size<=limit){
            return transactions;
        }
        return transactions.subList(size-limit,size);
    }
}
