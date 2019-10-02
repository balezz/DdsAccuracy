package ru.metrologexpert.lut;

public class LutFactory {

    public LUT createLUT(LutType name, int nPhase, int nAmp) {

        if (name == LutType.QUDRO) {
            return new QuadLUT(nPhase, nAmp);
        }
        if (name == LutType.SUBQUADRO) {
            return new QuadSubLUT(nPhase, nAmp);
        }
        if (name == LutType.SUNDERLAND) {
            return new SunderLUT(nPhase, nAmp);
        }
        else
            return new SimpleLUT(nPhase, nAmp);
    }
}
