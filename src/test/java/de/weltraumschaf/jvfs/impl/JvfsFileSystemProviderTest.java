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
import de.weltraumschaf.jvfs.JvfsOptions;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link JvfsFileSystemProviderOld}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileSystemProviderTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON
    private final JvfsFileSystemProvider sut = new JvfsFileSystemProvider();

    @Test
    public void getScheme() throws URISyntaxException, IOException {
        assertThat(sut.getScheme(), is(equalTo("jvfs")));
    }

    @Test
    public void newFileSystem_callsCheckUri() throws URISyntaxException, IOException{
        final JvfsFileSystemProvider spy = spy(sut);
        final URI uri = new URI("jvfs:///foo/bar");
        spy.newFileSystem(uri, JvfsOptions.DEFAULT.getEnv());
        verify(spy, times(1)).checkUri(uri);
    }

    @Test
    public void newFileSystem() throws URISyntaxException, IOException {
        final JvfsOptions opts = JvfsOptions.builder().capacity("10M").readonly(false).create();
        sut.newFileSystem(new URI("jvfs:///foo/bar"), opts.getEnv());
    }

    @Test
    public void getFileSystem_callsCheckUri() throws URISyntaxException {
        final JvfsFileSystemProvider spy = spy(sut);
        final URI uri = new URI("jvfs:///foo/bar");
        spy.getFileSystem(uri);
        verify(spy, atLeast(1)).checkUri(uri);
    }

    @Test
    public void newAsynchronousFileChannel_isUnsupportedOperation() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.newAsynchronousFileChannel(null, null, null, (FileAttribute<?>[]) null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newFileChannel() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        final Set<OpenOption> opts = JvfsCollections.newSet();
        final FileAttribute<Object> attrs = mock(FileAttribute.class);
        sut.newFileChannel(path, opts, attrs);
        verify(path, times(1)).newFileChannel(opts, attrs);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newByteChannel() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        final Set<OpenOption> opts = JvfsCollections.newSet();
        final FileAttribute<Object> attrs = mock(FileAttribute.class);
        sut.newByteChannel(path, opts, attrs);
        verify(path, times(1)).newByteChannel(opts, attrs);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newDirectoryStream() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        final DirectoryStream.Filter<Path> filter = mock(DirectoryStream.Filter.class);
        sut.newDirectoryStream(path, filter);
        verify(path, times(1)).newDirectoryStream(filter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createDirectory() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        final FileAttribute<Object> attrs = mock(FileAttribute.class);
        sut.createDirectory(path, attrs);
        verify(path, times(1)).createDirectory(attrs);
    }

    @Test
    public void delete() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        sut.delete(path);
        verify(path, times(1)).delete();
    }

    @Test
    public void copy() throws IOException {
        final JvfsPath src = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        final JvfsPath dst = new JvfsPath(mock(JvfsFileSystem.class));
        sut.copy(src, dst);
        verify(src, times(1)).copy(dst);
    }

    @Test
    public void move() throws IOException {
        final JvfsPath src = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        final JvfsPath dst = new JvfsPath(mock(JvfsFileSystem.class));
        sut.move(src, dst);
        verify(src, times(1)).move(dst);
    }

    @Test
    public void isSameFile() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        final JvfsPath path2 = new JvfsPath(mock(JvfsFileSystem.class));
        sut.isSameFile(path, path2);
        verify(path, times(1)).isSameFile(path2);
    }

    @Test
    public void isHidden() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        sut.isHidden(path);
        verify(path, times(1)).isHidden();
    }

    @Test
    public void getFileStore() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        sut.getFileStore(path);
        verify(path, times(1)).getFileStore();
    }

    @Test
    public void checkAccess() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        sut.checkAccess(path);
        verify(path, times(1)).checkAccess();
    }

    @Test
    public void getFileAttributeView() throws IOException {
        final Path path = new JvfsPath(mock(JvfsFileSystem.class));
        final FileAttributeView view = sut.getFileAttributeView(path, null);
        assertThat(view, is(not(nullValue())));
        assertThat(view, is(instanceOf(JvfsFileAttributeView.class)));
        assertThat(((JvfsFileAttributeView) view).getPath(), is(sameInstance(path)));
    }

    @Test
    public void readAttributes_returnsBasicFileAttribute_nullOnBadType() throws IOException {
        final Path path = new JvfsPath(mock(JvfsFileSystem.class));
        assertThat(sut.readAttributes(path, BasicFileAttributesStub.class), is(nullValue()));
    }

    @Test
    public void readAttributes_returnsBasicFileAttribute() throws IOException {
        final JvfsFileSystem fs = mock(JvfsFileSystem.class);
        final JvfsPath path = spy(new JvfsPath(fs));
        final JvfsFileAttributes expectedAttrs = new JvfsFileAttributes(JvfsFileEntry.newFile("/"));
        when(fs.getFileAttributes(anyString())).thenReturn(expectedAttrs);
        final BasicFileAttributes attrs = sut.readAttributes(path, BasicFileAttributes.class);
        assertThat(attrs, is(not(nullValue())));
        assertThat(attrs, is(instanceOf(JvfsFileAttributes.class)));
        assertThat(attrs, is(sameInstance((BasicFileAttributes) expectedAttrs)));
        verify(path, times(1)).getAttributes();
    }

    @Test
    @Ignore("Not supported yet.")
    public void readAttributes_returnsMap() throws IOException {
        final Map<String, Object> expectedAttrs = JvfsCollections.newMap();
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        when(path.readAttributes("foo")).thenReturn(expectedAttrs);
        final Map<String, Object> attrs = sut.readAttributes(path, "foo");
        assertThat(attrs, is(not(nullValue())));
        assertThat(attrs, is(sameInstance(expectedAttrs)));
        verify(path, times(1)).readAttributes("foo");
    }

    @Test
    @Ignore("Not supported yet.")
    public void setAttribute() throws IOException {
        final JvfsPath path = spy(new JvfsPath(mock(JvfsFileSystem.class)));
        sut.setAttribute(path, "foo", "bar");
        verify(path, times(1)).setAttribute("foo", "bar");
    }

    @Test @Ignore
    public void getPath() {

    }

    @Test @Ignore
    public void checkUri() {

    }

    @Test
    public void toJvfsPath() {
        thrown.expect(ProviderMismatchException.class);
        JvfsFileSystemProvider.toJvfsPath(mock(Path.class));
    }

    private abstract static class BasicFileAttributesStub implements BasicFileAttributes {
        // Empty stub to test other type.
    }
}
