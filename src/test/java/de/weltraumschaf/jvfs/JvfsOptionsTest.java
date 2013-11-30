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

import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link JvfsOptions}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsOptionsTest {

    @Test
    public void defaultOptions() {
        assertThat(JvfsOptions.DEFAULT.getCapacity(), is(equalTo(JvfsQuantity.forValue(0L))));
        assertThat(JvfsOptions.DEFAULT.isReadonly(), is(false));
    }

    @Test
    public void createByBuilder() {
        final JvfsOptions opts = JvfsOptions.builder()
            .readonly(true)
            .capacity("1k")
            .create();

        assertThat(opts.isReadonly(), is(true));
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(1024L))));
    }

    @Test
    public void forValue() {
        Map<String, Object> env = JvfsCollections.newMap();
        JvfsOptions opts = JvfsOptions.forValue(env);
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(0L))));
        assertThat(opts.isReadonly(), is(false));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), "1k");
        env.put(JvfsOptions.Option.READONLY.key(), "true");
        opts = JvfsOptions.forValue(env);
        assertThat(opts.isReadonly(), is(true));
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(1024L))));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), 1024L);
        env.put(JvfsOptions.Option.READONLY.key(), "true");
        opts = JvfsOptions.forValue(env);
        assertThat(opts.isReadonly(), is(true));
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(1024L))));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), JvfsQuantity.forValue(1024L));
        env.put(JvfsOptions.Option.READONLY.key(), "true");
        opts = JvfsOptions.forValue(env);
        assertThat(opts.isReadonly(), is(true));
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(1024L))));
    }

    @Test
    public void testHashCode() {
        final JvfsOptions sut1 = JvfsOptions.builder().capacity("1M").create();
        final JvfsOptions sut2 = JvfsOptions.builder().capacity("1M").create();
        final JvfsOptions sut3 = JvfsOptions.builder().capacity("2M").create();

        assertThat(sut1.hashCode(), is(sut1.hashCode()));
        assertThat(sut1.hashCode(), is(sut2.hashCode()));
        assertThat(sut2.hashCode(), is(sut1.hashCode()));
        assertThat(sut2.hashCode(), is(sut2.hashCode()));

        assertThat(sut3.hashCode(), is(sut3.hashCode()));
        assertThat(sut3.hashCode(), is(not(sut2.hashCode())));
        assertThat(sut3.hashCode(), is(not(sut1.hashCode())));
    }

    @Test
    public void equals() {
        final JvfsOptions sut1 = JvfsOptions.builder().capacity("1M").create();
        final JvfsOptions sut2 = JvfsOptions.builder().capacity("1M").create();
        final JvfsOptions sut3 = JvfsOptions.builder().capacity("2M").create();

        assertThat(sut1.equals(null), is(false));
        assertThat(sut1.equals(""), is(false));

        assertThat(sut1.equals(sut1), is(true));
        assertThat(sut1.equals(sut2), is(true));
        assertThat(sut2.equals(sut1), is(true));
        assertThat(sut2.equals(sut2), is(true));

        assertThat(sut3.equals(sut3), is(true));
        assertThat(sut3.equals(sut2), is(false));
        assertThat(sut3.equals(sut1), is(false));
    }

    @Test
    public void testToString() {
        assertThat(JvfsOptions.DEFAULT.toString(), is(equalTo("JvfsOptions{id=, capacity=0, readonly=false}")));
    }
}
