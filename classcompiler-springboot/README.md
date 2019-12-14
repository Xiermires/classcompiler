# classcompiler-springboot
A facade for the compiler-core on Spring Boot.
<p>
It provides a couple of rest end-points:
<ul>
<li>/upload 
<li>/compile
</ul>

## /upload 

Upload files into the compiler-core in memory file system.
<p>
Supported file types:
<ul> 
<li>java source (.java)
<li>java class (.class)
<li>java jar (.jar)
</ul>

## /compile

Upload a source file and compiles it using all previously uploaded classes as class path.

## Deploy in Kubernetes

<ol>
<li>docker build -t class-compiler:1.0-SNAPSHOT .
<li>kubectl create deployment class-compiler --image=class-compiler:1.0-SNAPSHOT
<li>kubectl expose deployment class-compiler --type=LoadBalancer --port=8080 --name=class-compiler-service
<li>minikube service class-compiler-service
</ol>
