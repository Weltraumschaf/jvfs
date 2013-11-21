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

import de.weltraumschaf.jvfs.JvfsFileSystems;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.IOUtils;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import org.junit.After;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the whole setup.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsIntegrationTest {

    @Before
    public void registerDefaultProvider() {
//        JvfsFileSystems.registerUnixAsDefault();
    }

    @After
    public void unregisterDefaultProvider() {
//        JvfsFileSystems.unregisterDefault();
    }

    @Test
    @Ignore
    public void createWriteAndReadFiles() throws URISyntaxException, IOException {
        assertThat(
            System.getProperty(JvfsFileSystems.IMPLEMENTATION_PROPERTY_NAME),
            is(equalTo(JvfsFileSystems.IMPLEMENTATION_CLASS_NAME)));
        final Path foo = Paths.get(URI.create("file:///tmp/foo"));
        assertThat(foo, is(instanceOf(JvfsPath.class)));
        Files.createFile(foo);
        final Path bar = Paths.get(URI.create("file:///tmp/bar"));
        assertThat(bar, is(instanceOf(JvfsPath.class)));
        Files.createFile(bar);
        final Path baz = Paths.get(URI.create("file:///tmp/baz"));
        assertThat(baz, is(instanceOf(JvfsPath.class)));
        Files.createFile(baz);

        OutputStream out = Files.newOutputStream(foo);
        IOUtils.write("foo", out);
        IOUtils.closeQuietly(out);
        out = Files.newOutputStream(bar);
        IOUtils.write("bar", out);
        IOUtils.closeQuietly(out);
        out = Files.newOutputStream(baz);
        IOUtils.write("baz", out);
        IOUtils.closeQuietly(out);

        InputStream in = Files.newInputStream(foo);
        assertThat(IOUtils.toString(in), is("foo"));
        in = Files.newInputStream(bar);
        assertThat(IOUtils.toString(in), is("bar"));
        in = Files.newInputStream(baz);
        assertThat(IOUtils.toString(in), is("baz"));
    }

    @Test
    @Ignore
    public void jvfsProtocol() throws URISyntaxException, IOException {
        final Path path = Paths.get(new URI("jvfs:///foo"));
        assertThat(path, is(instanceOf(JvfsPath.class)));
        Files.createFile(path);
    }
}
