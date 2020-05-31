# rebalancing-service

## Start service
For simplicity `rebalancing-service` is run as a SpringBootApplication inside an embedded tomcat.
The application provides the following configuration parameters that can be set as environment variables or
changed in `application.yaml`:

|configuration parameter|environment variable|description|default|
|---|---|---|---|
| csvImport.customer | `PATH_TO_CUSTOMERS_CSV` | absolute path to `customers.csv` | /customers.csv|
| csvImport.strategy | `PATH_TO_STRATEGY_CSV` | absolute path to `strategy.csv` | /strategy.csv|
| fps.baseUrl | `FPS_BASE_URL` | base url of FPS | http://localhost:8081|
| fps.batchSize | `FPS_BATCH_SIZE` | maximum batch size of trades in requests to FPS endpoint `/execute` | 50|

After adjusting the configuration parameters e.g. by providing environment variables
```
export PATH_TO_CUSTOMERS_CSV=/tmp/customers.csv
export PATH_TO_STRATEGY_CSV=/tmp/strategy.csv
export FPS_BASE_URL=http://localhost:8082
```

the application can be started as follows:
```
./mvnw clean spring-boot:run
```
## API
`rebalancing-service` offers a minimalistic API allowing to trigger a rebalancing of all customer portfolios:
```
POST /rebalance
```
This endpoint returns a `202` and asynchronously executes the portfolio rebalancings, i.e. the following steps:
* read customers from `customers.csv`
* read strategies from `strategy.csv`
* assign a strategy to each customer
* calculate all trades and partition them into batches
* execute trades over FPS

If an error occurs during the execution then the program
* terminates if the error occurs during parsing of csv files
* continues if the error occurs during the interaction with the FPS

It is safe to reexecute this application if an error encounters.  For errors related to parsing, first the
data errors in csv files need to be fixed or the parser needs to be written more flexible. 

## Design decisions
* git: single branch strategy as I am working without collaborators
* git: conventional commits
* csv import: use dependency `org.apache.commons:commons-csv` to parse csv
* http client: use reactive http client `WebClient`
* resiliency: fail fast on exceptions during csv import
* resiliency: log errors during http requests to FPS and continue processing
* resiliency/performance: only do http requests to execute trades if trade contains changes
* provide trigger to rebalancing as an API endpoint

## Assumptions
* csv files come in correct format and no attributes are missing
* customer portfolio contains the value (e.g. in USD) of stocks, bonds and cash, each trade
must be a zero sum game change. This assumption is based on the fact, that there are no prices for bonds and stocks. 
However the example in the challenge does not suggest that it should be zero sum games.. 
* `customers.csv` conatains pairwise disjoint `customerId` values

## Limits/Potential refactorings
* extract common logic in `CustomerImporter` and `StrategyImporter`?
* implement retry if client requests fail within 5xx range
* decouple csv import and processing
* provide scheduled cron job to automate the rebalancing process to exactly once per day 
* persist customer portfolios of failed calls to FPS `/execute` and retry with a scheduled job
* publish outcome of processing to an event to some topic after the processing such that a client of `/rebalance` endpoint
can get information  
* offer API documentation over swagger API
* dockerize application

