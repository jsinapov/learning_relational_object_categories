package exp;

import java.util.ArrayList;

import learning.relational.rep.Domain;
import learning.relational.rep.UnaryRelation;

import data.contextmodel.ContextDataDB;
import data.contextmodel.DataUtils;

public class RelationalEXP {
	final static String datapath = "/home/jsinapov/research/datasets/matrix_reasoning/features/";
	final static String [] behaviors = {"look","grasp","lift_slow","hold","shake","high_velocity_shake",
								"low_drop","tap","poke","push","crush"};
	final static String [] modalities = {"color","audio","proprioception"};
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String[]> contexts = new ArrayList<String[]>();
		ArrayList<String> contexts_strings = new ArrayList<String>();
		
		for (int b = 0; b < behaviors.length;b++){
			for (int m = 0; m < modalities.length; m++){
				if (DataUtils.validCombination(behaviors[b],modalities[m])){
					String [] context = new String[2];
					context[0]=behaviors[b];
					context[1]=modalities[m];
					contexts.add(context);
					contexts_strings.add(DataUtils.contextString(context));
				}
			}		
		}
		System.out.println("Using "+contexts_strings.size()+" contexts: "+contexts_strings.toString());
		
		//load database
		ContextDataDB CDB = DataUtils.loadMatrixReasoningDB(datapath, contexts);
		
		//get objects
		ArrayList<String> objects = DataUtils.getObjectsMR();
		System.out.println("\t"+objects.size()+" objects: "+objects.toString());
		
		//specify domain for sets
		Domain D_set = new Domain(objects,true);
		
		//specify unary relation
		UnaryRelation R = new UnaryRelation(D_set);
	}
}
