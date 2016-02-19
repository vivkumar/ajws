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

import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.util.JMException;

import java.io.IOException;

/**
 * Class implementing a typical experimental study using Jikes RVM Work-Stealing. 
 */
public class StandardStudy extends Experiment {

	/**
	 * Main method
	 * @param args
	 * @throws JMException
	 * @throws IOException
	 */
	public static void main(String[] args) throws JMException, IOException {
		StandardStudy exp = new StandardStudy();

		exp.experimentName_ = "StandardStudy";

		if(args.length > 0) {
			exp.algorithmNameList_ = new String[]{args[0]};
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

		exp.experimentBaseDirectory_ = Directory.outDir() + "/" +
				exp.experimentName_;
		exp.paretoFrontDirectory_ = Directory.outDir() + "/paretoFronts";

		exp.initExperiment();

		final long s = System.nanoTime();
		// Run the experiments
		exp.runExperiment() ;
		final double d = (((double)(System.nanoTime() - s))/((double)(1.0E9))) * 1000;
		System.out.printf("Time = %d msec \n",(int)d);
	} // main
} // StandardStudy


