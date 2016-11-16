# rxjava-reactor
This repository is just for the guys from Reactor 3 and rxJava test a possible bug.

1. Start the OLG project. It will start on port 8081
2. Start the Aggregation project. It will start on port 8080

In the aggregator service I already add reactor 3 and rxJava.

I was able to reproduce the problem just running apache benchmark

For RxJava just run:
ab -c 5 -n 150 localhost:8080/wishlist

For Reactor junt run:
ab -c 5 -n 150 localhost:8080/wishlistReactor

You will see that when you run the reactor endpoint some JSON deserialization will fail due to the response going to another call. 
