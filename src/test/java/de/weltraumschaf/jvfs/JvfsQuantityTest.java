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

package de.weltraumschaf.jvfs;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link JvfsQuantity}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsQuantityTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    private static final long FACTOR = 1024L;

    @Test
    public void forValue_string_throwsExceptionIfNull() {
        thrown.expect(NullPointerException.class);
        JvfsQuantity.forValue(null);
    }

    @Test
    public void forValue_string_throwsExceptionIfEmpty() {
        thrown.expect(IllegalArgumentException.class);
        JvfsQuantity.forValue("");
    }

    @Test
    public void forValue_string() {
        JvfsQuantity sut = JvfsQuantity.forValue("1024");
        assertThat(sut.value(), is(FACTOR));
        assertThat(sut, is(JvfsQuantity.forValue("1024")));
        assertThat(sut, is(JvfsQuantity.forValue(FACTOR)));

        sut = JvfsQuantity.forValue("1024k  ");
        assertThat(sut.value(), is(FACTOR * FACTOR));
        assertThat(sut, is(JvfsQuantity.forValue("1024K")));
        assertThat(sut, is(JvfsQuantity.forValue(FACTOR * FACTOR)));
        assertThat(sut, is(JvfsQuantity.forValue("1M")));

        sut = JvfsQuantity.forValue(" 17M  ");
        assertThat(sut.value(), is(FACTOR * FACTOR * 17L));
        assertThat(sut, is(JvfsQuantity.forValue("17m")));
        assertThat(sut, is(JvfsQuantity.forValue(FACTOR * FACTOR * 17L)));
    }

    @Test
    public void forValue_long() {
        JvfsQuantity sut = JvfsQuantity.forValue(FACTOR);
        assertThat(sut.value(), is(FACTOR));
        assertThat(sut, is(JvfsQuantity.forValue("1024")));
        assertThat(sut, is(JvfsQuantity.forValue(FACTOR)));

        sut = JvfsQuantity.forValue(FACTOR * FACTOR);
        assertThat(sut.value(), is(FACTOR * FACTOR));
        assertThat(sut, is(JvfsQuantity.forValue("1024K")));
        assertThat(sut, is(JvfsQuantity.forValue(FACTOR * FACTOR)));
        assertThat(sut, is(JvfsQuantity.forValue("1M")));

        sut = JvfsQuantity.forValue(FACTOR * FACTOR * 17L);
        assertThat(sut.value(), is(FACTOR * FACTOR * 17L));
        assertThat(sut, is(JvfsQuantity.forValue("17m")));
        assertThat(sut, is(JvfsQuantity.forValue(FACTOR * FACTOR * 17L)));
    }

    @Test
    public void parseQuantity_long_throwsExceptionIfNegative() {
        thrown.expect(IllegalArgumentException.class);
        JvfsQuantity.forValue(-1L);
    }

    @Test
    public void parseQuantity() {
        assertThat(JvfsQuantity.parseQuantity(" 24 "), is(24L));
        assertThat(JvfsQuantity.parseQuantity("1024"), is(FACTOR));
        assertThat(JvfsQuantity.parseQuantity("1k "), is(FACTOR));
        assertThat(JvfsQuantity.parseQuantity("1K"), is(FACTOR));
        assertThat(JvfsQuantity.parseQuantity("1m"), is(FACTOR * FACTOR));
        assertThat(JvfsQuantity.parseQuantity("1M"), is(FACTOR * FACTOR));
        assertThat(JvfsQuantity.parseQuantity(" 1g"), is(FACTOR * FACTOR * FACTOR));
        assertThat(JvfsQuantity.parseQuantity("1G"), is(FACTOR * FACTOR * FACTOR));
        assertThat(JvfsQuantity.parseQuantity("15G"), is(FACTOR * FACTOR * FACTOR * 15L));
    }

    @Test
    public void testHashCode() {
        final JvfsQuantity sut1 = JvfsQuantity.forValue("1M");
        final JvfsQuantity sut2 = JvfsQuantity.forValue("1M");
        final JvfsQuantity sut3 = JvfsQuantity.forValue("2M");

        assertThat(sut1.hashCode(), is(sut1.hashCode()));
        assertThat(sut1.hashCode(), is(sut2.hashCode()));
        assertThat(sut2.hashCode(), is(sut1.hashCode()));
        assertThat(sut2.hashCode(), is(sut2.hashCode()));

        assertThat(sut3.hashCode(), is(sut3.hashCode()));
        assertThat(sut3.hashCode(), is(not(sut1.hashCode())));
        assertThat(sut3.hashCode(), is(not(sut2.hashCode())));
    }

    @Test
    public void testEquals() {
        final JvfsQuantity sut1 = JvfsQuantity.forValue("1M");
        final JvfsQuantity sut2 = JvfsQuantity.forValue("1M");
        final JvfsQuantity sut3 = JvfsQuantity.forValue("2M");

        assertThat(sut1.equals(null), is(false));
        assertThat(sut1.equals("foo"), is(false));

        assertThat(sut1.equals(sut1), is(true));
        assertThat(sut1.equals(sut2), is(true));
        assertThat(sut2.equals(sut1), is(true));
        assertThat(sut2.equals(sut2), is(true));

        assertThat(sut3.equals(sut3), is(true));
        assertThat(sut3.equals(sut1), is(false));
        assertThat(sut3.equals(sut2), is(false));
        assertThat(sut1.equals(sut3), is(false));
        assertThat(sut2.equals(sut3), is(false));
    }

    @Test
    public void testToString() {
        assertThat(JvfsQuantity.forValue("1M").toString(), is(equalTo("1048576")));
    }
}
