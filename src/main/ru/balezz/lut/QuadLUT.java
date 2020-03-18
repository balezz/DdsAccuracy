package ru.balezz.lut;

import static java.lang.Math.PI;

/**
 * Class intended to evaluate values of
 * measurement signal and standard error
 */
class QuadLUT extends LUT {

    public QuadLUT(int nPhase, int nAmp) {
        this.nPhase = nPhase;
        this.nAmp = nAmp;
        this.init();
    }

    int nPhase;                                       // Phase accumulator digit capacity
    int addrPhaseMax;                                 // lut internal address max value
    int phaseMax;                                     // external address max value
    int nAmp;                                         // Synthesis signal amplitude digit capacity
    int ampMax;
    int[] LookUpTable;                                // lut = LookUpTable

    public void init() {
        addrPhaseMax = 1 << (nPhase - 2);
        phaseMax = 1 << nPhase;
        ampMax = 1 << nAmp;
        LookUpTable = new int[addrPhaseMax];
        for (int i = 0; i < addrPhaseMax; i++) {
            LookUpTable[i] = (int) Math.round(ampMax * Math.sin(PI/2 * (i - 1) / addrPhaseMax));
        }
    }

    /**
     * function returns value of sin(x) from LookUpTable[] array pi/2
     *
     * @param phiInt index of lut in [0; nPhi]
     * @return int value of sin(phiInt) in [0; ampMax]
     * TODO: clear indexing of LookUpTable[i]
     */
    public int getValue(int phiInt) {
        if ((phiInt >= 0) & (phiInt < phaseMax / 4)) {
            return LookUpTable[phiInt];
        }
        if ((phiInt >= phaseMax / 4) & (phiInt < phaseMax / 2)) {
            return LookUpTable[phaseMax / 2 - phiInt - 1];
        }
        if ((phiInt >= phaseMax / 2) & (phiInt < 3 * phaseMax / 4)) {
            return -LookUpTable[phiInt - phaseMax / 2];
        }
        if ((phiInt >= 3 * phaseMax / 4) & (phiInt < phaseMax)) {
            return -LookUpTable[phaseMax - phiInt - 1];
        } else {
            System.err.println("Index out of lut");
            return 0;
        }
    }

}
