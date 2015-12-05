package com.example.braeden.waveswap_app.audio.input;

/**
 * Created by Peter Sulucz on 12/5/2015.
 */
public class ValueNormalizer {
    public static boolean[] NormalizeValues(float[] values, float threshold)
    {
        boolean[] results = new boolean[values.length];
        for(int i = 0; i < results.length; i++)
        {
            // clear to false
            results[i] = false;
        }

        float max = 0;

        // get the maximum value in the array
        for(float f : values)
        {
            max = Math.max(max, f);
        }

        // if the max value is less than the threshold, just return
        if(max < threshold)
            return results;

        // check to see if the values are valid
        for(int i = 0; i < values.length; i++)
        {
            float percentDiff = PercentDifference(max, values[i]);

            // if the percent difference is less than 10%
            if(percentDiff < 0.1f)
            {
                results[i] = true;
            }
        }

        return results;
    }

    private static float PercentDifference(float a, float b)
    {
        float avg = (a+b)/2;
        float dif = Math.abs(a-b);
        return dif / avg;
    }
}
