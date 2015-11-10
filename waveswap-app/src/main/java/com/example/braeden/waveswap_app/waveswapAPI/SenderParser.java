package com.example.braeden.waveswap_app.waveswapAPI;
import java.io.*;

import net.beadsproject.beads.data.SampleAudioFormat;
import net.beadsproject.beads.data.audiofile.AudioFileType;
import net.beadsproject.beads.data.audiofile.FileFormatException;
import net.beadsproject.beads.data.audiofile.OperationUnsupportedException;
import net.beadsproject.beads.data.audiofile.WavFileReaderWriter;

/**
 * Parses a file to eventually be converted to a sound wave.
 * @authors Hannah Roth, Matthew Montera
 * @date September 15th, 2015
 */
public class SenderParser {

	private static final int sampleRate = 44100;
	
	public static final int BIT_BY_BIT = 1;
	public static final int CASCADE = 2;
	
	public float[] transmissionDescriptor = {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

	public float[] createDescriptor(int transmissionSpeed, float lowFrequency,
			float sensitivity, int replicationAmount) {
		float[] data = new float[transmissionDescriptor.length * replicationAmount];

		for (int i = 0; i < transmissionDescriptor.length; i++) {
			for (int k = i * replicationAmount, j = 0; j < replicationAmount; k++, j++) {
				data[k] = transmissionDescriptor[i] * 1000;
			}
		}

		return data;
	}

	/**
	 * In order to account for sampleRate being variable, as well as
	 * similar frequencies being copied over time, sending a transmission
	 * now has new semantics:
	 * lowFrequency is UNUSED as a byte->frequency conversion.  Rather,
	 * lowFrequency appears when the SAME frequency would be used twice in a row.
	 * So, for example, [5 5 5 4] becomes [5 lowFrequency 5 4].
	 * Lastly, replicationAmount refers to the amount of copies that data will have,
	 * so [5 5 5 4] with replicationAmount 2 becomes [5 5 lf lf 5 5 4 4]
	 */
	public float[] createData(byte[] bytes, int transmissionSpeed,
			float lowFrequency, float sensitivity, int replicationAmount) {
		int numBytes = bytes.length;
		int numBits = numBytes * 8;
		int paddingBits = (numBits % transmissionSpeed > 0) ? (transmissionSpeed - numBits
				% transmissionSpeed) : 0;
		int totalBits = numBits + paddingBits; // Padding at the end of the  transmission
		int dataSize = totalBits / transmissionSpeed * replicationAmount;

		// First dimension is the number of channels (frequency, overlay)
		// Second dimension is the number of frames (time)
		float[][] data = new float[1][dataSize];
		int byteIndex = 0;
		int bitIndex = 0;
		int dataIndex = 0;
		int maskShiftAmount = 7; // Shifts left this amount
		byte baseMask = 1;
		byte currentByte = bytes[byteIndex];
		float frequency;
		float previousFrequency = -999999f; //Used to check for lowFrequency

		while (dataIndex < dataSize) {
			// Set up start of loop
			frequency = lowFrequency + sensitivity;

			// Loop equal to transmission speed
			for (int i = 0; i < transmissionSpeed; i++) {
				// If you overrun the current byte, fetch a new one
				if (bitIndex > 7) {
					byteIndex++;

					// Normal scenario, there are more bytes to process
					// So reset variables for the new byte
					if (byteIndex < numBytes) {
						currentByte = bytes[byteIndex];
						bitIndex = 0;
						maskShiftAmount = 7;
					}
					// Padding scenario, all bytes have been processed and now
					// padding must be added.
					else {
						currentByte = 0;
						bitIndex = 0;
						maskShiftAmount = 7;
					}
				}
				byte currentBit = (byte) (currentByte & (baseMask << maskShiftAmount));
				if (currentBit != 0) {
					frequency += Math.pow(2, transmissionSpeed - i - 1)
							* sensitivity;
				}
				bitIndex++;
				maskShiftAmount--;
			}
			
			float effectiveFrequency = frequency == previousFrequency ? lowFrequency : frequency;
			
			for (int i = 0; i < replicationAmount; i++) {
				data[0][dataIndex + i] = effectiveFrequency;
			}
			
			previousFrequency = effectiveFrequency;
			
			dataIndex += replicationAmount;
		}
		return data[0];
	}
	
	//The data in frequencies should have the replication amount reflect the 
	//amount of time that the user would like to play the sound, so no need to worry
	//about that.
	private static float[][] createSineWave(float[] frequencies, int transmissionSpeed, float lowFrequency, float sensitivity, int method)
	{
		if(method == BIT_BY_BIT)
		{
			//To use BIT_BY_BIT, float[][] frequencies MUST have been made
			//by createData(...) using a transmissonSpeed of 1.
			//When using BIT_BY_BIT, "parameters" is: (float targetFrequency, int numChannels)
			//Where "targetFrequency" is lowFrequency + sensitivity (since only that frequency should play)
			return createSineWaveBitByBit(frequencies, lowFrequency, sensitivity, transmissionSpeed);
		}
		
		if(method == CASCADE)
		{
			//To use CASCADE, float[][] frequencies MUST have been made
			//by createData(...) using any transmission speed, but for now a
			//sensitivity of "500".
			//When using CASCADE, "parameters"
			return createSineWaveCascade(frequencies);
		}
		//Used the below as reference
		/*float[][] buffer = new float[1][frequencies[0].length];
		double phi = 0;
		for(int i = 0; i < buffer[0].length; i++){
			phi += 2*Math.PI*1.0/sampleRate*frequencies[0][i];
			buffer[0][i] = (float)Math.sin(phi);
		}
		return buffer;*/
		return null;
	}
	
	/**
	 * Concerns: size of frequencies may not be divisible by numChannels, resulting in
	 *           erroneous "0"s added to the end of the decoding.  However, since in the 
	 *           final stage the descriptor should give the number of bytes(bits?), this
	 *           shouldn't be a problem.
	 *           
	 *           Output: sine wave of floats on "transmissionSpeed" channels 
	 */
	private static float[][] createSineWaveBitByBit(float[] frequencies, float lowFrequency, float sensitivity, int numChannels)
	{
		//E.x., frequencies.length = {1-numChannels} : 1, so frequencies.length + (numChannels - frequencies.length % numChannels) = closest high number divisible by numChannels
		int frequenciesPerChannel = (frequencies.length + (numChannels - frequencies.length % numChannels)) / numChannels;
		float[][] buffer = new float[numChannels][frequenciesPerChannel];
		double phi = 0;
		
		//Add the floats to the buffer if they meet targetFrequency
		int currentChannel = 0;
		int currentFrequency = 0;
		boolean wasTargetFrequency = false;
		for(int i = 0; i < frequencies.length; i++)
		{
			float frequency = frequencies[i];
			
			if((frequency == lowFrequency + sensitivity*2 || frequency == lowFrequency && wasTargetFrequency))
			{
				//Change frequency to channel equivalent.
				//Channel 0: lowFrequency
				//Channel 1: lowFrequency + sensitivity, etc.
				frequency = lowFrequency + sensitivity * currentChannel;
				phi += 2*Math.PI*1.0/sampleRate*frequency;
				buffer[currentChannel][currentFrequency] = (float)Math.sin(phi);
				wasTargetFrequency = true;
			}
			else
			{
				buffer[currentChannel][currentFrequency] = 0;
				wasTargetFrequency = false;
			}
			
			currentChannel++;
			if(currentChannel >= numChannels)
			{
				currentChannel = 0;
				currentFrequency++;
			}
		}
		
		return buffer;
	}
	
	private static float[][] createSineWaveCascade(float[] frequencies)
	{
		return null;
	}
	
	
	/**
	 * Converts a byte array into an audio file
	 * 
	 * @param filePath Path to place the audio file
	 * @param transmissionSpeed Bits to read per transmission e.g., 2 = 00, 01, 10, 11
	 * @param lowFrequency Frequency that transmission begins at, e.g., lowFrequency = 10Hz, 00 = 10Hz
	 * @param sensitivity Difference between frequencies that hardware can detect
	 */
	public void createAudioFile(byte[] bytes, String filePath,
			int transmissionSpeed, float lowFrequency, float sensitivity, int replication, int method) {
		
		float[] parsedDescriptor = createDescriptor(method == BIT_BY_BIT ? 1 : transmissionSpeed, lowFrequency, sensitivity, replication);
		float[] parsedData = createData(bytes, method == BIT_BY_BIT ? 1 : transmissionSpeed, lowFrequency, sensitivity, replication);
		
		float[][] descriptor = createSineWave(parsedDescriptor, transmissionSpeed, lowFrequency, sensitivity, method);
		float[][] data = createSineWave(parsedData, transmissionSpeed, lowFrequency, sensitivity, method);
		
		if(descriptor.length != data.length) return;
		
		float[][] transmission = new float[descriptor.length][descriptor[0].length + data[0].length];

		for (int i = 0; i < descriptor.length; i++) {
			for (int j = 0; j < descriptor[0].length; j++)
			{
				transmission[i][j] = descriptor[i][j];
			}
		}

		for (int i = 0; i < descriptor.length; i++) {
			for (int j = 0; j < data[0].length; j++)
			{
				transmission[i][j+descriptor[0].length] = data[0][j];
			}
		}

		WavFileReaderWriter wfrw = new WavFileReaderWriter();
		try {
			SampleAudioFormat format = new SampleAudioFormat(44100, 16, 1);
			wfrw.writeAudioFile(data, filePath, AudioFileType.WAV, format);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OperationUnsupportedException e) {
			e.printStackTrace();
		} catch (FileFormatException e) {
			e.printStackTrace();
		}
	}

	public void parseFile(File file, String filePath, int transmissionSpeed, float lowFrequency, 
			float sensitivity, int replication, int method) {
		try {
			// Put file bytes into fileData byte array
			byte[] fileData = new byte[(int) file.length()];
			FileInputStream in = new FileInputStream(file);
			in.read(fileData);
			in.close();

			createAudioFile(fileData, filePath, transmissionSpeed, lowFrequency, sensitivity, replication, method);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
