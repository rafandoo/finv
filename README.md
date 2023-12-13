<h1 align="center">FInv 📈</h1>

> Status do projeto: ⏳ Em andamento.


## 📝 Descrição do projeto 

<p align="justify">
A biblioteca FInv (Finance Invest) é uma biblioteca de software que provê funções para análise financeira e investimentos no mercado de ações brasileiro, sendo uma biblioteca simples e poderosa que facilita o desenvolvimento de aplicações de investimento, fornecendo funções prontas para consultar, calcular e visualizar indicadores financeiros.
</p>

## 👨‍💻 Tecnologias

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
</p>


## ▶️ Exemplos de uso

Execute the command below to run the project in your shell.

```java
// Exemplo 1: Obtendo informações básicas de uma ação (stock)
Stock stock1 = Finv.get("INTC");
stock1.print();

// Exemplo 2: Obtendo informações específicas de uma ação com eventos de dividendos no período de 2020
Stock stock2 = Finv.get("AAPL", Event.DIVIDENDS, "2020-01-01", "2020-12-31");
stock2.print();
System.out.println("Dividend Yield: " + Finv.stats(stock2, Stats.DIVIDEND_YIELD));

// Exemplo 3: Obtendo informações detalhadas de uma ação com eventos variados, no período de 2020, com frequência mensal
List<Event> events = new ArrayList<>();
events.add(Event.DIVIDENDS);
events.add(Event.SPLIT);
events.add(Event.HISTORY);

Stock stock3 = Finv.get("AAPL", events, "2020-01-01", "2020-12-31", Frequency.MONTHLY);
stock3.print();
```

## 📄 Documentos 

[Javadoc](https://rafandoo.github.io/finv/)

[Diagrama de classe](https://github.com/rafandoo/finv/blob/366516693bf23ce551f3abbce7803e39b5a562c0/project/UML.png)

[Relatório Técnico](https://github.com/rafandoo/finv/blob/76e66c642d15681c78aa80333e0ce9cdf6d603f7/project/FINV_%20Biblioteca%20de%20Dados%20Financeiros%20de%20Bolsas%20de%20Valores%20.pdf)


## 📚 Bibliotecas de terceiros 

[org.json](https://www.json.org/json-en.html)

[FasterXML Jackson](https://github.com/FasterXML/jackson)

## 🔧 Funcionalidades 

✔️ Consulta Básica de Ações;

✔️ Consulta Específica com Eventos;

✔️ Consulta Específica com Eventos;

✔️ Cálculo de Estatísticas;

## 👀 Observações

Este projeto ainda está em desenvolvimento e pode receber atualizações para incluir mais funcionalidades e melhorias. Consulte a documentação Javadoc para informações mais detalhadas sobre cada classe e método.

## 🔑 Licença

The [MIT License](https://github.com/rafandoo/finv/blob/a34dd637b33f34bf343be35bec260dc4f08b073d/LICENSE) (MIT)

Copyright :copyright: 2023 - Rafael Camargo

