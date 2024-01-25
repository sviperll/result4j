/*
 * #%L
 * %%
 * Copyright (C) 2024 Victor Nazarov <asviraspossible@gmail.com>
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

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A result that is either a successful execution with the value or a failure with the error-value.
 * <p>
 * {@code Result}-type is created for interoperability between normal Java-code that
 * throws exception and more functional code.
 * You do not need {@code Result}-type most of the time in Java-code, where
 * you can directly throw exceptions.
 * But there are situations, where more functional-style is used.
 * In such situations pure-functions are expected that throw no exceptions.
 * Handling exception in such situations can be cumbersome and require a lot of boilerplate code.
 * {@code Result}-type and associated helper-classes (like {@link Catcher}) help with
 * exception handling and allow to write idiomatic functional code that can interact with
 * methods that throw exceptions.
 * <p>
 * Result can be either success or failure, but not both at the same time.
 * Enclosed successful value or error value can be of different types,
 * represented by generic type-parameters.
 * <p>
 * The {@link Result#success(Object)} and {@link Result#error(Object)} methods
 * create a new Result-value, that is respectively either a successful result or an error.
 *
 * <pre>{@code
 *     Result<String, E> suc = Result.success("Hello, World!");
 * }</pre>
 * <p>
 * The above line declares successful result value.
 *
 * <pre>{@code
 *     Result<String, Integer> err = Result.error(404);
 * }</pre>
 * <p>
 * The above line declares error-value.
 *
 * <p>
 * The {@link Result.Success} and {@link Result.Error} records are
 * subtypes of the {@code Result}-type and allow to use pattern matching to distinguish between
 * a successful result and an error
 *
 * <pre>{@code
 *     Result<String, Integer> result = ...;
 *     switch (result) {
 *         case Result.Success<String, Integer>(String value) ->
 *                 System.out.println(value);
 *         case Result.Error<String, Integer>(Integer code) ->
 *                 throw new IOException("%s: error".formatted(code));
 *     }
 * }</pre>
 * <p>
 * Pattern matching can be used to check unknown result value as shown above.
 *
 * <pre>{@code
 *     Result<String, Integer> receivedResult = ...;
 *     String value =
 *             receivedResult.throwError(
 *                     code -> new IOException("%s: error".formatted(code))
 *             );
 *     System.out.println(value);
 * }</pre>
 * <p>
 * Instead of a low-level pattern-matching,
 * higher level helper-methods are available in {@code Result}-class.
 * In the snippet above {@link Result#throwError(Function)} is used to throw exception when
 * {@code Result} contains error.
 *
 * <pre>{@code
 *     String concatenation =
 *             Stream.of("a.txt", "b.txt", "c.txt")
 *                     .map(name -> loadFile(name))
 *                     .collect(ResultCollectors.toSingleResult(Collectors.join()))
 *                     .throwError(Function.identity());
 * }</pre>
 * <p>
 * In the above example we expect that the {@code loadFile} method returns the {@code Result}-type
 * instead of throwing an exception.
 * This allows us to use more functional-style, by using this method in the lambda-expression that
 * is not expected to throw any exceptions.
 * Handling exception in such situations can be cumbersome and require a lot of boilerplate code.
 * <p>
 * {@link ResultCollectors} class provides helper-methods to
 * combine multiple {@code Result}s into a single one.
 * {@link Catcher} class allows to adapt exception-throwing methods to
 * return {@code Result}-type instead.
 *
 * @param <R> type of successful result value
 * @param <E> type representing error
 * @see Catcher
 * @see ResultCollectors
 */
