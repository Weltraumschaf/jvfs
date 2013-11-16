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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.IOUtils;
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
        JvfsFileSystems.registerUnixAsDefault();
    }

    @After
    public void unregisterDefaultProvider() {
        JvfsFileSystems.unregisterDefault();
    }

    @Test
    public void testSomeMethod() throws URISyntaxException, IOException {
        final Path foo = Paths.get(URI.create("file:///tmp/foo"));
        Files.createFile(foo);
        final Path bar = Paths.get(URI.create("file:///tmp/bar"));
        Files.createFile(bar);
        final Path baz = Paths.get(URI.create("file:///tmp/baz"));
        Files.createFile(baz);

//        final OutputStream out = Files.newOutputStream(foo);
//        IOUtils.write("hello world", out);
//        IOUtils.closeQuietly(out);
//        final InputStream in = Files.newInputStream(foo);
//        assertThat(IOUtils.toString(in), is("hello world"));
    }
}