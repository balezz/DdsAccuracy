package ru.metrologexpert;

public class DdsSimple extends DDS {

    DdsSimple (int nP, int nA, int F0, int FX) {
        super(nP, nA, F0, FX);
    }

    void initTable() {
        table = new int[phaseMax];
        for (int i = 0; i < phaseMax; i++) {
            table[i] = (int) Math.round( ampMax * Math.sin( (2 * pi * i) / phaseMax) );
        }
    }

    int getLUT(int phiInt) {
        if((phiInt < phaseMax) & (phiInt >= 0)) {
            return table[phiInt];
        } else {
            System.err.println("Index out of LUT");
            return 0;
        }
    }

}
