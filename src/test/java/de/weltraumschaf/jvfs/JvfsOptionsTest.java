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

import java.util.Arrays;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link JvfsOptions}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsOptionsTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    @Test
    public void defaultOptions() {
        assertThat(JvfsOptions.DEFAULT.getCapacity(), is(equalTo(JvfsQuantity.forValue(0L))));
        assertThat(JvfsOptions.DEFAULT.isReadonly(), is(false));
        assertThat(JvfsOptions.DEFAULT.identifier(), is(equalTo("")));
    }

    @Test
    public void withEmptyMap() {
        final JvfsOptions sut = new JvfsOptions(JvfsCollections.<String, Object>newMap());
        assertThat(sut.getCapacity(), is(equalTo(JvfsQuantity.forValue(0L))));
        assertThat(sut.isReadonly(), is(false));
        assertThat(sut.identifier(), is(equalTo("")));
    }

    @Test
    public void createByBuilder() {
        final JvfsOptions opts = JvfsOptions.builder()
                .readonly(true)
                .capacity("1k")
                .identifier("foo")
                .create();

        assertThat(opts.isReadonly(), is(true));
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(1024L))));
        assertThat(opts.identifier(), is(equalTo("foo")));
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

        //CHECKSTYLE:OFF
        assertThat(sut1.equals(null), is(false));
        assertThat(sut1.equals(""), is(false));
        //CHECKSTYLE:ON

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

    @Test
    public void isReadOnly_string() {
        Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.READONLY.key(), (Object) "true");
        JvfsOptions sut = new JvfsOptions(env);
        assertThat(sut.isReadonly(), is(true));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.READONLY.key(), (Object) "false");
        sut = new JvfsOptions(env);
        assertThat(sut.isReadonly(), is(false));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.READONLY.key(), (Object) "snafu");
        sut = new JvfsOptions(env);
        assertThat(sut.isReadonly(), is(false));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.READONLY.key(), (Object) "1");
        sut = new JvfsOptions(env);
        assertThat(sut.isReadonly(), is(false));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.READONLY.key(), (Object) "");
        sut = new JvfsOptions(env);
        assertThat(sut.isReadonly(), is(false));
    }

    @Test
    public void isReadOnly_boolean() {
        Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.READONLY.key(), (Object) true);
        JvfsOptions sut = new JvfsOptions(env);
        assertThat(sut.isReadonly(), is(true));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.READONLY.key(), (Object) false);
        sut = new JvfsOptions(env);
        assertThat(sut.isReadonly(), is(false));

        env.put(JvfsOptions.Option.READONLY.key(), (Object) Boolean.TRUE);
        sut = new JvfsOptions(env);
        assertThat(sut.isReadonly(), is(true));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.READONLY.key(), (Object) Boolean.FALSE);
        sut = new JvfsOptions(env);
        assertThat(sut.isReadonly(), is(false));
    }

    @Test
    public void isReadOnly_elseThrowsException() {
        final Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.READONLY.key(), (Object) 23L);
        final JvfsOptions sut = new JvfsOptions(env);
        thrown.expect(IllegalArgumentException.class);
        sut.isReadonly();
    }

    @Test
    public void isReadOnly_nullThrowsException() {
        final Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.READONLY.key(), (Object) null);
        final JvfsOptions sut = new JvfsOptions(env);
        thrown.expect(IllegalArgumentException.class);
        sut.isReadonly();
    }

    @Test
    public void getCapacity_capacity() {
        final Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), (Object) JvfsQuantity.forValue(23L));
        final JvfsOptions sut = new JvfsOptions(env);

        assertThat(sut.getCapacity(), is(equalTo(JvfsQuantity.forValue(23L))));
    }

    @Test
    public void getCapacity_string() {
        final Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), (Object) "23");
        final JvfsOptions sut = new JvfsOptions(env);
        assertThat(sut.getCapacity(), is(equalTo(JvfsQuantity.forValue(23L))));
    }

    @Test
    public void getCapacity_long() {
        final Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), (Object) 23L);
        final JvfsOptions sut = new JvfsOptions(env);
        assertThat(sut.getCapacity(), is(equalTo(JvfsQuantity.forValue(23L))));
    }

    @Test
    public void getCapacity_elseThrowsException() {
        final Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), (Object) new Object());
        final JvfsOptions sut = new JvfsOptions(env);
        thrown.expect(IllegalArgumentException.class);
        sut.getCapacity();
    }

    @Test
    public void getCapacity_nullThrowsException() {
        final Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.CAPACITY.key(), (Object) null);
        final JvfsOptions sut = new JvfsOptions(env);
        thrown.expect(IllegalArgumentException.class);
        sut.getCapacity();
    }

    @Test
    public void identifier() {
        Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.ID.key(), (Object) "name");
        JvfsOptions sut = new JvfsOptions(env);
        assertThat(sut.identifier(), is(equalTo("name")));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.ID.key(), (Object) true);
        sut = new JvfsOptions(env);
        assertThat(sut.identifier(), is(equalTo("true")));

        env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.ID.key(), (Object) 42);
        sut = new JvfsOptions(env);
        assertThat(sut.identifier(), is(equalTo("42")));
    }

    @Test
    public void identifier_nullThrowsException() {
        final Map<String, Object> env = JvfsCollections.newMap();
        env.put(JvfsOptions.Option.ID.key(), (Object) null);
        final JvfsOptions sut = new JvfsOptions(env);
        assertThat(sut.identifier(), is(equalTo("null")));
    }

    @Test
    public void optionNames() {
        assertThat(Arrays.asList(JvfsOptions.Option.values()), containsInAnyOrder(
                JvfsOptions.Option.CAPACITY,
                JvfsOptions.Option.READONLY,
                JvfsOptions.Option.ID
        ));
        assertThat(JvfsOptions.Option.CAPACITY.key(), is(equalTo("capacity")));
        assertThat(JvfsOptions.Option.READONLY.key(), is(equalTo("readonly")));
        assertThat(JvfsOptions.Option.ID.key(), is(equalTo("id")));
    }

}
