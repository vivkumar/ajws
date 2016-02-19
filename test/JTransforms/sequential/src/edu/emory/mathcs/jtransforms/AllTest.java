package edu.emory.mathcs.jtransforms;

import edu.emory.mathcs.jtransforms.dct.*;
import edu.emory.mathcs.jtransforms.dht.*;
import edu.emory.mathcs.jtransforms.dst.*;
import edu.emory.mathcs.jtransforms.fft.*;

public class AllTest {
	public static void main(String[] args) {
		final long startTime = System.currentTimeMillis();
		System.out.println("1) Starting all tests in benchmark BenchmarkDoubleDCT =>");
		BenchmarkDoubleDCT.testAll(args);
		System.out.println("2) Starting all tests in benchmark BenchmarkFloatDCT =>");
		BenchmarkFloatDCT.testAll(args);
		System.out.println("3) Starting all tests in benchmark BenchmarkDoubleDHT =>");
		BenchmarkDoubleDHT.testAll(args);
		System.out.println("4) Starting all tests in benchmark BenchmarkFloatDHT =>");
		BenchmarkFloatDHT.testAll(args);
		System.out.println("5) Starting all tests in benchmark BenchmarkDoubleDST =>");
		BenchmarkDoubleDST.testAll(args);
		System.out.println("6) Starting all tests in benchmark BenchmarkFloatDST =>");
		BenchmarkFloatDST.testAll(args);
		System.out.println("7) Starting all tests in benchmark BenchmarkDoubleFFT =>");
		BenchmarkDoubleFFT.testAll(args);
		System.out.println("8) Starting all tests in benchmark BenchmarkFloatFFT =>");
		BenchmarkFloatFFT.testAll(args);
		final long time = System.currentTimeMillis() - startTime;
		final double secs = ((double)time) / 1000.0;
		System.out.println("Total Time: " + secs);
	}

}
