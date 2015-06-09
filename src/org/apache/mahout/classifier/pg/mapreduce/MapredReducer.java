package org.apache.mahout.classifier.pg.mapreduce;

import com.google.common.base.Preconditions;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.mahout.classifier.pg.builder.PGgenerator;
import org.apache.mahout.classifier.pg.data.Dataset;
import org.apache.mahout.classifier.pg.mapreduce.partial.StrataID;
import org.apache.mahout.keel.Algorithms.Instance_Generation.Basic.PrototypeSet;

import java.io.IOException;

/**
 * Base class for Mapred mappers. Loads common parameters from the job
 */
public class MapredReducer<KEYIN,VALUEIN,KEYOUT,VALUEOUT> extends Reducer<KEYIN,VALUEIN,KEYOUT,VALUEOUT> {
  
  private boolean noOutput;
  
  protected PGgenerator pg_algorithm;
  
  private Dataset dataset;
  protected String header;

  protected PrototypeSet join = new PrototypeSet();
  protected int strata;

  
  /**
   * 
   * @return if false, the mapper does not estimate and output predictions
   */
  protected boolean isNoOutput() {
    return noOutput;
  }
  
  protected PGgenerator getPGgeneratorBuilder() {
    return pg_algorithm;
  }
  
  protected Dataset getDataset() {
    return dataset;
  }
  
  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    super.setup(context);
    
    Configuration conf = context.getConfiguration();
    
    configure(!Builder.isOutput(conf), Builder.getPGgeneratorBuilder(conf), Builder.loadDataset(conf), Builder.getHeader(conf));
  }
  
  /**
   * Useful for testing
   */
  protected void configure(boolean noOutput, PGgenerator pg_algorithm, Dataset dataset, String header) {
    Preconditions.checkArgument(pg_algorithm != null, "PGgenerator not found in the Job parameters");
    this.noOutput = noOutput;
    this.pg_algorithm = pg_algorithm;
    this.dataset = dataset;
    this.header = header;
  }

  
  /**
   * Generic reducer, it only adds all the RSs.
   */
  
protected void reduce(KEYIN id, Iterable<VALUEIN> rs, Context context)
		throws IOException, InterruptedException {
	// TODO Apéndice de método generado automáticamente
	
	//System.out.println("Si paso por aquí: "+id);
	//strata = (StrataID) id;

	for(VALUEIN value: rs){
		context.progress();
		MapredOutput prueba = (MapredOutput) value;
		PrototypeSet strato = prueba.getRS();
	
		join.add(strato);
    	//System.out.println("Amos copon "+mojon.size());
	}
	
	// if you write here, the cleanup does not work.
	//MapredOutput salida= new MapredOutput(join);
	//context.write((KEYOUT) id, (VALUEOUT) salida);

}


}


