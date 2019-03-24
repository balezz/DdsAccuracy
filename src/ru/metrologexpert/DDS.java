package ru.metrologexpert;

import static java.lang.Math.*;

/**
 * Abstract class to modeling direct digital synthesis, described in article
 * "Methods of Mapping from Phase to Sine Amplitude in Direct Digital Synthesis"
 * Jouko Vankka, IEEE TRANSACTIONS OF ULTRASONICS..., VOL.44, NO.2, MARCH 1997
 * <br>
 * Using pattern 'Template method', so methods evalU() and evalError() are +template,
 * initLUT() and getLut() are subMethod and depending onto synthesizer realisation.
 *
 */
class DDS {
    DDS(int nP, int nAmp, double fClk, double fOut) {
        this.nPhase = nP;
        this.nAmp = nAmp;
        this.fClk = fClk;
        this.fOut = fOut;

        phaseMax = 1 << nPhase;         // Phase accumulator max value
        ampMax = 1 << nAmp;
        dPhi = fOut / fClk;             // TODO: check if  fOut > fClk/2 -> oversampling
        dPhiInt = (int) round(phaseMax * (fOut / fClk));
        N_sample = (int)fClk;           // time loop = 1 sec, so N_sample = fClk

    }

    double fClk;                        // Standard clock frequency value
    double fOut;                        // Synthesis frequency value
    int nPhase;                         // Phase accumulator digit capacity
    int phaseMax;                       // Phase accumulator max value = 2^(nPhase-1), if greater - oversampling
    int nAmp;                           // Synthesis signal amplitude digit capacity
    int ampMax;                         // Synthesis signal amplitude max value = 2^nAmp
    int phiInt = 0;                     // Current rounded value of synthesized signal phase, in (0; phaseMax)
    double phiPrec;                     // Current precise value of synthesized+ signal phase, in (0; 2pi)
    double error = 0;                   // Calculated MSE of synthesis signal
    double dPhi;                        // Precise phase increment on each standard tick
    public int[] U;                            // Counting values of synthesis signal, in (0; ampMax)
    public double[] Uref;                      // Reference values of signal, need to calculate MSE

    public int getN_sample() {
        return N_sample;
    }

    int N_sample;
    int dPhiInt;


    public int[] getU() {
        return U;
    }

    public double getError() {
        return error;
    }


    /**
     * Main modeling circle, synthesizing U(t) = ampMax*sin(2*PI*dPhi*i).
     * Evaluate N_sample values. <br>
     *
     * */
    final void evalU() {
        SimpleLUT lut = new SimpleLUT(nPhase, nAmp);     // TODO: setting LUT algorithm from class argument

        U = new int[N_sample];
        Uref = new double[N_sample];

        for (int i = 0; i < N_sample; i++) {
            phiInt += dPhiInt;
            phiInt %= phaseMax;
            U[i] = lut.getValue(phiInt);
            Uref[i] = ampMax * sin(2 * PI * phiPrec);
            phiPrec += dPhi;
        }

        evalError();
    }


    /**
     * Evaluate Mean Standard Error (MSE)
     */
    final void evalError() {
        for (int i = 0; i < N_sample; i++) {
            error += pow(Uref[i] - U[i], 2);
        }
        error = sqrt(error/ N_sample) / ampMax;
    }

}




