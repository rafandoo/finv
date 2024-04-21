package br.dev.rplus.finv.stats;

import br.dev.rplus.finv.Stock;

public interface StatisticsCalculator {
    double calculate(Stock stock);
}
