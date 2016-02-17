
/**
 * Borrowed from AJ and Habanero-Java library
 * Modifed by Vivek Kumar
 */

import java.util.Random;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Account {
	private double balance;
	private long id;
	private final Lock lock;

	public Account(long id) {
		this.id = id;
		this.lock = new ReentrantLock();
	}

	public long id() {
		return id;
	}

	public void lock() {
		lock.lock();
	}
	
	public void unlock() {
		lock.unlock();
	}
	
	public void credit(double amount) {
		lock();
		balance += amount;
		unlock();
	}

	public void debit(double amount) {
		lock();
		balance -= amount;
		unlock();
	}

	public double getBalance() {
		lock();
		final double bal = balance;
		unlock();
		return bal;
	}

	public double getInterest(double interestRate) {
		lock();
		final double interest = (balance*interestRate);
		unlock();
		return interest;
	}

	public void addInterest(double interestRate) {
		lock();
		balance += getInterest(interestRate);
		unlock();
	}
}

class LinkedAccount extends Account {
	final Account parentAccount; 

	LinkedAccount(Account parent, int id){ 
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

	public void run(){
		a2.lock(); 
		try { Thread.sleep(1000); }catch(InterruptedException e){} // added to show the deadlock
		a1.lock();
		a2.credit(amount);
		a1.debit(amount);
		a2.unlock(); 
		a1.unlock();
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
