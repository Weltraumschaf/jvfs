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
        Map<String, Object> env = JvfsCollections.newHashMap();
        JvfsOptions opts = JvfsOptions.forValue(env);
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(0L))));
        assertThat(opts.isReadonly(), is(false));

        env = JvfsCollections.newHashMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), "1k");
        env.put(JvfsOptions.Option.READONLY.key(), "true");
        opts = JvfsOptions.forValue(env);
        assertThat(opts.isReadonly(), is(true));
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(1024L))));

        env = JvfsCollections.newHashMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), 1024L);
        env.put(JvfsOptions.Option.READONLY.key(), "true");
        opts = JvfsOptions.forValue(env);
        assertThat(opts.isReadonly(), is(true));
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(1024L))));

        env = JvfsCollections.newHashMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), JvfsQuantity.forValue(1024L));
        env.put(JvfsOptions.Option.READONLY.key(), "true");
        opts = JvfsOptions.forValue(env);
        assertThat(opts.isReadonly(), is(true));
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(1024L))));
    }
}
