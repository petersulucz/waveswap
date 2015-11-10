package com.example.braeden.waveswap_app.waveswapAPI;
import java.io.*;

/**
 * Parses a file to eventually be converted to a sound wave.
 * @authors Hannah Roth, Matthew Montera
 * @date September 15th, 2015
 */
public class ReceiverParser {
	private static final int BYTE_LENGTH = 8;
	private static final int INT_LENGTH = 32;

	/**
	 * Decode the audio file AFTER relevant bits have been taken from the front
	 * of the transmission that determine: Size TransmissionSpeed LowFrequency
	 * Sensitivity
	 */
	public static byte[] decodeData(float[][] audioMatrix, int size,
			int transmissionSpeed, float lowFrequency, float sensitivity) {
		// Restrict to one channel for now until we know what they are for.
		byte[] bytes = new byte[size];

		byte currentByte = 0; // e.g. 00000000
		int currentByteIndex = 0; // e.g. 0-(size-1)
		byte currentByteBitsRemaining = BYTE_LENGTH; // e.g. 0-8
		float currentFrequency;
		float previousFrequency = -99999f;

		// Where "currentByte" is the byte that is currently being constructed.
		// If information is
		// added to "currentByte", but it is not full, the information is
		// left-shifted leaving space
		// on the right for new information.
		// "currentBits" is the value of the data, i.e. 15 = 1111
		// "currentBitsSize" is how many bits are relevant in "currentBits"
		// "bitsInCurrentByte" is how many bits in "currentByte" are full

		// Loop over the entire audioMatrix
		for (int index = 0; index < audioMatrix[0].length; index++) {
			currentFrequency = audioMatrix[0][index];
			
			if(currentFrequency == lowFrequency)
			{
				//This means that there is a repeated bit sequence, so set
				//the frequency to the frequency BEFORE the lowFrequency
				currentFrequency = previousFrequency;
				previousFrequency = lowFrequency;
			}
			else if(currentFrequency == previousFrequency)
			{
				//This means that there is a repeated frequency, which is
				//regulated by "replicationAmount" in SenderParser, and does
				//not mean that there are any additional bits.  By definition,
				//each successive float that defines a bit sequence is different
				//from the floats that surround it.
				continue;
			}
			
			// Should be an exact division
			int currentValue = Math.round((currentFrequency - lowFrequency - sensitivity) / sensitivity); 
			int currentValueSize = transmissionSpeed;

			// Fill up bytes until you no longer can
			while (currentValueSize >= currentByteBitsRemaining) {
				// Construct a byte whose value is the necessary bits in "currentValue"
				// Shifted all the way to the right, with 0's placed in
				byte rightShiftedCurrentValue = (byte) (currentValue >>> (currentValueSize - currentByteBitsRemaining));

				// Since the currentValue by virtue of this branch fills up the
				// remaining space in currentByte,
				// we don't need to do any left shifting, just |= to currentByte
				currentByte |= rightShiftedCurrentValue;

				// Add to the array of bytes
				bytes[currentByteIndex] = currentByte;

				// Maintain variables for correct execution
				currentValueSize -= currentByteBitsRemaining; // Subtract the
				// number of bits that went into currentByte
				currentValue &= -1 >>> (INT_LENGTH - currentValueSize); // Erase
				// the bits we just used up
				currentByteBitsRemaining = BYTE_LENGTH; // Reset
				currentByteIndex++; // Increment
				currentByte = 0; // Reset

				// Check to see if the array is finished
				if (currentByteIndex >= size) {
					currentValueSize = 0;
					break;
				}
			}

			// Add the rest of the currentValue to currentByte, and left shift it to the very end
			if (currentValueSize > 0) {
				byte leftShiftedCurrentValue = (byte) (currentValue << (currentByteBitsRemaining - currentValueSize));
				currentByte |= leftShiftedCurrentValue;
				// Maintain variables for correct execution
				currentByteBitsRemaining -= currentValueSize;
			}
		}
		return bytes;
	}

	public static void createFile(byte[] bytes, String filepath) throws IOException{
		FileOutputStream stream = new FileOutputStream(filepath);
		stream.write(bytes);
		stream.close();
	}

	/**
	 * Main method takes file to parse as parameter.
	 * @param args is the list of parameters
	 */
	public static void main(String[] args) {
		if (args[0] != null && args[1] != null) {
			File file = new File(args[0]);
			try {
				// Put file bytes into fileData byte array
				byte[] fileData = new byte[(int) file.length()];
				FileInputStream in = new FileInputStream(file);
				in.read(fileData);
				in.close();
				//float[][] audioMatrix, int size, int transmissionSpeed, float lowFrequency, float sensitivity
				//decodeData(fileData, args[1], 1, 1, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new IllegalArgumentException("You must input a file");
		}
	}
}
