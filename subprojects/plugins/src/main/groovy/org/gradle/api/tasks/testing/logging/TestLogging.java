/*
 * Copyright 2012 the original author or authors.
 *
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
 */

package org.gradle.api.tasks.testing.logging;

import org.gradle.api.Experimental;

import java.util.Set;

/**
 * Options that determine which test events get logged, and at which detail.
 */
public interface TestLogging extends org.gradle.api.tasks.testing.TestLogging {
    /**
     * Returns the events to be logged.
     *
     * @return the events to be logged
     */
    @Experimental
    Set<TestLogEvent> getEvents();

    /**
     * Sets the events to be logged.
     *
     * @param events the events to be logged
     */
    @Experimental
    void setEvents(Set<TestLogEvent> events);

    /**
     * Sets the events to be logged. Events can be passed as enum values
     * (e.g. {@link TestLogEvent#FAILED}) or Strings (e.g. "failed").
     *
     * @param events the events to be logged
     */
    @Experimental
    void events(Object... events);

    /**
     * Returns the minimum granularity of the events to be logged. Typically, 0
     * corresponds to the Gradle-generated test suite for the whole test run, 1 corresponds to
     * the Gradle-generated test suite for a particular test JVM, 2 corresponds to a test class,
     * and 3 corresponds to a test method. These values will vary if user-defined
     * suites are executed.
     * <p>-1 denotes the highest granularity and corresponds to an atomic test.
     *
     * @return the minimum granularity of the events to be logged
     */
    @Experimental
    int getMinGranularity();

    /**
     * Sets the minimum granularity of the events to be logged. Typically, 0
     * corresponds to the Gradle-generated test suite for the whole test run, 1 corresponds to
     * the Gradle-generated test suite for a particular test JVM, 2 corresponds to a test class,
     * and 3 corresponds to a test method. These values will vary if user-defined
     * suites are executed.
     * <p>-1 denotes the highest granularity and corresponds to an atomic test.
     *
     * @param granularity the minimum granularity of the events to be logged
     */
    @Experimental
    void setMinGranularity(int granularity);

    /**
     * Convenience method that delegates to {@link #setMinGranularity(int)}.
     */
    @Experimental
    void minGranularity(int granularity);

    /**
     * Returns the maximum granularity of the events to be logged. Typically, 0
     * corresponds to the Gradle-generated test suite for the whole test run, 1 corresponds to
     * the Gradle-generated test suite for a particular test JVM, 2 corresponds to a test class,
     * and 3 corresponds to a test method. These values will vary if user-defined
     * suites are executed.
     * <p>-1 denotes the highest granularity and corresponds to an atomic test.
     *
     * @return the maximum granularity of the events to be logged
     */
    @Experimental
    int getMaxGranularity();

    /**
     * Returns the maximum granularity of the events to be logged. Typically, 0
     * corresponds to the Gradle-generated test suite for the whole test run, 1 corresponds to
     * the Gradle-generated test suite for a particular test JVM, 2 corresponds to a test class,
     * and 3 corresponds to a test method. These values will vary if user-defined
     * suites are executed.
     * <p>-1 denotes the highest granularity and corresponds to an atomic test.
     *
     * @param granularity the maximum granularity of the events to be logged
     */
    @Experimental
    void setMaxGranularity(int granularity);

    /**
     * Convenience method that delegates to {@link #setMaxGranularity(int)}.
     */
    @Experimental
    void maxGranularity(int granularity);

    /**
     * Returns the display granularity of the events to be logged. For example, if set to 0,
     * a method-level event will be displayed as "Test Run > Test Worker x > org.SomeClass > org.someMethod".
     * If set to 2, the same event will be displayed as "org.someClass > org.someMethod".
     * <p>-1 denotes the highest granularity and corresponds to an atomic test.
     *
     * @return the display granularity of the events to be logged
     */
    @Experimental
    int getDisplayGranularity();

    /**
     * Sets the display granularity of the events to be logged. For example, if set to 0,
     * a method-level event will be displayed as "Test Run > Test Worker x > org.SomeClass > org.someMethod".
     * If set to 2, the same event will be displayed as "org.someClass > org.someMethod".
     * <p>-1 denotes the highest granularity and corresponds to an atomic test.
     *
     * @param granularity the display granularity of the events to be logged
     */
    @Experimental
    void setDisplayGranularity(int granularity);

