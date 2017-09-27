import javax.xml.crypto.dom.DOMCryptoContext;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Toolbox {


    public static void write2ArrayListsToFile(ArrayList<Double> xVals, ArrayList<Double> yVals, String filename){

        try {
            File file = new File(filename+".txt");

            if(!file.exists()) file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for(int i = 0; i < xVals.size(); i ++){
                String output = String.valueOf(xVals.get(i)) + ", " + String.valueOf(yVals.get(i));
                bw.write(output);
                bw.newLine();
            }
            bw.close();
        }catch (IOException e){}
    }


    public static void write2ArraysToFile(double[] xVals, double[] yVals, String filename){


        try {
            File file = new File(filename+".txt");

            if(!file.exists()) file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            for(int i = 0; i < xVals.length; i ++){
                String output = String.valueOf(xVals[i]) + ", " + String.valueOf(yVals[i]);
                bw.write(output);
                bw.newLine();
            }
            bw.close();
        }catch (IOException e){}
    }
}
