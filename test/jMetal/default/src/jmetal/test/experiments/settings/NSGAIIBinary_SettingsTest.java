package jmetal.test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.NSGAIIBinary_Settings;
import jmetal.operators.crossover.SinglePointCrossover;
import jmetal.operators.mutation.BitFlipMutation;
import jmetal.problems.ZDT.ZDT5;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 26/06/13
 * Time: 19:41
 * To change this template use File | Settings | File Templates.
 */
public class NSGAIIBinary_SettingsTest {
  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings nsgaIISettings = new NSGAIIBinary_Settings("ZDT5");
    Algorithm algorithm = nsgaIISettings.configure() ;
    Problem problem = new ZDT5("Binary") ;

    SinglePointCrossover crossover = (SinglePointCrossover)algorithm.getOperator("crossover") ;
    double pc = (Double)crossover.getParameter("probability") ;
    BitFlipMutation mutation = (BitFlipMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;

    assertEquals("NSGAIIBinary_SettingsTest", 100, ((Integer)algorithm.getInputParameter("populationSize")).intValue());
    assertEquals("NSGAIIBinary_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());

    assertEquals("NSGAIIBinary_SettingsTest", 0.9, pc, epsilon);
    assertEquals("NSGAIIBinary_SettingsTest", 1.0/problem.getNumberOfBits(), pm, epsilon);
  }
}
