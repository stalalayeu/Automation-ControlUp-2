package com.controlup.automation.utils;

import com.jayway.awaitility.Duration;
import com.jayway.awaitility.core.*;
import com.jayway.awaitility.pollinterval.FixedPollInterval;
import com.jayway.awaitility.pollinterval.PollInterval;
import org.hamcrest.Matcher;
import org.openqa.selenium.WebElement;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Duration.TEN_SECONDS;
import static com.jayway.awaitility.Duration.ZERO;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.core.Is.isA;
import static org.testng.Assert.fail;

public class Await {

    private ConditionFactory conditionFactory;
    private String errorMessage;
    private Callable<String> callableErrorMessage;
    private String exceptionMessage;
    private Callable<String> callableExceptionMessage;

    public Await() {
        conditionFactory = new ConditionFactory(TEN_SECONDS, //default timeout
                new FixedPollInterval(new Duration(350, MILLISECONDS)), //poll interval
                ZERO, //poll delay
                true, //default settings as in Awaitility
                new PredicateExceptionIgnorer(e -> false)); //default settings as in Awaitility
    }

    /**
     * Create Await with auto pollInterval logic
     *
     * @param timeout is milliseconds
     */
    public Await(long timeout) {
        long pollInterval;
        if (timeout >= 10000) {
            pollInterval = 1000;
        } else if (timeout > 3000) {
            pollInterval = 500;
        } else if (timeout > 1000) {
            pollInterval = 300;
        } else {
            pollInterval = 200;
        }
        conditionFactory = new ConditionFactory(new Duration(timeout, MILLISECONDS), //default timeout
                new FixedPollInterval(new Duration(pollInterval, MILLISECONDS)), //poll interval
                ZERO, //poll delay
                true, //default settings as in Awaitility
                new PredicateExceptionIgnorer(e -> false)); //default settings as in Awaitility
    }

    /**
     * Create Await instance with atMost wait time in seconds
     *
     * @param seconds timeout in seconds
     * @return the Await instance
     */
    public static Await waitFor(int seconds) {
        if (seconds > 650 || seconds == 0) {
            throw new RuntimeException("Waiter timeout set to " + seconds + "s. Is it a typo?");
        }
        return new Await(seconds * 1000);
    }

    /**
     * Handle condition evaluation results each time evaluation of a condition occurs. Works only with a Hamcrest matcher-based condition.
     *
     * @param conditionEvaluationListener the condition evaluation listener
     * @return the Await instance
     */
    public Await conditionEvaluationListener(@SuppressWarnings("rawtypes") ConditionEvaluationListener conditionEvaluationListener) {
        conditionFactory = conditionFactory.conditionEvaluationListener(conditionEvaluationListener);
        return this;
    }

    /**
     * clean all messages, so that if you wish you could re-use your Await object
     */
    private void forgetErrorMessages() {
        errorMessage = null;
        callableErrorMessage = null;
        exceptionMessage = null;
        callableExceptionMessage = null;
    }

    /**
     * Instruct Await that in case of waiting fail create TestNG fail (java.lang.AssertionError) with such static message
     *
     * @param failMessage failure message
     * @return the Await instance
     */
    public Await withError(String failMessage) {
        forgetErrorMessages();
        errorMessage = failMessage;
        return this;
    }

    /**
     * Instruct Await that in case of waiting fail create TestNG fail (java.lang.AssertionError) dynamically by execution provided callable
     * if String errorMessage is empty
     *
     * @param callableFailMessage dynamically generated failure message, will be called if waiters condition fails
     * @return the Await instance
     */
    public Await withError(Callable<String> callableFailMessage) {
        forgetErrorMessages();
        callableErrorMessage = callableFailMessage;
        return this;
    }

    /**
     * Instruct Await that in case of waiting fail throw com.jayway.awaitility.core.ConditionTimeoutException
     * with custom message
     *
     * @param customExceptionMessage custom message which is added to ConditionTimeoutException in case of waiter failure
     * @return the Await instance
     */
    public Await withException(String customExceptionMessage) {
        forgetErrorMessages();
        exceptionMessage = customExceptionMessage;
        return this;
    }

    /**
     * Instruct Await that in case of waiting fail throw com.jayway.awaitility.core.ConditionTimeoutException
     * with custom message
     *
     * @param callableCustomExceptionMessage callable which return custom message which is called in case waiter fails
     * @return the Await instance
     */
    public Await withException(Callable<String> callableCustomExceptionMessage) {
        forgetErrorMessages();
        callableExceptionMessage = callableCustomExceptionMessage;
        return this;
    }

    /**
     * Await at most <code>timeout</code> before failing waiting.
     *
     * @param timeout the timeout
     * @return the Await instance
     */
    public Await atMost(Duration timeout) {
        conditionFactory = conditionFactory.atMost(timeout);
        return this;
    }

