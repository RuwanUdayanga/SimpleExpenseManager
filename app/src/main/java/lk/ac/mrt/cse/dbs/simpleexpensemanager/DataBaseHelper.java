package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType.EXPENSE;

public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String ACCOUNT_TABLE = "ACCOUNT_TABLE";
    public static final String COLUMN_ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    public static final String COLUMN_BANK = "BANK";
    public static final String COLUMN_ACCOUNT_HOLDER = "ACCOUNT_HOLDER";
    public static final String ACCOUNT_BALANCE = "ACCOUNT_BALANCE";
    private static final String TRANSACTION_TABLE = "TRANSACTION_TABLE";
    private static final String COLUMN_TRANS_DATE = "TRANS_DATE";
    private static final String COLUMN_EXPENCE_TYPE = "EXPENCE_TYPE";
    private static final String COLUMN_AMOUNT = "AMOUNT";
    private static final String COLUMN_TRANS_ID = "TRANS_ID";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "200508T.db", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + ACCOUNT_TABLE + "(" + COLUMN_ACCOUNT_NUMBER + " TEXT PRIMARY KEY, " + COLUMN_BANK + " TEXT, " + COLUMN_ACCOUNT_HOLDER + " TEXT, " + ACCOUNT_BALANCE + " Double )";
        db.execSQL(createTableStatement);
        String createTableStatement1 = "CREATE TABLE " + TRANSACTION_TABLE + "(" + COLUMN_TRANS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TRANS_DATE + " TEXT, " + COLUMN_ACCOUNT_NUMBER + " TEXT, " + COLUMN_EXPENCE_TYPE + " TEXT, " + COLUMN_AMOUNT + " Double )";
        db.execSQL(createTableStatement1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean add_account(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ACCOUNT_NUMBER, account.getAccountNo());
        cv.put(COLUMN_BANK, account.getBankName());
        cv.put(COLUMN_ACCOUNT_HOLDER, account.getAccountHolderName());
        cv.put(ACCOUNT_BALANCE, account.getBalance());

        long insert = db.insert(ACCOUNT_TABLE, null, cv);
        db.close();
        if(insert==-1){
            return false;
        }
        else{
            return true;
        }
    }

    public List<String> get_Acc_Number_List() {
        List<String> returnList = new ArrayList<>();

        String queryString = "SELECT* FROM " + ACCOUNT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                String accountNumber = cursor.getString(0);
                returnList.add(accountNumber);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public List<Account> get_Acc_List() {
        List<Account> returnList = new ArrayList<>();

        String queryString = "SELECT* FROM " + ACCOUNT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.moveToFirst()) {
            do {
                String accountNumber = cursor.getString(0);
                String bank = cursor.getString(1);
                String accountHolder = cursor.getString(2);
                double Balance = cursor.getDouble(3);

                Account account = new Account(accountNumber, bank, accountHolder, Balance);
                returnList.add(account);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public Account get_Acc(String accountNo){
        Account account;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ACCOUNT_TABLE, new String[]{COLUMN_ACCOUNT_NUMBER, COLUMN_BANK, COLUMN_ACCOUNT_HOLDER,ACCOUNT_BALANCE}, COLUMN_ACCOUNT_NUMBER+ "=?",new String[]{String.valueOf(accountNo)},null, null, null, null);
        if (cursor.moveToFirst()) {
            String accountNumber = cursor.getString(0);
            String bank = cursor.getString(1);
            String accountHolder = cursor.getString(2);
            double Balance = cursor.getFloat(3);
            account = new Account(accountNumber, bank, accountHolder, Balance);
        }
        else {
            account = new Account("-1", "Invalid", "Invalid", 0);
        }
        cursor.close();
        db.close();
        return account;
    }

    public boolean updateBalance(String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        Double newBalance;
        Cursor cursor = db.query(ACCOUNT_TABLE, new String[]{COLUMN_ACCOUNT_NUMBER, COLUMN_BANK, COLUMN_ACCOUNT_HOLDER,ACCOUNT_BALANCE}, COLUMN_ACCOUNT_NUMBER+ " =? ",new String[]{String.valueOf(accountNo)},null,null,null);
        if(cursor.moveToFirst()) {
            Double currentBalance = cursor.getDouble(3);
            if(expenseType.equals(EXPENSE)){
                newBalance = currentBalance - amount;
            }
            else{
                newBalance = currentBalance + amount;
            }
            cv.put(ACCOUNT_BALANCE,newBalance);
            int count = db.update(ACCOUNT_TABLE, cv, COLUMN_ACCOUNT_NUMBER+" = ?",new String[]{String.valueOf(accountNo)});
            cursor.close();
            db.close();
            return true;
        }

        else{
            cursor.close();
            db.close();
            return false;
        }
    }

    public boolean removeAccount(String accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(ACCOUNT_TABLE, new String[]{COLUMN_ACCOUNT_NUMBER, COLUMN_BANK, COLUMN_ACCOUNT_HOLDER,ACCOUNT_BALANCE}, COLUMN_ACCOUNT_NUMBER+ " =? ",new String[]{String.valueOf(accountNo)},null,null,null);

        if (cursor.moveToFirst()) {
            db.delete(ACCOUNT_TABLE, COLUMN_ACCOUNT_NUMBER+" = ?",new String[]{String.valueOf(accountNo)});
            cursor.close();
            db.close();
            return true;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }

    // Helper methods for TransactonDAO

    public void addTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TRANS_DATE, String.valueOf(date));
        cv.put(COLUMN_ACCOUNT_NUMBER, accountNo);
        cv.put(COLUMN_EXPENCE_TYPE, String.valueOf(expenseType));
        cv.put(COLUMN_AMOUNT, amount);

        db.insert(TRANSACTION_TABLE, null, cv);

        db.close();
    }

    public List<Transaction> getAllTransactios() {
        List<Transaction> resultList = new ArrayList<>();
        String queryString = "SELECT* FROM " + TRANSACTION_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()){
            do{
                Date transDate = new Date(cursor.getString(1));
                String accNumber = cursor.getString(2);
                ExpenseType expenceType = ExpenseType.valueOf(cursor.getString(3));
                Double amount = cursor.getDouble(4);

                Transaction transaction = new Transaction(transDate,accNumber,expenceType,amount);
                resultList.add(transaction);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resultList;
    }
}