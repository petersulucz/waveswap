/*
package com.example.braeden.waveswap_app.waveswapAPI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//import junit.framework.TestCase;

*/
/**
 * Parses a file to eventually be converted to a sound wave.
 * @authors Hannah Roth, Matthew Montera
 * @date September 15th, 2015
 *//*

public class ParserTest {
	private SenderParser sender;
	
	*/
/**
	 * Set up a sender for parsing
	 *//*

	public void setUp() {
		sender = new SenderParser();
	}

	*/
/**
	 * Test one.
	 *//*

	public void testOne()
	{
		int sizeBytes = 16;
		int minTS = 1;
		int incTS = 1;
		int maxTS = 14;
		float minLF = -1;
		float incLF = 0.1f;
		float maxLF = 1;
		float minS = 4f;
		float incS = 0.1f;
		float maxS = 5;
		byte[] testBytes = new byte[sizeBytes];

		int numSuccess = 0;
		int numFail = 0;

		for (int i = 0; i < sizeBytes; i++) {
			testBytes[i] = (byte) i;
		}

		System.out.println("Tests Beginning");

		for (int i = minTS; i <= maxTS; i += incTS) {
			for (float j = minLF; j <= maxLF; j += incLF) {
				for (float k = minS; k <= maxS; k += incS) {
					if (!testData(sizeBytes, testBytes, i, j, k)) {
						System.out.println("Test Fail - TransmissionSpeed: "
								+ i + " LowFrequency: " + j
								+ " Sensitivity: " + k);
						numFail++;
					} else {
						numSuccess++;
					}
				}
			}
		}
		System.out.println("Tests Finished - " + numSuccess
				+ " Successes and " + numFail + " Failures");
	}

	*/
/**
	 * Test with real data. Change file path on different machines.
	 * Chose any file.
	 *//*

//	public void testRealData() {
//		File file = new File("/Users/banana/Desktop/Pictures/bronzeMedal.png");
//		try {
//			// Put file bytes into fileData byte array
//			byte[] fileData = new byte[(int) file.length()];
//			FileInputStream in = new FileInputStream(file);
//			in.read(fileData);
//			in.close();
//			float[][] data = sender.createData(fileData, 1, 1, 1, 2);
//			byte[] returnBytes = ReceiverParser.decodeData(data, fileData.length, 1, 1, 1);
//			ReceiverParser.createFile(returnBytes, "test1");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	*/
/**
	 * @param sizeBytes
	 * @param testBytes
	 * @param testTS
	 * @param testLF
	 * @param testS
	 * @return
	 *//*

//	public boolean testData(int sizeBytes, byte[] testBytes, int testTS,
//			float testLF, float testS) {
//		float[][] data = sender.createData(testBytes, testTS, testLF, testS, 2);
//		byte[] returnBytes = ReceiverParser.decodeData(data, sizeBytes, testTS, testLF, testS);
//		return isEqual(testBytes, returnBytes);
//	}

	*/
/**
	 * @param bytes is the first array of bytes
	 * @param other is the second array of bytes
	 * @return if the two arrays of bytes are equal
	 *//*

	private static boolean isEqual(byte[] bytes, byte[] other) {
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] != other[i]) {
				return false;
			}
		}
		return true;
	}
}
*/
