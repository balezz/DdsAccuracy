package ru.metrologexpert;

import static java.lang.Math.*;

/**
 * Abstract class to modeling direct digital synthesis.
 *
 */
abstract class DDS {
    DDS(){}
    DDS(int nP, int nA, int F0, int FX) {
        nPhase = nP;
        phaseMax = 1 << nP;
        ampMax = 1 << nA;
        Fo = F0;
        Fx = FX;
        dPhi = ((double)FX)/F0;                 // приращение фазы синтезируемого сигнала
                                                // на каждом периоде частоты стандарта
        this.initTable();                       // Инициализация таблицы LUT #subMethod()
        this.evalU();                           // Вычисление отсчетов +templateMethod()
        this.evalError();                       // Вычисление погрешности +templateMethod()
    }

    int Fo;                                     // Значение частоты стандарта
    int Fx;                                     // Значение синтезируемой частоты
    int nPhase;                                 // разрядность фазового аккумулятора
    int phaseMax;                               // максимальное значение фазового аккумулятора
    int ampMax;                                 // максимальное значение амплитуды синтезируемого сигнала
    int phiInt = 0;                             // Текущее значение фазы синтезируемого сигнала,
                                                // округленное до ближайшего целого, диап (0; phaseMax)
    double phi = 0;                             // Точное текущее значение фазы, диап (0; 2pi)
    double error = 0;                           // Вычисленное значение погрешности формирования сигнала
    double dPhi;                                // приращение фазы на каждом периоде стандарта частоты
    int[] table;                                // таблица LUT
    int[] U;                                    // Значение отсчетов синтезируемого сигнала,
                                                // диапазон (0; ampMax)
    double[] Uref;


    /**
     * главный цикл моделирования сигнала U(t) с частотой Fo
     * на промежутке времени (0; 1) с
     *
     */
    void evalU() {
        U = new int[Fo];                        // Массив значений синтезированного сигнала в точках времени 1/F0
        Uref = new double[Fo];                  // Массив точных значений синусоиды диап (0; ampMax)
        for (int i = 0; i < Fo; i++) {
            phiInt = (int)(phi * phaseMax) % phaseMax;
            U[i] = getLUT(phiInt);
            Uref[i] = ampMax * sin(2*PI*phi);
            phi += dPhi;
        }
    }

    /**
     * Evaluate standard error,
     * error interval in [0; 1]
     */
    void evalError() {
        for (int i = 0; i < Fo; i++) {
            error += pow(Uref[i] - U[i], 2);
        }
        error = sqrt(error/Fo) / ampMax;
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




