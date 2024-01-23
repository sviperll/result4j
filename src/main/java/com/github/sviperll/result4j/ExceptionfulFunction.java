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

/**
 * Like {@link java.util.function.Function}, but throws checked exception.
 * <p>
 * An explicit implementation of this interface is discouraged.
 * It is mainly used to specify the input parameter for the
 * {@link Catcher.ForFunctions#catching(ExceptionfulFunction)} method.
 * It probably makes sense to introduce more specific extensions of this interface when
 * you need to frequently use the same exception type.
 * For instance, it probably makes sense to define {@code IOFunction} interface,
 * if you need to frequently use {@code java.io.IOException}.
 *
 * @param <T> the argument of the function
 * @param <R> the result of the function
 * @param <E> the exception thrown by the function
 * @see Catcher.ForFunctions#catching(ExceptionfulFunction)
 */
@FunctionalInterface
public interface ExceptionfulFunction<T, R, E extends Throwable> {
    R apply(T argument) throws E;

    default <S> ExceptionfulFunction<T, S, E> andThen(
            ExceptionfulFunction<? super R, ? extends S, ? extends E> after
    ) {
        return argument -> after.apply(apply(argument));
    }

    default <U> ExceptionfulFunction<U, R, E> compose(
            ExceptionfulFunction<? super U, ? extends T, ? extends E> before
    ) {
        return argument -> apply(before.apply(argument));
    }
}
