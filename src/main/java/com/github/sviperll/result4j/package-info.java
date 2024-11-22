/*
 * #%L
 * %%
 * Copyright (C) 2024 The result4j Contributors (https://github.com/sviperll/result4j)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * The package provides {@link Result}-type similar to Result-type in Rust that
 * allows to return either successful result or otherwise some kind of error.
 * <p>
 * In Java, the native way of reporting errors are exceptions, either checked or unchecked.
 * You do not need {@link Result}-type most of the time in Java-code, where
 * you can directly throw exceptions.
 * But there are situations, where more functional-style is used.
 * In such situations pure-functions are expected that throw no exceptions.
 * Handling exception in such situations can be cumbersome and require a lot of boilerplate code.
 * Result-type and associated helper-classes (like {@link Catcher}) help with exception handling and
 * allow to write idiomatic functional code that can interact with methods that throw exceptions.
 * <p>
 * {@link Result}-type provides a way to pass error information as a first-class value through
 * the code written in functional style.
 * Routines are provided for interoperability of normal code that uses exception and
 * functional code that uses Result-type, so that exceptions can be caught and propagated as
 * errors in {@link Result}-type and then rethrown again later in the control-flow.
 *
 * {@snippet lang="java" :
 *     Catcher.ForFunctions<IOException> io =
 *         Catcher.of(IOException.class).forFunctions();
 *     String concatenation =
 *             Stream.of("a.txt", "b.txt", "c.txt")
 *                     .map(io.catching(name -> loadResource(name)))
 *                     .collect(ResultCollectors.toSingleResult(Collectors.join()))
 *                     .throwError(Function.identity());
 * }
 * <p>
 * Above code uses {@link Catcher} class to adapt functions that
 * throw exceptions to return Result-type instead.
 * {@link ResultCollectors} class contains helper-methods to
 * collect multiple {@link Result}s into a single one.
 * You do not need {@link Catcher} class in normal Java-code, where
 * you can directly throw exceptions.
 * But the above snippet can serve as an example of situations, where more functional-style is used.
 * In such situations pure-functions are expected that throw no exceptions.
 * Handling exception in such situations can be cumbersome and require a lot of boilerplate code.
 * <p>
 * There is also an {@link AdaptingCatcher} class that allows to adapt or wrap exceptions.
 *
 * {@snippet lang="java" :
 *     AdaptingCatcher.ForFunctions<IOException, PipelineException> io =
 *             Catcher.of(IOException.class).map(PipelineException::new).forFunctions();
 *     AdaptingCatcher.ForFunctions<MLException, PipelineException> ml =
 *             Catcher.of(MLException.class).map(PipelineException::new).forFunctions();
 *     List<Animal> animals1 =
 *             List.of("cat.jpg", "dog.jpg")
 *                     .stream()
 *                     .map(io.catching(Fakes::readFile))
 *                     .map(Result.flatMapping(ml.catching(Fakes::recognizeImage)))
 *                     .collect(ResultCollectors.toSingleResult(Collectors.toList()))
 *                     .throwError(Function.identity());
 *     Assertions.assertEquals(List.of(Animal.CAT, Animal.DOG), animals1);
 * }
 */
package com.github.sviperll.result4j;
