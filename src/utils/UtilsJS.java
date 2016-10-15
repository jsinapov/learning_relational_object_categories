package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import learning.classification.eval.EvaluationJS;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffLoader;

public class UtilsJS {
	
	public static double [] flattenMatrix(double [][] M){
		double [] x = new double[M.length*M[0].length];
		int c = 0;
		for (int i = 0; i < M.length; i++){
			for (int j = 0; j < M[i].length; j++){
				x[c]=M[i][j];
				c++;
			}
		}
		
		return x;
	}
	
	
	
	public static double [][] listToMatrix(ArrayList<double[]>  data){
		int dim=data.get(0).length;
		double [][] A = new double[data.size()][dim];
		for (int i = 0; i < data.size(); i++){
			double [] x_i = data.get(i);
			for (int j = 0; j < x_i.length; j++){
				A[i][j]=x_i[j];
			}
		}
		return A;
	}
	
	public static double [] instanceToArray(Instance inst, boolean hasClass){
		int end = inst.numAttributes();
		if (hasClass)
			end--;
		
		double [] values = new double[end];
		
		for (int i = 0; i < end; i ++){
			values[i]=inst.value(i);
		}
		
		return values;
	}
	
	public static ArrayList<String[]> generateStringPairs(ArrayList<String> items, boolean includeSelfPairs, boolean bothOrders){
		 ArrayList<String[]> pairs = new  ArrayList<String[]>();
		 
		 for (int i = 0; i < items.size(); i++){
			 int start_index = 0;
			 if (!bothOrders)
				 start_index = i;
			 
			 for (int j = start_index; j < items.size(); j++){
				 if (i == j){
					 if (includeSelfPairs){
						 String [] pair_ij = new String[2];
						 pair_ij[0]=items.get(i);
						 pair_ij[1]=items.get(j);
						 pairs.add(pair_ij);
					 }
				 }
				 else {
					 String [] pair_ij = new String[2];
					 pair_ij[0]=items.get(i);
					 pair_ij[1]=items.get(j);
					 pairs.add(pair_ij);
				 }
			 }
		 }
		 
		 
		 return pairs;
	}
	
	public static ArrayList<String> bubbleSortDescending( double [] num, ArrayList<String> objects )
	{
	     int j;
	     boolean flag = true;   // set flag to true to begin first pass
	     double temp;   //holding variable
	     String temp_object;

	     ArrayList<String> sorted = new ArrayList<String>();
	     sorted.addAll(objects);
	     
	     while ( flag )
	     {
	    	 flag= false;    //set flag to false awaiting a possible swap
	         for( j=0;  j < num.length -1;  j++ )
	         {
	        	 if ( num[ j ] < num[j+1] )   // change to > for ascending sort
	             {
	        		 temp_object = sorted.get(j);
	        		 temp = num[ j ];                //swap elements
	        		 
	        		 sorted.set(j, sorted.get(j+1));
	                 num[ j ] = num[ j+1 ];
	              
	                 sorted.set(j+1, temp_object);
	                 num[ j+1 ] = temp;
	                 
	                 flag = true;              //shows a swap occurred 
	             }
	         }
	     }
	     
	     return sorted;
	} 
	
	public static ArrayList<String> bubbleSort( double [] num, ArrayList<String> objects )
	{
	     int j;
	     boolean flag = true;   // set flag to true to begin first pass
	     double temp;   //holding variable
	     String temp_object;

	     ArrayList<String> sorted = new ArrayList<String>();
	     sorted.addAll(objects);
	     
	     while ( flag )
	     {
	    	 flag= false;    //set flag to false awaiting a possible swap
	         for( j=0;  j < num.length -1;  j++ )
	         {
	        	 if ( num[ j ] > num[j+1] )   // change to > for ascending sort
	             {
	        		 temp_object = sorted.get(j);
	        		 temp = num[ j ];                //swap elements
	        		 
	        		 sorted.set(j, sorted.get(j+1));
	                 num[ j ] = num[ j+1 ];
	              
	                 sorted.set(j+1, temp_object);
	                 num[ j+1 ] = temp;
	                 
	                 flag = true;              //shows a swap occurred 
	             }
	         }
	     }
	     
	     return sorted;
	} 
	
	
	public static String [] getIntersection(String [] A, String [] B){
		ArrayList<String> I = new ArrayList<String>();
		
		for (int i = 0; i < A.length; i++){
			for (int j = 0; j < B.length; j++){
				if (A[i].equals(B[j])){
					I.add(A[i]);
					break;
				}
			}
		}
		
		String [] result = new String[I.size()];
		for (int i = 0; i < result.length; i++)
			result[i]=I.get(i);
		return result;
	}
	
