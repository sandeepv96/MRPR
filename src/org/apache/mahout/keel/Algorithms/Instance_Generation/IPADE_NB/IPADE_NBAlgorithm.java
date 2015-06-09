/**
	IPADE_NB.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  23-7-2009
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.
**/

package  org.apache.mahout.keel.Algorithms.Instance_Generation.IPADE_NB;

import org.apache.mahout.keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import org.apache.mahout.keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import org.apache.mahout.keel.Algorithms.Instance_Generation.*;
import org.apache.mahout.keel.Algorithms.Instance_Generation.utilities.*;

import java.util.*;

/**
 * IPADE_NB algorithm calling.
 * @author Isaac Triguero
 */
public class IPADE_NBAlgorithm extends PrototypeGenerationAlgorithm<IPADE_NBGenerator>
{
    /**
     * Builds a new IPADE algorithm
     * @param train Training data set.
     * @param params Parameters of the method.
     */
    protected IPADE_NBGenerator buildNewPrototypeGenerator(PrototypeSet train, Parameters params)
    {
       return new IPADE_NBGenerator(train, params);    
    }
    
     /**
     * Main method. Executes IPADE_NB algorithm.
     * @param args Console arguments of the method.
     */
    public static void main(String args[])
    {
        IPADE_NBAlgorithm isaak = new IPADE_NBAlgorithm();
        isaak.executeNB(args);
    }
}
