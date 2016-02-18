package jmetal.test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.SPEA2_Settings;
import jmetal.operators.crossover.SBXCrossover;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Fonseca;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 27/06/13
 * Time: 23:12
 * To change this template use File | Settings | File Templates.
 */
public class SPEA2_SettingsTest {
  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings SPEA2Settings = new SPEA2_Settings("Fonseca");
    Algorithm algorithm = SPEA2Settings.configure() ;
    Problem problem = new Fonseca("Real") ;
    SBXCrossover crossover = (SBXCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    double dic = (Double)crossover.getParameter("distributionIndex") ;
    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    assertEquals("SPEA2_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("SPEA2_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("SPEA2_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());

    assertEquals("SPEA2_SettingsTest", 0.9, pc, epsilon);
    assertEquals("SPEA2_SettingsTest", 20.0, dic, epsilon);

    assertEquals("SPEA2_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    assertEquals("SPEA2_SettingsTest", 20.0, dim, epsilon);
  }
}
