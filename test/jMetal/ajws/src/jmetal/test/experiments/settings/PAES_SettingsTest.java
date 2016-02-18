package jmetal.test.experiments.settings;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.PAES_Settings;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.problems.Fonseca;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: antelverde
 * Date: 27/06/13
 * Time: 07:36
 * To change this template use File | Settings | File Templates.
 */
public class PAES_SettingsTest {
  @Test
  public void testConfigure() throws Exception {
    double epsilon = 0.000000000000001 ;
    Settings paesSettings = new PAES_Settings("Fonseca");
    Algorithm algorithm = paesSettings.configure() ;
    Problem problem = new Fonseca("Real") ;

    PolynomialMutation mutation = (PolynomialMutation)algorithm.getOperator("mutation") ;
    double pm = (Double)mutation.getParameter("probability") ;
    double dim = (Double)mutation.getParameter("distributionIndex") ;

    assertEquals("PAES_SettingsTest", 100, ((Integer)algorithm.getInputParameter("archiveSize")).intValue());
    assertEquals("PAES_SettingsTest", 25000, ((Integer)algorithm.getInputParameter("maxEvaluations")).intValue());
    assertEquals("PAES_SettingsTest", 1.0/problem.getNumberOfVariables(), pm, epsilon);
    assertEquals("PAES_SettingsTest", 20.0, dim, epsilon);
    assertEquals("PAES_SettingsTest", 5, ((Integer)algorithm.getInputParameter("biSections")).intValue());
  }
}
