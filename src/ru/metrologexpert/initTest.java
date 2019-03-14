package ru.metrologexpert;

public class initTest {
    static int [][] tabAlphaBeta;
    static int [][] tabAlphaGamma;
    static int maxPhiA = 8;
    static int maxPhiB = 8;
    static int maxPhiY = 8;
    static int ampMax = 1024;
    static int nPhiA = 3;
    static int nPhiB = 3;
    static int nPhiY = 3;
    static double pi = 3.14;
    static int phaseMax = 1024;


    public static void main(String args[]) {
        tabAlphaBeta = new int[maxPhiA][maxPhiB];
        tabAlphaGamma = new int[maxPhiA][maxPhiY];
        for (int a = 0; a < maxPhiA; a++) {
            for (int b = 0; b < maxPhiB; b++) {
                tabAlphaBeta[a][b] = (int) ( ampMax *
                        Math.sin((pi/2 * (a<<(nPhiY+nPhiB) + b<<nPhiY) / phaseMax)));
            }
        }
        for (int a = 0; a < maxPhiA; a++) {
            for(int y = 0; y < maxPhiY; y++) {
                tabAlphaGamma[a][y] = (int)(ampMax *
                        (Math.cos(pi/2 * (a<<(nPhiY+nPhiB) + (maxPhiB/2)<<nPhiY) / phaseMax )) *
                        Math.sin( (pi/2 * y) / phaseMax) );
            }
        }
        System.out.println(1<<nPhiY);
        System.out.println(( (0<<(nPhiY+nPhiB)) + (7<<nPhiY)) );
        System.out.println(pi/2 * ((0<<(nPhiY+nPhiB)) + (7<<nPhiY)));
        System.out.println((pi/2 * ((0<<(nPhiY+nPhiB)) + (7<<nPhiY)) / phaseMax));
        System.out.println(Math.sin((pi/2 * ((0<<(nPhiY+nPhiB)) + (7<<nPhiY)) / phaseMax))) ;
        System.out.println(ampMax*Math.sin((pi/2 * ((0<<(nPhiY+nPhiB)) + (7<<nPhiY)) / phaseMax))) ;

    }
}
