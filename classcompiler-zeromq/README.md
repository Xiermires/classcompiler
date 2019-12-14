# classcompiler-zeromq
A facade for the compiler-core using zeromq sockets.
<p>
It consists of a server and a client.
<p>
The server,
```java 
	final ZeroMQClassCompilerServer server = new ZeroMQClassCompilerServer(2, 5554, 5555); // upload (5554) , compile (5555)
```
,provides a couple of sockets to:
<ul>
<li>upload 
<li>compile
</ul>

The client,
```java 
	final ZeroMQClassCompilerClient client = new ZeroMQClassCompilerClient("127.0.0.1", 5554, 5555); // upload (5554) , compile (5555)
```
, connects to the server and communicates the upload / compile requests through sockets.

## upload 

Upload files into the compiler-core in memory file system.
<p>
Supported file types:
<ul> 
<li>java class (.class)
</ul>

## compile

Upload a source file and compiles it using all previously uploaded classes as class path.
