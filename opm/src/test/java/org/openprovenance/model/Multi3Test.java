package org.openprovenance.model;
import java.io.File;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * Unit test for simple multistep edges
 */
public class Multi3Test 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Multi3Test( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */


    static OPMGraph graph1;




    public void testMULTI3() throws JAXBException
    {
        OPMFactory oFactory=new OPMFactory();

        Collection<Account> black=Collections.singleton(oFactory.newAccount("black"));
        
        Process p1=oFactory.newProcess("p1",
                                       black,
                                       "p1");
        Process p2=oFactory.newProcess("p2",
                                       black,
                                       "p2");




        Artifact a1=oFactory.newArtifact("a1",
                                         black,
                                         "a1");
        Artifact a2=oFactory.newArtifact("a2",
                                         black,
                                         "a2");
        Artifact a3=oFactory.newArtifact("a3",
                                         black,
                                         "a3");

        Used u0=oFactory.newUsed(p2,oFactory.newRole("r1"),a3,black);
        UsedStar u1=oFactory.newUsedStar(p2,a1,black);
        UsedStar u2=oFactory.newUsedStar(p2,a2,black);
        UsedStar u3=oFactory.newUsedStar(p2,a3,black);


        WasGeneratedBy wg0=oFactory.newWasGeneratedBy(a1,oFactory.newRole("r1"),p1,black);
        WasGeneratedByStar wg1=oFactory.newWasGeneratedByStar(a1,p1,black);
        WasGeneratedByStar wg2=oFactory.newWasGeneratedByStar(a2,p1,black);
        WasGeneratedByStar wg3=oFactory.newWasGeneratedByStar(a3,p1,black);



        WasDerivedFrom wd1=oFactory.newWasDerivedFrom(a3,a2,black);
        WasDerivedFrom wd2=oFactory.newWasDerivedFrom(a2,a1,black);

        WasDerivedFromStar wd3=oFactory.newWasDerivedFromStar(a3,a2,black);
        WasDerivedFromStar wd4=oFactory.newWasDerivedFromStar(a2,a1,black);
        WasDerivedFromStar wd5=oFactory.newWasDerivedFromStar(a3,a1,black);



        WasTriggeredByStar wt1=oFactory.newWasTriggeredByStar(p2,p1,black);

        OPMGraph graph=oFactory.newOPMGraph(black,
                                            new Overlaps[] { },
                                            new Process[] {p1,p2},
                                            new Artifact[] {a1,a2,a3},
                                            new Agent[] {  },
                                            new Object[] {u0,u1,u2,u3,
                                                          wg0, wg1,wg2,wg3,
                                                          wt1,
                                                          wd1,wd2,wd3,wd4,wd5} );





        OPMSerialiser serial=OPMSerialiser.getThreadOPMSerialiser();
        serial.serialiseOPMGraph(new File("target/multi3.xml"),graph,true);

        
        //System.out.println(sw);

        graph1=graph;
        System.out.println("MULTI3 Test asserting True");
        assertTrue( true );


        
    }
    

    /** Produces a dot representation
     * of created graph. */
    public void testMULTI3Conversion() throws java.io.FileNotFoundException,  java.io.IOException   {
        OPMToDot toDot=new OPMToDot("src/main/resources/defaultConfigMulti.xml"); // with multisteps
        
        toDot.convert(graph1,"target/multi3.dot", "target/multi3.pdf");
    }

}
