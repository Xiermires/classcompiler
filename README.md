# classcompiler
An standalone, on demand class compiler.

Basically like every simple memory compiler out there, difference being the whole classpath is mapped to an in memory file system.

It comes with two front-ends:
<ul>
<li>Spring Boot
<li>ZeroMQ
</ul>

The Spring Boot front-end comes with a docker file to generate a deployable image in for instance minikube (see instructions)

The ZeroMQ is not 'dockerized'. 