	public static double [][] addMatrices(ArrayList<double[][]> matrices, boolean average){
		int dim = matrices.get(0).length;
		double [][] R = new double[dim][dim];
		for (int i = 0; i < R.length; i++)
			for (int j = 0; j < R.length;j++)
				R[i][j]=0.0;
		
		for (int m = 0; m < matrices.size(); m++){
			double [][] M = matrices.get(m);
			for (int i = 0; i < R.length; i++)
				for (int j = 0; j < R.length;j++)
					R[i][j]+=M[i][j];
		}
		
		if (average){
			for (int i = 0; i < R.length; i++)
				for (int j = 0; j < R.length;j++)
					R[i][j]=R[i][j]/(double)matrices.size();
		}
		
		return R;
	}
	
	public static int indicatorNEQ(int x1, int x2){
		if (x1 != x2)
			return 1;
		else return 0;
	}
	
	public static int indexOf(String [] A, String x){
		for (int i = 0; i < A.length;i++){
			System.out.println("'"+A[i]+"'\t'"+x+"'\t"+A[i].equals(x));
			
			if (A[i].equals(x))
				return i;
		}
		return -1;
	}
	
	public static Instance toInstance(double [] f){
		ArrayList<Attribute> attrInfo = new ArrayList<Attribute>();
		
		for (int i = 0; i < f.length;i++){
			Attribute a = new Attribute(new String("a"+ i));
			attrInfo.add(a);
		}
		
		Instances weka_data = new Instances("data", attrInfo, 0);
		
		Instance inst_i = new DenseInstance(weka_data.numAttributes());
		inst_i.setDataset(weka_data);
		
		for (int j = 0; j < f.length; j++)
			inst_i.setValue(j, f[j]);
		
		return inst_i;
	}
	
	public static Instances toInstances(ArrayList<double[]> data, ArrayList<String> labels, ArrayList<String> class_label_set){
		if (class_label_set == null){
			class_label_set = new ArrayList<String>();
			for (int i = 0; i < labels.size(); i++){
				if (!class_label_set.contains(labels.get(i))){
					class_label_set.add(labels.get(i));
				}
			}
		}
		
		double [] features_example = data.get(0);
		
		ArrayList<Attribute> attrInfo = new ArrayList<Attribute>();
		
		//input attributes
		for (int i = 0; i < features_example.length;i++){
			Attribute a = new Attribute(new String("a"+ i));
			attrInfo.add(a);
		}
		
		//class attribute
		ArrayList<String> class_vals = new ArrayList<String>();
		for (int i = 0; i < class_label_set.size(); i++)
			class_vals.add(class_label_set.get(i));
		Attribute a_class = new Attribute(new String("class"),class_vals);
		attrInfo.add(a_class);
		
		//compute instances
		Instances weka_data = new Instances("data", attrInfo, 0);
		weka_data.setClassIndex(weka_data.numAttributes()-1);
		for (int i =0; i < data.size();i++){
			double [] x_i = data.get(i);
			
			Instance inst_i = new DenseInstance(weka_data.numAttributes());
			inst_i.setDataset(weka_data);
			
			for (int j = 0; j < x_i.length; j++)
				inst_i.setValue(j, x_i[j]);
			
			if (labels.get(i).equals("?"))
				inst_i.setClassMissing();
			else
				inst_i.setClassValue(labels.get(i));
		
			
			weka_data.add(inst_i);
		}
		
		return weka_data;
	}
	
