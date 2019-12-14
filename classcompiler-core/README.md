# classcompiler-core
An on-demand compiler based using EclipseCompiler and Jimfs as the supporting on memory file system.
<p>
It provides the following features:
<ul>
<li>upload java class to the classpath
<li>upload java source (if required at any compile unit it will be compiled itself) 
<li>upload and compile a java source
</ul>

## NIOClassLoader

The provided NIOClassLoader allows resolving the previously compiled files and resolve them into classes.
<p>
Use the compiler and the class loader to support a possible dynamic runtime if desired.
  