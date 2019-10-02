package ru.metrologexpert.lut;

import static java.lang.Math.PI;

class QuadSubLUT extends LUT {


    public QuadSubLUT(int nPhase, int nAmp) {
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

    void init() {
        phaseMax = 1 << nPhase;
        addrPhaseMax = 1 << (nPhase - 2);
        ampMax = 1 << nAmp;
        LookUpTable = new int[addrPhaseMax];
        for (int i = 0; i < addrPhaseMax; i++) {
            LookUpTable[i] = (int) Math.round(ampMax * Math.sin(PI * 0.5 * i / addrPhaseMax)) - i;
        }
    }


    /**
     * function returns value of sin(x) from LookUpTable[i] array pi/2
     * and add index i
     *
     * @param phiInt index of lut in [0; nPhi]
     * @return int value of sin(phiInt) in [0; ampMax]
     * TODO: indexing of LookUpTable[i]
     */
    public int getValue(int phiInt) {
        if ((phiInt >= 0) & (phiInt < phaseMax / 4)) {
            int phase = phiInt;
            return LookUpTable[phase] + phase;
        }
        if ((phiInt >= phaseMax / 4) & (phiInt < phaseMax / 2)) {
            int phase = phaseMax / 2 - phiInt - 1;
            return LookUpTable[phase] + phase;
        }
        if ((phiInt >= phaseMax / 2) & (phiInt < 3 * phaseMax / 4)) {
            int phase = phiInt - phaseMax / 2;
            return - LookUpTable[phase] - phase;
        }

        if ((phiInt >= 3 * phaseMax / 4) & (phiInt < phaseMax)) {
            int phase = phaseMax - phiInt - 1;
            return - LookUpTable[phase] - phase;
        } else {
            System.err.println("Index out of lut");
            return 0;
        }
    }



}
