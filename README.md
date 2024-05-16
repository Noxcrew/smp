# SMP
<img alt="GitHub License" src="https://img.shields.io/github/license/noxcrew/smp">
<img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/noxcrew/smp/build.yml">
<img alt="GitHub Release" src="https://img.shields.io/github/v/release/noxcrew/smp">

SMP (Simple Maths Parser) is a library that can solve relatively simple mathematical expressions in a textual format.
It is written in Kotlin, and is a Kotlin-first library.

## What is SMP?

### Values
Values can be provided as either an integer, double or a named variable.
Variables can consist of letters and underscores and are resolved either at or before computation using a suspending provider.
This allows for variables to be easily computed from costly sources.

### Operators
Support is provided for the following operators:
* addition (+),
* subtraction (-),
* multiplication (*),
* division (/), and
* exponentiation (^).

Additionally, parentheses can be used.

## What is SMP not?
SMP is not a general purpose expression solver.
It is intended for simple use cases that can be easily and simply represented with strings.

It is not a library intended for consumption in Java or other non-Kotlin JVM languages.

## Usage
### Dependency
SMP can be found on Noxcrew's public Maven repository and added to a Gradle project as follows:

```kotlin
repositories {
    maven {
        name = "noxcrew"
        url = uri("https://maven.noxcrew.com/public")
    }
}

dependencies {
    implementation("com.noxcrew.smp:smp:VERSION")
}
```

### Example
Some simple examples of how to use the library can be seen below.
For further examples, see the test files.

```kotlin
// Computes an expression, skipping the resolve steps.
SMP.computeUnresolved("10+(100^2)")

// You can create your own instances with variable resolution.
val myInstance = SMP.create(...)

// You can store the IR of an expression for later computation.
val expression = myInstance.parse("100/my_variable")
expression.compute()
```

## Documentation
Documentation for how to use the library can be found on the library's entrypoint, the `SMP` interface.
Javadocs/Dokka docs are also provided.
