import java.util.Random;
import java.io.IOException;
import java.util.Random;

import ajws.OrderedLock;
class Account implements ajws.Atomic {
public final OrderedLock getLockForA() {
return _$lock_A;
}
public final OrderedLock getLock() {
return this.getLockForA();
}
protected final OrderedLock _$lock_A;
  private double balance;
  private long id;
  public Account(long id) {
  this(id, new OrderedLock());
  }
  public Account(long id, OrderedLock A) {
    super();
  _$lock_A = A;
    this.id = id;
  }
  public long id(){
    return id;
  }
  public long id_internal(){
    return id;
  }
  public void credit(double amount){
    synchronized(_$lock_A) {
    balance += amount;
  }
    }
  public void credit_internal(double amount){
    balance += amount;
  }
  public void debit(double amount){
    synchronized(_$lock_A) {
    balance -= amount;
  }
    }
  public void debit_internal(double amount){
    balance -= amount;
  }
  public double getBalance(){
    synchronized(_$lock_A) {
    return balance;
  }
    }
  public double getBalance_internal(){
    return balance;
  }
  public double getInterest(double interestRate){
    synchronized(_$lock_A) {
    return (balance * interestRate);
  }
    }
  public double getInterest_internal(double interestRate){
    return (balance * interestRate);
  }
  public void addInterest(double interestRate){
    synchronized(_$lock_A) {
    balance += getInterest_internal(interestRate);
  }
    }
  public void addInterest_internal(double interestRate){
    balance += getInterest_internal(interestRate);
  }
}

class LinkedAccount extends Account implements ajws.Atomic {
  final Account parentAccount;
  LinkedAccount(Account parent, int id) {
  this(parent, id, new OrderedLock());
  }
  LinkedAccount(Account parent, int id, OrderedLock A) {
  super(id, A);
    parentAccount = parent;
  }
  public void debit(double amt){
    synchronized(_$lock_A) {
    parentAccount.debit_internal(amt);
  }
    }
  public void debit_internal(double amt){
    parentAccount.debit_internal(amt);
  }
}

class TransferThread extends Thread {
  final Account a1;
  final Account a2;
  private final int amount;
  public TransferThread(Account a1, Account a2, int amount) {
    super();
    this.a1 = a1;
    this.a2 = a2;
    this.amount = amount;
  }
  public void run()
  {
  ajws.OrderedLock l1 = null, l2 = null;
  
  ajws.OrderedLock l3 = a2.getLockForA();
  ajws.OrderedLock l4 = a1.getLockForA();
  if (l3.getIndex() > l4.getIndex()) {
  l1 = l3;
  l2 = l4;
  } else {
  l1 = l4;
  l2 = l3;
  }
  synchronized(l1) {
  synchronized(l2) {{
    a2.credit(amount);
    a1.debit(amount);
  }
  }
  }
  }
  public void run_internal(){
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