public sealed interface Result<R, E> {
    /**
     * Produces function-object that transforms error-values of results.
     * <p>
     * Similarly to {@link Result#mapError(Function)},
     * this method allows to apply a transformation to a error-value, associated with a result,
     * to get a new result.
     * In contrast to {@link Result#mapError(Function)},
     * instead of applying the transformation to some particular result,
     * this method produces a {@link Function} that
     * can be later applied to some future not yet known result or results.
     * <p>
     * This method can be used to make multi-level transformation easier to read.
     *
     * <pre>{@code
     *     List<Result<R, E>> results = ...;
     *     results.stream()
     *         .map(result -> result.mapError(e -> transform(e)))
     *         ...
     * }</pre>
     * <p>
     * Instead of the above code, one can write
     *
     * <pre>{@code
     *     List<Result<R, E>> results = ...;
     *     results.stream()
     *         .map(Result.errorMapping(e -> transform(e)))
     *         ...
     * }</pre>
     *
     * @param transformation transformation to be applied to error-values
     * @param <R> type of successful result value
     * @param <E1> type representing error-value associated with input
     * @param <E2> type representing error-value associated with output
     * @see Result#mapError(Function)
     */
    static <R, E1, E2> Function<Result<R, E1>, Result<R, E2>> errorMapping(
            Function<? super E1, ? extends E2> transformation
    ) {
        return result1 -> result1.mapError(transformation);
    }

    /**
     * Produces function-object that transforms values of successful results.
     * <p>
     * Similarly to {@link Result#map(Function)},
     * this method allows to apply a transformation to a value, associated with a successful result,
     * to get a new result.
     * In contrast to {@link Result#map(Function)},
     * instead of applying the transformation to some particular result,
     * this method produces a {@link Function} that
     * can be later applied to some future not yet known result or results.
     * <p>
     * This method can be used to make multi-level transformation easier to read.
     *
     * <pre>{@code
     *     List<Result<R, E>> results = ...;
     *     results.stream()
     *         .map(result -> result.map(value -> transform(value)))
     *         ...
     * }</pre>
     * <p>
     * Instead of the above code, one can write
     *
     * <pre>{@code
     *     List<Result<R, E>> results = ...;
     *     results.stream()
     *         .map(Result.mapping(e -> transform(e)))
     *         ...
     * }</pre>
     *
     * @param transformation transformation to be applied to a value of a successful result
     * @param <T> type of successful input result value
     * @param <R> type of successful output result value
     * @param <E> type representing error-value
     * @see Result#map(Function)
     */
    static <T, R, E> Function<Result<T, E>, Result<R, E>> mapping(
            Function<? super T, ? extends R> transformation
    ) {
        return result1 -> result1.map(transformation);
    }

    /**
     * Produces function-object that transforms values of successful results.
     * <p>
     * Similarly to {@link Result#flatMap(Function)},
     * this method allows to apply a transformation to a value, associated with a successful result,
     * to get a new result.
     * Transformation itself is partial and can produce either a successful result or an error.
     * In contrast to {@link Result#flatMap(Function)},
     * instead of applying the transformation to some particular result,
     * this method produces a {@link Function} that
     * can be later applied to some future not yet known result or results.
     * <p>
     * This method can be used to make multi-level transformation easier to read.
     *
     * <pre>{@code
     *     List<Result<R, E>> results = ...;
     *     results.stream()
     *         .map(result -> result.flatMap(value -> actOn(value)))
     *         ...
     * }</pre>
     * <p>
     * Instead of the above code, one can write
     *
     * <pre>{@code
     *     List<Result<R, E>> results = ...;
     *     results.stream()
     *         .map(Result.flatMapping(e -> actOn(e)))
     *         ...
     * }</pre>
     *
     * @param transformation transformation to be applied to a value of a successful result
     * @param <T> type of successful input result value
     * @param <R> type of successful output result value
     * @param <E> type representing error-value
     * @see Result#mapError(Function)
     */
    static <T, R, E> Function<Result<T, E>, Result<R, E>> flatMapping(
            Function<? super T, ? extends Result<R, E>> transformation
    ) {
        return result1 -> result1.flatMap(transformation);
    }

    /**
     * Produces {@code Result}-value containing given successful result value.
     *
     * @param <R> type of successful result value
     * @param <E> type representing error-value
     * @param result successful result value
     * @return {@code Result}-value containing given successful result value
     */
    static <R, E> Result<R, E> success(R result) {
        return new Success<>(result);
    }

