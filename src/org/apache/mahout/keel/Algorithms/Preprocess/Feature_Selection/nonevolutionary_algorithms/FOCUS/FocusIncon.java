/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/**
 * <p>
 * @author Written by Manuel Chica Serrano (University of Jaen) 01/09/2005
 * @author Modified by Jose Joaquin Aguilera Garcia (University of Jaen) 19/12/2008
 * @author Modified by Cristobal Jose Carmona del Jesus (University of Jaen) 19/12/2008
 * @author Modified by Jose Joaquin Aguilera Garcia (University of Jaen) 03/02/2009
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package org.apache.mahout.keel.Algorithms.Preprocess.Feature_Selection.nonevolutionary_algorithms.FOCUS;

import org.core.Files;
import org.core.Randomize;

import java.util.*;
import org.apache.mahout.keel.Dataset.*;
import org.apache.mahout.keel.Algorithms.Preprocess.Feature_Selection.Datos;

public class FocusIncon {
    /**
     * <p>
     * Main class of focus method for feature selection using inconsistency ratio as evaluation measure.
     * 
		 * This class implements FOCUS for features selection. The running process consists of generate 
 		 * all posible features subsets. The stopping criteria will be satisfy a determinate inconsistency ratio
 		 * (read as parameter)
     * </p>
     */    
    
    /** Datos class with all information about datasets and feature selection methods  */
    private Datos data;
    
    
    /** needed parameters for backward method */
    private Parametros params;
    
   
    /** a boolean array with selected features */
    private boolean features[];
       
    
    /** interior class using for reading all parameters */
    private class Parametros{
    
        /** algorithm name */
        String nameAlgorithm;
        
        
        /** number of nearest neighbours for KNN Classifier */
        int paramKNN;
            
        
        /** pathname of training dataset */
        String trainFileNameInput;
    
    
        /** pathname of test dataset */
        String testFileNameInput;
    
    
        /** pathname of test dataset only with selected features */
        String testFileNameOutput;
    
    
        /** pathname of training dataset only with selected features */
        String trainFileNameOutput;
    
    
        /** pathname of an extra file with additional information about the algorithm results */
        String extraFileNameOutput;
            
        
        /** allowed inconsistency ratio */
        double inconAllow;
    
        
        Parametros(){};
        /**
         * <p>
         * Constructor of the Parametros Class 
         * </p>
         * @param nombreFileParametros is the pathname of input parameter file 
         */ 
        Parametros (String nombreFileParametros){
        
            try{
                int i;
                String fichero, linea, tok;
                StringTokenizer lineasFile, tokens;

                /* read the parameter file using Files class */
                fichero = Files.readFile(nombreFileParametros);
                fichero += "\n";
                
                /* remove all \r characters. it is neccesary for a correct use in Windows and UNIX  */
                fichero = fichero.replace('\r', ' ');

                /* extract the differents tokens of the file */
                lineasFile = new StringTokenizer(fichero, "\n");

                i=0;
                while(lineasFile.hasMoreTokens()) {
                    
                    linea = lineasFile.nextToken();    
                    i++;
                    tokens = new StringTokenizer(linea, " ,\t");
                    if(tokens.hasMoreTokens()){

                        tok = tokens.nextToken();
                        if(tok.equalsIgnoreCase("algorithm")) nameAlgorithm = getParamString(tokens);
                        else if(tok.equalsIgnoreCase("inputdata")) getInputFiles(tokens);
                        else if(tok.equalsIgnoreCase("outputdata")) getOutputFiles(tokens);
                        else if(tok.equalsIgnoreCase("paramKNN")) paramKNN = getParamInt(tokens);    
                        else if(tok.equalsIgnoreCase("inconAllow")) inconAllow = getParamFloat(tokens);
                        else throw new java.io.IOException("Syntax error on line " + i + ": [" + tok + "]\n");
                    }                                                      

                }


            } catch(java.io.FileNotFoundException e){
                System.err.println(e + "Parameter file");
            }catch(java.io.IOException e){
                System.err.println(e + "Aborting program");
                System.exit(-1);
            }

            /** show the read parameter in the standard output */
            String contents = "-- Parameters echo --- \n";
            contents += "Algorithm name: " + nameAlgorithm +"\n";
            contents += "Input Train File: " + trainFileNameInput +"\n";
            contents += "Input Test File: " + testFileNameInput +"\n";
            contents += "Output Train File: " + trainFileNameOutput +"\n";
            contents += "Output Test File: " + testFileNameOutput +"\n";
            contents += "Parameter k of KNN Algorithm: " + paramKNN + "\n";
            contents += "Ratio of Inconsistency: " + inconAllow + "\n";
            System.out.println(contents);

        }
    
        /** obtain a float value from the parameter file  
            @param s is the StringTokenizer */
        private double getParamFloat(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Float.parseFloat(val);
        }
        
        
        /** obtain an integer value from the parameter file  
            @param s is the StringTokenizer  */
        private int getParamInt(StringTokenizer s){
            String val = s.nextToken();
            val = s.nextToken();
            return Integer.parseInt(val);
        }
            
        
        /** obtain a string value from the parameter file  
            @param s is the StringTokenizer */
        private String getParamString(StringTokenizer s){
            String contenido = "";
            String val = s.nextToken();
            while(s.hasMoreTokens())
                contenido += s.nextToken() + " ";

            return contenido.trim();
        }


        /**obtain the names of the input files from the parameter file  
            @param s is the StringTokenizer */
        private void getInputFiles(StringTokenizer s){
            String val = s.nextToken();

            trainFileNameInput = s.nextToken().replace('"',  ' ').trim();
            testFileNameInput = s.nextToken().replace('"',  ' ').trim();
        }


        /** obtain the names of the output files from the parameter file  
            @param s is the StringTokenizer */
        private void getOutputFiles(StringTokenizer s){
            String val = s.nextToken();

            trainFileNameOutput = s.nextToken().replace('"',  ' ').trim();
            testFileNameOutput = s.nextToken().replace('"',  ' ').trim();
            extraFileNameOutput = s.nextToken().replace('"',  ' ').trim();        

        }
    
    }

    
    /**
     * <p>
     * Creates a new instance of FocusIncon 
     * </p>
     * @param ficParametros is the name of the param file
     */
    public FocusIncon(String ficParametros) {
        
        /* loads the parameter file */
        params = new Parametros(ficParametros);
        
        /* loads both of training and test datasets */
        data = new Datos (params.trainFileNameInput, params.testFileNameInput, params.paramKNN);
        
    }
       
    /**
     * <p>
     * Creates a new instance of LVFIncon 
     * </p>
     * @param ficParametros is the name of the param file
     */
    public FocusIncon(InstanceSet train, int paramKNN, double inconAllow) {

        /* loads the parameters  */
    	params = new Parametros();
        params.trainFileNameInput = "train1.txt";
        params.testFileNameInput = "test.txt";
        
        params.paramKNN=paramKNN;
        params.inconAllow=inconAllow;
      
  
        
        /* loads both of training and test datasets */
        data = new Datos (train, params.paramKNN);
        
    }
    
    
    
    /**
     * <p>
     * neccesary method for FOCUS Algorithm. Sets a boolean array from an integer pointers array (a pointer
     * value, e.g. 5,  will set 5th position, in this case, of the boolean array as true)
     * </p>
     * @param fv, a boolean array result of method's calling
     * @param pointers, is the integer pointers array 
     * @param tamPoiters, is the size of pointers array
     */
    private static void establecerValoresBooleanos(boolean fv[], int pointers[], int tamPointers){
        for(int i=0; i<fv.length; i++)
            fv[i] = false;
        
        for(int i=0; i<tamPointers; i++)
            fv[pointers[i]-1] = true;
    }
    
    
    /**
     * <p>
     * initializes the pointers array to 1...N (N is the logical size of array, passed as argument) 
     * </p>
     * @param pointers is the integer pointers array
     * @param tam is the size of pointers array
     */
    private static void inicializarMascara(int pointers[], int tam){
        for(int i=0; i<tam; i++)
            pointers[i] = i+1;            
    }
   
    
    /**
     * <p>
     * cleans the integer pointers array (0 as new value)
     * </p>
     * @param pointers is the integer pointers array
     */
    private static void limpiarMascara(int pointers[]){
        for(int i=0; i<pointers.length; i++)
            pointers[i] = 0;
    }
    
    
    /**
     * <p>
     * generates the next possible combination of selected features with a specified size
     * </p>
     * @param pointers is the pointers array that will be changed
     * @param tam is the logical size of the pointers array (also, it represents the number of features
     *        to be selected in the next combination) 
     * @return returns the number of failed attempts produced in the method 
     */
    private static int siguienteCombinacion(int pointers[], int tam){        
        int FCnt, i, j, val, intentosFallidos=0;

        for(i = tam-1; i >= 0; i--)
            if(pointers[i]!=0)
                if(pointers[i] + tam - i == pointers.length + 1)
                    intentosFallidos++;
                else{
                    pointers[i]++;
                    if(intentosFallidos!=0){
			intentosFallidos = 0;
                        val = pointers[i];
			for(j = 1, FCnt = i+1; FCnt < tam; FCnt++, j++)
				pointers[FCnt] = val + j;
			while(FCnt < pointers.length){
				pointers[FCnt] = 0;
                                FCnt++;
                        }
                    }   
                    return intentosFallidos;
                }                    
        
        return intentosFallidos;
    }
    
           
    /**
     * <p>
     * main method for FocusIncon. This is an EXHAUSTIVE SEARCH ALGORITHM. Begins with subsets of size 1,
     * and searchs a subset which performs the inconsistency ratio (specified as input parameter)
     * </p>
     */
    private void focus(){
        boolean fv[];
        int mascara[];
        int i,j, intentosFallidos;
        
        /* allocates memory for fv & the pointers array. these structures help us to try
           with the different generated subsets */
        fv = new boolean[data.returnNumFeatures()];
        mascara = new int[data.returnNumFeatures()];
        
        for(i=1; i<=data.returnNumFeatures(); i++){
            intentosFallidos = 0;
            
            limpiarMascara(mascara);
            inicializarMascara(mascara, i);
            establecerValoresBooleanos(fv, mascara, i);
           
            while(intentosFallidos != i){
                
                establecerValoresBooleanos(fv, mascara, i);                
                
                /* calculates the inconsistency ratio from the generated features vector */
                if(data.medidaInconsistencia(fv) <= params.inconAllow ){
                    /* the consistent subset has been found */
                    features = fv;
                    
                    return;                    
                }
                
                /* if the generated subset doesn't perform the inconsistency ratio, it will try with an other subset, 
                    modifying its pointers array with the next combination */                
                intentosFallidos += siguienteCombinacion(mascara, i);
            }
        }                       
        
        System.err.println("ERROR: It couldn't be possible to find any solution with this inconsistency ratio.");
        System.err.println("Please to reduce inconsistency ratio parameter");
        System.exit(0);            
        
    }
    
    
    /** 
     * <p>
     * Method interface for FOCUS algorithm
     * </p>
     */
    public void ejecutar(){
        String resultado;
        int i, numFeatures;
        Date d;
       
        d = new Date();
        resultado = "RESULTS generated at " + String.valueOf((Date)d) 
                        + " \n--------------------------------------------------\n";
        resultado += "Algorithm Name: " + params.nameAlgorithm + "\n";
       
        /* call of FOCUS algorithm */
        focus();
            
        resultado += "\nPARTITION Filename: "+ params.trainFileNameInput +"\n---------------\n\n";
        resultado += "Features selected: \n";
            
        for(i=numFeatures=0; i<features.length; i++) 
            if(features[i] == true){ 
                resultado += Attributes.getInputAttribute(i).getName() + " - ";
                numFeatures++;
            }   
        
        resultado += "\n\n" + String.valueOf(numFeatures) + " features of " 
                + Attributes.getInputNumAttributes() + "\n\n" ;
        
        resultado += "Error in test (using train for prediction): " 
                + String.valueOf(data.validacionCruzada(features)) + "\n";
        resultado += "Error in test (using test for prediction): " 
                + String.valueOf(data.LVOTest(features)) + "\n";
        
        resultado += "---------------\n";        
            
        System.out.println("Experiment completed successfully");
              
        /* creates the new training and test datasets only with the selected features */
        Files.writeFile(params.extraFileNameOutput, resultado);
        data.generarFicherosSalida(params.trainFileNameOutput, params.testFileNameOutput, features);
               
        
    }
    
    /** 
     * <p>
     * Return the features obtained by LVF algorithm
     * </p>
     */
    public boolean[] getFeatures(){
        focus();
        System.out.println("Experiment completed successfully");
        return this.features;
    }
    
    
    
}


