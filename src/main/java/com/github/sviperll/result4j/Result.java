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

public sealed interface Result<R, E> {
    static <R, E1, E2> Function<Result<R, E1>, Result<R, E2>> errorMapping(
            Function<? super E1, ? extends E2> conversion
    ) {
        return result1 -> result1.mapError(conversion);
    }

    static <T, R, E> Function<Result<T, E>, Result<R, E>> mapping(
            Function<? super T, ? extends R> conversion
    ) {
        return result1 -> result1.map(conversion);
    }

    static <T, R, E> Function<Result<T, E>, Result<R, E>> flatMapping(
            Function<? super T, ? extends Result<R, E>> transformation
    ) {
        return result1 -> result1.flatMap(transformation);
    }

    static <R, E> Result<R, E> success(R result) {
        return new Success<>(result);
    }

    static <R, E> Result<R, E> error(E error) {
        return new Error<>(error);
    }

    static <R, E> Result<Optional<R>, E> fromOptionalResult(
            Optional<Result<R, E>> value
    ) {
        return value.map(Result.mapping(Optional::of))
                .orElseGet(() -> Result.success(Optional.empty()));
    }

    static <R, E> Result<R, E> fromOptional(Optional<R> optional, E error) {
        Optional<Result<R, E>> optionalResult = optional.map(Result::success);
        return optionalResult.orElse(Result.error(error));
    }

    boolean isError();
    <U> Result<U, E> andThen(Result<U, E> result);
    <U> Result<U, E> map(Function<? super R, ? extends U> transformation);
    <E1> Result<R, E1> mapError(Function<? super E, ? extends E1> transformation);
    Optional<R> discardError();
    R recoverError(Function<? super E, ? extends R> transformation);
    default <X extends Exception> R throwError(Function<? super E, X> errorToExceptionConvertion)
            throws X {
        return switch (this) {
            case Success<R, E> success -> success.result;
            case Error<R, E> error -> throw errorToExceptionConvertion.apply(error.error);
        };
    }

    void ifSuccess(Consumer<R> consumer);

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
