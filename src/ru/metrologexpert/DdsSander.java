package ru.metrologexpert;


import static java.lang.Math.PI;

/**
* Class for modeling digital synthesis modified Sunderland architecture
 * <br>
 * D.A.Sunderland and others
 * "CMOS/SOS frequency synthesizer LSI circuit for spread spectrum communication"
 * IEEE J.Solid-State Circuits, vol. SC-19, pp. 497-505, Aug. 1984
 * <br>
 * The original Sunderland technique is based on simple trigonometric identities.
 * The phase address of the quarter of the sine wave is decomposed to
 * $\phi = a + b + y$, with the word-lengths of the variables: nPhiA, nPhiB, nPhiY.
 * In this way the nP=12 phase bits are divided into three 4 bit fractions
 * such that $a < 1, b < 2^{-4}, y < 2^{-8}$
 * The desired sine function is given by:
 * $sin(\pi/2(a+b+y)) \approx sin(\pi/2(a+b)) + cos(\pi/2(a))sin(\pi/2(y))$
* */
public class DdsSander extends DDS {
    int nPhiA;                          // digit capacity of alpha
    int nPhiB;                          // разрядность регистра угла бета
    int nPhiY;                          // разрядность регистра угла гамма
    int maxPhiA;                        // макс значение угла альфа
    int maxPhiB;                        // макс значение угла бета
    int maxPhiY;                        // макс значение угла гамма
    int[] aby;
    int[][] tabAlphaBeta;               // таблица LUT для углов альфа, бета
    int[][]tabAlphaGamma;               // таблица LUT для углов альфа, гамма

    DdsSander(int nP, int nA, int F0, int FX) {
        phaseMax = 1 << nP;
        ampMax = 1 << nA;
        Fo = F0;
        Fx = FX;
        dPhi = ((double)FX)/F0;                 // приращение фазы синтезируемого сигнала
                                                // на каждом периоде частоты стандарта
        nPhiY = nP / 3;
        nPhiB = nP / 3;
        nPhiA = nP - nPhiY - nPhiB - 2;
        maxPhiA = 1 << (nPhiA);
        maxPhiB = 1 << (nPhiB);
        maxPhiY = 1 << (nPhiY);
        this.initTable();                       // Инициализация таблицы LUT #subMethod()
        this.evalU();                           // Вычисление отсчетов +templateMethod()
        this.evalError();                       // Вычисление погрешности +templateMethod()
    }

    /**
     * initialize tabAlphaBeta and tabAlphaGamma
     * with values sin(0 : pi/2)
     */
    void initTable() {
        tabAlphaBeta = new int[maxPhiA][maxPhiB];
        tabAlphaGamma = new int[maxPhiA][maxPhiY];
        for (int a = 0; a < maxPhiA; a++) {
            for (int b = 0; b < maxPhiB; b++) {
                tabAlphaBeta[a][b] = (int) ( ampMax *
                        Math.sin((2*PI * ((a<<(nPhiY+nPhiB)) + (b<<nPhiY)) / phaseMax)));
            }
        }
        for (int a = 0; a < maxPhiA; a++) {
            for(int y = 0; y < maxPhiY; y++) {
                tabAlphaGamma[a][y] = (int)(ampMax *
                        (Math.cos(2*PI * ((a<<(nPhiY+nPhiB)) + ((maxPhiB/2)<<nPhiY)) / phaseMax )) *
                            Math.sin( (2*PI * y) / phaseMax) );
            }
        }
        System.out.println("Таблицы инициализированы");
    }

    @Override
    int getLUT(int phiInt) {
        if ((phiInt >= 0) & (phiInt < phaseMax / 4)) {
            return lutAdapter (phiInt);
        }
        if ((phiInt >= phaseMax / 4) & (phiInt < phaseMax / 2)) {
            return lutAdapter (phaseMax / 2 - phiInt - 1);
        }
        if ((phiInt >= phaseMax / 2) & (phiInt < 3 * phaseMax / 4)) {
            return - lutAdapter(phiInt - phaseMax / 2);
        }
        if ((phiInt >= 3 * phaseMax / 4) & (phiInt < phaseMax)) {
            return - lutAdapter(phaseMax - phiInt - 1);
        } else {
            System.err.println("Index out of LUT");
            return 0;
        }
    }

    /**
     * adapter allows invoke LUT by simple int index
     * function returns value of sin(x) from tabAlphaBeta[][] and tabAlphaGamma[][] array
     * @param phiInt index of LUT in [0; nPhi]
     * @return int value of sin(phiInt) in [0; ampMax]
     */
    int lutAdapter(int phiInt) {
        if((phiInt < phaseMax) & (phiInt >= 0)) {
            aby = dividePhi(phiInt);
            return tabAlphaBeta[aby[0]][aby[1]] + tabAlphaGamma[aby[0]][aby[2]];
        } else {
            System.err.println("Index out of LUT");
            return 0;
        }
    }

    public double getError() {
        return error;
    }

    /**
     * Method is divide phaseMax value into three values
     * of a b y with capacity nA nB nY bits
     * @param phiInt divided phaseMax value
     * @return array of {a, b, y}
     */
    int[] dividePhi(int phiInt) {
        int[] aby = new int[3];
        if (phiInt > 1 << (nPhiA + nPhiB + nPhiY)) {
            System.err.print("Out of index");
            return aby;
        }
        int temp;
        aby[2] = phiInt % maxPhiY;
        temp = phiInt >> nPhiY;
        aby[1] = temp % maxPhiB;
        temp = temp >> nPhiB;
        aby[0] = temp % maxPhiA;
        // System.out.println(phiInt + " = " + aby[0] + "  " + aby[1] + "  " + aby[2] + " " + multyPhi(aby));
        return aby;
    }

    /**
     * Method is multyplex array of {a, b, y}
     * into value phiInt
     * @param aby array of {a, b, y}
     * @return
     */
    int multyPhi(int[] aby) {
        return 1 << (nPhiB + nPhiY) * aby[0] + (1 << (nPhiY)) * aby[1] + aby[2];
    }

}