	public static Instances toInstances(ArrayList<double[]> data){
		double [] features_example = data.get(0);
		
		ArrayList<Attribute> attrInfo = new ArrayList<Attribute>();
		
		for (int i = 0; i < features_example.length;i++){
			Attribute a = new Attribute(new String("a"+ i));
			attrInfo.add(a);
		}
		
		Instances weka_data = new Instances("data", attrInfo, 0);
		for (int i =0; i < data.size();i++){
			double [] x_i = data.get(i);
			
			Instance inst_i = new DenseInstance(weka_data.numAttributes());
			inst_i.setDataset(weka_data);
			
			for (int j = 0; j < x_i.length; j++)
				inst_i.setValue(j, x_i[j]);
			
			
			weka_data.add(inst_i);
		}
		
		return weka_data;
	}
	
	public static ArrayList<double[]> toFeatureVectors(Instances data){
		ArrayList<double[]> features = new ArrayList<double[]>();
		
		for (int i = 0; i < data.numInstances();i++){
			Instance inst = data.instance(i);
			
			double [] f_i = new double[inst.numAttributes()];
			for (int k = 0; k < f_i.length; k++)
				f_i[k]=inst.value(k);
			
			features.add(f_i);
		}
		
		return features;
	}
	
	public static double [] computeHistogram(double [] values, double min, double max, int n_bins, boolean normalize){
		double [] hist = new double[n_bins];
		for (int i = 0; i < hist.length; i++)
			hist[i]=0.0;
		
		double offset = (max-min)/(double)n_bins;
		
		for (int i = 0; i < values.length; i++){
			
			for (int b = n_bins; b > 0; b--){
				
				if (values[i] >= (b-1)*offset){
					hist[b-1]+=1.0;
					break;
				}

			}	
		}
		return hist;
	}
	
