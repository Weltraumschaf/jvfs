/*
 *  LICENSE
 *
 * "THE BEER-WARE LICENSE" (Revision 43):
 * "Sven Strittmatter" <weltraumschaf@googlemail.com> wrote this file.
 * As long as you retain this notice you can do whatever you want with
 * this stuff. If we meet some day, and you think this stuff is worth it,
 * you can buy me a non alcohol-free beer in return.
 *
 * Copyright (C) 2012 "Sven Strittmatter" <weltraumschaf@googlemail.com>
 */
package de.weltraumschaf.jvfs.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link JvfsAssertions}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsAssertionsTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    @Test
    public void invokeConstructorByReflectionThrowsException() throws Exception {
        thrown.expect(InvocationTargetException.class);
        assertThat(JvfsAssertions.class.getDeclaredConstructors().length, is(1));
        final Constructor<JvfsAssertions> ctor = JvfsAssertions.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        ctor.newInstance();
    }

    @Test
    public void notNull_throwsExceptionIfNameIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Argument name must not be null!");
        JvfsAssertions.notNull("foo", null);
    }

    @Test
    public void notNull_throwsExceptionIfNameIsEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument name must not be empty!");
        JvfsAssertions.notNull("foo", "");
    }

    @Test
    public void notNull_doesNotThrowExceptionIfObjectIsNotNull() {
        JvfsAssertions.notNull("foo", "bar");
    }

    @Test
    public void notNull_throwExceptionIfObjectIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Parameter 'bar' must not be null!");
        JvfsAssertions.notNull(null, "bar");
    }

    @Test
    public void notEmpty_throwsExceptionIfNameIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Argument name must not be null!");
        JvfsAssertions.notEmpty("foo", null);
    }

    @Test
    public void notEmpty_throwsExceptionIfNameIsEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument name must not be empty!");
        JvfsAssertions.notEmpty("foo", "");
    }

    @Test
    public void notEmpty_throwsExceptionIfStringIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Parameter 'foo' must not be null!");
        JvfsAssertions.notEmpty(null, "foo");
    }

    @Test
    public void notEmpty_throwsExceptionIfStringIsEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter 'foo' must not be empty!");
        JvfsAssertions.notEmpty("", "foo");
    }

    @Test
    public void lessThan_throwsExceptionIfNameIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Argument name must not be null!");
        JvfsAssertions.lessThan(1, 2, null);
    }

    @Test
    public void lessThan_throwsExceptionIfNameIsEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument name must not be empty!");
        JvfsAssertions.lessThan(1, 2, "");
    }

    @Test
    public void lessThan_doesNotThrowExceptionIfLessThan() {
        JvfsAssertions.lessThan(1, 2, "bar");
    }

    @Test
    public void lessThan_throwsExceptionIfGreaterThan() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Paramater 'bar' must be less than '1'!");
        JvfsAssertions.lessThan(2, 1, "bar");
    }

    @Test
    public void lessThanEqual_throwsExceptionIfNameIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Argument name must not be null!");
        JvfsAssertions.lessThanEqual(1, 2, null);
    }

    @Test
    public void lessThanEqual_throwsExceptionIfNameIsEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument name must not be empty!");
        JvfsAssertions.lessThanEqual(1, 2, "");
    }

    @Test
    public void lessThanEqual_doesNotThrowExceptionIfLessThan() {
        JvfsAssertions.lessThanEqual(1, 2, "bar");
    }

    @Test
    public void lessThanEqual_doesNotThrowExceptionIfEqual() {
        JvfsAssertions.lessThanEqual(2, 2, "bar");
    }

    @Test
    public void lessThanEqual_throwsExceptionIfGreaterThan() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Paramater 'bar' must be less than or equal '1'!");
        JvfsAssertions.lessThanEqual(2, 1, "bar");
    }

    @Test
    public void greaterThan_throwsExceptionIfNameIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Argument name must not be null!");
        JvfsAssertions.greaterThan(2, 1, null);
    }

    @Test
    public void greaterThan_throwsExceptionIfNameIsEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument name must not be empty!");
        JvfsAssertions.greaterThan(2, 1, "");
    }

    @Test
    public void greaterThan_doesNotThrowExceptionIfLessThan() {
        JvfsAssertions.greaterThan(2, 1, "bar");
    }

    @Test
    public void greaterThan_throwsExceptionIfLessThan() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Paramater 'bar' must be greater than '2'!");
        JvfsAssertions.greaterThan(1, 2, "bar");
    }

    @Test
    public void greaterThanEqual_throwsExceptionIfNameIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Argument name must not be null!");
        JvfsAssertions.greaterThanEqual(2, 1, null);
    }

    @Test
    public void greaterThanEqual_throwsExceptionIfNameIsEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument name must not be empty!");
        JvfsAssertions.greaterThanEqual(2, 1, "");
    }

    @Test
    public void greaterThanEqual_doesNotThrowExceptionIfGreaterThan() {
        JvfsAssertions.greaterThanEqual(2, 1, "bar");
        JvfsAssertions.greaterThanEqual(2L, 1L, "bar");
    }

    @Test
    public void greaterThanEqual_doesNotThrowExceptionIfEqual() {
        JvfsAssertions.greaterThanEqual(2, 2, "bar");
        JvfsAssertions.greaterThanEqual(2L, 2L, "bar");
    }

    @Test
    public void greaterThanEqual_throwsExceptionIfLessThan() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Paramater 'bar' must be greater than or equal '2'!");
        JvfsAssertions.greaterThanEqual(1, 2, "bar");
    }

    @Test
    public void isEqual_throwsExceptionIfNameIsNull() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("Argument name must not be null!");
        JvfsAssertions.isEqual(2, 1, null);
    }

    @Test
    public void isEqual_throwsExceptionIfNameIsEmpty() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument name must not be empty!");
        JvfsAssertions.isEqual(2, 1, "");
    }

    @Test
    public void isEqual_doesNotThrowExcpetionIfEqual() {
        JvfsAssertions.isEqual("foo", "foo", "bar");
        JvfsAssertions.isEqual(Integer.valueOf(42), Integer.valueOf(42), "bar");
    }

    @Test
    public void isEqual_throwsExceptionIfNotEqual() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Parameter 'baz' is not equal to 'bar'!");
        JvfsAssertions.isEqual("foo", "bar", "baz");
    }

}
