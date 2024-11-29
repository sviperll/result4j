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

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Allows to adapt exception-throwing methods to return {@link Result}-type instead.
 * <p>
 * Instead of dealing with methods that can throw exceptions, these methods
 * can be adapted to be function-objects that return {@link Result}-values.
 * {@link Catcher} class serves for a basic use case where
 * you get exception-instance as an error-value of the result.
 * {@code AdaptingCatcher} usage is similar to that of the {@link Catcher} class, but
 * {@code AdaptingCatcher} allows to adapt caught exceptions, so that
 * Result error values are not literal exception-instances, but
 * some values that are obtained by transforming these caught exceptions.
 *
 * {@snippet lang="java" :
 *     // @highlight region="highlightint" substring="Integer"
 *     // @highlight substring="AdaptingCatcher" :
 *     AdaptingCatcher<HTTPException, Integer> http =
 *             // @link substring="map" target="Catcher#map(Function)" :
 *             Catcher.of(HTTPException.class).map(HTTPException::errorCode);
 *     Supplier<Result<JSONObject, Integer>> supplier =
 *             http.forSuppliers().catching(() -> loadJson());
 *     Result<JSONObject, Integer> result = supplier.get();
 *     // @end region="highlightint"
 * }
 * <p>
 * In the example above exception-throwing method {@code loadJson}
 * is adapted to be a {@link Supplier}-object, that
 * supplies a result, that can be either a {@code JSONObject} in case of the success, or
 * an {@link Integer} HTTP-code in case of an error.
 * HTTP-error code is extracted from {@code HTTPException}
 * using the {@code HTTPException::errorCode} method as
 * specified in the call to {@link Catcher#map(Function)}.
 * <p>
 * Another reason to adapt caught exceptions is to wrap different low-level exceptions into
 * a single common high-level exception type.
 *
 * {@snippet lang="java" :
 *     // @highlight region substring="IOException" type="highlighted"
 *     // @highlight regex="\bio\b" type="highlighted" :
 *     AdaptingCatcher.ForFunctions<IOException, PipelineException> io =
 *             Catcher.of(IOException.class).map(PipelineException::new).forFunctions();
 *     // @end
 *     // @highlight region substring="MLException" type="highlighted"
 *     // @highlight substring="ml" type="highlighted" :
 *     AdaptingCatcher.ForFunctions<MLException, PipelineException> ml =
 *             Catcher.of(MLException.class).map(PipelineException::new).forFunctions();
 *     // @end
 *     List<Animal> animals1 =
 *             List.of("cat.jpg", "dog.jpg")
 *                     .stream()
 *                     // @highlight substring="io" type="highlighted" :
 *                     .map(io.catching(Fakes::readFile))
 *                     // @highlight substring="ml" type="highlighted" :
 *                     .map(Result.flatMapping(ml.catching(Fakes::recognizeImage)))
 *                     .collect(ResultCollectors.toSingleResult(Collectors.toList()))
 *                     .orOnErrorThrow(Function.identity());
 * }
 * <p>
 * In the example above two methods are used {@code readFile} and {@code recognizeImage}.
 * These two methods throw different exceptions:
 * {@code IOException} and {@code MLException} respectively.
 * In this example we adapt these methods with two different {@code AdaptingCatcher}s,
 * one is {@code io}, and another is {@code ml}.
 * These {@code AdaptingCatcher}s are configured to handle corresponding exceptions types,
 * but adapted functions
 * do not return {@code IOException} or {@code MLException} as their error-values.
 * Instead adapted functions in both cases
 * return single high-level {@code PipelineException} as an error-value.
 * <p>
 * {@code AdaptingCatcher} class has methods that
 * specialize it to deal with different varieties of methods
 * (number of arguments or being {@code void}).
 * These varieties are referenced by correspondence to a functional-interface from
 * {@link java.util.function} package.
 *
 * <table>
 *   <caption>Specialized AdaptingCatcher classes</caption>
 *   <tr>
 *     <th>Functional interface</th>
 *     <th>AdaptingCatcher method</th>
 *     <th>Specialized subclass</th>
 *   </tr>
 *   <tr>
 *     <td>{@link java.util.function.BiConsumer}</td>
 *     <td>{@link AdaptingCatcher#forBiConsumers()}</td>
 *     <td>{@link AdaptingCatcher.ForBiConsumers}</td>
 *   <tr>
 *   <tr>
 *     <td>{@link java.util.function.BiFunction}</td>
 *     <td>{@link AdaptingCatcher#forBiFunctions()}</td>
 *     <td>{@link AdaptingCatcher.ForBiFunctions}</td>
 *   <tr>
 *   <tr>
 *     <td>{@link java.util.function.Consumer}</td>
 *     <td>{@link AdaptingCatcher#forConsumers()}</td>
 *     <td>{@link AdaptingCatcher.ForConsumers}</td>
 *   <tr>
 *   <tr>
 *     <td>{@link java.util.function.Function}</td>
 *     <td>{@link AdaptingCatcher#forFunctions()}</td>
 *     <td>{@link AdaptingCatcher.ForFunctions}</td>
 *   <tr>
 *   <tr>
 *     <td>{@link Runnable}</td>
 *     <td>{@link AdaptingCatcher#forRunnables()}</td>
 *     <td>{@link AdaptingCatcher.ForRunnables}</td>
 *   <tr>
 *   <tr>
 *     <td>{@link java.util.function.Supplier}</td>
 *     <td>{@link AdaptingCatcher#forSuppliers()}</td>
 *     <td>{@link AdaptingCatcher.ForSuppliers}</td>
 *   <tr>
 * </table>
 *
 * @param <S> source exception type
 * @param <D> error result type
 * @see Catcher
 * @see Result
 */
public class AdaptingCatcher<S extends Exception, D> {
    private final Class<S> exceptionClass;
    private final Function<? super S, ? extends D> conversion;

    AdaptingCatcher(Class<S> exceptionClass, Function<? super S, ? extends D> conversion) {
        this.exceptionClass = exceptionClass;
        this.conversion = conversion;
    }

    /**
     * @param <E> new error result type
     */
    public <E> AdaptingCatcher<S, E> map(
            Function<? super D, ? extends E> conversion
    ) {
        return new AdaptingCatcher<>(exceptionClass, this.conversion.andThen(conversion));
    }

    /**
     * Specializes {@link AdaptingCatcher} to deal with methods that
     * correspond to a shape of {@link java.util.function.Function} functional interface.
     *
     * @see AdaptingCatcher.ForFunctions
     */
    public ForFunctions<S, D> forFunctions() {
        return new ForFunctions<>(this);
    }

    /**
     * Specializes {@link AdaptingCatcher} to deal with methods that
     * correspond to a shape of {@link java.util.function.BiFunction} functional interface.
     *
     * @see AdaptingCatcher.ForBiFunctions
     */
    public ForBiFunctions<S, D> forBiFunctions() {
        return new ForBiFunctions<>(this);
    }

    /**
     * Specializes {@link AdaptingCatcher} to deal with methods that
     * correspond to a shape of {@link java.util.function.Supplier} functional interface.
     *
     * @see AdaptingCatcher.ForSuppliers
     */
    public ForSuppliers<S, D> forSuppliers() {
        return new ForSuppliers<>(this);
    }

    /**
     * Specializes {@link AdaptingCatcher} to deal with methods that
     * correspond to a shape of {@link Runnable} functional interface.
     *
     * @see AdaptingCatcher.ForRunnables
     */
    public ForRunnables<S, D> forRunnables() {
        return new ForRunnables<>(this);
    }

    /**
     * Specializes {@link AdaptingCatcher} to deal with methods that
     * correspond to a shape of {@link java.util.function.Consumer} functional interface.
     *
     * @see AdaptingCatcher.ForConsumers
     */
    public ForConsumers<S, D> forConsumers() {
        return new ForConsumers<>(this);
    }

    /**
     * Specializes {@link AdaptingCatcher} to deal with methods that
     * correspond to a shape of {@link java.util.function.BiConsumer} functional interface.
     *
     * @see AdaptingCatcher.ForBiConsumers
     */
    public ForBiConsumers<S, D> forBiConsumers() {
        return new ForBiConsumers<>(this);
    }

    private D adapt(Exception exception) {
        if (exceptionClass.isInstance(exception)) {
            return conversion.apply(exceptionClass.cast(exception));
        } else if (exception instanceof RuntimeException runtimeException) {
            throw runtimeException;
        } else {
            throw new UnexpectedCheckedExceptionException(exception);
        }
    }

    /**
     * Specialized version of {@link AdaptingCatcher} that deals with methods that
     * correspond to a shape of {@link java.util.function.Function} functional interface.
     *
     * @param <S> source exception type
     * @param <D> error result type
     * @see ForFunctions#catching(ExceptionfulFunction)
     */
    public static class ForFunctions<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForFunctions(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        /**
         * @param <E> new error result type
         */
        public <E> ForFunctions<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forFunctions();
        }

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
         *             Catcher.ForFunctions<IOException> catcher = ...
         *             Function<String, Result<String, IOException>> f =
         *                 catcher.catching(MyMain::loadResult);
         *             Result<String, IOException> result = f.apply("my-resource");
         *         }
         *     }
         * }</pre>
         * <p>
         * In the example above, instead of having {@code loadResource} method that
         * can throw {@code IOException},
         * we convert it to be a {@link Function} object that returns {@code IOException} as
         * part of its result.
         *
         * @param <T> argument type of functions
         * @param <R> the type of a successful result
         * @param function function to adapt
         */
        public <T, R> Function<T, Result<R, D>> catching(
                ExceptionfulFunction<? super T, ? extends R, ? extends S> function
        ) {
            return (argument) -> {
                try {
                    return Result.success(function.apply(argument));
                } catch (Exception e) {
                    return Result.error(adapter.adapt(e));
                }
            };
        }

    }

    /**
     * Specialized version of {@link AdaptingCatcher} that deals with methods that
     * correspond to a shape of {@link java.util.function.BiFunction} functional interface.
     *
     * @param <S> source exception type
     * @param <D> error result type
     * @see ForBiFunctions#catching(ExceptionfulBiFunction)
     */
    public static class ForBiFunctions<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForBiFunctions(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        /**
         * @param <E> new error result type
         */
        public <E> ForBiFunctions<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forBiFunctions();
        }

        /**
         * Allows to adapt exception-throwing methods to return {@link Result}-type instead.
         * <p>
         * Instead of dealing with methods that can throw exceptions, these methods
         * can be adapted to be function-objects that return exception as part of its result.
         *
         * <pre>{@code
         *     class MyMain {
         *         String loadResource(String name, Language language) throws IOException {
         *             // ...
         *         }
         *
         *         void main(String[] args) throws IOException {
         *             Catcher.ForBiFunctions<IOException> catcher = ...
         *             BiFunction<String, Language, Result<String, IOException>> f =
         *                 catcher.catching(MyMain::loadResult);
         *             Result<String, IOException> result = f.apply("my-resource", Language.EN);
         *         }
         *     }
         * }</pre>
         * <p>
         * In the example above, instead of having {@code loadResource} method that
         * can throw {@code IOException},
         * we convert it to be a {@link BiFunction} object that returns {@code IOException} as
         * part of its result.
         *
         * @param <T> first argument type of functions
         * @param <U> second argument type of functions
         * @param <R> the type of a successful result
         * @param function function to adapt
         */
        public <T, U, R> BiFunction<T, U, Result<R, D>> catching(
                ExceptionfulBiFunction<? super T, ? super U, ? extends R, ? extends S> function
        ) {
            return (argument1, argument2) -> {
                try {
                    return Result.success(function.apply(argument1, argument2));
                } catch (Exception e) {
                    return Result.error(adapter.adapt(e));
                }
            };
        }
    }

    /**
     * Specialized version of {@link AdaptingCatcher} that deals with methods that
     * correspond to a shape of {@link java.util.function.Supplier} functional interface.
     *
     * @param <S> source exception type
     * @param <D> error result type
     * @see ForSuppliers#catching(ExceptionfulSupplier)
     */
    public static class ForSuppliers<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForSuppliers(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        /**
         * @param <E> new error result type
         */
        public <E> ForSuppliers<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forSuppliers();
        }


        /**
         * Allows to adapt exception-throwing methods to return {@link Result}-type instead.
         * <p>
         * Instead of dealing with methods that can throw exceptions, these methods
         * can be adapted to be function-objects that return exception as part of its result.
         *
         * <pre>{@code
         *     class MyMain {
         *         String loadResource() throws IOException {
         *             // ...
         *         }
         *
         *         void main(String[] args) throws IOException {
         *             Catcher.ForSuppliers<IOException> catcher = ...
         *             Supplier<Result<String, IOException>> f =
         *                 catcher.catching(MyMain::loadResult);
         *             Result<String, IOException> result = f.get();
         *         }
         *     }
         * }</pre>
         * <p>
         * In the example above, instead of having {@code loadResource} method that
         * can throw {@code IOException},
         * we convert it to be a {@link Supplier} object that returns {@code IOException} as
         * part of its result.
         *
         * @param <T> type of supplied value
         * @param supplier supplier to adapt
         */
        public <T> Supplier<Result<T, D>> catching(
                ExceptionfulSupplier<? extends T, ? extends S> supplier
        ) {
            return () -> {
                try {
                    return Result.success(supplier.get());
                } catch (Exception e) {
                    return Result.error(adapter.adapt(e));
                }
            };
        }

    }

    /**
     * Specialized version of {@link AdaptingCatcher} that deals with methods that
     * correspond to a shape of {@link java.util.function.Consumer} functional interface.
     *
     * @param <S> source exception type
     * @param <D> error result type
     * @see ForConsumers#catching(ExceptionfulConsumer)
     */
    public static class ForConsumers<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForConsumers(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        /**
         * @param <E> new error result type
         */
        public <E> ForConsumers<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forConsumers();
        }

        /**
         * Allows to adapt exception-throwing methods to return {@link Result}-type instead.
         * <p>
         * Instead of dealing with methods that can throw exceptions, these methods
         * can be adapted to be function-objects that return exception as part of its result.
         *
         * <pre>{@code
         *     class MyMain {
         *         void saveData(String data) throws IOException {
         *             // ...
         *         }
         *
         *         void main(String[] args) throws IOException {
         *             Catcher.ForConsumers<IOException> catcher = ...
         *             Function<String, Result<Void, IOException>> f =
         *                 catcher.catching(MyMain::saveData);
         *             Result<Void, IOException> result = f.apply("data");
         *         }
         *     }
         * }</pre>
         * <p>
         * In the example above, instead of having {@code saveData} method that
         * can throw {@code IOException},
         * we convert it to be a {@link Function} object that returns {@code IOException} as
         * part of its result.
         * Note that even though, the original method corresponds to
         * the {@link java.util.function.Consumer} functional interface,
         * we need its adapted version to be a {@link Function}, because
         * exception is now part of the result.
         *
         * @param <T> the type of consumed values
         * @param consumer consumer to adapt
         */
        public <T> Function<T, Result<Void, D>> catching(
                ExceptionfulConsumer<? super T, ? extends S> consumer
        ) {
            return (argument) -> {
                try {
                    consumer.accept(argument);
                    return Result.success(null);
                } catch (Exception e) {
                    return Result.error(adapter.adapt(e));
                }
            };
        }
    }

    /**
     * Specialized version of {@link AdaptingCatcher} that deals with methods that
     * correspond to a shape of {@link java.util.function.BiConsumer} functional interface.
     *
     * @param <S> source exception type
     * @param <D> error result type
     * @see ForBiConsumers#catching(ExceptionfulBiConsumer)
     */
    public static class ForBiConsumers<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForBiConsumers(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        /**
         * @param <E> new error result type
         */
        public <E> ForBiConsumers<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forBiConsumers();
        }

        /**
         * Allows to adapt exception-throwing methods to return {@link Result}-type instead.
         * <p>
         * Instead of dealing with methods that can throw exceptions, these methods
         * can be adapted to be function-objects that return exception as part of its result.
         *
         * <pre>{@code
         *     class MyMain {
         *         void saveData(Path path, String data) throws IOException {
         *             // ...
         *         }
         *
         *         void main(String[] args) throws IOException {
         *             Catcher.ForBiConsumers<IOException> catcher = ...
         *             BiFunction<Path, String, Result<Void, IOException>> f =
         *                 catcher.catching(MyMain::saveData);
         *             Result<Void, IOException> result = f.apply(Path.of("x.txt"), "data");
         *         }
         *     }
         * }</pre>
         * <p>
         * In the example above, instead of having {@code saveData} method that
         * can throw {@code IOException},
         * we convert it to be a {@link Function} object that returns {@code IOException} as
         * part of its result.
         * Note that even though, the original method corresponds to
         * the {@link java.util.function.Consumer} functional interface,
         * we need its adapted version to be a {@link Function}, because
         * exception is now part of the result.
         *
         * @param <T> the type of first value from pair of consumed values
         * @param <U> the type of second value from pair of consumed values
         * @param consumer consumer to adapt
         */
        public <T, U> BiFunction<T, U, Result<Void, D>> catching(
                ExceptionfulBiConsumer<? super T, ? super U, ? extends S> consumer
        ) {
            return (argument1, argument2) -> {
                try {
                    consumer.accept(argument1, argument2);
                    return Result.success(null);
                } catch (Exception e) {
                    return Result.error(adapter.adapt(e));
                }
            };
        }
    }

    /**
     * Specialized version of {@link AdaptingCatcher} that deals with methods that
     * correspond to a shape of {@link Runnable} functional interface.
     *
     * @param <S> source exception type
     * @param <D> error result type
     * @see ForRunnables#catching(ExceptionfulRunnable)
     */
    public static class ForRunnables<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForRunnables(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        /**
         * @param <E> new error result type
         */
        public <E> ForRunnables<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forRunnables();
        }


        /**
         * Allows to adapt exception-throwing methods to return {@link Result}-type instead.
         * <p>
         * Instead of dealing with methods that can throw exceptions, these methods
         * can be adapted to be function-objects that return exception as part of its result.
         *
         * <pre>{@code
         *     class MyMain {
         *         void updateData() throws IOException {
         *             // ...
         *         }
         *
         *         void main(String[] args) throws IOException {
         *             Catcher.ForRunnables<IOException> catcher = ...
         *             Supplier<Result<Void, IOException>> f =
         *                 catcher.catching(MyMain::updateData);
         *             Result<Void, IOException> result = f.get();
         *         }
         *     }
         * }</pre>
         * <p>
         * In the example above, instead of having {@code saveData} method that
         * can throw {@code IOException},
         * we convert it to be a {@link Function} object that returns {@code IOException} as
         * part of its result.
         * Note that even though, the original method corresponds to
         * the {@link java.util.function.Consumer} functional interface,
         * we need its adapted version to be a {@link Function}, because
         * exception is now part of the result.
         *
         * @param runnable runnable to adapt
         */
        public Supplier<Result<Void, D>> catching(
                ExceptionfulRunnable<? extends S> runnable
        ) {
            return () -> {
                try {
                    runnable.run();
                    return Result.success(null);
                } catch (Exception e) {
                    return Result.error(adapter.adapt(e));
                }
            };
        }
    }

    @SuppressWarnings(value = "serial")
    static class UnexpectedCheckedExceptionException extends IllegalStateException {

        UnexpectedCheckedExceptionException(Throwable e) {
            super("Unexpected checked exception", e);
        }
    }
}
