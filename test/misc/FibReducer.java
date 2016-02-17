/*
 * Vivek Kumar
 * Adapted from https://www.cilkplus.org/tutorial-cilk-plus-reducers
 */

public class FibReducer {
  @Atomicset(F);		
  @Atomic(F) private int sum = 0;
  public int result() {
    return sum;
  }
  public void compute(int n) {
    if (n < 2) {
      sum += n;			
    }
    else {
      async { compute(n-1); }
      compute(n-2);	
    }
  }
  public static void main (String[] args) {
    int n = 40;
    FibReducer fib = new FibReducer();
    finish {
      fib.compute(n);
    }
    System.out.println("Fib("+n+") = "+fib.result());
  }
}
