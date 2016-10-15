package learning.relational.features;

import java.util.ArrayList;

import utils.UtilsJS;
import weka.core.Utils;

import data.contextmodel.DataPointBM;
import data.contextmodel.DataUtils;

import learning.relational.rep.RelDatapoint;
import learning.relational.rep.iface.Element;
import learning.relational.rep.AtomicElement;

public class RelFeatureCreatorMR implements IRelationalFeatureCreator {

	@Override
	//here, we assume that there is a single BM token for each atomic object in the relation
	public double[] generateFeatures(RelDatapoint D,ArrayList<DataPointBM> bm_data) {
		ArrayList<Element> rel_data = D.getElements();
		if (D.getSchema().equals("1 a")){ //unary relation
			Element A = rel_data.get(0);
			if (A.isSet()==false){ //over a single object
				DataPointBM object_data = bm_data.get(0);
				return object_data.getFeatures();
			}
			else { //over a set of objects
				
			}
		}
		else if (D.getSchema().equals("2 aa")){
			
			//absolute difference only for now
			DataPointBM object_data_A = bm_data.get(0);
			DataPointBM object_data_B = bm_data.get(1);
			double [] f_a = object_data_A.getFeatures();
			double [] f_b = object_data_B.getFeatures();
			double [] f = new double[f_a.length];
			for (int i = 0; i < f.length; i++){
				f[i]=f_a[i]-f_b[i];
			}
			return f;
		}
		else if (D.getSchema().equals("1 s")){
			//System.out.println("\nComputing features for "+D.toString());
			
			ArrayList<String> set_objects = D.getAtomicElementIDs();
			ArrayList<DataPointBM> tokens = new ArrayList<DataPointBM>();
			for (int i = 0; i < set_objects.size(); i++){
				tokens.add(DataUtils.getDataWithObject(bm_data, set_objects.get(i)).get(0));
			}
		//	System.out.println("...using "+tokens.size()+" tokens: "+tokens);
			
			double [] f = new double[tokens.get(0).getFeatures().length];
			for (int i = 0; i < f.length; i++)
				f[i]=0.0;
			
			double [][] links = new double[f.length][set_objects.size()*(set_objects.size()-1)];
			int c = 0;
			for (int i = 0; i < set_objects.size(); i++){
				for (int j = 0; j < set_objects.size(); j++){
					if (i!=j){
						double [] f_a = tokens.get(i).getFeatures();
						double [] f_b = tokens.get(j).getFeatures();
						double [] f_temp = new double[f_a.length];
						for (int k = 0; k < f_temp.length; k++){
							f_temp[k]=Math.abs(f_a[k]-f_b[k]);
							links[k][c]=f_temp[k];
						}
						c++;
					}
				}
			}
			
			//UtilsJS.printMatrix(links);
			
			for (int i = 0; i < f.length; i++){
				f[i]=Utils.mean(links[i]);
				//System.out.print(f[i]+",");
			}
			//System.out.println();
			
			
			return f;
		}
		
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<double[]> generateFeatures(ArrayList<RelDatapoint> D_list,ArrayList<DataPointBM> bm_data) {
		// TODO Auto-generated method stub
		return null;
	}

}
