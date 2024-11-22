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

package com.github.sviperll.result4j;

import java.util.function.Function;

/**
 * Allows to adapt exception-throwing methods to return {@link Result}-type instead.
 * <p>
 * Instead of dealing with methods that can throw exceptions, these methods
 * can be adapted to be function-objects that return exception as part of its result.
 *
 * <pre>{@code
 *     class MyMain {
 *         String loadResource(String name) throws IOException {
 *             // ...
 *         }
 *
 *         void main(String[] args) throws IOException {
 *             Function<String, Result<String, IOException>> f =
 *                 Catcher.of(IOException.class).forFunctions().catching(MyMain::loadResource);
 *             Result<String, IOException> result = f.apply("my-resource");
 *         }
 *     }
 * }</pre>
 * <p>
 * In the example above, instead of having {@code loadResource} method that
 * can throw {@code IOException},
 * we convert it to be a {@link Function} object that returns {@code IOException} as
 * part of its result.
 * <p>
 * The {@link Catcher#of(Class)} method creates a new {@code Catcher}-object that deals with the
 * exceptions of given type.
 * {@code Catcher} class has methods that
 * specialize it to deal with different varieties of methods
 * (number of arguments or being {@code void}).
 * These varieties are referenced by correspondence to a functional-interface from
 * {@link java.util.function} package.
 *
 * <table>
 *   <caption>Specialized Catcher classes</caption>
 *   <tr><th>Functional interface</th><th>Catcher method</th><th>Specialized subclass</th></tr>
 *   <tr>
 *     <td>{@link java.util.function.BiConsumer}</td>
 *     <td>{@link Catcher#forBiConsumers()}</td>
 *     <td>{@link Catcher.ForBiConsumers}</td>
 *   <tr>
 *   <tr>
 *     <td>{@link java.util.function.BiFunction}</td>
 *     <td>{@link Catcher#forBiFunctions()}</td>
 *     <td>{@link Catcher.ForBiFunctions}</td>
 *   <tr>
 *   <tr>
 *     <td>{@link java.util.function.Consumer}</td>
 *     <td>{@link Catcher#forConsumers()}</td>
 *     <td>{@link Catcher.ForConsumers}</td>
 *   <tr>
 *   <tr>
 *     <td>{@link java.util.function.Function}</td>
 *     <td>{@link Catcher#forFunctions()}</td>
 *     <td>{@link Catcher.ForFunctions}</td>
 *   <tr>
 *   <tr>
 *     <td>{@link Runnable}</td>
 *     <td>{@link Catcher#forRunnables()}</td>
 *     <td>{@link Catcher.ForRunnables}</td>
 *   <tr>
 *   <tr>
 *     <td>{@link java.util.function.Supplier}</td>
 *     <td>{@link Catcher#forSuppliers()}</td>
 *     <td>{@link Catcher.ForSuppliers}</td>
 *   <tr>
 * </table>
 *
 * <p>
 * Often you do not need exception-objects to represent error-result and instead
 * may want to convert exceptions to some other error-values.
 * In other situations, you may be not interested in original exceptions and
 * may want to wrap original low-level exceptions into a higher-level exception class.
 * {@link AdaptingCatcher} serves for these purposes and
 * can be created with {@link Catcher#map(Function)} class.
 *
 * @param <E> exception type
 * @see Result
 * @see Catcher.ForBiConsumers
 * @see Catcher.ForBiFunctions
 * @see Catcher.ForConsumers
 * @see Catcher.ForFunctions
 * @see Catcher.ForRunnables
 * @see Catcher.ForSuppliers
 * @see AdaptingCatcher
 */
public class Catcher<E extends Exception> extends AdaptingCatcher<E, E> {
    public static <E extends Exception> Catcher<E> of(Class<E> exceptionClass) {
        return new Catcher<>(exceptionClass);
    }

    private Catcher(Class<E> exceptionClass) {
        super(exceptionClass, Function.identity());
    }

    /**
     * Specializes {@link Catcher} to deal with methods that
     * correspond to a shape of {@link java.util.function.Function} functional interface.
     *
     * @see Catcher.ForFunctions
     */
    @Override
    public ForFunctions<E> forFunctions() {
        return new ForFunctions<>(this);
    }

