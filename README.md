# rebalancing-service

## Design decisions
* git: single branch strategy as I am working without collaborators
* csv import: use dependency `org.apache.commons:commons-csv` to parse csv

## Assumptions
* csv files come in correct format and no attributes are missing

## Limits/Potential refactorings
* Extract common logic in `CustomerImporter` and `StrategyImporter`?
