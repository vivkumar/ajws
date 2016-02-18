//  runExperiment.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//       Jorge Rodriguez
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo, Jorge Rodriguez
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

package jmetal.experiments.util;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.experiments.Experiment;
import jmetal.experiments.Settings;
import jmetal.util.JMException;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Class implementing the steps to run an experiment
 */
public class RunExperiment {

	public Experiment experiment_ ;
	public HashMap<String, Object> map_ ;
	public int numberOfProblems_ ;
	public int iter;

	String experimentName_;
	String[] algorithmNameList_; // List of the names of the algorithms to be executed
	String[] problemList_; // List of problems to be solved
	String[] paretoFrontFile_; // List of the files containing the pareto fronts
	// corresponding to the problems in problemList_
	String[] indicatorList_; // List of the quality indicators to be applied
	String experimentBaseDirectory_; // Directory to store the results
	String latexDirectory_; // Directory to store the latex files
	String rDirectory_; // Directory to store the generated R scripts
	String paretoFrontDirectory_; // Directory containing the Pareto front files
	String outputParetoFrontFile_; // Name of the file containing the output
	// Pareto front
	String outputParetoSetFile_; // Name of the file containing the output
	// Pareto set
	Settings[] algorithmSettings_; // Paremeter experiments.settings of each algorithm
	

	public RunExperiment(Experiment experiment, int i, 
			HashMap<String, Object> map,
			int numberOfProblems) {
		experiment_ = experiment ;
		iter = i;
		map_ = map ;
		numberOfProblems_ = numberOfProblems;
	}

	public void solveAllProblems(int algo_id) {
			async {	// 
				for(int i=0; i<problemList_.length; i++) {
					solveIndividualProblem(algo_id, i);
				}
			}
	}
	
	public void solveIndividualProblem(int algo_id, int prob_id) {
		// STEP 2: get the problem from the list
		String problemName = problemList_[prob_id];
		
		// STEP 3: check the file containing the Pareto front of the problem
		
		// STEP 4: configure the algorithms
		Algorithm algorithm = null; // jMetal algorithms to be executed
		try {
			
			algorithm = experiment_.algorithmSettings(problemName, prob_id, algo_id);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(algorithm == null) {
			System.out.println("ERROR: For algorithm "+algorithmNameList_[algo_id]+" Settings Object is NULL");
			System.exit(-1);
		}
		
		// STEP 5: run the algorithms
		
		// STEP 6: create output directories
		String directory = experimentBaseDirectory_ + "/data/" + algorithmNameList_[algo_id] + "/" +
				problemList_[prob_id];
		File experimentDirectory = new File(directory);
		if (!experimentDirectory.exists()) {
			boolean result = new File(directory).mkdirs();
			System.out.println("Creating " + directory);
		}
		// STEP 7: run the algorithm
		System.out.println(Thread.currentThread().getName() + " Running algorithm: " + 
				algorithmNameList_[algo_id] +
				", problem: " + problemList_[prob_id] +
				", run: " + iter);
		SolutionSet resultFront = null;
		try {
			try {
				resultFront= algorithm.execute();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JMException ex) {
			Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		// STEP 8: put the results in the output directory
		resultFront.printObjectivesToFile(directory + "/" + outputParetoFrontFile_ + "." + iter);
		resultFront.printVariablesToFile(directory + "/" + outputParetoSetFile_ + "." + iter);
	}
	
	public void run() {
		String experimentName = (String) map_.get("experimentName");
		experimentBaseDirectory_ = (String) map_.get("experimentDirectory");
		algorithmNameList_ = (String[]) map_.get("algorithmNameList");
		problemList_ = (String[]) map_.get("problemList");
		indicatorList_ = (String[]) map_.get("indicatorList");
		paretoFrontDirectory_ = (String) map_.get("paretoFrontDirectory");
		paretoFrontFile_ = (String[]) map_.get("paretoFrontFile");
		outputParetoFrontFile_ = (String) map_.get("outputParetoFrontFile");
		outputParetoSetFile_ = (String) map_.get("outputParetoSetFile");

		finish {		// 
			async {
				for(int i=0; i<algorithmNameList_.length; i++) {
					solveAllProblems(i);
				}
			}
		}
	}
}