    /**
     * Specializes {@link Catcher} to deal with methods that
     * correspond to a shape of {@link java.util.function.BiFunction} functional interface.
     *
     * @see Catcher.ForBiFunctions
     */
    @Override
    public ForBiFunctions<E> forBiFunctions() {
        return new ForBiFunctions<>(this);
    }

    /**
     * Specializes {@link Catcher} to deal with methods that
     * correspond to a shape of {@link java.util.function.Supplier} functional interface.
     *
     * @see Catcher.ForSuppliers
     */
    @Override
    public ForSuppliers<E> forSuppliers() {
        return new ForSuppliers<>(this);
    }

    /**
     * Specializes {@link Catcher} to deal with methods that
     * correspond to a shape of {@link Runnable} functional interface.
     *
     * @see Catcher.ForRunnables
     */
    @Override
    public ForRunnables<E> forRunnables() {
        return new ForRunnables<>(this);
    }

    /**
     * Specializes {@link Catcher} to deal with methods that
     * correspond to a shape of {@link java.util.function.Consumer} functional interface.
     *
     * @see Catcher.ForConsumers
     */
    @Override
    public ForConsumers<E> forConsumers() {
        return new ForConsumers<>(this);
    }

    /**
     * Specializes {@link Catcher} to deal with methods that
     * correspond to a shape of {@link java.util.function.BiConsumer} functional interface.
     *
     * @see Catcher.ForBiConsumers
     */
    @Override
    public ForBiConsumers<E> forBiConsumers() {
        return new ForBiConsumers<>(this);
    }

    /**
     * Specialized version of {@link Catcher} that deals with methods that
     * correspond to a shape of {@link java.util.function.Function} functional interface.
     *
     * @param <E> exception type
     * @see ForFunctions#catching(ExceptionfulFunction)
     */
    public static class ForFunctions<E extends Exception>
            extends AdaptingCatcher.ForFunctions<E, E> {
        private ForFunctions(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }

    /**
     * Specialized version of {@link Catcher} that deals with methods that
     * correspond to a shape of {@link java.util.function.BiFunction} functional interface.
     *
     * @param <E> exception type
     * @see ForBiFunctions#catching(ExceptionfulBiFunction)
     */
    public static class ForBiFunctions<E extends Exception>
            extends AdaptingCatcher.ForBiFunctions<E, E> {
        private ForBiFunctions(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }

    /**
     * Specialized version of {@link Catcher} that deals with methods that
     * correspond to a shape of {@link java.util.function.Supplier} functional interface.
     *
     * @param <E> exception type
     * @see ForSuppliers#catching(ExceptionfulSupplier)
     */
    public static class ForSuppliers<E extends Exception>
            extends AdaptingCatcher.ForSuppliers<E, E> {
        private ForSuppliers(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }

    /**
     * Specialized version of {@link Catcher} that deals with methods that
     * correspond to a shape of {@link java.util.function.Consumer} functional interface.
     *
     * @param <E> exception type
     * @see ForConsumers#catching(ExceptionfulConsumer)
     */
    public static class ForConsumers<E extends Exception>
            extends AdaptingCatcher.ForConsumers<E, E> {
        private ForConsumers(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }

    /**
     * Specialized version of {@link Catcher} that deals with methods that
     * correspond to a shape of {@link java.util.function.BiConsumer} functional interface.
     *
     * @param <E> exception type
     * @see ForBiConsumers#catching(ExceptionfulBiConsumer)
     */
    public static class ForBiConsumers<E extends Exception>
            extends AdaptingCatcher.ForBiConsumers<E, E> {
        private ForBiConsumers(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }

    /**
     * Specialized version of {@link Catcher} that deals with methods that
     * correspond to a shape of {@link Runnable} functional interface.
     *
     * @param <E> exception type
     * @see ForRunnables#catching(ExceptionfulRunnable)
     */
    public static class ForRunnables<E extends Exception>
            extends AdaptingCatcher.ForRunnables<E, E> {
        private ForRunnables(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }

    }
}
