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

<hr></hr>
## Notes for running aggregate cluster demo on Envoyproxy
__if you have any issues/questions please open an issue on this github project, I'll be happy to look at it__

Envoyproxy version - 
<pre>
/ # /usr/local/bin/envoy --version

/usr/local/bin/envoy  version: b67c14052c49890a7e3afe614d50979c346c024b/1.13.1/Clean/RELEASE/BoringSSL
</pre>

1. The microservices used are present in package _src/main/kotlin_ and envoy files in _extras/envoy_
2. Build the dockerfile for service using src/docker/Dockerfile.jvm and run 6 instances of it, make note of each instance ip and include it in envoy.yaml (see below) 
3. Build the envoy using Dockerfile and envoy config file present in extras/envoy (may need to edit config file based on service instance ip's)


### Handy Commands
#### Building and running envoy
<pre>
docker build -f Dockerfile_envoy -t local_envoy .
docker run -d -p 8001:8001 -p 8002:8002 local_envoy
</pre>

I'm running 6 instances of the service - ports exposed 1130-1180. The service exposes below URL's -
- The path /kotlin/test prints a Hello there! message (see below)
- The path /kotlin/set_health?value=true|false turns individual ono/off health check 

##### Example
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

#### Cluster set up and Tests
I have 3 instances in each cluster
In kotlin_cluster_1, first two endpoints have priority 0 and the third one has priority 1
In the second cluster, no priority is specified so all instances have priority 0

Now, lets run the following tests 
1. With all instances healthy - all requests will go to the first two instances of the first cluster (primary cluster with instances having priority 0)
2. Set one of the p0 instance unhealthy - Now you should see request shared between remaining p0 instance and the p1 instance in cluster 1

You can try more tests as describe here - https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/upstream/aggregate_cluster


### Enable SSL on Envoyproxy
Generate a self-signed certificate (see below section) and enable transport_socket on the listener in envoy.yaml
<pre>
C:\Users\rkbal\IdeaProjects\appmesh-microsrvc\extras\envoy (master -> origin)
λ curl -k  https://192.168.99.100:8002/kotlin/test
Hello there!!. The server is running @  feda26a2c650/172.17.0.4: 8080 and the time is 2020-04-05T13:19:51.596696
</pre>

#### Generating SSL self signed certificate
<pre>
OpenSSL> genrsa -out rsa_key.key
Generating RSA private key, 2048 bit long modulus (2 primes)
....................+++++
....................................................................................................................................................................+++++
e is 65537 (0x010001)
OpenSSL> help

OpenSSL> req -new -key rsa_key.key -out cert.csr
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:IN
State or Province Name (full name) [Some-State]:Karnataka
Locality Name (eg, city) []:Bengaluru
Organization Name (eg, company) [Internet Widgits Pty Ltd]:daalitoi inc
Organizational Unit Name (eg, section) []:dev
Common Name (e.g. server FQDN or YOUR name) []:raghav
Email Address []:rkbalgi@gmail.com

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:

OpenSSL> x509 -req -in cert.csr -days 5000 -out cert.pem
We need a private key to sign with
error in x509
OpenSSL> x509 -req -in cert.csr -signkey rsa_key.key -days 5000 -out cert.pem
Signature ok
subject=C = IN, ST = Karnataka, L = Bengaluru, O = daalitoi inc, OU = dev, CN = raghav, emailAddress = rkbalgi@gmail.com
Getting Private key
OpenSSL> 

</pre>

It looks like you can create a new key with a single command like this :-)
<pre>
openssl req -nodes -x509 -newkey rsa:4096 -keyout example-com.key -out example-com.crt -days 365
</pre>