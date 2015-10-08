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
public class Parser {
    
    public static final int BYTE_LENGTH = 8;
    public static final int INT_LENGTH = 32;
        
    public static boolean isEqual(byte[] bytes, byte[] other)
    {
        for( int i = 0; i < bytes.length; i++ )
        {
            if( bytes[i] != other[i] )
            {
                return false;
            }
        }
            
        return true;
    }
        
    public static String stringByte(byte b)
    {
        String sb = "";
        for(int i = 7; i >= 0; i--)
        {
            int currentBit = (b & (1 << i)) == 0 ? 0 : 1;
            if(currentBit == 1)
            {
                sb = sb + "1";
            }
            else
            {
                sb = sb + "0";
            }
        }
        return sb;
    }
        
    public static void printBytes(byte[] bytes)
    {
        int index = 0;
        for( byte b : bytes)
        {
            String sb = "";
            for(int i = 7; i >= 0; i--)
            {
                int currentBit = (b & (1 << i)) == 0 ? 0 : 1;
                if(currentBit == 1)
                {
                    sb = sb + "1";
                }
                else
                {
                    sb = sb + "0";
                }
            }
            System.out.println("Byte #" + index + ": " + sb);
            index++;
        }
    }
        
    public static void printFloats
        (float[][] floats)
    {
        for(int i = 0; i < floats[0].length; i++)
        {
            System.out.println(floats[0][i]);
        }
    }
    
    public static float[][] createData
        (byte[] bytes,
         int transmissionSpeed,
         float lowFrequency,
         float sensitivity)
    {
        int numBytes = bytes.length;
        int numBits = numBytes * 8;
        int paddingBits = (numBits % transmissionSpeed > 0) ? (transmissionSpeed - numBits
                % transmissionSpeed)
                : 0;
        int totalBits = numBits + paddingBits; // Padding at the end of the
                                               // transmission
        int dataSize = totalBits / transmissionSpeed;

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

        while (dataIndex < dataSize)
        {
            // Set up start of loop
            frequency = lowFrequency;

            // Loop equal to transmission speed
            for (int i = 0; i < transmissionSpeed; i++)
            {
                // If you overrun the current byte, fetch a new one
                if (bitIndex > 7)
                {

                    byteIndex++;

                    // Normal scenario, there are more bytes to process
                    // So reset variables for the new byte
                    if (byteIndex < numBytes
                    {
                        currentByte = bytes[byteIndex];
                        bitIndex = 0;
                        maskShiftAmount = 7;
                    }
                    // Padding scenario, all bytes have been processed and now
                    // padding
                    // must be added.
                    else
                    {
                        currentByte = 0;
                        bitIndex = 0;
                        maskShiftAmount = 7;
                    }
                }
                byte currentBit = (byte) (currentByte & (baseMask << maskShiftAmount));
                if (currentBit != 0)
                {
                    frequency += Math.pow(2, transmissionSpeed - i - 1)
                            * sensitivity;
                }
                bitIndex++;
                maskShiftAmount--;
            }
            data[0][dataIndex] = frequency; // does this work? yes.
            dataIndex++;
        }
        return data;
    }
    
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

            int currentValue = Math
                    .round((currentFrequency - lowFrequency) / sensitivity); // Should
                                                                             // be
                                                                             // an
                                                                             // exact
                                                                             // division
            int currentValueSize = transmissionSpeed;

            // Fill up bytes until you no longer can
            while (currentValueSize >= currentByteBitsRemaining) {
                // Construct a byte whose value is the necessary bits in
                // "currentValue"
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
                                                              // number of bits
                                                              // that went into
                                                              // currentByte
                currentValue &= -1 >>> (INT_LENGTH - currentValueSize); // Erase
                                                                        // the
                                                                        // bits
                                                                        // we
                                                                        // just
                                                                        // used
                                                                        // up
                currentByteBitsRemaining = BYTE_LENGTH; // Reset
                currentByteIndex++; // Increment
                currentByte = 0; // Reset

                // Check to see if the array is finished
                if (currentByteIndex >= size) {
                    currentValueSize = 0;
                    break;
                }
            }

            // Add the rest of the currentValue to currentByte, and left shift
            // it to the very end
            if (currentValueSize > 0) {
                byte leftShiftedCurrentValue = (byte) (currentValue << (currentByteBitsRemaining - currentValueSize));

                currentByte |= leftShiftedCurrentValue;

                // Maintain variables for correct execution
                currentByteBitsRemaining -= currentValueSize;
            }
        }
        return bytes;
    }
        
    public static boolean testData
        (int sizeBytes,
         byte[] testBytes,
         int testTS,
         float testLF,
         float testS)
    {
        float[][] data = createData(testBytes, testTS, testLF, testS);
        byte[] returnBytes = decodeData(data, sizeBytes, testTS, testLF, testS);
        return isEqual(testBytes, returnBytes);
    }
    
    public static void testOne
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
                        System.out
                                .println("Test Fail - TransmissionSpeed: "
                                        + i + " LowFrequency: " + j
                                        + " Sensitivity: " + k);
                        numFail++;
                    }
                    else {
                        numSuccess++;
                    }
                }
            }
        }

        System.out.println("Tests Finished - " + numSuccess
                + " Successes and " + numFail + " Failures");
    }
    
    /**
     * Converts a byte array into an audio file
     * 
     * @param filePath Path to place the audio file
     * @param fileName Audio file name
     * @param transmissionSpeed Bits to read per transmission e.g., 2 = 00, 01, 10, 11
     * @param lowFrequency Frequency that transmission begins at, e.g., lowFrequency = 10Hz, 00 = 10Hz
     * @param sensitivity Difference between frequencies that hardware can detect
     */
    public static void createAudioFile
        (byte[] bytes,
         String filePath,
         int transmissionSpeed,
         float lowFrequency,
         float sensitivity)
    {
        
        float[][] data = createData(bytes, transmissionSpeed, lowFrequency, sensitivity);
        
        WavFileReaderWriter wfrw = new WavFileReaderWriter();
        try
        {
            SampleAudioFormat format = new SampleAudioFormat(44100, 16, 1);
            wfrw.writeAudioFile(data, filePath, AudioFileType.WAV, format);
            System.out.println("hello");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OperationUnsupportedException e) {
            e.printStackTrace();
        } catch (FileFormatException e) {
            e.printStackTrace();
        }
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
                    
                    createAudioFile(fileData, args[1], 1, 1, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        } else {
            throw new IllegalArgumentException("You must input a file");
        }
    }
}
