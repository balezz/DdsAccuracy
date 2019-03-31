package ru.metrologexpert;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class TestDds {

    public static String FILE_NAME = "out/error.csv";
    double[] errorArray = new double[20];


    public static void main(String args[]) {
        TestDds initTest = new TestDds();
        initTest.printDdsSample();
        initTest.evalErrorArray();
        initTest.writeErrorFile();
    }

    void printDdsSample() {
        DDS dds = new DDS(16, 8, 4000, 100);
        dds.evalU();
        double error = dds.getMse();
        System.out.println(Arrays.toString(dds.getU()));
        System.out.println("Error = " + error);
    }

    void evalErrorArray() {
        for(int i = 8; i <20; i++){
            DDS dds = new DDS(i, 8, 4000, 100);
            dds.evalU();
            errorArray[i] = dds.getMse();
        }
    }

    void writeErrorFile() {
        try {
            PrintWriter out = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(FILE_NAME)));
            for (int i = 8; i < 20; i++) {
               out.println(errorArray[i]);
            }
            out.close();
        } catch (IOException e) {
            System.err.print("File not created");
        }
    }

}
