package exp;

import utils.UtilsJS;

public class GoogleEXP {

	/**
	 * @param args
	 */
	
	public static void partialSortedProblem(int [] X){
		
		int [] s = new int[X.length-1];
		for (int i = 1; i < X.length; i ++){
			
			if (X[i] > X[i-1])
				s[i-1]=1;
			else s[i-1]=0;
		}
		
		UtilsJS.printArray(X);
		UtilsJS.printArray(s);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int [] input = {0, 1, 5, 2, 6, 7, 3, 9};
		
		partialSortedProblem(input);
	}

}
