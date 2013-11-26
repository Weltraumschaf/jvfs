/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.weltraumschaf.jvfs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link JvfsCollections}.
 *
 * @author Sven.Strittmatter
 */
public class JvfsCollectionsTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    @Test
    public void invokeConstructorByReflectionThrowsException() throws Exception {
        thrown.expect(InvocationTargetException.class);
        assertThat(JvfsCollections.class.getDeclaredConstructors().length, is(1));
        final Constructor<JvfsCollections> ctor = JvfsCollections.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        ctor.newInstance();
    }

    @Test
    public void newList() {
            final List<Object> l = JvfsCollections.newList();
        assertThat(l, is(not(nullValue())));
        assertThat(l, is(not(sameInstance(JvfsCollections.newList()))));
        assertThat(l, is(not(sameInstance(JvfsCollections.newList()))));
        assertThat(l, is(not(sameInstance(JvfsCollections.newList()))));
    }

    @Test
    public void newList_withSize() {
        final List<Object> l = JvfsCollections.newList(1);
        assertThat(l, is(not(nullValue())));
        assertThat(l, is(not(sameInstance(JvfsCollections.newList(1)))));
        assertThat(l, is(not(sameInstance(JvfsCollections.newList(1)))));
        assertThat(l, is(not(sameInstance(JvfsCollections.newList(1)))));
    }

    @Test
    public void newList_copy() {
        final List<String> l = JvfsCollections.newList();
        l.add("foo");
        l.add("bar");
        l.add("baz");

        final List<String> copy = JvfsCollections.newList(l);
        assertThat(copy, is(not(sameInstance(l))));
        assertThat(copy, contains("foo", "bar", "baz"));
    }

    @Test
    public void newMap() {
        final Map<Object, Object> m = JvfsCollections.newMap();
        assertThat(m, is(not(nullValue())));
        assertThat(m, is(not(sameInstance(JvfsCollections.newMap()))));
        assertThat(m, is(not(sameInstance(JvfsCollections.newMap()))));
        assertThat(m, is(not(sameInstance(JvfsCollections.newMap()))));
    }

    @Test
    public void newSet() {
        final Set<Object> s = JvfsCollections.newSet();
        assertThat(s, is(not(nullValue())));
        assertThat(s, is(not(sameInstance(JvfsCollections.newSet()))));
        assertThat(s, is(not(sameInstance(JvfsCollections.newSet()))));
        assertThat(s, is(not(sameInstance(JvfsCollections.newSet()))));
    }

    @Test
    public void asList() {
        List<String> l = JvfsCollections.asList();
        assertThat(l, is(not(nullValue())));
        assertThat(l.size(), is(0));

        l = JvfsCollections.asList("foo", "bar", "baz");
        assertThat(l, is(not(nullValue())));
        assertThat(l, contains("foo", "bar", "baz"));
    }
}