	public static void writeArrayToFile(double [] values, String filename){
		try {
			FileWriter FW = new FileWriter(new File(filename));
			for (int i= 0; i < values.length; i++){
				FW.write(new String(values[i]+"\n"));
			}
			FW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeFeaturesToFile(ArrayList<double[]> features,String filename){
		try {
			FileWriter FW = new FileWriter(new File(filename));
			
			for (int j = 0; j < features.size(); j++){
				double [] values = features.get(j);
				for (int i= 0; i < values.length-1; i++)
					FW.write(new String(values[i]+"\t"));
				FW.write(new String(values[values.length-1]+"\n"));
			}
			FW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeDoubleListToFile(ArrayList<Double> A, String filename){
		try {
			FileWriter FW = new FileWriter(new File(filename));
			for (int i= 0; i < A.size(); i++){
				FW.write(new String(A.get(i).doubleValue()+"\n"));
			}
			FW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Instances getTrainClassCV(Instances data, int fold){
		Instances subset = new Instances(data);
		subset.delete();
		
		for (int i = 0; i < data.numInstances();i++){
			if (subset.classAttribute().indexOfValue(data.instance(i).stringValue(data.classAttribute())) != fold){
				subset.add(data.instance(i));
			}
		}
		
		
		return subset;
	}
	
	public static Instances getTestClassCV(Instances data, int fold){
		Instances subset = new Instances(data);
		subset.delete();
		
		for (int i = 0; i < data.numInstances();i++){
			if (subset.classAttribute().indexOfValue(data.instance(i).stringValue(data.classAttribute())) == fold){
				subset.add(data.instance(i));
			}
		}
		
		
		return subset;
	}
	
	public static Instances loadArff(String filename) throws IOException{
		ArffLoader AR=new ArffLoader();
		AR.setFile(new File(filename));
		AR.setSource(new File(filename));
		Instances data = AR.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		return data;
	}
	
	public static EvaluationJS evaluateClassCV(Instances data, Classifier C){
		int num_folds = data.classAttribute().numValues();
		
		try {
			EvaluationJS EV = new EvaluationJS(data);
			
			for (int i = 0; i < num_folds; i ++){
				Instances train = UtilsJS.getTrainClassCV(data, i);
				Instances test = UtilsJS.getTestClassCV(data, i);
				
				C.buildClassifier(train);
				
				EV.evaluateModel(C, test);
			}
			
			
			return EV;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Classifier arffToClassifier(String filename, Classifier C) throws Exception {
		Classifier C_out =  AbstractClassifier.makeCopy(C);
		
		ArffLoader AR=new ArffLoader();
		AR.setFile(new File(filename));
		AR.setSource(new File(filename));
		Instances data = AR.getDataSet();
		data.setClassIndex(data.numAttributes()-1);
		
		if (data.numInstances()>0)
			C_out.buildClassifier(data);
		
		return C_out;
	}
	
	public static int [] findLargestK(double [] A, int k){
		int [] largest_k = new int[k];
		double [] largest_k_scores = new double[k];
		for (int i = 0; i < largest_k.length;i++){
			largest_k[i]=-1;
			largest_k_scores[i] = Double.MIN_VALUE;
		}
	
		for (int i = 0; i < A.length; i++){
			
			int min_current_index = Utils.minIndex(largest_k_scores);
			
			if (A[i] > largest_k_scores[min_current_index]){
				largest_k_scores[min_current_index]=A[i];
				largest_k[min_current_index]=i;
			}
		}
		
		return largest_k;
	}
	
	public static void printArray(int [] A){
		for (int i = 0; i < A.length;i++)
			System.out.print(A[i]+"\t");
		System.out.println();
	}
	
	public static void printArray(double [] A){
		for (int i = 0; i < A.length;i++)
			System.out.print(A[i]+"\t");
		System.out.println();
	}
	
	public static void printMatrix(double [][] A){
		for (int i = 0; i < A.length;i++){
			for (int j = 0; j < A[i].length;j++){
				System.out.print("\t"+A[i][j]);
				
			}
			System.out.println();
		}
	}
	
	public static void printMatrix(int [][] A){
		for (int i = 0; i < A.length;i++){
			for (int j = 0; j < A[i].length;j++){
				System.out.print("\t"+A[i][j]);
				
			}
			System.out.println();
		}
		
	}
	
	public static double [][] loadSquareMatrix(String filename){
		double [][] M = new double[5][5];// = new double[rows][columns];
		boolean init = false;
		try {
			BufferedReader BR = new BufferedReader(new FileReader(new File(filename)));
			int n = 0; 
			while(true){
				String line = BR.readLine();
				
				if (line == null)
					break;
				
				//System.out.println("Line:\t"+line);
			
				StringTokenizer st = new StringTokenizer(line);
				
				//System.out.println("Found "+st.countTokens()+" tokens");
				
				if (init == false){
					M = new double[st.countTokens()][st.countTokens()];
					init = true;
				}
				
				int num_tokens = st.countTokens();
				
				for (int i = 0; i <  num_tokens;i++){
					String token = st.nextToken();
					
					M[n][i] = Double.parseDouble(token);
					
					//System.out.print(token +" -> "+M[n][i]+"\t");
				}
				//System.out.println();
				n++;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return M;
	}
	
	public static double [][] loadMatrixV2(String filename, int rows, int columns){
		double [][] M = new double[rows][columns];
		
		try {
			BufferedReader BR = new BufferedReader(new FileReader(new File(filename)));
			int n = 0; 
			while(true){
				String line = BR.readLine();
				
				if (line == null)
					break;
				
				StringTokenizer st = new StringTokenizer(line);
				
				for (int i = 0; i < columns; i++)
					M[n][i] = Double.parseDouble(st.nextToken());
				n++;
				if (n >= M.length)
					break;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return M;
	}
	
	public static double [] loadArray(String filename){
		ArrayList<Double> values = new ArrayList<Double>();

		try {
			BufferedReader BR = new BufferedReader(new FileReader(new File(filename)));
			while(true){
				String line = BR.readLine();
				
				if (line == null)
					break;
				
				values.add(new Double(Double.parseDouble(line)));
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double [] A = new double[values.size()];
		for (int i = 0; i < A.length; i++)
			A[i]=values.get(i);
		return A;
	}
	
	public static double [][] loadMatrix(String filename, int rows, int columns){
		double [][] M = new double[rows][columns];
		
		try {
			BufferedReader BR = new BufferedReader(new FileReader(new File(filename)));
			int n = 0; 
			while(true){
				String line = BR.readLine();
				
				if (line == null)
					break;
				
				String [] tokens = line.split("\t");
				for (int i = 0; i < columns; i++)
					M[n][i] = Double.parseDouble(tokens[i]);
				n++;
				if (n >= M.length)
					break;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return M;
	}
	
	public static void saveMatrixComma(double [][] M, String filename){
		try {
			FileWriter FW = new FileWriter(new File(filename));
			for (int i = 0; i < M.length; i++){
				for (int j = 0; j < M[i].length;j++){
					FW.write(new String(""+M[i][j]));
					if (j < M[i].length-1)
						FW.write(new String(","));
					else
						FW.write(new String("\n"));
				}
			}
			
			FW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void saveMatrix(double [][] M, String filename){
		try {
			FileWriter FW = new FileWriter(new File(filename));
			for (int i = 0; i < M.length; i++){
				for (int j = 0; j < M[i].length;j++){
					FW.write(new String(""+M[i][j]));
					if (j < M[i].length-1)
						FW.write(new String("\t"));
					else
						FW.write(new String("\n"));
				}
			}
			
			FW.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static boolean contains(String [] set, String x){
		for (int i = 0; i < set.length; i++)
			if (set[i].equals(x))
				return true;
		
		return false;
	}
	
	public static ArrayList<String> toArrayList(String [] A){
		ArrayList<String> a_list = new ArrayList<String>();
		for (int i = 0; i < A.length; i++)
			a_list.add(A[i]);
		return a_list;
	}
	
	public static String [] toArray(ArrayList<String> beh_trans_states){
		String [] array = new String[beh_trans_states.size()];
		for (int i = 0; i < beh_trans_states.size(); i++)
			array[i]=beh_trans_states.get(i);
		return array;
	}
	
	public static double [] toArray(ArrayList<Double> input){
		double [] array = new double[input.size()];
		for (int i = 0; i < input.size(); i++)
			array[i]=input.get(i).doubleValue();
		return array;
	}
	
	public static ArrayList<String[]> shuffleContexts(ArrayList<String[]> elements, int seed){
		ArrayList<String[]> temp = new ArrayList<String[]>();
		temp.addAll(elements);
		
		
		ArrayList<String[]> shuffled = new ArrayList<String[]>();
		Random r = new Random(seed);
		for (int i = 0; i < elements.size(); i++){
			int index = r.nextInt(temp.size());
			shuffled.add(temp.get(index));
			temp.remove(index);
		}
		
	
		return shuffled;
	}
	
	public static ArrayList<String[]> shuffleContexts(ArrayList<String[]> elements, Random r){
		ArrayList<String[]> shuffled = new ArrayList<String[]>();
		
		ArrayList<Integer> positions = new ArrayList<Integer>();
		for (int i = 0;i < elements.size(); i++)
			positions.add(new Integer(i));
		
		
		String [] shuffled_array = new String[elements.size()];
		
		for (int i = 0; i < elements.size(); i++){
			int index = r.nextInt(positions.size());
			int r_i = positions.get(index).intValue();
			shuffled.add(elements.get(index));
			positions.remove(index);
		}
		
	
		return shuffled;
	}
	
	public static Instances shuffleInstances(Instances data, int seed){
		Random r = new Random(seed);
		
		for (int i = 0; i < data.numInstances();i++){
			int swap_index = i + r.nextInt(data.numInstances()-i);
			data.swap(i, swap_index);	
		}
		return data;
	}
	
	public static Instances randomInstancesSubset(Instances data, int k, int seed){
		Instances shuffled = shuffleInstances(data,seed);
		
		Instances subset = new Instances(shuffled);
		if (k >= shuffled.numInstances())
			return subset;
		else {
			subset.delete();
			for (int i = 0; i < k; i ++){
				Instance x_i = shuffled.instance(i);
				x_i.setDataset(subset);
				subset.add(x_i);
			}
			return subset;
		}
		
	}
	
	public static ArrayList<String> shuffle(ArrayList<String> elements, Random r){
		ArrayList<String> shuffled = new ArrayList<String>();
		
		ArrayList<Integer> positions = new ArrayList<Integer>();
		for (int i = 0;i < elements.size(); i++)
			positions.add(new Integer(i));
		
		String [] shuffled_array = new String[elements.size()];
		
		for (int i = 0; i < elements.size(); i++){
			int index = r.nextInt(positions.size());
			int r_i = positions.get(index).intValue();
			shuffled_array[r_i]=elements.get(i);
			positions.remove(index);
		}
		
		for (int i = 0;i < shuffled_array.length;i++)
			shuffled.add(shuffled_array[i]);

		return shuffled;
	}
	
	public static ArrayList<String> getRandomSubset(ArrayList<String> elements, int subset_K, Random r){
		ArrayList<String> shuffled = shuffle(elements,r);
		ArrayList<String> subset = new ArrayList<String>();
		for (int i = 0; i < subset_K; i ++){
			subset.add(shuffled.get(i));
		}
		return subset;
	}
	
	public static ArrayList<Integer> shuffleObjects(ArrayList<Integer> elements, Random r){
		ArrayList<Integer> shuffled = new ArrayList<Integer>();
		
		ArrayList<Integer> positions = new ArrayList<Integer>();
		for (int i = 0;i < elements.size(); i++)
			positions.add(new Integer(i));
		
		
		Integer [] shuffled_array = new Integer[elements.size()];
		
		for (int i = 0; i < elements.size(); i++){
			int index = r.nextInt(positions.size());
			int r_i = positions.get(index).intValue();
			shuffled_array[r_i]=elements.get(i);
			positions.remove(index);
		}
		
		for (int i = 0;i < shuffled_array.length;i++)
			shuffled.add(shuffled_array[i]);
		
		return shuffled;
	}
	
	public static ArrayList<Integer> getRandomObjectSubset(ArrayList<Integer> elements, int subset_K, Random r){
		ArrayList<Integer> shuffled = shuffleObjects(elements,r);
		ArrayList<Integer> subset = new ArrayList<Integer>();
		for (int i = 0; i < subset_K; i ++){
			subset.add(shuffled.get(i));
		}
		return subset;
	}
	
	public static ArrayList<String> subtractStringList(ArrayList<String> A, ArrayList<String> B){
		ArrayList<String> subset = new ArrayList<String>();
		
		for (int i = 0; i < A.size(); i++)
			if (!B.contains(A.get(i)))
				subset.add(A.get(i));
		
		return subset;
	}
	
	public static ArrayList<String> removeOnceFromList(ArrayList<String> A, String r_object){
		for (int i = 0; i < A.size(); i++){
			if (A.get(i).equals(r_object)){
				A.remove(i);
				return A;
			}
		}
		
		return A;
	}
	
	public static ArrayList<double[]> loadFeaturesFromFile(String filename){
		ArrayList<double[]> features = new ArrayList<double[]>();
		
		try {
			BufferedReader BR = new BufferedReader(new FileReader(new File(filename)));
			
			while (true){
				String line = BR.readLine();
				
				if (line == null)
					break;
				
				String [] tokens = line.split(",");
				
				double [] f_i = new double[tokens.length];
				for (int k = 0; k < tokens.length;k++)
					f_i[k]=Double.parseDouble(tokens[k]);

				features.add(f_i);
				
			}
			
			BR.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return features;
		
		
	}
	
	public static void saveFeaturesToFile(ArrayList<double[]> features, String filename){
		FileWriter FW;
		try {
			FW = new FileWriter(new File(filename));
			
			for (int i = 0; i < features.size();i++){
				double [] f_i = features.get(i);
				for (int j = 0; j < f_i.length;j++){
					FW.write(new String(f_i[j]+""));
					if (j < f_i.length-1)
						FW.write(",");
					else FW.write("\n");
				}
			}
			
			FW.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static Instances balanceDataset(Instances data, String positive){
		Instances balanced = new Instances(data);
		balanced.delete();
		
		Instances positive_data = new Instances(data);
		Instances negative_data = new Instances(data);
		positive_data.delete();
		negative_data.delete();
		
		for (int i = 0; i < data.numInstances();i++){
			Instance inst_i = data.instance(i);
			if (inst_i.stringValue(inst_i.classAttribute()).equals(positive))
				positive_data.add(inst_i);
			else{
				negative_data.add(inst_i);
				balanced.add(inst_i);
			}
		}
		
		if (positive_data.numInstances() >= negative_data.numInstances())
			return data;
		
		int mult = (int)Math.floor((double)negative_data.numInstances()/(double)positive_data.numInstances());
		
		if (mult == 1)
			return data;
		
		for (int k = 0; k < mult; k++){
			for (int i = 0; i < positive_data.numInstances();i++){
				Instance inst_i = positive_data.instance(i);
				balanced.add(inst_i);
			}
		}
		//System.out.println("\t\tData point numbers:\t"+positive_data.numInstances() +"\t"+negative_data.numInstances()+"\t"+balanced.numInstances());
		
		return balanced;
	}
}

