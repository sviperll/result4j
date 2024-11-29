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
import java.util.function.Consumer;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;


public class ResultAssert<R, E> extends AbstractAssert<ResultAssert<R, E>, Result<R, E>> {

    public static <R, E> ResultAssert<R, E> assertThat(Result<R, E> actual) {
        return new ResultAssert<>(actual);
    }

    private ResultAssert(Result<R, E> actual) {
        super(actual, ResultAssert.class);
    }

    public ResultAssert<R, E> isError() {
        isNotNull();
        Assertions.assertThat(actual.isError()).describedAs("Check if Result is an Error").isTrue();
        return this;
    }

    public ResultAssert<R, E> isSuccess() {
        isNotNull();
        Assertions.assertThat(actual.isError())
                .describedAs("Check if Result is a Success")
                .isFalse();
        return this;
    }

    public ResultAssert<R, E> hasErrorEqualTo(E expectedError) {
        isNotNull();
        Assertions.assertThat(actualError())
                .describedAs("Check Result Error")
                .isEqualTo(expectedError);
        return this;
    }

    public ResultAssert<R, E> hasErrorThat(Consumer<E> consumer) {
        isNotNull();
        consumer.accept(actualError());
        return this;
    }

    public ResultAssert<R, E> hasSuccessEqualTo(R expectedSuccess) {
        isNotNull();
        Assertions.assertThat(actualSuccess())
                .describedAs("Check Result Success")
                .isEqualTo(expectedSuccess);
        return this;
    }

    public ResultAssert<R, E> hasSuccessThat(Consumer<R> consumer) {
        isNotNull();
        consumer.accept(actualSuccess());
        return this;
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
