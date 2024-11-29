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

package com.github.sviperll.result4jassertj;

import com.github.sviperll.result4j.Result;
import java.util.Objects;
import java.util.function.Consumer;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

/**
 * AssertJ {@link org.assertj.core.api.Assert assertions} that can be applied to a {@link Result}.
 *
 * @param <R> type of successful result value.
 * @param <E> type representing error.
 *
 * @see Result
 * @see <a href=https://assertj.github.io/doc/">AssertJ</a>
 * @since 1.2.0
 */
public class ResultAssert<R, E> extends AbstractAssert<ResultAssert<R, E>, Result<R, E>> {

    /**
     * Create assertion for {@link Result}.
     *
     * @param actual the actual value.
     * @param <R>    type of successful result value.
     * @param <E>    type representing error.
     * @return the created assertion object.
     */
    public static <R, E> ResultAssert<R, E> assertThat(Result<R, E> actual) {
        return new ResultAssert<>(actual);
    }

    /**
     * Create a new {@code ResultAssert} instance.
     *
     * @param actual the actual value.
     */
    private ResultAssert(Result<R, E> actual) {
        super(actual, ResultAssert.class);
    }

    /**
     * Verifies that the {@link Result} represents Error.
     * <p>
     * Example: {@snippet lang="java" : assertThat(result).isError(); }
     *
     * @return {@code this} assertion object.
     * @throws AssertionError if the {@code Result} represents Success.
     */
    public ResultAssert<R, E> isError() {
        isNotNull();
        Assertions.assertThat(actual.isError()).describedAs("Check if Result is an Error").isTrue();
        return myself;
    }

    /**
     * Verifies that the {@link Result} represents Success.
     * <p>
     * Example: {@snippet lang="java" : assertThat(result).isSuccess(); }
     *
     * @return {@code this} assertion object.
     * @throws AssertionError if the {@code Result} represents Error.
     */
    public ResultAssert<R, E> isSuccess() {
        isNotNull();
        Assertions.assertThat(actual.isError())
                .describedAs("Check if Result is a Success")
                .isFalse();
        return myself;
    }

    /**
     * Verifies that the {@link Result} represents Error and the error value
     * is equal to the expected value.
     * <p>
     * Example: {@snippet lang="java" : assertThat(result).hasErrorEqualTo(expectedError); }
     *
     * @param expectedError the expected error value.
     * @return {@code this} assertion object.
     * @throws AssertionError if the {@code Result} represents Success or
     *     the error value is not equal to the expected value.
     */
    public ResultAssert<R, E> hasErrorEqualTo(E expectedError) {
        isNotNull();
        Assertions.assertThat(actualError())
                .describedAs("Check Result Error")
                .isEqualTo(expectedError);
        return myself;
    }

    /**
     * Verifies that the Error value object satisfied the given requirements
     * expressed as {@link Consumer}.
     * This is terminal operation of this assertion chain.
     * <p>
     * This is useful to perform an assertions on an Error value object,
     * passed assertion is evaluated and all failures are reported.
     * <p>
     * Example:
     * {@snippet lang="java" :
     * Result result = Result.error(
     *      new IllegalArgumentException("Name is invalid"));
     *
     * // assertions succeed:
     * ResultAssert.assertThat(result)
     *     .isError()
     *     .hasErrorThat(error ->
     *          Assertions.assertThat(error)
     *            .isInstanceOf(IllegalArgumentException.class)
     *            .hasMessage("Name is invalid")
     *     );
     *
     * // assertion fails:
     * ResultAssert.assertThat(result)
     *     .isError()
     *     .hasErrorThat(error ->
     *          Assertions.assertThat(error)
     *            .isInstanceOf(NullPointerException.class)
     *            .hasMessage("Name is invalid")
     *     );
     *  }
     *
     * @param consumer the consumer to assert the Error value object — must not be {@code null}.
     * @throws NullPointerException if Consumer is {@code null}.
     */
    public void hasErrorThat(Consumer<? super E> consumer) {
        Objects.requireNonNull(consumer, "Error value consumer should be non null");
        isNotNull();
        consumer.accept(actualError());
    }

    /**
     * Verifies that the {@link Result} represents Success and
     * the success value is equal to the expected value.
     * <p>
     * Example: {@snippet lang="java" : assertThat(result).hasSuccessEqualTo(expectedSuccess); }
     *
     * @param expectedSuccess the expected success value.
     * @return {@code this} assertion object.
     * @throws AssertionError if the {@code Result} represents Error or
     *     the success value is not equal to the expected value.
     */
    public ResultAssert<R, E> hasSuccessEqualTo(R expectedSuccess) {
        isNotNull();
        Assertions.assertThat(actualSuccess())
                .describedAs("Check Result Success")
                .isEqualTo(expectedSuccess);
        return myself;
    }

    /**
     * Verifies that the Success value object satisfied the given requirements
     * expressed as {@link Consumer}.
     * This is terminal operation of this assertion chain.
     * <p>
     * This is useful to perform an assertions on a Success value object,
     * passed assertion is evaluated and all failures are reported.
     * <p>
     * Example:
     * {@snippet lang="java" :
     * Result result = Result.success(List.of("one", "two", "three"));
     *
     * // assertions succeed:
     * ResultAssert.assertThat(result)
     *     .isSuccess()
     *     .hasSuccessThat(success ->
     *          Assertions.assertThat(success)
     *              .hasSize(3)
     *              .containsExactlyInAnyOrder("three", "two", "one")
     *     );
     *
     * // assertion fails:
     * ResultAssert.assertThat(result)
     *     .isSuccess()
     *     .hasSuccessThat(success ->
     *          Assertions.assertThat(success)
     *              .hasSize(2)
     *              .containsExactly("three", "two", "one")
     *     );
     *  }
     *
     * @param consumer the consumer to assert the Success value object — must not be {@code null}.
     * @throws NullPointerException if Consumer is {@code null}.
     */
    public void hasSuccessThat(Consumer<? super R> consumer) {
        Objects.requireNonNull(consumer, "Success value consumer should be non null");
        isNotNull();
        consumer.accept(actualSuccess());
    }

    private E actualError() {
        if (actual instanceof Result.Error(E error)) {
            return error;
        }
        throw failure("Expected Result to be Error but was Success");
    }

    private R actualSuccess() {
        if (actual instanceof Result.Success(R result)) {
            return result;
        }
        throw failure("Expected Result to be Success but was Error");
    }
}
