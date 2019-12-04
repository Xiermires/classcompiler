# classcompiler
An standalone, on demand class compiler.

Basically like every simple memory compiler out there, difference being the classpath is also in memory.

It comes with some spring-boot frontend to allow uploading classpath files to a tomcat server and writes all compiling results into a in memory file system using jimfs.

TO-DO: Evaluating the idea of maintaining some remote, multi-version and very slow class loading mechanism. 

