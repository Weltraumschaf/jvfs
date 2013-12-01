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
import de.weltraumschaf.jvfs.JvfsOptions;
import java.io.IOException;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link JvfsFileSystem}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileSystemTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON
    private final JvfsOptions opts = JvfsOptions.builder().capacity("1M").create();
    private final JvfsFileSystemProvider provider = new JvfsFileSystemProvider();
    private final JvfsFileSystem sut = new JvfsFileSystem(provider, opts);

    @Test
    public void provider() {
        assertThat(sut.provider(), is(sameInstance((FileSystemProvider) provider)));
    }

    @Test
    public void openClose() throws IOException {
        assertThat(sut.isOpen(), is(true));
        sut.close();
        assertThat(sut.isOpen(), is(false));
    }

    @Test
    public void isReadOnly() {
        assertThat(sut.isReadOnly(), is(opts.isReadonly()));
    }

    @Test
    public void getSeparator() {
        assertThat(sut.getSeparator(), is(equalTo(JvfsFileSystems.DIR_SEP)));
    }

    @Test
    public void getRootDirectories_throwsExceptionIfClosed() throws IOException {
        sut.close();
        thrown.expect(ClosedFileSystemException.class);
        sut.getRootDirectories();
    }

    @Test
    @Ignore
    public void getRootDirectories() {

    }

    @Test
    public void getFileStores_throwsExceptionIfClosed() throws IOException {
        sut.close();
        thrown.expect(ClosedFileSystemException.class);
        sut.getFileStores();
    }

    @Test
    public void getFileStores() {
        assertThat((List<FileStore>) sut.getFileStores(), hasSize(1));
        assertThat(((List<FileStore>) sut.getFileStores()).get(0), is(instanceOf(JvfsFileStore.class)));
    }

    @Test
    public void supportedFileAttributeViews_throwsExceptionIfClosed() throws IOException {
        sut.close();
        thrown.expect(ClosedFileSystemException.class);
        sut.supportedFileAttributeViews();
    }

    @Test
    public void supportedFileAttributeViews() {
        assertThat(sut.supportedFileAttributeViews(), hasSize(1));
        assertThat(sut.supportedFileAttributeViews(), hasItem(JvfsFileAttributeView.BASIC_VIEW_NAME));
    }

    @Test
    public void getUserPrincipalLookupService_notSupported() {
        thrown.expect(UnsupportedOperationException.class);
        sut.getUserPrincipalLookupService();
    }

    @Test
    public void newWatchService_notSupported() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.newWatchService();
    }

    @Test
    public void getPath_throwsExceptionIfClosed() throws IOException {
        sut.close();
        thrown.expect(ClosedFileSystemException.class);
        sut.getPath("foo", "bar");
    }

    @Test
    public void getPath() {
        Path path = sut.getPath("/foo");
        assertThat(path.toString(), is(equalTo("/foo")));
        assertThat(path.getFileSystem(), is(sameInstance((FileSystem) sut)));

        path = sut.getPath("/foo", "bar", "baz");
        assertThat(path.toString(), is(equalTo("/foo/bar/baz")));
        assertThat(path.getFileSystem(), is(sameInstance((FileSystem) sut)));
    }

    @Test
    public void getPathMatcher_glob() {
        final String regex = "glob:*.java";
        final PathMatcher m = JvfsPathMatcher.newMatcher(regex);
        assertThat(sut.getPathMatcher(regex), is(equalTo(m)));
    }

    @Test
    public void getPathMatcher_java() {
        final String regex = "regex:^*\\.java$";
        final PathMatcher m = JvfsPathMatcher.newMatcher(regex);
        assertThat(sut.getPathMatcher(regex), is(equalTo(m)));
    }

    @Test
    public void add() {
        final JvfsFileEntry file = JvfsFileEntry.newFile("/foo/bar/baz");
        file.setReadable(true);
        file.setWritable(true);
        file.setExecutable(true);
        sut.add(file);

        final JvfsFileEntry root = sut.get("/");
        assertThat(root.isDirectory(), is(true));
        assertThat(root.isReadable(), is(true));
        assertThat(root.isWritable(), is(true));
        assertThat(root.isExecutable(), is(true));
        final JvfsFileEntry foo = sut.get("/foo");
        assertThat(foo.isDirectory(), is(true));
        assertThat(foo.isReadable(), is(true));
        assertThat(foo.isWritable(), is(true));
        assertThat(foo.isExecutable(), is(true));
        final JvfsFileEntry bar = sut.get("/foo/bar");
        assertThat(bar.isDirectory(), is(true));
        assertThat(bar.isReadable(), is(true));
        assertThat(bar.isWritable(), is(true));
        assertThat(bar.isExecutable(), is(true));
        assertThat(sut.get("/foo/bar/baz"), is(sameInstance(file)));
    }
}
