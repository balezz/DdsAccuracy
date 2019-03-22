package ru.metrologexpert;

import static java.lang.Math.PI;

class SimpleLUT {

    public SimpleLUT(int nPhase, int nAmp) {
        this.nPhase = nPhase;
        this.nAmp = nAmp;
        this.init();
    }

    int nPhase;                                       // Phase accumulator digit capacity
    int addrPhaseMax;                                 // LUT address max value
    int nAmp;                                         // Synthesis signal amplitude digit capacity
    int ampMax;
    int[] LookUpTable;                                // LUT = LookUpTable


    final void init() {
        addrPhaseMax = 1 << nPhase;
        ampMax = 1 << nAmp;
        LookUpTable = new int[addrPhaseMax];
        for (int i = 0; i < addrPhaseMax; i++) {
            LookUpTable[i] = (int) Math.round( ampMax * Math.sin( (2 * PI * i) / addrPhaseMax) );
        }
    }

    int getValue(int phiInt) {
            return LookUpTable[phiInt];
    }

}