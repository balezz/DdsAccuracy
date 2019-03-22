package ru.metrologexpert;

import java.util.Arrays;

public class initTest {

    public static void main(String args[]) {
        DDS dds = new DDS(8, 8, 4000, 100);
        dds.evalU();
        dds.evalError();
        System.out.print(Arrays.toString(dds.getU()));
    }
}
