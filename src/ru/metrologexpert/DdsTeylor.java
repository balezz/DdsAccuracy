package ru.metrologexpert;

public class DdsTeylor extends DDS {

    int nAlpha;
    int nTheta;
    int thetaMax;
    int alphaMax;
    int[] tabSin;                   // таблица LUT
    int[] tabK1Cos;                 // таблица LUT
    int[][] tabK2Sin;                // таблица LUT

    DdsTeylor(int nP, int nA, int F0, int FX) {
        nAlpha = 8;
        nTheta = nP - nAlpha;
        alphaMax = twoPow(nAlpha);
        thetaMax = twoPow(nTheta);
        phaseMax = twoPow(nP);
        ampMax = twoPow(nA);
        Fo = F0;
        Fx = FX;
        dPhi = ((double) FX) / F0;                 // приращение фазы синтезируемого сигнала
                                                    // на каждом периоде частоты стандарта
        this.initTable();                       // Инициализация таблицы LUT #subMethod()
        this.evalU();                           // Вычисление отсчетов +templateMethod()
        this.evalError();                       // Вычисление погрешности +templateMethod()
    }

    @Override
    void initTable() {
        tabSin = new int[alphaMax];
        for (int alpa = 0; alpa < alphaMax; alpa++) {
            tabSin[alpa] = (int) ( ampMax * Math.sin((2 * pi * (alpa<<nTheta) / phaseMax)));
        }

        tabK1Cos = new int[alphaMax];
        for (int alpha = 0; alpha < alphaMax; alpha++) {
            tabK1Cos[alpha] = (int) ( ampMax * 0.125 * pi * Math.cos((2 * pi * (alpha<<nTheta) / phaseMax)));
        }

        tabK2Sin = new int[alphaMax][thetaMax];
        for (int phi = 0; phi < nTheta; phi++) {
            for (int alpha = 0; alpha < nAlpha; alpha++) {
                double k2 = 0.5 * pi * pi * (phi - alpha) * (phi - alpha) / (Math.pow(phaseMax, 4)) ;
                tabK2Sin[alpha][phi] = (int) ( ampMax * k2
                        * Math.sin((2 * pi * (alpha<<(nTheta)) / phaseMax)));
            }
        }
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
        if((phiInt < phaseMax / 4) & (phiInt >= 0)) {
            int[] alphaTheta = dividePhi(phiInt);           // alpha = alphaTheta[0] - старшие разряды
                                                            // theta = alphaTheta[1] - младшие разряды
            return tabSin[alphaTheta[0]>>nTheta] + (int)(((double)alphaTheta[1] / phaseMax)
                    * tabK1Cos[alphaTheta[0]>>nTheta]) - tabK2Sin[alphaTheta[1]][alphaTheta[0]>>nTheta];
        } else {
            System.err.println("Index out of LUT");
            return 0;
        }
    }

    /**
     * Method is divide phaseMax value into three values
     * of a b y with capacity nA nB nY bits
     * @param phiInt divided phaseMax value
     * @return array of {a, b, y}
     */
    int[] dividePhi(int phiInt) {
        int[] alphaTheta = new int[2];
        if (phiInt > twoPow(nTheta + nAlpha)) {
            System.err.println("Out of index");
            return alphaTheta;
        }
        alphaTheta[0] = (phiInt >> nTheta)<<nTheta;
        alphaTheta[1] = phiInt - (alphaTheta[0]);

        System.out.println(phiInt + " = " + alphaTheta[0] + "  " + alphaTheta[1]);
        return alphaTheta;
    }

}


