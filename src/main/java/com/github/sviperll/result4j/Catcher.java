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

import java.util.function.Function;

/**
 * @param <E> exception type
 */
public class Catcher<E extends Exception> extends AdaptingCatcher<E, E> {
    public static <E extends Exception> Catcher<E> of(Class<E> exceptionClass) {
        return new Catcher<>(exceptionClass);
    }

    private Catcher(Class<E> exceptionClass) {
        super(exceptionClass, Function.identity());
    }

    @Override
    public ForFunctions<E> forFunctions() {
        return new ForFunctions<>(this);
    }

    @Override
    public ForBiFunctions<E> forBiFunctions() {
        return new ForBiFunctions<>(this);
    }

    @Override
    public ForSuppliers<E> forSuppliers() {
        return new ForSuppliers<>(this);
    }

    @Override
    public ForRunnables<E> forRunnables() {
        return new ForRunnables<>(this);
    }

    @Override
    public ForConsumers<E> forConsumers() {
        return new ForConsumers<>(this);
    }

    @Override
    public ForBiConsumers<E> forBiConsumers() {
        return new ForBiConsumers<>(this);
    }

    public static class ForFunctions<E extends Exception>
            extends AdaptingCatcher.ForFunctions<E, E> {
        private ForFunctions(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }

    public static class ForBiFunctions<E extends Exception>
            extends AdaptingCatcher.ForBiFunctions<E, E> {
        private ForBiFunctions(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }

    public static class ForSuppliers<E extends Exception>
            extends AdaptingCatcher.ForSuppliers<E, E> {
        private ForSuppliers(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }

    public static class ForConsumers<E extends Exception>
            extends AdaptingCatcher.ForConsumers<E, E> {
        private ForConsumers(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }

    public static class ForBiConsumers<E extends Exception>
            extends AdaptingCatcher.ForBiConsumers<E, E> {
        private ForBiConsumers(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }

    public static class ForRunnables<E extends Exception>
            extends AdaptingCatcher.ForRunnables<E, E> {
        private ForRunnables(AdaptingCatcher<E, E> adapter) {
            super(adapter);
        }
    }
}
