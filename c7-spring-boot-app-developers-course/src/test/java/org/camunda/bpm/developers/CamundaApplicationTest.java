package org.camunda.bpm.developers;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class CamundaApplicationTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CamundaApplicationTest(String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( CamundaApplicationTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue( true );
    }
}
