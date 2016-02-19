//  StandardStudy.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.experiments.studies;

import jmetal.core.Algorithm;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.*;
import jmetal.experiments.util.Friedman;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing a typical experimental study. Five algorithms are 
 * compared when solving the ZDT, DTLZ, and WFG benchmarks, and the hypervolume,
 * spread and additive epsilon indicators are used for performance assessment.
 */
public class StandardStudy extends Experiment {

	/**
	 * Configures the algorithms in each independent run
	 * @param problemName The problem to solve
	 * @param problemIndex
	 * @throws ClassNotFoundException 
	 */
	public void algorithmSettings(String problemName, 
			int problemIndex, 
			Algorithm[] algorithm) throws ClassNotFoundException {
		try {
			int numberOfAlgorithms = algorithmNameList_.length;

			HashMap[] parameters = new HashMap[numberOfAlgorithms];

			for (int i = 0; i < numberOfAlgorithms; i++) {
				parameters[i] = new HashMap();
			} // for

			if (!paretoFrontFile_[problemIndex].equals("")) {
				for (int i = 0; i < numberOfAlgorithms; i++)
					parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
			} // if

			for (int i = 0; i < numberOfAlgorithms; i++) {
				algorithm[i] = getSettingsObject(i, problemName, parameters[i]);
			} // for
		} catch (IllegalArgumentException ex) {
			Logger.getLogger(StandardStudy.class.getName()).log(Level.SEVERE, null, ex);
		} 
	} // algorithmSettings

	/**
	 * Main method
	 * @param args
	 * @throws JMException
	 * @throws IOException
	 */
	public static void main(String[] args) throws JMException, IOException {
		StandardStudy exp = new StandardStudy();

		System.out.println("USAGE: <Threads> <Algorithm Name> <Problem Name>");
		exp.experimentName_ = "StandardStudy";

		int threads = 1;
		if(args.length > 0) {
			threads = Integer.parseInt(args[0]);
		}

		if(args.length > 1) {
			exp.algorithmNameList_ = new String[]{args[1]};
		}
		else {
			exp.algorithmNameList_ = new String[]{
//					"PAES", 
//					"AbYSS", 
////					"CellDE", 
//					"GDE3", 
////					"IBEA", 	// ==>>> VERY TIME CONSUMING
//					"MOCell", 
//					"SPEA2", //"MOCHC",	// ==>>> VERY TIME CONSUMING
//					"OMOPSO",//, "RandomSearch",
					"pSMPSO",
//					"MOEAD",	// => DOES NOT WORK. GIVES SOME FILE NOT FOUND EXCEPTION
					"pNSGAII",
			};
		}

		if(args.length > 1) {
			exp.problemList_ = new String[]{args[1]};
			String fileName = args[1] + ".pf";
			exp.paretoFrontFile_ = new String[]{fileName};
		}
		else {
//			exp.problemList_ = new String[]{"ZDT1", "Kursawe", "WFG1", "DTLZ1"};//, "WFG1", "DTLZ1"};   
//			exp.paretoFrontFile_ = new String[]{"ZDT1.pf", "Kursawe.pf", "WFG1.pf", "DTLZ1.pf"};
			exp.problemList_ = new String[]{"ZDT1", "Kursawe", "WFG1", "DTLZ1"};//, "WFG1", "DTLZ1"};   
			exp.paretoFrontFile_ = new String[]{"ZDT1.pf", "Kursawe.pf", "WFG1.pf", "DTLZ1.pf"};
		}

		int numberOfAlgorithms = exp.algorithmNameList_.length;

		exp.experimentBaseDirectory_ = Directory.outDir() + "/" +
				exp.experimentName_;
		exp.paretoFrontDirectory_ = Directory.outDir() + "/paretoFronts";

		exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

		exp.initExperiment();

		final long s = System.nanoTime();
		// Run the experiments
		exp.runExperiment(threads) ;
		final double d = (((double)(System.nanoTime() - s))/((double)(1.0E9))) * 1000;
		System.out.printf("Time = %d msec \n",(int)d);
	} // main
} // StandardStudy


