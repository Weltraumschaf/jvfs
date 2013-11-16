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
import java.util.Arrays;
import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link JvfsObject}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsObjectTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    @Test
    public void invokeConstructorByReflectionThrowsException() throws Exception {
        thrown.expect(InvocationTargetException.class);
        assertThat(JvfsObject.class.getDeclaredConstructors().length, is(1));
        final Constructor<JvfsObject> ctor = JvfsObject.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        ctor.newInstance();
    }

    @Test
    public void testHashCode() {
        assertThat(
                JvfsObject.hashCode("foo", "bar", "baz"),
                is(Arrays.hashCode(new Object[]{"foo", "bar", "baz"})));
    }

    @Test
    public void testHashCode_throwsExceptionIfNullPassedIn() {
        thrown.expect(NullPointerException.class);
        assertThat(JvfsObject.hashCode((Object[]) null), is(0));
    }

    @Test
    public void equal() {
        assertThat(JvfsObject.equal(null, null), is(true));
        assertThat(JvfsObject.equal("", ""), is(true));
        assertThat(JvfsObject.equal("foo", "foo"), is(true));
        assertThat(JvfsObject.equal(Integer.valueOf(42), Integer.valueOf(42)), is(true));

        assertThat(JvfsObject.equal(null, ""), is(false));
        assertThat(JvfsObject.equal("", null), is(false));
        assertThat(JvfsObject.equal("foo", "bar"), is(false));
        assertThat(JvfsObject.equal("23", Integer.valueOf(23)), is(false));
        assertThat(JvfsObject.equal(Integer.valueOf(42), Integer.valueOf(23)), is(false));
    }

    @Test
    public void notEqual() {
        assertThat(JvfsObject.notEqual(null, ""), is(true));
        assertThat(JvfsObject.notEqual("", null), is(true));
        assertThat(JvfsObject.notEqual("foo", "bar"), is(true));
        assertThat(JvfsObject.notEqual("23", Integer.valueOf(23)), is(true));
        assertThat(JvfsObject.notEqual(Integer.valueOf(42), Integer.valueOf(23)), is(true));

        assertThat(JvfsObject.notEqual(null, null), is(false));
        assertThat(JvfsObject.notEqual("", ""), is(false));
        assertThat(JvfsObject.notEqual("foo", "foo"), is(false));
        assertThat(JvfsObject.notEqual(Integer.valueOf(42), Integer.valueOf(42)), is(false));
    }

}