    /**
     * Convenience method that delegates to {@link #setDisplayGranularity(int)}.
     */
    @Experimental
    void displayGranularity(int granularity);

    /**
     * Tells whether exceptions that occur during test execution will be logged.
     * Typically these exceptions coincide with a "failed" event.
     *
     * @return whether exceptions that occur during test execution will be logged
     */
    @Experimental
    boolean getShowExceptions();

    /**
     * Sets whether exceptions that occur during test execution will be logged.
     *
     * @param flag whether exceptions that occur during test execution will be logged
     */
    @Experimental
    void setShowExceptions(boolean flag);

    /**
     * Convenience method for {@link #setShowExceptions(boolean)}.
     */
    @Experimental
    void showExceptions(boolean flag);

    /**
     * Tells whether causes of exceptions that occur during test execution will be logged.
     * Only relevant if {@code showExceptions} is {@code true}.
     *
     * @return whether causes of exceptions that occur during test execution will be logged
     */
    @Experimental
    boolean getShowCauses();

    /**
     * Sets whether causes of exceptions that occur during test execution will be logged.
     * Only relevant if {@code showExceptions} is {@code true}.
     *
     * @param flag whether causes of exceptions that occur during test execution will be logged
     */
    @Experimental
    void setShowCauses(boolean flag);

    /**
     * Convenience method for {@link #setShowCauses(boolean)}.
     */
    @Experimental
    void showCauses(boolean flag);

    /**
     * Tells whether stack traces of exceptions that occur during test execution will be logged.
     *
     * @return whether stack traces of exceptions that occur during test execution will be logged
     */
    @Experimental
    boolean getShowStackTraces();

    /**
     * Sets whether stack traces of exceptions that occur during test execution will be logged.
     *
     * @param flag whether stack traces of exceptions that occur during test execution will be logged
     */
    @Experimental
    void setShowStackTraces(boolean flag);

    /**
     * Convenience method for {@link #setShowStackTraces(boolean)}.
     */
    @Experimental
    void showStackTraces(boolean flag);

    /**
     * Returns the format to be used for logging test exceptions. Only relevant if {@code showStackTraces} is {@code true}.
     *
     * @return the format to be used for logging test exceptions
     */
    @Experimental
    TestExceptionFormat getExceptionFormat();

    /**
     * Sets the format to be used for logging test exceptions. Only relevant if {@code showStackTraces} is {@code true}.
     *
     * @param exceptionFormat the format to be used for logging test exceptions
     */
    @Experimental
    void setExceptionFormat(TestExceptionFormat exceptionFormat);

    /**
     * Convenience method for {@link #setExceptionFormat(TestExceptionFormat)}. Accepts both enum values and Strings.
     */
    @Experimental
    void exceptionFormat(Object exceptionFormat);

    /**
     * Returns the set of filters to be used for sanitizing test stack traces.
     *
     * @return the set of filters to be used for sanitizing test stack traces
     */
    @Experimental
    Set<TestStackTraceFilter> getStackTraceFilters();

    /**
     * Sets the set of filters to be used for sanitizing test stack traces.
     *
     * @param stackTraces the set of filters to be used for sanitizing test stack traces
     */
    @Experimental
    void setStackTraceFilters(Set<TestStackTraceFilter> stackTraces);

    /**
     * Convenience method for {@link #setStackTraceFilters(java.util.Set)}. Accepts both enum values and Strings.
     */
    @Experimental
    void stackTraceFilters(Object... stackTraces);

    /**
     * Tells whether output on standard out and standard error will be logged. Equivalent to checking if both
     * log events {@link TestLogEvent#STANDARD_OUT} and {@link TestLogEvent#STANDARD_ERROR} are set.
     */
     boolean getShowStandardStreams();

    /**
     * Sets whether output on standard out and standard error will be logged. Equivalent to setting
     * log events {@link TestLogEvent#STANDARD_OUT} and {@link TestLogEvent#STANDARD_ERROR}.
     */
     TestLogging setShowStandardStreams(boolean flag);

    /**
     * Convenience method for {@link #setShowStandardStreams(boolean)}.
     */
    void showStandardStreams(boolean flag);
}
