## Notes for AppMesh demo

A simple QuarkusIO based microservice that I developed for testing AWS App Mesh. The microservice has 
3 endpoints
* /greeting/test - just like a ping, returns OK
* /greeting/hello - This calls another service (greeting/hello_impl)
* /greeting/hello_impl - returns a greeting based on a configured env variable

On App Mesh, we deploy the same application as 3 different services
1. gateway
2. greeting_a - the greeting/hello_impl of this returns A
3. greeting_b - the greeting/hello_impl of this returns B

Now, we define a virtual service called greeting which directs 50% of traffic to greeting_a and the rest
50% to greeting_b. 

The gateway service's /greeting/hello calls http://greeting.local:8080/greeting/hello_impl which is captured by
envoy which routes it to greeting_a or greeting_b based on the weights assigned


Some more details here - 

https://medium.com/@rkbalgi/my-experiment-with-aws-app-mesh-41600fe3189f
https://medium.com/@rkbalgi/app-mesh-routing-internals-c0344d3527da


## Notes for running aggregate cluster demo on envoy
1. The relevant microservices are present in package _src/main/kotlin
2. Build the dockerfile for service using src/docker/Dockerfile.jvm and run 6 instances of it, make note of each instance ip and include it in envoy.yaml (see below) 
3. Build the envoy using Dockerfile and envoy config file present in extras/envoy (may need to edit config file based on service instance ip's)


 
