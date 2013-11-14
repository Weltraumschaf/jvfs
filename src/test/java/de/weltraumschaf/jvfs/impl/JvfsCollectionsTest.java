/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.weltraumschaf.jvfs.impl;

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
    public void newArrayList() {
        final List<Object> l = JvfsCollections.newArrayList();
        assertThat(l, is(not(nullValue())));
        assertThat(l, is(not(sameInstance(JvfsCollections.newArrayList()))));
        assertThat(l, is(not(sameInstance(JvfsCollections.newArrayList()))));
        assertThat(l, is(not(sameInstance(JvfsCollections.newArrayList()))));
    }

    @Test
    public void newArrayList_withSize() {
        final List<Object> l = JvfsCollections.newArrayList(1);
        assertThat(l, is(not(nullValue())));
        assertThat(l, is(not(sameInstance(JvfsCollections.newArrayList(1)))));
        assertThat(l, is(not(sameInstance(JvfsCollections.newArrayList(1)))));
        assertThat(l, is(not(sameInstance(JvfsCollections.newArrayList(1)))));
    }

    @Test
    public void newHashMap() {
        final Map<Object, Object> m = JvfsCollections.newHashMap();
        assertThat(m, is(not(nullValue())));
        assertThat(m, is(not(sameInstance(JvfsCollections.newHashMap()))));
        assertThat(m, is(not(sameInstance(JvfsCollections.newHashMap()))));
        assertThat(m, is(not(sameInstance(JvfsCollections.newHashMap()))));
    }

    @Test
    public void newHashSet() {
        final Set<Object> s = JvfsCollections.newHashSet();
        assertThat(s, is(not(nullValue())));
        assertThat(s, is(not(sameInstance(JvfsCollections.newHashSet()))));
        assertThat(s, is(not(sameInstance(JvfsCollections.newHashSet()))));
        assertThat(s, is(not(sameInstance(JvfsCollections.newHashSet()))));
    }

}
