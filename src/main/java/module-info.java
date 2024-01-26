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

/**
 * The module provides the definition of the Result-type similar to Result-type in Rust that
 * allows to return either successful result or otherwise some kind of error.
 * <p>
 * In Java, the native way of reporting errors are exceptions, either checked or unchecked.
 * You do not need Result-type most of the time in Java-code, where
 * you can directly throw exceptions.
 * But there are situations, where more functional-style is used.
 * In such situations pure-functions are expected that throw no exceptions.
 * Handling exception in such situations can be cumbersome and require a lot of boilerplate code.
 * Result-type and associated helper-classes help with exception handling and
 * allow to write idiomatic functional code that can interact with methods that throw exceptions.
 *
 * @see com.github.sviperll.result4j/com.github.sviperll.result4j
 */
module com.github.sviperll.result4j {
    requires java.base;
}
