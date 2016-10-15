package utils;

public class DistUtils {

	public static double computeL2(double[] x, double[] y) {
		
		double value = 0;
		double P = 2;
		
		for (int i = 0; i < x.length; i++){
			value += Math.pow(Math.abs(x[i]-y[i]),P);
		}
		
		return Math.pow(value, 1.0/P);
		
	}
}
