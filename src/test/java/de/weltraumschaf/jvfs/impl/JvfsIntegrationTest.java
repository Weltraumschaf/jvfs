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

import de.weltraumschaf.jvfs.JvfsCollections;
import de.weltraumschaf.jvfs.JvfsFileSystems;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests JVFS via the NIO API.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsIntegrationTest {

    @Test
    public void createWriteAndReadFiles() throws URISyntaxException, IOException {
        final Path foo = Paths.get(URI.create("jvfs:///tmp/foo"));
        assertThat(foo, is(instanceOf(JvfsPath.class)));
        Files.createFile(foo);
        final Path bar = Paths.get(URI.create("jvfs:///tmp/bar"));
        assertThat(bar, is(instanceOf(JvfsPath.class)));
        Files.createFile(bar);
        final Path baz = Paths.get(URI.create("jvfs:///tmp/baz"));
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
    public void createFile() throws IOException {
        final Path uri = Paths.get(URI.create("jvfs:///tmp/foo"));
        final Path foo = Files.createFile(uri, JvfsFileSystems.createFileAttribute(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE,
            PosixFilePermission.OWNER_EXECUTE));
        assertThat(foo.isAbsolute(), is(true));
        assertThat(Files.isDirectory(foo), is(false));
        assertThat(Files.isReadable(foo), is(true));
        assertThat(Files.isWritable(foo), is(true));
        assertThat(Files.isExecutable(foo), is(true));

        final Path parent = foo.getParent();
        assertThat(parent.toString(), is(equalTo("/tmp")));
        assertThat(parent.isAbsolute(), is(true));
        assertThat(Files.isDirectory(parent), is(true));
        assertThat(Files.isReadable(parent), is(true));
        assertThat(Files.isWritable(parent), is(true));
        assertThat(Files.isExecutable(parent), is(true));
    }

    @Test
    @Ignore
    public void createDirecotries() throws IOException {
        final Path foo = Files.createDirectories(Paths.get(URI.create("jvfs:///tmp/foo")));
        assertThat(foo.isAbsolute(), is(true));
        assertThat(Files.isDirectory(foo), is(true));
        assertThat(Files.isReadable(foo), is(true));
        assertThat(Files.isWritable(foo), is(true));
        assertThat(Files.isExecutable(foo), is(true));
    }

    @Test
    @Ignore
    public void deleteFiles() {
    }

    @Test
    @Ignore
    public void copyFiles() {
    }

    @Test
    @Ignore
    public void moveFiles() {
    }

    @Test
    @Ignore
    public void fileAttributes() {
    }

}
