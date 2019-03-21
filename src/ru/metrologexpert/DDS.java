package ru.metrologexpert;

import static java.lang.Math.*;

/**
 * Abstract class to modeling direct digital synthesis, described in article
 * "Methods of Mapping from Phase to Sine Amplitude in Direct Digital Synthesis"
 * Jouko Vankka, IEEE TRANSACTIONS OF ULTRASONICS..., VOL.44, NO.2,MARCH 1997
 * <br>
 *
 *
 */
abstract class DDS {
    DDS(){}
    DDS(int nP, int nAmp, int fClk, int fOut) {
        this.nPhase = nP;
        this.nAmp = nAmp;
        this.fClk = fClk;
        this.fOut = fOut;

        this.initTable();               // Initializing LookUpTable (LUT)     #subMethod()
        this.evalU();                   // Evaluate sample                    +templateMethod()
        this.evalError();               // Evaluate Mean Standard Error (MSE) +templateMethod()
    }

    double fClk;                           // Standard clock frequency value
    double fOut;                           // Synthesis frequency value
    int nPhase;                         // Phase accumulator digit capacity
    int phaseMax = 1 << (nPhase - 1);   // Phase accumulator max value = 2^(nPhase-1), if greater - oversampling
    int addrPhaseMax = 1 << nPhase;      // LUT address max value
    int nAmp;                           // Synthesis signal amplitude digit capacity
    int ampMax = 1 << nAmp;             // Synthesis signal amplitude max value = 2^nAmp
    int phiInt = 0;                     // Current rounded value of synthesized signal phase, in (0; phaseMax)
    double phiPrec = 0;                 // Current precise value of synthesized+ signal phase, in (0; 2pi)
    double error = 0;                   // Calculated MSE of synthesis signal
    double dPhi = fOut / fClk;          // Precise phase increment on each standard tick
    int[] table;                        // LUT table
    int[] U;                            // Counting values of synthesis signal, in (0; ampMax)
    double[] Uref;                      // Reference values of signal, need to calculate MSE
    int N_sample = (int)fClk;           // time loop = 1 sec, so N_sample = fClk
    int dPhiInt = (int) round(phaseMax * (fOut / fClk));

    /**
     * Main modeling circle, synthesizing U(t) = ampMax*sin(2*PI*dPhi*i)
     */
    void evalU() {
        U = new int[N_sample];
        Uref = new double[N_sample];
        for (int i = 0; i < N_sample; i++) {
            phiInt += dPhiInt;
            phiInt %= addrPhaseMax;
            U[i] = getLUT(phiInt);

            Uref[i] = ampMax * sin(2*PI* phiPrec);
            phiPrec += dPhi;
        }
    }

    /**
     * Evaluate standard error,
     * error interval in [0; 1]
     */
    void evalError() {
        for (int i = 0; i < fClk; i++) {
            error += pow(Uref[i] - U[i], 2);
        }
        error = sqrt(error/ fClk) / ampMax;
    }

    public double getError() {
        return error;
    }

    /**
     * Инициализация таблицы LUT с заданными значениями phaseMax и ampMax
     */
    abstract void initTable();

    /**
     * Метод возвращает значение sin(x) из массива table[]
     * @param phiInt index of LUT in [0; phaseMax]
     * @return int value of sin(phiInt) in [0; ampMax]
     */
    abstract int getLUT(int phiInt);

}




