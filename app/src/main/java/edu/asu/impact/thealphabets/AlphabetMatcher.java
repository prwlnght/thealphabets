package edu.asu.impact.thealphabets;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Gautam on 7/14/2016.
 */
public class AlphabetMatcher {

    File testFile;
    double[][] rawData = new double[17][50];
    double[][] modelData = new double[85*26][3];
    ArrayList<double[]> testFeatures = new ArrayList<>(5);
    double[] means = new double[17];
    double[] max = new double[17];
    double[] min = new double[17];
    double[] totEng = new double[17];
    double[] std = new double[17];

   //String SDCARD_LOCATION =  getApplicationContext().getExternalFilesDir(null).getAbsolutePath();

    public AlphabetMatcher(File f){
        this.testFile = f;
        readFile(testFile,0);
        getMoments();
    }


    void readFile(File file, int fileType){

        BufferedReader br = null;

        String line = "";
        try {
            br = new BufferedReader(new FileReader(file));
            int k =0;  //will go to 50
            while ((line = br.readLine()) != null) {
                // use comma as separator



                // for FileType testFile(0)
                if(fileType == 0){
                    String[] row = line.split(",");
                    for(int i=0;i<17;i++){

                        rawData[i][k]= Double.parseDouble(row[i]);

                    }
                }else{ //for reading featureSelectionWorking.csv file
                    String[] row = line.split("\",\"");
                    if(k != 0){
                        if(k == 2210){
                            Log.d("A","do nothing");
                        }
                        modelData[k-1][0] = Double.parseDouble(row[5]);   //threshold lower
                        modelData[k-1][1] = Double.parseDouble(row[6]);  // threshold upper
                        modelData[k-1][2] = Double.parseDouble(row[7].split("\",")[1]);  // normalized weight
                    }
                }

                k++;

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void getMoments(){
        int i,j=0;

        for(i=0;i<17;i++){
            double maximum = -1000, minimum = 1000;

            for(j=0;j<50;j++){
                means[i] = means[i] + rawData[i][j];
                if(rawData[i][j] > maximum){
                    maximum = rawData[i][j];
                }
                if(rawData[i][j] < minimum){
                    minimum = rawData[i][j];
                }
                totEng[i] = totEng[i] + (rawData[i][j]* rawData[i][j]);
            }
            means[i] = means[i]/50;
            max[i] = maximum;
            min[i] = minimum;
            std[i] = getStd(means[i],i);

        }

        testFeatures.add(0,means);
        testFeatures.add(1,max);
        testFeatures.add(2,min);
        testFeatures.add(3,std);
        testFeatures.add(4,totEng);
    }

    double getStd(double mean,int row){
        int i;
        double sum=0,temp;
        for(i=0;i<50;i++){
            temp = rawData[row][i] - mean;
            temp = temp*temp;
            sum = sum + temp;
        }
        sum = sum/50;
        sum = Math.sqrt(sum);
        return sum;
    }

    double[] checkAlphabet(){
        int alphabet,features,sensors,moments;
        double[] weights = new double[26];
        double weight =0;

        for(alphabet =0; alphabet<26; alphabet++){     // a to z
            for(moments = 0;moments<5;moments++){      // mean,max,min,std,totEng
                double[] Moment = testFeatures.get(moments);

                for(sensors =0;sensors<17;sensors++) {  // EMG0,EMG1 ....ORN3
                    features = alphabet*5*17+moments*17+sensors;
                    if ((Moment[sensors] > modelData[features][0]) && (Moment[sensors] < modelData[features][1])) {
                        weight += modelData[features][2];
                    }
                }
            }
            weights[alphabet] = weight;
            weight =0;

        }

        return weights;
    }

    String WeightSorter(double[] weights){
        double max =-1000;
        int index=0;
        for(int i=0;i<26;i++){
            if(weights[i] > max){
                max = weights[i];
                index = i;
            }
        }

        return String.valueOf(Character.toChars(65+index));
    }


}
