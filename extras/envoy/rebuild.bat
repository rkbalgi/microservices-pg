docker rm -f le
docker build -f Dockerfile_envoy -t local_envoy .
docker run -d --name le -p 8001:8001 -p 8002:8002 local_envoy

