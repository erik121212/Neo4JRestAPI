# Neo4JRestAPI
Rest API built in Scala to retrieve information from of a Neo4j graph database

# How to get started?
- Download and install Neo4J from here:
 [http://neo4j.com/download-thanks/?edition=community&flavour=winstall64&release=2.3.2&_ga=1.137363126.72303192.1454961161#winstall](http://neo4j.com/download-thanks/?edition=community&flavour=winstall64&release=2.3.2&_ga=1.137363126.72303192.1454961161#winstall "Download Neo4J")
 
- Launch the Neo4J browser using the following url: [http://localhost:7474/browser/ ](http://localhost:7474/browser/  "Neo4J Browser")
- Copy-paste the content from this file into the Neo4J Browser and run it to load an initial small set of data:  *src\main\tools\cypher\Cypherqueries*
- Run the Main class of this project, once the project is running you can use the following url's:

> [http://localhost:9000/customers/674444](http://localhost:9000/customers/674444 "Retrieve data of customer 674444")
> [http://localhost:9000/customers/674444/accounts](http://localhost:9000/customers/674444/accounts "Retrieve the accounts of customer 674444")
> [http://localhost:9000/company/123456](http://localhost:9000/company/123456 "Retrieve the data of company 123456")
> [http://localhost:9000/company/123456/accounts](http://localhost:9000/company/123456/accounts "Retrieve the accounts of company 123456")
> [http://localhost:9000/address/1234AB2a](http://localhost:9000/address/1234AB2a "Retrieve the address details at 1234AB2a")
> [http://localhost:9000/address/1234AB2a/customers](http://localhost:9000/address/1234AB2a/customers "Retrieve the customers at address 1234AB2a")
> [http://localhost:9000/address/1234AB2a/customers/accounts](http://localhost:9000/address/1234AB2a/customers/accounts "Retrieve the customers accounts at address 1234AB2a")








