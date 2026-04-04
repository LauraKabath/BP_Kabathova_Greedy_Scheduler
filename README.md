# Pažravý plánovač (Greedy Scheduler)

Tento repozitár obsahuje implementáciu desktopovej aplikácie vytvorenej v rámci bakalárskej práce, ktorá sa zaoberá využitím pažravých algoritmov pri riešení problémov plánovania úloh.

Aplikácia slúži ako experimentálny nástroj na demonštrovanie efektivity a praktického využitia pažravých algoritmov pri plánovaní úloh.

## Obsah repozitára

* `src/` – zdrojový kód aplikácie
* `evaluation/` – výsledky experimentov:

  * `dataset_generator/` – Python skript na generovanie datasetov úloh
  * `datasets/` – datasety (D1 - D6) použité pri experimentoch
  * `graphs/` – grafické vizualizácie metrík
  * `results/` – CSV súbory s výsledkami algoritmov
  * `schedules/` – vizualizované rozvrhy úloh

## Funkcionalita aplikácie

Aplikácia umožňuje:

* načítanie vstupných dát
* aplikovanie rôznych pažravých plánovacích stratégií
* porovnanie algoritmov na základe metrík (zisk, počet úloh, čas vykonania)
* vizualizáciu výsledkov (rozvrh, tabuľky, grafy)
* export výsledkov do CSV a PNG

## Spustenie aplikácie

Spustiteľné súbory (.exe, .jar) sa nachádzajú v sekcii **Releases** tohto repozitára.

