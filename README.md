Result-type for Java
====================

The project provides Result-type similar to Result-type in Rust that
allows to return either successful result or otherwise some kind of error.

In Java, the native way of reporting errors are exceptions, either checked or unchecked.
You do not need Result-type most of the time in Java-code, where
you can directly throw exceptions.
But there are situations, where more functional-style is used.
In such situations pure-functions are expected that throw no exceptions.
Handling exception in such situations can be cumbersome and require a lot of boilerplate code.
Result-type and associated helper-classes help with exception handling and
allow to write idiomatic functional code that can interact with methods that throw exceptions.

Result-type provides a way to pass error information as a first-class value through
the code written in functional style.
Routines are provided for interoperability of normal code that uses exception and
functional code that uses Result-type, so that exceptions can be caught and propagated as
errors in Result-type and then rethrown again later in the control-flow.

Requirements
------------

Java 21 is required.

Getting Started
---------------

result4j
[is available in Maven Central](https://central.sonatype.com/artifact/com.github.sviperll/result4j).

![Maven Central Version](https://img.shields.io/maven-central/v/com.github.sviperll/result4j)

````
        <dependency>
            <groupId>com.github.sviperll</groupId>
            <artifactId>result4j</artifactId>
            <version>$LATEST_VERSION<!-- see above --></version>
        </dependency>
````

Project Values and Goals
------------------------

 * Small well-defined library that requires minimal maintainance
 * Work with the broad range of mainstream Java code (not tailored to some niche flavor of Java)
 * No bloat
 * Zero or minimal dependencies
 * Work with Java-modules (JPMS) and modern Java

Overview
--------

[API Documentation is available for reference](https://www.javadoc.io/doc/com.github.sviperll/result4j).

Result type can be either a successful result or some kind of error.

````java
Result<String, E> suc = Result.success("Hello, World!");
````

The above line declares successful result value.

````java
Result<String, Integer> err = Result.error(404);
````

The above line declares error-value.

````java
Result<String, Integer> result = ...;
switch (result) {
    case Result.Success<String, Integer>(String value) -> System.out.println(value);
    case Result.Error<String, Integer>(Integer code) ->
            throw new IOException("%s: error".formatted(code));
}
````

Pattern matching can be used to check unknown result value as shown above.

````java
Result<String, Integer> receivedResult = ...;
String value = receivedResult.orOnErrorThrow(code -> new IOException("%s: error".formatted(code)));
System.out.println(value);
````

Instead of a low-level pattern-matching,
higher level helper-methods are available in `Result`-class.
In the snippet above `orOnErrorThrow` is used to throw exception when `Result` contains error.

Result-type is created for interoperability between normal Java-code that throws exception and
more functional code.

````java
Catcher.ForFunctions<IOException> io =
    Catcher.of(IOException.class).forFunctions();
String concatenation =
        Stream.of("a.txt", "b.txt", "c.txt")
                .map(io.catching(name -> loadResource(name)))
                .collect(ResultCollectors.toSingleResult(Collectors.join()))
                .orOnErrorThrow(Function.identity());
````

Above code uses `Catcher` class to adapt functions that
throw exceptions to return Result-type instead.
`ResultCollectors` class contains helper-methods to collect multiple `Result`s into a single one.
You do not need `Catcher` class in normal Java-code, where you can directly throw exceptions.
But the above snippet can serve as an example of situations, where more functional-style is used.
In such situations pure-functions are expected that throw no exceptions.
Handling exception in such situations can be cumbersome and require a lot of boilerplate code.

````java
class MyMain {
    String loadResource(String name) throws IOException {
        // ...
    }

    void main(String[] args) throws IOException {
        Catcher.ForFunctions<IOException> io =
            Catcher.of(IOException.class).forFunctions();
        Function<String, Result<String, IOException>> f = io.catching(MyMain::loadResult);
        Result<String, IOException> result = f.apply("my-resource");
        String value = result.orOnErrorThrow(Function.identity());
        System.out.println(value);
    }
}
````

The example above shows the usage of the `catching` method of the `Catcher` class, that
allows to adapt exception throwing method and instead to have a method that returns `Result` with
exception representing as an error-value.

There is also an `AdaptingCatcher` class that allows to adapt or wrap exceptions.

````java
AdaptingCatcher.ForFunctions<IOException, PipelineException> io =
        Catcher.of(IOException.class).map(PipelineException::new).forFunctions();
AdaptingCatcher.ForFunctions<MLException, PipelineException> ml =
        Catcher.of(MLException.class).map(PipelineException::new).forFunctions();
List<Animal> animals1 =
        List.of("cat.jpg", "dog.jpg")
                .stream()
                .map(io.catching(Fakes::readFile))
                .map(Result.flatMapping(ml.catching(Fakes::recognizeImage)))
                .collect(ResultCollectors.toSingleResult(Collectors.toList()))
                .orOnErrorThrow(Function.identity());
Assertions.assertEquals(List.of(Animal.CAT, Animal.DOG), animals1);
````
