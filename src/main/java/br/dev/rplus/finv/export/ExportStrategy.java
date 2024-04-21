package br.dev.rplus.finv.export;

import br.dev.rplus.finv.Stock;

public interface ExportStrategy {

    void export(Stock stock);
}
