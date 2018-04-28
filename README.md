# ChatMate
The legacy chat filtering Minecraft modification, written 1 1/2 years ago.

The project has long fallen out of maintenance, so no new code will be added to this repository. if there are missing files that I've misplaced, I'll track them down and upload them upon request. Feature suggestions and bug reports that do not specify a missing file will not be resolved or considered.

# Dependencies
The project compiles under Java 7 and up, and depends on [cglib](https://github.com/cglib/cglib) for version-independent code injection (current build.gradle is written to accept cglib-nodep-3.2.4).

# Building
Merge the hosted files with a Forge MDK (preferrably using a 1.8.x version), and ensure the following directory structure:
```
.
+-- libs
|   \-- cglib-*.jar
+-- src
|   \-- < mod code >
+-- build.gradle
\-- < other Forge MDK files >
```
