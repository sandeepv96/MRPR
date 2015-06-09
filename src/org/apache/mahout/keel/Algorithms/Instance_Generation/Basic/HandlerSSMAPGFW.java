package org.apache.mahout.keel.Algorithms.Instance_Generation.Basic;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.mahout.keel.Dataset.Attributes;
import org.apache.mahout.keel.Dataset.Instance;
import org.apache.mahout.keel.Dataset.InstanceAttributes;
import org.apache.mahout.keel.Dataset.InstanceSet;

import org.apache.mahout.keel.Algorithms.Instance_Generation.SSMACoDEFW.SSMACoDEFW;
import org.apache.mahout.keel.Algorithms.Instance_Generation.SSMACoDEFW.*;



import org.apache.mahout.keel.Algorithms.Semi_Supervised_Learning.Basic.*;

public class HandlerSSMAPGFW {
		
	private int[][] predictions;
	private String algSufix = "";
	private double[] pesos = null;
	
	public HandlerSSMAPGFW(){
	}
	
	
	public double[] getPesos(){
		return this.pesos;
	}

	
	public PrototypeSet ejecutar(InstanceSet train, PrototypeSet trainPG) throws Exception{
		
	
		// ejecutar el metodo
		for(int i = 0 ; i < ParametersSMO.numPartitions ; ++i){
	       // Attributes.clearAll();
	        String[] argumentos = new String[1];
	        argumentos[0] = "NOFILE";
		
		SSMACoDEFW ssma = new SSMACoDEFW (argumentos[0],train);
	        ssma.Script = argumentos[0];
	        ssma.establishTrain(trainPG);
	        
	        
	        ssma.ejecutar();

	        pesos = ssma.getPesos().clone();
		}
		
	
		//mojontron.print();
		
		InstanceSet IS = new InstanceSet();
		InstanceSet finalIS = new InstanceSet();
		
		
	
		try {
			Attributes.clearAll();
			finalIS.readSet("salida.dat" , true);
	          finalIS.setAttributesAsNonStatic();
	          InstanceAttributes att = finalIS.getAttributeDefinitions();
	          Prototype.setAttributesTypes(att);            
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		PrototypeSet result = new PrototypeSet(finalIS);
		
		deleteFiles();
		return result;
		
	}
	
		
	public void deleteFiles(){
		
		for(int i = 0 ; i < ParametersSMO.numPartitions ; ++i){
			File f = new File("train_" + algSufix + "_" + (i+1) + ".dat");
			f.delete();
			
			f = new File("test_" + algSufix + "_" + (i+1) + ".dat");
			f.delete();
		}
		
	}
	


//*******************************************************************************************************************************
  	
  	private void createConfigurationFiles() throws IOException{
  		
  		/*
  		for(int i = 0 ; i < ParametersSMO.numPartitions ; ++i){
  			BufferedWriter bf = new BufferedWriter(new FileWriter("config_" + algSufix + "_"+(i+1)+".txt"));
  			String cad = "";
  			cad += "algorithm = " + algSufix + "\n";
  			cad += "inputData = \"" + ParametersSMO.trainInputFile + "\" " +  "\""+  ParametersSMO.trainInputFile + "\" " +   "\"" +ParametersSMO.trainInputFile+ "\"\n";
  			cad += "outputData = \"train_" + algSufix + "_" + (i+1) + ".dat\" \"test_" + algSufix + "_" + (i+1) + ".dat\"\n\n";
  			//cad += "seed = " + ParametersSMO.seed + "\n";
  	  			
  			bf.write(cad);
  			bf.close();
  		}
  		*/
  	}

	
}
