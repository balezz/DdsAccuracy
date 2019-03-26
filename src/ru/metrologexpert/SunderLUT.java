package ru.metrologexpert;


import static java.lang.Math.*;

/**
* Class for modeling digital synthesis modified Sunderland architecture
 * <br>
 * D.A.Sunderland and others
 * "CMOS/SOS frequency synthesizer LSI circuit for spread spectrum communication"
 * IEEE J.Solid-State Circuits, vol. SC-19, pp. 497-505, Aug. 1984
 * <br>
 * The original Sunderland technique is based on simple trigonometric identities.
 * The phase address of the quarter of the sine wave is decomposed to
 * $\phiPrec = a + b + y$, with the word-lengths of the variables: nPhiA, nPhiB, nPhiY.
 * In this way the nP=12 phase bits are divided into three 4 bit fractions
 * such that $a < 1, b < 2^{-4}, y < 2^{-8}$
 * The desired sine function is given by:
 * $sin(\pi/2(a+b+y)) \approx sin(\pi/2(a+b)) + cos(\pi/2(a))sin(\pi/2(y))$
*/
public class SunderLUT implements ILUT {
    int nPhase;                                 // Phase accumulator digit capacity
    int addrPhaseMax;                           // LUT address max value
    int phaseMax;                               // External address max value
    int nAmp;                                   // Synthesis signal amplitude digit capacity
    int ampMax;
    int nPhiA;                                  // Digit capacity of alpha
    int nPhiB;                                  // Digit capacity of beta
    int nPhiY;                                  // Digit capacity of gamma
    int maxPhiA;                                // Max value of alpha phase register
    int maxPhiB;                                // Max value of alpha phase
    int maxPhiY;                                // Max value of alpha phase
    int[] aby;
    int[][] tabAlphaBeta;                       // Look Up Table for sin(PI/2(a+b))
    int[][]tabAlphaGamma;                       // Look Up Table for cos(PI/2(a+b_mean))*sin(PI/2(y))

    SunderLUT(int nP, int nA) {
        this.nPhase = nP;
        this.nAmp = nA;
        this.init();
    }

    /**
     * initialize tabAlphaBeta and tabAlphaGamma
     * with values sin(0 : pi/2)
    */
    public final void init() {
        ampMax = 1 << nAmp;
        phaseMax = 1 << nPhase;
        addrPhaseMax = 1 << (nPhase - 2);
        nPhiY = nPhase / 3;                         // digit capacity of registers a, b, y
        nPhiB = nPhase / 3;
        nPhiA = nPhase - nPhiY - nPhiB;
        maxPhiA = 1 << nPhiA;                       // max phase value of registers a, b, y
        maxPhiB = 1 << nPhiB;                       // = 2^nPhiA, 2^nPhiB, 2^nPhiY
        maxPhiY = 1 << nPhiY;
        tabAlphaBeta = new int[maxPhiA][maxPhiB];

        for (int i = 0; i < maxPhiA; i++) {
            int a_ = i << (nPhiY + nPhiB);
            for (int j = 0; j < maxPhiB; j++) {
                int b_ = j << nPhiY;
                tabAlphaBeta[i][j] = (int)(ampMax * sin(0.5*PI * (a_ + b_) / addrPhaseMax ));
            }
        }

        tabAlphaGamma = new int[maxPhiA][maxPhiY];
        for (int i = 0; i < maxPhiA; i++) {
            int a_ = i << (nPhiY + nPhiB);
            for(int k = 0; k < maxPhiY; k++) {
                tabAlphaGamma[i][k] = (int) (ampMax *
                        (cos(2 * PI * (a_ + ((maxPhiB / 2) << nPhiY)) / addrPhaseMax)) *
                        sin((2 * PI * k) / addrPhaseMax));
            }
        }
        // System.err.println("LUT initialized");
    }



    public int getValue(int phiInt) {
        if ((phiInt >= 0) & (phiInt < phaseMax / 4)) {
            return lutAdapter (phiInt);
        }
        if ((phiInt >= phaseMax / 4) & (phiInt < phaseMax / 2)) {
            return lutAdapter (phaseMax / 2 - phiInt - 1);
        }
        if ((phiInt >= phaseMax / 2) & (phiInt < 3 * phaseMax / 4)) {
            return - lutAdapter(phiInt - phaseMax / 2);
        }
        if ((phiInt >= 3 * phaseMax / 4) & (phiInt < phaseMax)) {
            return - lutAdapter(phaseMax - phiInt - 1);
        } else {
            System.err.println("Index out of LUT");
            return 0;
        }
    }


    /**
     * adapter allows invoke LUT by simple int index
     * function returns value of sin(x) from tabAlphaBeta[][] and tabAlphaGamma[][] array
     * @param phiInt index of LUT in [0; nPhi]
     * @return int value of sin(phiInt) in [0; ampMax]
     */
    int lutAdapter(int phiInt) {
        if((phiInt < addrPhaseMax) & (phiInt >= 0)) {
            aby = dividePhi(phiInt);
            return tabAlphaBeta[aby[0]][aby[1]] + tabAlphaGamma[aby[0]][aby[2]];
        } else {
            System.err.println("Index out of LUT");
            return 0;
        }
    }


    /**
     * Method is divide phaseMax value into three values
     * of a b y with capacity nA nB nY bits
     * @param phiInt divided phaseMax value
     * @return array of {a, b, y}
     */
    int[] dividePhi(int phiInt) {
        int[] aby = new int[3];
        if (phiInt > 1 << (nPhiA + nPhiB + nPhiY)) {
            System.err.print("Out of index");
            return aby;
        }
        int temp;
        aby[2] = phiInt % maxPhiY;
        temp = phiInt >> nPhiY;
        aby[1] = temp % maxPhiB;
        temp = temp >> nPhiB;
        aby[0] = temp % maxPhiA;
        // System.out.println(phiInt + " = " + aby[0] + "  " + aby[1] + "  " + aby[2] + " " + multyPhi(aby));
        return aby;
    }

    /**
     * Method is multyplex array of {a, b, y}
     * into value phiInt
     * @param aby array of {a, b, y}
     * @return
     */
    int multyPhi(int[] aby) {
        return 1 << (nPhiB + nPhiY) * aby[0] + (1 << (nPhiY)) * aby[1] + aby[2];
    }


}