    /**
     * Produces {@code Result}-value containing given error value.
     *
     * @param <R> type of successful result value
     * @param <E> type representing error-value
     * @param error error value
     * @return {@code Result}-value containing given error value
     */
    static <R, E> Result<R, E> error(E error) {
        return new Error<>(error);
    }

    /**
     * Produces {@code Result}-value with {@code Optional} successful result value.
     * <ul>
     *   <li>When input argument in non-{@link Optional#empty() empty} and
     *   contains a successful result,
     *   method result is also a successful result that
     *   contains a non-{@link Optional#empty() empty} value,
     *   the same as in input successful result.
     *
     *   <li>When input argument in non-{@link Optional#empty() empty} and
     *   contains an error result,
     *   method result is an error result with the same error value.
     *
     *   <li>When input argument in {@link Optional#empty() empty},
     *   method result is a successful result with {@link Optional#empty()} value.
     * </ul>
     *
     * @param <R> type of successful result value
     * @param <E> type representing error-value
     * @param value {@code Optional}-value containing {@code Result}-value
     * @return
     */
    static <R, E> Result<Optional<R>, E> fromOptionalResult(
            Optional<Result<R, E>> value
    ) {
        return value.map(Result.mapping(Optional::of))
                .orElseGet(() -> Result.success(Optional.empty()));
    }

    /**
     * Produces {@code Result}-value from {@code Optional} value.
     * <ul>
     *   <li>When input argument in non-{@link Optional#empty() empty},
     *   method result is a successful result that
     *   contains in an {@code Optional}-value.
     *
     *   <li>When input argument in {@link Optional#empty() empty},
     *   method result is an error result with the error value,
     *   provided as an argument to this method.
     * </ul>
     *
     * @param <R> type of successful result value
     * @param <E> type representing error-value
     * @param optional {@code Optional}-value
     * @param error error value
     * @return
     */
    static <R, E> Result<R, E> fromOptional(Optional<R> optional, E error) {
        Optional<Result<R, E>> optionalResult = optional.map(Result::success);
        return optionalResult.orElse(Result.error(error));
    }

    /** Checks that this value is an error. */
    boolean isError();

    /**
     * Combines this result with another one.
     * <ul>
     *   <li>When this result is an error, then
     *   the result of this method is an error, with the same error-value,
     *   irrespective of the provided argument.
     *   <li>When this result is a success, then
     *   the result of this method is exactly the same as provided argument.
     * </ul>
     *
     * @param <U> type of a successful result of a successor
     * @param result successor result
     */
    <U> Result<U, E> andThen(Result<U, E> result);

    /**
     * Transforms a value of this result, when this is a successful result.
     * <p>
     * This method allows to apply a transformation to a value,
     * associated with a successful result, to get a new result.
     * <ul>
     *   <li>When this result is an error, then
     *   the result of this method is an error, with the same error-value.
     *   <li>When this result is a success, then
     *   the result of this method a new result with transformed value.
     * </ul>
     *
     * @param <U> new type of a successful result value
     * @param transformation transformation to be applied to values
     */
    <U> Result<U, E> map(Function<? super R, ? extends U> transformation);

    /**
     * Transforms an error-value of this result, when this is an error.
     * <p>
     * this method allows to apply a transformation to a error-value,
     * associated with a result, to get a new result.
     * <ul>
     *   <li>When this result is an error, then
     *   the result of this method is an error, with the transformed error-value.
     *   <li>When this result is successful, then
     *   the result of this method the same successful result with the same value.
     * </ul>
     *
     * @param <E1> new type of an error-value
     * @param transformation transformation to be applied to error-values
     */
    <E1> Result<R, E1> mapError(Function<? super E, ? extends E1> transformation);

    /**
     * Produces optional-value, that is present when this is a successful result.
     */
    Optional<R> discardError();

