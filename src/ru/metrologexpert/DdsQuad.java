package ru.metrologexpert;

/**
 * Class intended to evaluate values of
 * measurement signal and standard error
 */
public class DdsQuad extends DDS {

    DdsQuad(int nP, int nA, int F0, int FX) {
        super(nP, nA, F0, FX);
    }

    @Override
    void initTable() {
        table = new int[phaseMax / 4];
        for (int i = 0; i < phaseMax / 4; i++) {
            table[i] = (int) Math.round(ampMax * Math.sin(2 * pi * (i - 0.5) / phaseMax));
        }
    }

    /**
     * function returns value of sin(x) from table[] array pi/2
     *
     * @param phiInt index of LUT in [0; nPhi]
     * @return int value of sin(phiInt) in [0; ampMax]
     * TODO: indexing of table[i]
     */
    @Override
    int getLUT(int phiInt) {
        if ((phiInt >= 0) & (phiInt < phaseMax / 4)) {
            return table[phiInt];
        }
        if ((phiInt >= phaseMax / 4) & (phiInt < phaseMax / 2)) {
            return table[phaseMax / 2 - phiInt - 1];
        }
        if ((phiInt >= phaseMax / 2) & (phiInt < 3 * phaseMax / 4)) {
            return -table[phiInt - phaseMax / 2];
        }
        if ((phiInt >= 3 * phaseMax / 4) & (phiInt < phaseMax)) {
            return -table[phaseMax - phiInt - 1];
        } else {
            System.err.println("Index out of LUT");
            return 0;
        }
    }

}
