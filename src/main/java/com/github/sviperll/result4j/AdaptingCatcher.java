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

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param <S> source exception type
 * @param <D> destination exception type
 */
public class AdaptingCatcher<S extends Exception, D> {
    private final Class<S> exceptionClass;
    private final Function<? super S, ? extends D> conversion;

    AdaptingCatcher(Class<S> exceptionClass, Function<? super S, ? extends D> conversion) {
        this.exceptionClass = exceptionClass;
        this.conversion = conversion;
    }

    /**
     * @param <E> new destination exception type
     */
    public <E> AdaptingCatcher<S, E> map(
            Function<? super D, ? extends E> conversion
    ) {
        return new AdaptingCatcher<>(exceptionClass, this.conversion.andThen(conversion));
    }

    public ForFunctions<S, D> forFunctions() {
        return new ForFunctions<>(this);
    }

    public ForBiFunctions<S, D> forBiFunctions() {
        return new ForBiFunctions<>(this);
    }

    public ForSuppliers<S, D> forSuppliers() {
        return new ForSuppliers<>(this);
    }

    public ForRunnables<S, D> forRunnables() {
        return new ForRunnables<>(this);
    }

    public ForConsumers<S, D> forConsumers() {
        return new ForConsumers<>(this);
    }

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

    public static class ForFunctions<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForFunctions(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        public <E> ForFunctions<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forFunctions();
        }

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

    public static class ForBiFunctions<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForBiFunctions(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        public <E> ForBiFunctions<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forBiFunctions();
        }

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

    public static class ForSuppliers<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForSuppliers(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        public <E> ForSuppliers<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forSuppliers();
        }

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

    public static class ForConsumers<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForConsumers(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        public <E> ForConsumers<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forConsumers();
        }

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

    public static class ForBiConsumers<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForBiConsumers(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        public <E> ForBiConsumers<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forBiConsumers();
        }

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

    public static class ForRunnables<S extends Exception, D> {

        private final AdaptingCatcher<S, D> adapter;
        ForRunnables(AdaptingCatcher<S, D> adapter) {
            this.adapter = adapter;
        }

        public <E> ForRunnables<S, E> map(
                Function<? super D, ? extends E> conversion
        ) {
            AdaptingCatcher<S, E> mappedAdapter = adapter.map(conversion);
            return mappedAdapter.forRunnables();
        }

        public <T> Supplier<Result<Void, D>> catching(
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
