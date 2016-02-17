/*
 * nqueens.hj was ported from the BOTS nqueens.c benchmark.  See below for provenance.
 *
 * @author Jun Shirako, Rice University
 * @author Vivek Sarkar, Rice University
 * 
 * This program computes all solutions to the n-queens problem where n is specified in argv[0] (default = 12),
 * and repeats the computation "repeat" times where "repeat" is specifies in argv[1] (default = ).
 * There is a cutoff value specified as an optional third parameter in argv[1] (default = 3)
 * that is used in the async seq clause to specify when a new async should be created.
 *
 * The program uses the count of the total number of solutions as a correctness check and also prints the execution time for each repetition.
 * The java AtomicInteger class is used to accumulate the total count as an illustration of how non-blocking operations in java.util.concurrent
 * can be used in conjunction with HJ.
 *
 * Note the use of single "finish" statement in find_queens() that awaits termination of all
 * async's created by the recursive calls to nqueens_kernel.
 * 
 * This program is a good example to illustrate the performance benefits of work-stealing vs. work-sharing schedulers.
 * Try "hjc nqueens.hj" to create a work-sharing implementation (default) and "hjc -rt w nqueens.hj" to create a work-stealing
 * implementation, and compare their performance by executing "hj nqueens 12 5 3" to solve a 12-queens problem with 5 repetitions and a cutoff at depth 3.
 * To study scalability on a multicore processor, you can execute "hj -places 1:<w> nqueens 12 5 3", where <w> is the number of worker threads.
 * Since "12 5 3" are default values, you can obtain the same measurements by executing "hj nqueens" and "hj -places 1:<w> nqueens"
 */

/**********************************************************************************************/
/*  This program is part of the Barcelona OpenMP Tasks Suite                                  */
/*  Copyright (C) 2009 Barcelona Supercomputing Center - Centro Nacional de Supercomputacion  */
/*  Copyright (C) 2009 Universitat Politecnica de Catalunya                                   */
/*                                                                                            */
/*  This program is free software; you can redistribute it and/or modify                      */
/*  it under the terms of the GNU General Public License as published by                      */
/*  the Free Software Foundation; either version 2 of the License, or                         */
/*  (at your option) any later version.                                                       */
/*                                                                                            */
/*  This program is distributed in the hope that it will be useful,                           */
/*  but WITHOUT ANY WARRANTY; without even the implied warranty of                            */
/*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                             */
/*  GNU General Public License for more details.                                              */
/*                                                                                            */
/*  You should have received a copy of the GNU General Public License                         */
/*  along with this program; if not, write to the Free Software                               */
/*  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA            */
/**********************************************************************************************/

/*
 * Original code from the Cilk project (by Keith Randall)
 * 
 * Copyright (c) 2000 Massachusetts Institute of Technology
 * Copyright (c) 2000 Matteo Frigo
 */


public class NQueens {
	// STATIC FIELDS
	public static int[] solutions = {
		1,
		0,
		0,
		2,
		10, /* 5 */
		4,
		40,
		92,
		352,
		724, /* 10 */
		2680,
		14200,
		73712,
		365596,
		2279184, 
		14772512
	};
	@Atomicset(S);
	@Atomic(S) private int nSolutions;
	private int[] A;
	private int size;

	public static void main(String[] args)  {
		int size = 12;
		if (args.length > 0)
		size = Integer.parseInt(args[0]);
		System.out.println("Size=" + size);
		int[] A = new int[0];
		NQueens nq = new NQueens(A, size);
		nq.compute();
	}

	public NQueens(int[] A, int size) {
		this.A = A;
		this.nSolutions = 0;
		this.size = size;
	}

	public void compute() {
		final long startTime = System.currentTimeMillis();
		nqueens(A, 0);
		final long time = System.currentTimeMillis() - startTime;
		final double secs = ((double)time) / 1000.0;
		verify_queens(size);
		System.out.println("Time = "+secs);
	}

	private void nqueens(int[] A, int depth) {
		if (size == depth) {
			nSolutions++;
			return;
		}

		/* try each possible position for queen <depth> */
		finish {
			async {
				for (int i=0; i < size; i++) {
					/* allocate a temporary array and copy <a> into it */
					int[] B = new int[depth+1];
					System.arraycopy(A, 0, B, 0, depth);
					B[depth] = i;
					boolean status = ok((depth +  1), B); 
					if (status) {
						nqueens(B, depth+1);
					}
				}
			}
		}
	}

	private void nqueens_kernel(int[] A, int depth, int i) {
	}

	/*
	 * <A> contains array of <n> queen positions.  Returns 1
	 * if none of the queens conflict, and returns 0 otherwise.
	 */
	private boolean ok(int n,  int[] A) {
		for (int i =  0; i < n; i++) {
			final int p = A[i];

			for (int j =  (i +  1); j < n; j++) {
				final int q = A[j];
				if (q == p || q == p - (j - i) || q == p + (j - i))
					return false;
			}
		}
		return true;
	}

	private void verify_queens(int size) {
		if (nSolutions == solutions[size-1] )
			System.out.println("OK");
		else
			System.out.println("Incorrect answer");
	}
}
