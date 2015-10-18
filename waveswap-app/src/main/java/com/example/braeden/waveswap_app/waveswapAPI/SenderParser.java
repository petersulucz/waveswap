package com.example.braeden.waveswap_app.waveswapAPI;

import net.beadsproject.beads.data.SampleAudioFormat;
import net.beadsproject.beads.data.audiofile.AudioFileType;
import net.beadsproject.beads.data.audiofile.FileFormatException;
import net.beadsproject.beads.data.audiofile.OperationUnsupportedException;
import net.beadsproject.beads.data.audiofile.WavFileReaderWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Parses a file to eventually be converted to a sound wave.
 * @authors Hannah Roth, Matthew Montera
 * @date September 15th, 2015
 */
public class SenderParser {

	public float[] transmissionDescriptor = {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

	public float[][] createDescriptor(int transmissionSpeed, float lowFrequency,
			float sensitivity, int replicationAmount) {
		float[][] data = new float[1][transmissionDescriptor.length * replicationAmount];

		for (int i = 0; i < transmissionDescriptor.length; i++) {
			for (int k = i * replicationAmount, j = 0; j < replicationAmount; k++, j++) {
				//data[1][k] = transmissionDescriptor[i] * 1000;
                data[0][k] = transmissionDescriptor[i] * 1000;
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
	public float[][] createData(byte[] bytes, int transmissionSpeed,
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
			for (int i = 0; i < replicationAmount; i++) {
				if (frequency != previousFrequency) {
					data[0][dataIndex + i] = frequency;
					previousFrequency = frequency;
				} else {
					data[0][dataIndex + i] = lowFrequency;
					previousFrequency = lowFrequency;
				}
			}
			dataIndex += replicationAmount;
		}
		return data;
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
			int transmissionSpeed, float lowFrequency, float sensitivity, int replication) {
		float[][] descriptor = createDescriptor(transmissionSpeed, lowFrequency, sensitivity, replication);
		float[][] data = createData(bytes, transmissionSpeed, lowFrequency, sensitivity, replication);
		float[][] transmission = new float[1][descriptor.length + data.length];

		for (int i = 0; i < descriptor.length; i++) {
			transmission[1][i] = descriptor[1][i];
		}

		for (int j = descriptor.length; j < data.length; j++) {
			transmission[1][j] = data[1][j];
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
			float sensitivity, int replication) {
		try {
			// Put file bytes into fileData byte array
			byte[] fileData = new byte[(int) file.length()];
			FileInputStream in = new FileInputStream(file);
			in.read(fileData);
			in.close();

			createAudioFile(fileData, filePath, transmissionSpeed, lowFrequency, sensitivity, replication);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
