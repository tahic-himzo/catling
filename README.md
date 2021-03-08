# catling
Simple Load Testing using fs2


A minimal layer on fs2 that allows you to create quick & extensible load tests for web APIs without relying on full-fledged solutions like gatling.

Currently supports data sourcing from SQL, CSV & in-memory.
It allows you to create custom parallel evaluators that take a batch of responses and derives aggregate metrics and emits them as a fs2 stream. 
A Latency & Status Code Evaluator are already included.

Check out the sample folder for example usage.
