# rebalancing-service

## Design decisions
* git: single branch strategy as I am working without collaborators
* git: conventional commits
* csv import: use dependency `org.apache.commons:commons-csv` to parse csv
* http client: use reactive http client `WebClient`
* resiliency: fail fast on exceptions during csv import
* resiliency: log errors during http requests to fps and continue processing
* resiliency: only do http requests to execute trades if trade contains changes

## Assumptions
* csv files come in correct format and no attributes are missing
* customer portfolio contains the value (e.g. in USD) of stocks, bonds and cash, each trade
must be a zero sum game change. This assumption is based on the fact, that there are no prices for bonds and stocks. 
However the example in the challenge does not suggest that it should be zero sum games.. 

## Limits/Potential refactorings
* Extract common logic in `CustomerImporter` and `StrategyImporter`?

