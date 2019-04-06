package ru.metrologexpert;

import ru.metrologexpert.lut.LUT;
import ru.metrologexpert.lut.LutFactory;
import ru.metrologexpert.lut.LutType;

import static java.lang.Math.*;

/**
 * Abstract class to modeling direct digital synthesis, described in article
 * "Methods of Mapping from Phase to Sine Amplitude in Direct Digital Synthesis"
 * Jouko Vankka, IEEE TRANSACTIONS OF ULTRASONICS..., VOL.44, NO.2, MARCH 1997
 * <br>
 * Using pattern 'Template method', so methods evalU() and evalMse() are +template,
 * initLUT() and getLut() are subMethod and depending onto synthesizer realisation.
 *
 */
public class DDS{

    private int phaseMax;                       // Phase accumulator max value = 2^(nPhase-1), if greater - oversampling
    private int ampMax;                         // Synthesis signal amplitude max value = 2^nAmp
    private int phiInt;                         // Current rounded value of synthesized signal phase, in (0; phaseMax)
    private int dPhiInt;                        // Rounded phase increment on each standard tick
    private double phiPrec;                     // Current precise value of synthesized+ signal phase, in (0; 2pi)
    private double dPhiPrec;                    // Precise phase increment on each standard tick
    private int[] U;                            // Counting values of synthesis signal, in (0; ampMax)
    private double[] Uref;                      // Reference values of signal, need to calculate MSE
    private double mse;                         // Calculated MSE of synthesis signal
    private int N_sample;
    private LUT lut;

    public DDS(int nPhase, int nAmp, double fClk, double fOut, LutType lutType) {

        phaseMax = 1 << nPhase;                 // Phase accumulator max value
        ampMax = 1 << nAmp;                     // Amplitude max value

        dPhiPrec = fOut / fClk;                     // TODO: check if  fOut > fClk/2 -> oversampling
        dPhiInt = (int) round(phaseMax * (fOut / fClk));
        N_sample = (int)fClk;                   // time loop = 1 sec, so N_sample = fClk
        lut = new LutFactory().createLUT(lutType, nPhase, nAmp);
    }


    public int[] getU() {
        return U;
    }


    public double getMse() {
        return mse;
    }


    /**
     * Main modeling circle, synthesizing U(t) = ampMax*sin(2*PI*dPhiPrec*i).
     * Evaluate N_sample values. <br>
     *
     * */
    public final void evalU() {
        U = new int[N_sample];
        Uref = new double[N_sample];

        for (int i = 0; i < N_sample; i++) {
            U[i] = lut.getValue(phiInt);
            phiInt += dPhiInt;
            phiInt %= phaseMax;
            Uref[i] = ampMax * sin(2 * PI * phiPrec);
            phiPrec += dPhiPrec;
        }

        evalMse();

    }


    /**
     * Evaluate Mean Standard Error (MSE)
     */
    final void evalMse() {
        for (int i = 0; i < N_sample; i++) {
            mse += pow(Uref[i] - U[i], 2);
        }
        mse = sqrt(mse / N_sample) / ampMax;
    }

}




