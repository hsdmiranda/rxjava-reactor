# rxjava-reactor
This repository is just for the guys from Reactor 3 and rxJava test a possible bug.

1. Start the OLG project. It will start on port 8081
2. Execute the Aggregation project. It will fire a lot of requests to the OLG project. 

In the aggregator service I already add reactor 3 and rxJava.

You will see that when you run the reactor endpoint some JSON deserialization will fail due to the response going to another call. 