    /**
     * Await at most <code>timeout</code> before failing waiting.
     *
     * @param timeout timeout amount
     * @param unit    timeout units
     * @return the Await instance
     */
    public Await atMost(long timeout, TimeUnit unit) {
        conditionFactory = conditionFactory.atMost(timeout, unit);
        return this;
    }

    /**
     * Await at most <code>timeInMs</code> milliseconds before failing waiting.
     *
     * @param timeInMs the timeout
     * @return the Await instance
     */
    public Await atMost(int timeInMs) {
        return atMost(new Duration(timeInMs, MILLISECONDS));
    }

    /**
     * Await at most <code>timeInMs</code> milliseconds before failing waiting.
     *
     * @param timeInMs the timeout
     * @return the Await instance
     */
    public Await atMost(long timeInMs) {
        return atMost(new Duration(timeInMs, MILLISECONDS));
    }

    /**
     * Specify the delay that will be used before Await starts polling for
     * the result the first time. If you don't specify a poll delay explicitly
     * it'll be 0.
     *
     * @param pollDelay the poll delay
     * @return the Await instance
     */
    public Await pollDelay(Duration pollDelay) {
        conditionFactory = conditionFactory.pollDelay(pollDelay);
        return this;
    }

    /**
     * Specify the polling interval Await will use for this await
     * statement. This means the frequency in which the condition is checked for
     * completion.
     *
     * @param pollInterval the poll interval
     * @return the Await instance
     */
    public Await pollInterval(PollInterval pollInterval) {
        conditionFactory = conditionFactory.pollInterval(pollInterval);
        return this;
    }

    /**
     * Specify the polling interval Await will use for this await
     * statement. This means the frequency in which the condition is checked for
     * completion.
     *
     * @param pollInterval the poll interval in milliseconds
     * @return the Await instance
     */
    public Await pollInterval(long pollInterval) {
        return pollInterval(pollInterval, MILLISECONDS);
    }

    /**
     * Specify the polling interval Awaitility will use for this await
     * statement. This means the frequency in which the condition is checked for
     * completion.
     *
     * @param pollInterval the poll interval
     * @param unit         the unit
     * @return the Await instance
     * @see FixedPollInterval
     */
    public Await pollInterval(long pollInterval, TimeUnit unit) {
        conditionFactory = conditionFactory.pollInterval(pollInterval, unit);
        return this;
    }

    /**
     * Instruct Await to ignore exceptions instance of the supplied exceptionType type.
     * Exceptions will be treated as evaluating to <code>false</code>.
     * This is useful in situations where the evaluated conditions may temporarily throw exceptions.
     * <p/>
     * <p>If you want to ignore a specific exceptionType then use {@link #ignoreException(Class)}</p>
     *
     * @param exceptionType The exception type (hierarchy) to ignore
     * @return the Await instance
     */
    public Await ignoreExceptionsInstanceOf(Class<? extends Exception> exceptionType) {
        conditionFactory = conditionFactory.ignoreExceptionsInstanceOf(exceptionType);
        return this;
    }

    /**
     * Instruct Await to ignore a specific exception and <i>no</i> subclasses of this exception.
     * Exceptions will be treated as evaluating to <code>false</code>.
     * This is useful in situations where the evaluated conditions may temporarily throw exceptions.
     * <p>If you want to ignore a subtypes of this exception then use {@link #ignoreExceptionsInstanceOf(Class)}} </p>
     *
     * @param exceptionType The exception type to ignore
     * @return the Await instance
     */
    public Await ignoreException(Class<? extends Exception> exceptionType) {
        conditionFactory = conditionFactory.ignoreException(exceptionType);
        return this;
    }

    /**
     * Instruct Await to ignore <i>all</i> exceptions that occur during evaluation.
     * Exceptions will be treated as evaluating to
     * <code>false</code>. This is useful in situations where the evaluated
     * conditions may temporarily throw exceptions.
     *
     * @return the Await instance
     */
    public Await ignoreExceptions() {
        conditionFactory = conditionFactory.ignoreExceptions();
        return this;
    }

    /**
     * Instruct Await to ignore exceptions that occur during evaluation and matches the supplied Hamcrest matcher.
     * Exceptions will be treated as evaluating to
     * <code>false</code>. This is useful in situations where the evaluated conditions may temporarily throw exceptions.
     *
     * @return the Await instance
     */
    public Await ignoreExceptionsMatching(Matcher<? super Exception> matcher) {
        conditionFactory = conditionFactory.ignoreExceptionsMatching(matcher);
        return this;
    }

