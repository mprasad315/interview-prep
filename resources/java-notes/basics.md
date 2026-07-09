# Components of Java
## JVM
Internal engine for running java which comprises of:
- Class Loader Subsystem (loads and verifies classes)
- Runtime Data Area (includes memory management areas like heap, stack, registers, method area)
- Execution Engine (interpreter, JIT compiler, GC – used for converting bytecode into native machine code and executing it)
## JRE
JVM + libraries & files (software package that allows you to run java program)
- Does not contain a compiler, so it can only run on existing software
## JDK
JRE + developer tools (javac, javadoc, jdb, jar)
- Primary addition is javac (compiler) which converts .java source files into .class bytecode files
- Implementation of the Java platform editions (like Java SE) – includes the JVM, APIs, and development tools that comply with the platform's specs
