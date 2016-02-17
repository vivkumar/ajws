
/**
 * Borrowed from HJlib (Habanero-Java library)
 * Original Author: Shams Imam
 * Modifed by Vivek Kumar
 */

import java.util.Random;
import java.io.IOException;
import java.util.Random;

class Account {
	@Atomicset(A);
	@Atomic(A) private double balance;
	private long id;

	public Account(long id) {
		this.id = id;
	}

	public long id() {
		return id;
	}

	public void credit(double amount) {
		balance += amount;
	}

	public void debit(double amount) {
		balance -= amount;
	}

	public double getBalance() {
		return balance;
	}

	public double getInterest(double interestRate) {
		return (balance*interestRate);
	}

	public void addInterest(double interestRate) {
		balance += getInterest(interestRate);
	}
}

class LinkedAccount extends Account {
	final @AliasAtomic(A=this.A) Account parentAccount; 

	LinkedAccount(@AliasAtomic(A=this.A) Account parent, int id){ 
		super(id);
		parentAccount=parent;
	}

	public void debit(double amt){
		parentAccount.debit(amt);
	}

}

class TransferThread extends Thread {
	final Account a1;
	final Account a2;
	private final int amount;

	public TransferThread(Account a1, Account a2, int amount){
		this.a1 = a1;
		this.a2 = a2;
		this.amount = amount;
	}

	@Atomic(a2.A) @Atomic(a1.A)  
	public void run(){
		a2.credit(amount);
		a1.debit(amount);
	}
}

public class Bank {
	public static void main(String[] args){
		final Account a1 = new Account(1);
		final Account a2 = new Account(2);
		final LinkedAccount a3 = new LinkedAccount(a2, 3);

		Thread t1 = new TransferThread(a1, a2, 50);
		Thread t2 = new TransferThread(a2, a1, 30);
		Thread t3 = new TransferThread(a3, a1, 30);
		t1.start();
		t2.start();
		t3.start();
	}
}
