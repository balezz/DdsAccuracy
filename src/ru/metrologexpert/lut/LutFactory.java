package ru.metrologexpert.lut;

public class LutFactory {

    public LUT createLUT(String name, int nPhase, int nAmp) {
        if (name == "QuadLUT") {
            return new QuadLUT(nPhase, nAmp);
        }
        if (name == "QuadSubLUT") {
            return new QuadSubLUT(nPhase, nAmp);
        }
        if (name == "SunderLUT") {
            return new SunderLUT(nPhase, nAmp);
        } else return new SimpleLUT(nPhase, nAmp);
    }
}
