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
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;

/**
 * AssertJ {@link org.assertj.core.api.Assert assertions} that can be applied to a {@link Result}.
 *
 * @param <R> type of successful result value.
 * @param <E> type representing error.
 * @see Result
 * @see <a href="https://assertj.github.io/doc/">AssertJ</a>
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
     * Verifies that the {@link Result} represents Error, and returns an
     * assertion object for the Error value.
     * <p>
     * The returned assertion can be used to perform more assertions on an Error value object.
     * <p>
     * Example:
     * <p>
     * {@snippet lang = "java":
     *     assertThat(result)
     *         .hasErrorThat()
     *         .asInstanceOf(InstanceOfAssertFactories.THROWABLE)
     *         .isExactlyInstanceOf(RuntimeException.class)
     *         .cause()
     *         .isExactlyInstanceOf(NumberFormatException.class)
     *         .hasMessage("For input string: \"xyz\"");
     *}
     *
     * @return assertion object for Error.
     * @throws AssertionError if the {@code Result} represents Success.
     */
    public ObjectAssert<E> hasErrorThat() {
        isNotNull();

        if (actual instanceof Result.Error(E error)) {
            return Assertions.assertThat(error);
        }
        throw failure("Expected Result to be Error but was Success");
    }

    /**
     * Verifies that the {@link Result} represents Success, and returns an
     * assertion object for the Success value.
     * <p>
     * The returned assertion can be used to perform more assertions on a Success value object.
     * <p>
     * Example:
     * <p>
     * {@snippet lang = "java":
     *     assertThat(result)
     *         .hasSuccessThat()
     *         .asInstanceOf(list(Integer.class))
     *         .containsExactlyInAnyOrderElementsOf(List.of(123, 456, 234));
     *}
     *
     * @return assertion object for Success value.
     * @throws AssertionError if the {@code Result} represents Error.
     */
    public ObjectAssert<R> hasSuccessThat() {
        isNotNull();

        if (actual instanceof Result.Success(R result)) {
            return Assertions.assertThat(result);
        }
        throw failure("Expected Result to be Success but was Error");
    }

}
