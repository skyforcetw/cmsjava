/*
 * @(#)demoFFT.java		1.10 05/02/24
 *
 *
 * COPYRIGHT NOTICE
 * Copyright (c) 2005
 * Laboratory of Neuro Imaging, UCLA.
 */
 
/**
 * The <code>demoFFT</code> class demonstrates the usage of the FFT library for image processing.
 *
 * @version	1.10, 05/02/24
 * @author	Herbert H.H. Chang and Daniel J. Valentino
 * @since	JDK1.4
 */

public class demoFFT
{  
  
	public static void main (String[] args)
	{
		System.out.println("demoFFT");
		
		// The length of the data.
		int lengthFFT = 1024;
		double[] realArray = new double[lengthFFT];
		double[] imagArray = new double[lengthFFT];
		double amp = 100.0;
		
		/* Experiment 1: cos function. */
		for (int i = 0; i < lengthFFT; i++) {
			realArray[i] = amp * Math.cos(20 * i * Math.PI / lengthFFT);
			imagArray[i] = 0.0;
		}
		// Take the forward FFT.
		FastFourierTransform.fastFT(realArray, imagArray, true);
		// Take the inverse FFT.
		FastFourierTransform.fastFT(realArray, imagArray, false);
		
		/* Experiment 2: sin function. */
		for (int i = 0; i < lengthFFT; i++) {
			realArray[i] = amp * Math.sin(20 * i * Math.PI / lengthFFT);
			imagArray[i] = 0.0;
		}
		// Take the forward FFT.
		FastFourierTransform.fastFT(realArray, imagArray, true);
		// Take the inverse FFT.
		FastFourierTransform.fastFT(realArray, imagArray, false);
		
		/* Experiment 3: box function. */
		int range1 = lengthFFT / 4;
		int range2 = 3 * lengthFFT / 4;
		for (int i = 0; i < lengthFFT; i++) {
			if ((i >= range1) && (i < range2)) {
				realArray[i] = amp;
			}
			else {
				realArray[i] = 0.0;
			}
			imagArray[i] = 0.0;
		}
		// Take the forward FFT.
		FastFourierTransform.fastFT(realArray, imagArray, true);
		// Take the inverse FFT.
		FastFourierTransform.fastFT(realArray, imagArray, false);
	}
}