    /**
     * Instruct Await to ignore exceptions that occur during evaluation and matches the supplied <code>predicate</code>.
     * Exceptions will be treated as evaluating to
     * <code>false</code>. This is useful in situations where the evaluated conditions may temporarily throw exceptions.
     *
     * @return the condition factory.
     */
    public Await ignoreExceptionsMatching(Predicate<Exception> predicate) {
        conditionFactory = conditionFactory.ignoreExceptionsMatching(predicate);
        return this;
    }

    /**
     * Don't catch uncaught exceptions in other threads. This will <i>not</i>
     * make the await statement fail if exceptions occur in other threads.
     *
     * @return the condition factory
     */
    public Await dontCatchUncaughtExceptions() {
        conditionFactory = conditionFactory.dontCatchUncaughtExceptions();
        return this;
    }

    private void conditionTimeoutExceptionHandling(ConditionTimeoutException ex) {
        if (errorMessage != null) {
            throwAssertionError(errorMessage, ex);
        } else if (callableErrorMessage != null) {
            throwAssertionError(callableErrorMessage, ex);
        } else if (exceptionMessage != null) {
            throw getTimeoutException(exceptionMessage, ex);
        } else if (callableExceptionMessage != null) {
            throw getTimeoutException(callableExceptionMessage, ex);
        }
        throw ex;
    }

    private ConditionTimeoutException getTimeoutException(String message, ConditionTimeoutException ex) {
        ConditionTimeoutException toThrow = new ConditionTimeoutException(message + "\n" + ex.getMessage());
        toThrow.setStackTrace(ex.getStackTrace());
        forgetErrorMessages();

        return toThrow;
    }

    private ConditionTimeoutException getTimeoutException(Callable<String> message, ConditionTimeoutException ex) {
        try {
            return getTimeoutException(message.call(), ex);
        } catch (Exception e) {
            forgetErrorMessages();
            throw new RuntimeException(e.getMessage() + ";\nCondition failed: " + ex.getMessage(), e.getCause());
        }
    }

    private void throwAssertionError(String message, ConditionTimeoutException ex) {
        String error = message + ";\n" + ex.getMessage();
        forgetErrorMessages();
        fail(error);
    }

    private void throwAssertionError(Callable<String> message, ConditionTimeoutException ex) {
        try {
            throwAssertionError(message.call(), ex);
        } catch (Exception e) {
            forgetErrorMessages();
            throw new RuntimeException(e.getMessage() + ";\nCondition failed: " + ex.getMessage(), e.getCause());
        }
    }

    /**
     * Specify the condition that must be met when waiting for a method call.
     * E.g.
     * <p>&nbsp;</p>
     * <pre>
     * await().untilCall(to(orderService).size(), is(greaterThan(2)));
     * </pre>
     *
     * @param <T>     the generic type
     * @param ignore  the return value of the method call
     * @param matcher The condition that must be met when
     * @return a T object.
     * @throws ConditionTimeoutException If condition was not fulfilled within the given time period and withError is not set
     */
    public <T> T untilCall(T ignore, Matcher<? super T> matcher) {
        try {
            return conditionFactory.untilCall(ignore, matcher);
        } catch (ConditionTimeoutException ex) {
            conditionTimeoutExceptionHandling(ex);
            return null;
        }
    }

    /**
     * Await until a {@link Callable} supplies a value matching the specified
     *
     * @param supplier the supplier that is responsible for getting the value that
     *                 should be matched.
     * @param matcher  the matcher The hamcrest matcher that checks whether the
     *                 condition is fulfilled.
     * @return a T object.
     * @throws ConditionTimeoutException If condition was not fulfilled within the given time period and withError is not set
     **/
    public <T> T until(Callable<T> supplier, Matcher<? super T> matcher) {
        try {
            return conditionFactory.until(supplier, matcher);
        } catch (ConditionTimeoutException ex) {
            conditionTimeoutExceptionHandling(ex);
            return null;
        }
    }

    /**
     * Await until a {@link Callable} returns <code>true</code>.
     *
     * @param conditionEvaluator the condition evaluator
     * @throws ConditionTimeoutException If condition was not fulfilled within the given time period and withError is not set
     */
    public void until(Callable<Boolean> conditionEvaluator) {
        try {
            conditionFactory.until(conditionEvaluator);
        } catch (ConditionTimeoutException ex) {
            conditionTimeoutExceptionHandling(ex);
        }
    }

    //DON'T use this in your tests!!, this is for internal use in ElementsUtil only!
    protected WebElement untilGot(Callable<WebElement> callable) {
        try {
            return conditionFactory.until(callable, isA(WebElement.class));
        } catch (ConditionTimeoutException ex) {
            conditionTimeoutExceptionHandling(ex);
            return null;
        }
    }
}
