
/**
 * Borrowed from AJ and Habanero-Java library
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

class Transfer {
	final Account a1;
	final Account a2;
	private final int amount;

	public Transfer(Account a1, Account a2, int amount){
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

		Transfer[] transfer = new Transfer[3];
		transfer[0] = new Transfer(a1, a2, 50);
		transfer[1] = new Transfer(a2, a1, 30);
		transfer[2] = new Transfer(a3, a1, 30);
		
		finish {
			for(int i=0; i<transfer.length; i++) {
				async {
					transfer[i].run();
				}
			}
		}
		
	}
}

