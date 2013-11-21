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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.spi.FileSystemProvider;
import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link JvfsFileSystems}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileSystemsTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    @Test
    public void dirsep() {
        assertThat(JvfsFileSystems.DIR_SEP, is(equalTo(System.getProperty("file.separator"))));
    }

    @Test
    public void protocol() {
        assertThat(JvfsFileSystems.PROTOCOL_FILE, is(equalTo("file")));
        assertThat(JvfsFileSystems.PROTOCOL_JVFS, is(equalTo("jvfs")));
    }

    @Test
    public void newProvider() {
        final FileSystemProvider fsp = JvfsFileSystems.newUnixProvider();
        assertThat(fsp, is(not(nullValue())));
        assertThat(fsp, is(not(sameInstance(JvfsFileSystems.newUnixProvider()))));
    }

    @Test
    public void getRootUri() throws URISyntaxException {
        assertThat(JvfsFileSystems.createRootUri(), is(equalTo(new URI("jvfs:///"))));
    }

    @Test
    public void registerAsDefault() {
        assertThat(System.getProperty(
            "java.nio.file.spi.DefaultFileSystemProvider"),
            is(anyOf(nullValue(), equalTo((Object) ""))));
        JvfsFileSystems.registerUnixAsDefault();
        assertThat(System.getProperty("java.nio.file.spi.DefaultFileSystemProvider"),
            is(equalTo("de.weltraumschaf.jvfs.impl.JvfsFileSystemProviderOld")));
        JvfsFileSystems.unregisterDefault();
        assertThat(System.getProperty(
            "java.nio.file.spi.DefaultFileSystemProvider"),
            is(anyOf(nullValue(), equalTo((Object) ""))));
    }
}
