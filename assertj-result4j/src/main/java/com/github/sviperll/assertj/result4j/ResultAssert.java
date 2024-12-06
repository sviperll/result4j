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

package com.github.sviperll.assertj.result4j;

import com.github.sviperll.result4j.Result;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;

/**
 * AssertJ {@link org.assertj.core.api.Assert assertions} that can be applied to a {@link Result}.
 * <p>
 * To use this class you may
 * statically import the {@link ResultAssert#assertThat(Result)} method and
 * use it together with the other variants provided by AssertJ.
 *
 * {@snippet lang="java":
 *     // Standard AsserJ assertions:
 *     import static org.assertj.core.api.Assertions.assertThat;
 *
 *     // Assertions for the Result-values
 *     import static com.github.sviperll.assertj.result4j.ResultAssert.assertThat;
 * }
 *
 * With the above static-imports, test-code may just use unqualified {@code assertThat}-calls.
 *
 * {@snippet lang="java":
 *     void myOperationSucceeds() {
 *         Result<String, Integer> result = myOperation();
 *
 *         assertThat(result).isSuccess();
 *     }
 * }
 *
 * @param <R> type of successful result value.
 * @param <E> type representing error.
 *
 * @see Result
 * @see <a href="https://assertj.github.io/doc/">AssertJ</a>
 * @since 1.2.0
 */
public class ResultAssert<R, E> extends AbstractAssert<ResultAssert<R, E>, Result<R, E>> {

    /**
     * Creates assertion for {@link Result}.
     *
     * @param actual the actual value.
     * @param <R>    type of successful result value.
     * @param <E>    type representing error.
     * @return the created assertion object.
     */
    public static <R, E> ResultAssert<R, E> assertThat(Result<R, E> actual) {
        return new ResultAssert<>(actual);
    }

    private ResultAssert(Result<R, E> actual) {
        super(actual, ResultAssert.class);
    }

    /**
     * Verifies that the {@link Result} represents an error.
     * <p>
     * Example:
     *
     * {@snippet lang="java":
     *     assertThat(result).isError();
     * }
     *
     * @return {@code this} assertion object.
     * @throws AssertionError if the {@code Result} represents Success.
     */
    public ResultAssert<R, E> isError() {
        hasErrorThat();
        return myself;
    }

    /**
     * Verifies that the {@link Result} represents Success.
     * <p>
     * Example:
     *
     * {@snippet lang="java":
     *     assertThat(result).isSuccess();
     * }
     *
     * @return {@code this} assertion object.
     * @throws AssertionError if the {@code Result} represents Error.
     */
    public ResultAssert<R, E> isSuccess() {
        hasSuccessValueThat();
        return myself;
    }

    /**
     * Verifies that result-value represents an error and provides an assert-object that
     * can be used to assert properties of the underlying error-value.
     * <p>
     * This operation switches the subject of assertions.
     *
     * {@snippet lang="java":
     *     ResultAssert.assertThat(result)
     *             .isNotNull()                  // 1
     *             .isNotEqualTo(anotherResult)  // 2
     *             .hasErrorThat()               // @highlight substring="hasErrorThat"
     *             .isNotNull()                  // 3
     * }
     * <p>
     * Lines 1 and 2 in the example above assert properties of the {@link Result}-object
     * referenced by the {@code result} variable, but
     * line 2 now represents an assertion,
     * not about the whole {@link Result}-object, but
     * about the error-value inside this object.
     * This is so, because
     * the line 3 follows after the call to the {@code hasErrorThat} method.
     * <p>
     * Note that the object that is returned by the method is of the {@link ObjectAssert}-type,
     * if you need more specific assertions,
     * you may want to follow the {@code hasErrorThat} call with the call to
     * the {@link AbstractAssert#asInstanceOf(org.assertj.core.api.InstanceOfAssertFactory)} or
     * the {@link AbstractAssert#satisfies(java.util.function.Consumer...)} method.
     * <p>
     * Examples:
     *
     * {@snippet lang="java":
     *     ResultAssert.assertThat(result)
     *             .hasErrorThat()
     *             .isExactlyInstanceOf(RuntimeException.class)
     *             .asInstanceOf(InstanceOfAssertFactories.THROWABLE)
     *             .cause()
     *             .isExactlyInstanceOf(NumberFormatException.class)
     *             .hasMessage("For input string: \"xyz\"");
     * }
     * <p>
     * Another example that uses
     * the {@link AbstractAssert#satisfies(java.util.function.Consumer...)} method.
     *
     * {@snippet lang="java":
     *     ResultAssert.assertThat(result)
     *             .isError()
     *             .hasErrorThat()
     *             .satisfies(
     *                     error ->
     *                             Assertions.assertThat(error)
     *                                     .isInstanceOf(NullPointerException.class)
     *                                     .hasMessage("xyz")
     *             );
     * }
     *
     */
    public ObjectAssert<E> hasErrorThat() {
        isNotNull();
        if (!(actual instanceof Result.Error(E error))) {
            throw failure("Expected Result to be Error, but was Success");
        }
        return Assertions.assertThat(error);
    }

    /**
     * Verifies that result-value represents a result of the successful operation and
     * provides an assert-object that can be used to assert properties of the underlying value.
     * <p>
     * This operation switches the subject of assertions.
     *
     * {@snippet lang="java":
     *     ResultAssert.assertThat(result)
     *             .isNotNull()                  // 1
     *             .isNotEqualTo(anotherResult)  // 2
     *             .hasValueThat()               // @highlight substring="hasValueThat"
     *             .isNotNull()                  // 3
     * }
     * <p>
     * Lines 1 and 2 in the example above assert properties of the {@link Result}-object
     * referenced by the {@code result} variable, but
     * line 3 now represents an assertion,
     * not about the whole {@link Result}-object, but
     * about the value inside this object.
     * This is so, because
     * the line 3 follows after the call to the {@code hasSuccessValueThat} method.
     * <p>
     * Note that the object that is returned by the method is of the {@link ObjectAssert}-type,
     * if you need more specific assertions,
     * you may want to follow the {@code hasSuccessValueThat} call with the call to
     * the {@link AbstractAssert#asInstanceOf(org.assertj.core.api.InstanceOfAssertFactory)} or
     * the {@link AbstractAssert#satisfies(java.util.function.Consumer...)} method.
     * <p>
     * Examples:
     *
     * {@snippet lang="java":
     *     ResultAssert.assertThat(result)
     *             .hasValueThat()
     *             .asInstanceOf(InstanceOfAssertFactories.list(Integer.class))
     *             .containsExactlyInAnyOrderElementsOf(List.of(123, 456, 234));
     * }
     */
    public ObjectAssert<R> hasSuccessValueThat() {
        isNotNull();
        if (!(actual instanceof Result.Success(R value))) {
            throw failure("Expected Result to be Success, but was Error");
        }
        return Assertions.assertThat(value);
    }
}
