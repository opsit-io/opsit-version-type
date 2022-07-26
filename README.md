
io.opsit.version.Version
========================

## Introduction

*io.opsit.version.Version* is an immutable datatype that reporesents software version.

It supports the specifications of Semantic Versioning and can be used like a Number:
arithmetic operations and comparisons are supported.

## Usage example

```java
import io.opsit.version.Version;

var v1 = Version.parseVersion("1.2.3"); 

v1.isSemantic();            // => true

var v2 = Version.parseVersion("1.2.3-beta");

v2.isSemantic();            // => true

var isLater = v1.compareTo(v2); // => 1

var v3 = v1.add(Version.parseVersion("0.0.1-alpha")); // => 1.2.4-alpha

var v4 = Version.parseVersion("1.2.3.4"); // => 1.2.3.4

v4.isSemantic();            // => false

var v5 = Version.mkSemanticVersion(1,2,3, 
                                   new String[] {"alpha"."1"}, 
                                   new String[] {"build1"} )   // => 1.2.3-alpha.1+build1

```

## Legal

Copyright © 2022 Opsit.io

Distributed under the AGPL License
