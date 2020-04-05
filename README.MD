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


### Handy Commands

docker build -f Dockerfile_envoy -t local_envoy .
docker run -d -p 8001:8001 -p 8002:8002 local_envoy

I'm running 6 instances of the service - ports exposed 1130-1180. The path /kotlin/set_health?value=true|false turns individual ono/off health checks on the service

### My services
<pre>
c:\Users\rkbal\IdeaProjects\appmesh-microsrvc\extras\envoy (master -> origin)
λ docker ps
CONTAINER ID        IMAGE                 COMMAND                  CREATED             STATUS              PORTS                                         NAMES
d62e30975197        local_envoy           "/docker-entrypoint.…"   About an hour ago   Up About an hour    0.0.0.0:8001-8002->8001-8002/tcp, 10000/tcp   trusting_buck
d2bc2efb96ca        rkbalgi/agg_cluster   "/deployments/run-ja…"   43 hours ago        Up 41 hours         8778/tcp, 9779/tcp, 0.0.0.0:1180->8080/tcp    c6
6e057a19feb3        rkbalgi/agg_cluster   "/deployments/run-ja…"   43 hours ago        Up 41 hours         8778/tcp, 9779/tcp, 0.0.0.0:1170->8080/tcp    c5
0cdfb08dc333        rkbalgi/agg_cluster   "/deployments/run-ja…"   43 hours ago        Up 41 hours         8778/tcp, 9779/tcp, 0.0.0.0:1160->8080/tcp    c4
feda26a2c650        rkbalgi/agg_cluster   "/deployments/run-ja…"   43 hours ago        Up 41 hours         8778/tcp, 9779/tcp, 0.0.0.0:1150->8080/tcp    c3
57cfc84144d7        rkbalgi/agg_cluster   "/deployments/run-ja…"   43 hours ago        Up 41 hours         8778/tcp, 9779/tcp, 0.0.0.0:1140->8080/tcp    c2
707dcb6dc1fe        rkbalgi/agg_cluster   "/deployments/run-ja…"   44 hours ago        Up 41 hours         8778/tcp, 9779/tcp, 0.0.0.0:1130->8080/tcp    c1

</pre>

Example of running a /kotlin/test
<pre>
λ curl http://192.168.99.100:8002/kotlin/test
Hello there!!. The server is running @  d2bc2efb96ca/172.17.0.7: 8080 and the time is 2020-04-03T18:28:21.364939
</pre>

 