    /**
     * Recovers from error and produces the value of the successful result.
     * <p>
     * Returns the value of this result when this is a successful result, or otherwise
     * produces the value by transforming error-value.
     *
     * @param transformation transformation that
     *     is applied to error-value to get a successful result value
     */
    R recoverError(Function<? super E, ? extends R> transformation);

    /**
     * Throws an exception, by converting error-value to an exception instance.
     * <p>
     * Returns the value of this result when this is a successful result, or otherwise
     * throws an exception, by creating exception instance from error-value.
     *
     * @param <X> type of thrown exception
     * @param errorToExceptionConvertion function to convert error-value to an exception
     * @return the value of this result, when it is a successful result
     * @throws X when this is an error result
     */
    default <X extends Exception> R throwError(Function<? super E, X> errorToExceptionConvertion)
            throws X {
        return switch (this) {
            case Success<R, E> success -> success.result;
            case Error<R, E> error -> throw errorToExceptionConvertion.apply(error.error);
        };
    }

    /** Invokes given consumer for a successful result value. */
    void ifSuccess(Consumer<R> consumer);

    /**
     * Transforms a value of this result, when this is a successful result.
     * <p>
     * This method allows to apply a transformation to a value,
     * associated with a successful result, to get a new result.
     * Transformation itself is partial and can produce either a successful result or an error.
     * <ul>
     *   <li>When this result is an error, then
     *   the result of this method is an error, with the same error-value.
     *   <li>When this result is a success, then
     *   the result of this method the result of applying transformation to the argument.
     * </ul>
     *
     * @param <U> new type of a successful result value
     * @param transformation transformation to be applied to values
     */
    default <U> Result<U, E> flatMap(
            Function<? super R, ? extends Result<U, E>> transformation
    ) {
        Result<? extends Result<U, E>, E> inflated = this.map(transformation);
        return switch (inflated) {
            case Success<? extends Result<U, E>, E>(var result) -> result;
            case Error<? extends Result<U, E>, E> error -> error.safeCast();
        };
    }

    record Success<R, E>(R result) implements Result<R, E> {
        @Override
        public <U> Result<U, E> map(
                Function<? super R, ? extends U> transformation
        ) {
            return Result.success(transformation.apply(result));
        }
        @Override
        public <E1> Result<R, E1> mapError(
                Function<? super E, ? extends E1> transfiormation
        ) {
            return safeCast();
        }

        @SuppressWarnings("unchecked")
        private <E1> Success<R, E1> safeCast() {
            return (Success<R, E1>) this;
        }

        @Override
        public <U> Result<U, E> andThen(Result<U, E> result) {
            return result;
        }

        @Override
        public Optional<R> discardError() {
            return Optional.of(result);
        }

        @Override
        public boolean isError() {
            return false;
        }

        @Override
        public R recoverError(
                Function<? super E, ? extends R> transformation
        ) {
            return result;
        }

        @Override
        public void ifSuccess(Consumer<R> consumer) {
            consumer.accept(result);
        }
    }

    record Error<R, E>(E error) implements Result<R, E> {
        @Override
        public <U> Result<U, E> map(
                Function<? super R, ? extends U> transformation
        ) {
            return safeCast();
        }

        @Override
        public <E1> Result<R, E1> mapError(
                Function<? super E, ? extends E1> transformation
        ) {
            return Result.error(transformation.apply(error));
        }

        @SuppressWarnings("unchecked")
        private <R1> Error<R1, E> safeCast() {
            return (Error<R1, E>) this;
        }

        @Override
        public <U> Result<U, E> andThen(Result<U, E> result) {
            return safeCast();
        }

        @Override
        public Optional<R> discardError() {
            return Optional.empty();
        }

        @Override
        public boolean isError() {
            return true;
        }

        @Override
        public R recoverError(
                Function<? super E, ? extends R> transformation
        ) {
            return transformation.apply(error);
        }

        @Override
        public void ifSuccess(Consumer<R> consumer) {
        }
    }
}
