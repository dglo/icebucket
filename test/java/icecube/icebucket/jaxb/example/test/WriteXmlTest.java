/*
 * class: WriteXmlTest
 *
 * Version $Id: WriteXmlTest.java,v 1.1 2005/09/22 11:19:52 patton Exp $
 *
 * Date: September 19 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.icebucket.jaxb.example.test;

import icecube.icebucket.jaxb.example.WriteXml;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * This class defines the tests that any WriteXml object should pass.
 *
 * @author patton
 * @version $Id: WriteXmlTest.java,v 1.1 2005/09/22 11:19:52 patton Exp $
 */
public class WriteXmlTest
        extends TestCase
{

    // public static final member data

    // protected static final member data

    // static final member data

    // private static final member data

    // private static member data

    // private instance member data

    /**
     * The object being tested.
     */
    private WriteXml testObject;

    // constructors

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public WriteXmlTest(String name)
    {
        super(name);
    }

    // instance member method (alphabetic)

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     *
     * @throws Exception if super class setUp fails.
     */
//    protected void setUp()
//            throws Exception
//    {
//        super.setUp();
//    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     *
     * @throws Exception if super class tearDown fails.
     */
    protected void tearDown()
            throws Exception
    {
        testObject = null;
        super.tearDown();
    }

    /**
     * Test marshalling of test data
     */
    public void testFileWriting()
            throws JAXBException,
                   IOException
    {
        testObject = new WriteXml();
        testObject.addGromitValue("Cheese");
        testObject.addWallaceValue(5);
        testObject.addGromitValue("Rocket");
        testObject.writeXml("JaxbTest.xml");
    }

    // static member methods (alphabetic)

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(WriteXmlTest.class);
    }

    // Description of this object.
    // public String toString() {}

    /**
     * Main routine which runs text test in standalone mode.
     *
     * @param args the arguments with which to execute this method.
     */
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
