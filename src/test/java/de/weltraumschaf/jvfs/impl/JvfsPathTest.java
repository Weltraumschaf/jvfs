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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link JvfsPath}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsPathTest {

    private static final String DIR_SEP = JvfsFileSystems.DIR_SEP;
    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON
    private final JvfsFileSystem fs = mock(JvfsFileSystem.class);

    private String createPath(final boolean absolute, final String ... names) {
        final StringBuilder buffer = new StringBuilder();

        if (absolute) {
            buffer.append(DIR_SEP);
        }

        boolean first = true;

        for (final String name : names) {
            if (!first) {
                buffer.append(DIR_SEP);
            }

            buffer.append(name);
            first = false;
        }

        return buffer.toString();
    }

    @Test
    public void createPath() {
        assertThat(createPath(true, "foo", "bar", "baz"),
            is(equalTo(DIR_SEP + "foo" + DIR_SEP + "bar" + DIR_SEP + "baz")));
        assertThat(createPath(false, "foo", "bar", "baz"),
            is(equalTo("foo" + DIR_SEP + "bar" + DIR_SEP + "baz")));
    }

    @Test
    public void getFileSystem() {
        assertThat(new JvfsPath("", fs).getFileSystem(), is(sameInstance((FileSystem) fs)));
    }

    @Test
    public void isAbsolute() {
        assertThat(new JvfsPath(createPath(true, "foo", "bar"), fs).isAbsolute(), is(true));
        assertThat(new JvfsPath(createPath(false, "foo", "bar"), fs).isAbsolute(), is(false));
    }

    @Test
    public void getRoot() {
        assertThat(new JvfsPath(createPath(true, "foo", "bar"), fs).getRoot(),
            is(equalTo((Path) new JvfsPath(DIR_SEP, fs))));
        assertThat(new JvfsPath(createPath(false, "foo", "bar"), fs).getRoot(), is(nullValue()));
    }

    @Test
    public void getFileName() {
        assertThat(new JvfsPath("", fs).getFileName(), is(nullValue()));
        assertThat(new JvfsPath(DIR_SEP, fs).getFileName(), is(nullValue()));
        assertThat(new JvfsPath(createPath(true, "foo", "bar"), fs).getFileName(),
            is(equalTo((Path) new JvfsPath("bar", fs))));
    }

    @Test
    public void getParent() {
        assertThat(new JvfsPath("", fs).getParent(), is(nullValue()));
        assertThat(new JvfsPath("foo", fs).getParent(), is(nullValue()));
        assertThat(new JvfsPath(DIR_SEP, fs).getParent(), is(nullValue()));
        assertThat(new JvfsPath(createPath(true, "foo"), fs).getParent(), is((Path) new JvfsPath(DIR_SEP, fs)));
        assertThat(new JvfsPath(createPath(false, "foo", "bar"), fs).getParent(), is((Path) new JvfsPath("foo", fs)));
        assertThat(new JvfsPath(createPath(true, "foo", "bar"), fs).getParent(),
            is((Path) new JvfsPath(DIR_SEP + "foo", fs)));
        assertThat(new JvfsPath(createPath(true, "foo", "bar", "baz", "snafu"), fs).getParent(),
            is((Path) new JvfsPath(DIR_SEP + "foo" + DIR_SEP + "bar" + DIR_SEP + "baz", fs)));
    }

    @Test
    public void getNameCount() {
        assertThat(new JvfsPath("", fs).getNameCount(), is(0));
        assertThat(new JvfsPath("foo", fs).getNameCount(), is(1));
        assertThat(new JvfsPath(DIR_SEP, fs).getNameCount(), is(0));
        assertThat(new JvfsPath(createPath(false, "foo", "bar"), fs).getNameCount(), is(2));
        assertThat(new JvfsPath(createPath(true, "foo", "bar"), fs).getNameCount(), is(2));
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).getNameCount(), is(3));
        assertThat(new JvfsPath(createPath(true, "foo", "bar", "baz"), fs).getNameCount(), is(3));
    }

    @Test
    public void getName() {
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).getName(0),
            is(equalTo((Path) new JvfsPath(createPath(false, "foo"), fs))));
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).getName(1),
            is(equalTo((Path) new JvfsPath(createPath(false, "bar"), fs))));
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).getName(2),
            is(equalTo((Path) new JvfsPath(createPath(false, "baz"), fs))));
    }

    @Test
    public void subpath_throwsExceptionIfBeginIndexLessThanZero() {
        thrown.expect(IllegalArgumentException.class);
        new JvfsPath(createPath(false, "foo"), fs).subpath(-1, 0);
    }

    @Test
    public void subpath_throwsExceptionIfEndIndexLessThanZero() {
        thrown.expect(IllegalArgumentException.class);
        new JvfsPath(createPath(false, "foo"), fs).subpath(0, -1);
    }

    @Test
    public void subpath_throwsExceptionIfBeginEqualEndIndex() {
        thrown.expect(IllegalArgumentException.class);
        new JvfsPath(createPath(false, "foo"), fs).subpath(0, 0);
    }

    @Test
    public void subpath() {
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).subpath(0, 1),
            is(equalTo((Path) new JvfsPath(createPath(false, "foo"), fs))));
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).subpath(0, 2),
            is(equalTo((Path) new JvfsPath(createPath(false, "foo", "bar"), fs))));
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).subpath(0, 3),
            is(equalTo((Path) new JvfsPath(createPath(false, "foo", "bar", "baz"), fs))));
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).subpath(1, 3),
            is(equalTo((Path) new JvfsPath(createPath(false, "bar", "baz"), fs))));
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).subpath(2, 3),
            is(equalTo((Path) new JvfsPath(createPath(false, "baz"), fs))));
    }

    @Test
    public void startsWith_String() {
        final Path sut = new JvfsPath(createPath(false, "foo", "bar", "baz"), fs);
        assertThat(sut.startsWith("foo"), is(true));
        assertThat(sut.startsWith("foo" + DIR_SEP), is(true));
        assertThat(sut.startsWith("foo" + DIR_SEP + "bar"), is(true));
        assertThat(sut.startsWith("foo" + DIR_SEP + "bar" + DIR_SEP), is(true));
        assertThat(sut.startsWith("foo" + DIR_SEP + "bar" + DIR_SEP + "baz"), is(true));
        assertThat(sut.startsWith("foo" + DIR_SEP + "bar" + DIR_SEP + "baz" + DIR_SEP), is(true));
        assertThat(sut.startsWith("foo" + DIR_SEP + "bar" + DIR_SEP + "baz" + DIR_SEP + "snafu"), is(false));
        assertThat(sut.startsWith("bar"), is(false));
    }

    @Test
    public void startsWith_Path() {
        final Path sut = new JvfsPath(createPath(false, "foo", "bar", "baz"), fs);
        assertThat(sut.startsWith(new JvfsPath(createPath(false, "foo"), fs)), is(true));
        assertThat(sut.startsWith(new JvfsPath(createPath(false, "foo", "bar"), fs)), is(true));
        assertThat(sut.startsWith(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs)), is(true));
        assertThat(sut.startsWith(new JvfsPath(createPath(false, "bar"), fs)), is(false));
    }

    @Test
    public void endsWith_String() {
        final Path sut = new JvfsPath(createPath(false, "foo", "bar", "baz"), fs);
        assertThat(sut.endsWith("baz"), is(true));
        assertThat(sut.endsWith("bar" + DIR_SEP + "baz"), is(true));
        assertThat(sut.endsWith("foo" + DIR_SEP + "bar" + DIR_SEP + "baz"), is(true));
        assertThat(sut.endsWith("foo"), is(false));
    }

    @Test
    public void endsWith_Path() {
        final Path sut = new JvfsPath(createPath(false, "foo", "bar", "baz"), fs);
        assertThat(sut.endsWith(new JvfsPath(createPath(false, "baz"), fs)), is(true));
        assertThat(sut.endsWith(new JvfsPath(createPath(false, "bar" + DIR_SEP + "baz"), fs)), is(true));
        assertThat(sut.endsWith(
            new JvfsPath(createPath(false, "foo" + DIR_SEP + "bar" + DIR_SEP + "baz"), fs)),
            is(true));
        assertThat(sut.endsWith(new JvfsPath(createPath(false, "foo"), fs)), is(false));
    }

    @Test
    public void endsWith_Path_differentFileSystems() {
        final Path sut1 = new JvfsPath(createPath(false, "foo", "bar", "baz"), fs);
        final Path sut2 = new JvfsPath(createPath(false, "foo", "bar", "baz"), mock(JvfsFileSystem.class));
        assertThat(sut1.endsWith(sut2), is(false));
    }

    @Test
    public void endsWith_Path_otherLongerThanOwn() {
        final Path sut1 = new JvfsPath(createPath(false, "foo"), fs);
        final Path sut2 = new JvfsPath(createPath(false, "foo", "bar", "baz"), fs);
        assertThat(sut1.endsWith(sut2), is(false));
    }

    @Test
    public void endsWith_Path_differentComponentSize() {
        final Path sut1 = new JvfsPath(createPath(false, "foo", "bar", "baz"), fs);
        final Path sut2 = new JvfsPath(createPath(true, "bar", "baz"), fs);
        assertThat(sut1.endsWith(sut2), is(false));
    }

    @Test
    public void normalize() {
        final Path sut = new JvfsPath(createPath(false, "foo", ".", "bar", "..", "baz"), fs);
        assertThat(sut.normalize(), is(equalTo((Path) new JvfsPath(createPath(false, "foo", "baz"), fs))));
    }

    @Test
    public void resolve_Path() {
        final Path sut = new JvfsPath(createPath(true, "foo"), fs);
        final Path other = new JvfsPath(createPath(true, "bar"), fs);
        assertThat(sut.resolve(other), is(sameInstance(other)));
        assertThat(sut.resolve(new JvfsPath(createPath(false, ""), fs)), is(sameInstance(sut)));
        assertThat(sut.resolve(new JvfsPath(createPath(false, "bar"), fs)),
            is(equalTo((Path) new JvfsPath(createPath(true, "foo", "bar"), fs))));
    }

    @Test
    public void resolve_String() {
        final Path sut = new JvfsPath(createPath(true, "foo"), fs);
        assertThat(sut.resolve(JvfsPath.DIR_SEP + "bar"),
            is(equalTo((Path) new JvfsPath(createPath(true, "bar"), fs))));
        assertThat(sut.resolve(""), is(sameInstance(sut)));
        assertThat(sut.resolve("bar"),
            is(equalTo((Path) new JvfsPath(createPath(true, "foo", "bar"), fs))));
    }

    @Test
    public void resolveSibling_Path() {
        Path sut = new JvfsPath(createPath(true, ""), fs);
        Path other = new JvfsPath(createPath(false, "bar"), fs);
        assertThat(sut.resolveSibling(other), is(sameInstance(other)));

        sut = new JvfsPath(createPath(true, "foo"), fs);
        other = new JvfsPath(createPath(true, "bar"), fs);
        assertThat(sut.resolveSibling(other), is(sameInstance(other)));
        assertThat(sut.resolveSibling(new JvfsPath(createPath(false, ""), fs)),
            is(equalTo((Path) new JvfsPath(createPath(true, ""), fs))));

        assertThat(sut.resolveSibling(new JvfsPath(createPath(false, "bar"), fs)),
            is(equalTo((Path) new JvfsPath(createPath(true, "bar"), fs))));
    }

    @Test
    public void resolveSibling_String() {
        Path sut = new JvfsPath(createPath(true, ""), fs);
        assertThat(sut.resolveSibling("bar"),
            is(equalTo((Path) new JvfsPath(createPath(false, "bar"), fs))));

        sut = new JvfsPath(createPath(true, "foo"), fs);
        assertThat(sut.resolveSibling(JvfsPath.DIR_SEP + "bar"),
            is(equalTo((Path) new JvfsPath(createPath(true, "bar"), fs))));
        assertThat(sut.resolveSibling(""),
            is(equalTo((Path) new JvfsPath(createPath(true, ""), fs))));

        assertThat(sut.resolveSibling("bar"),
            is(equalTo((Path) new JvfsPath(createPath(true, "bar"), fs))));
    }

    @Test
    public void relativize() {
        Path sut = new JvfsPath(createPath(true, "a", "b"), fs);
        Path other = new JvfsPath(createPath(true, "a", "b", "c", "d"), fs);
        assertThat(sut.relativize(other),
            is(equalTo((Path) new JvfsPath(createPath(false, "c", "d"), fs))));
    }

    @Test
    public void relativize_throwsExceptionIfNotJvfsType() {
        thrown.expect(IllegalArgumentException.class);
        new JvfsPath(createPath(true, "a", "b"), fs).relativize(mock(Path.class));
    }

    @Test
    public void relativize_equalsReturnEmpty() {
        Path sut = new JvfsPath(createPath(true, "a", "b"), fs);
        assertThat(sut.relativize(new JvfsPath(createPath(true, "a", "b"), fs)),
            is(equalTo((Path) new JvfsPath("", fs))));
    }

    @Test
    public void register_3args() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        new JvfsPath(createPath(false, "foo"), fs).register(
            mock(WatchService.class), new WatchEvent.Kind<?>[0], new WatchEvent.Modifier[0]);
    }

    @Test
    public void register_WatchService_WatchEventKindArr() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        new JvfsPath(createPath(false, "foo"), fs).register(
            mock(WatchService.class), new WatchEvent.Kind<?>[0]);
    }

    @Test
    public void toUri() throws URISyntaxException {
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).toUri(),
            is(equalTo(new URI("jvfs:///foo/bar/baz"))));
    }

    @Test
    public void toAbsolutePath() {
        assertThat(
            new JvfsPath(createPath(true, "a", "b"), fs).toAbsolutePath().toString(),
            is(equalTo("/a/b")));
        assertThat(
            new JvfsPath(createPath(false, "a", "b"), fs).toAbsolutePath().toString(),
            is(equalTo("/a/b")));
    }

    @Test
    public void toRealPath() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        new JvfsPath(createPath(true, "a", "b"), fs).toRealPath();
    }

    @Test
    public void toFile() {
        thrown.expect(UnsupportedOperationException.class);
        new JvfsPath(createPath(true, "a", "b"), fs).toFile();
    }

    @Test
    public void iterator() {
        final Path sut = new JvfsPath(createPath(true, "a", "b", "c", "d"), fs);
        final Iterator<Path> it = sut.iterator();

        assertThat(it.hasNext(), is(true));
        assertThat(it.next().toString(), is(equalTo("a")));
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().toString(), is(equalTo("b")));
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().toString(), is(equalTo("c")));
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().toString(), is(equalTo("d")));
        assertThat(it.hasNext(), is(false));
    }

    @Test
    public void compareTo() {
        final Path sut1 = new JvfsPath(createPath(true, "a", "b"), fs);
        final Path sut2 = new JvfsPath(createPath(true, "a", "b"), fs);
        assertThat(sut1.compareTo(sut1), is(0));
        assertThat(sut1.compareTo(sut2), is(0));
        assertThat(sut2.compareTo(sut1), is(0));
        assertThat(sut2.compareTo(sut2), is(0));

        final Path sut3 = new JvfsPath(createPath(true, "c", "d"), fs);
        assertThat(sut1.compareTo(sut3), is(-2));
        assertThat(sut3.compareTo(sut1), is(2));

        final Path sut4 = new JvfsPath(createPath(true, "a", "b", "c", "d"), fs);
        assertThat(sut1.compareTo(sut4), is(-4));
        assertThat(sut4.compareTo(sut1), is(4));

        assertThat(sut3.compareTo(sut4), is(2));
        assertThat(sut4.compareTo(sut3), is(-2));
    }

    @Test
    public void testToString() {
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).toString(),
            is(equalTo(createPath(false, "foo", "bar", "baz"))));
        assertThat(new JvfsPath(createPath(true, "foo", "bar", "baz"), fs).toString(),
            is(equalTo(createPath(true, "foo", "bar", "baz"))));
    }

    @Test
    public void testHashCode() {
        final Path sut1 = new JvfsPath(createPath(true, "foo"), fs);
        final Path sut2 = new JvfsPath(createPath(true, "foo"), fs);
        final Path sut3 = new JvfsPath(createPath(true, "bar"), fs);

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
        final Path sut1 = new JvfsPath(createPath(true, "foo"), fs);
        final Path sut2 = new JvfsPath(createPath(true, "foo"), fs);
        final Path sut3 = new JvfsPath(createPath(true, "bar"), fs);

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

        final Path sut4 = new JvfsPath(createPath(true, "bar"), mock(JvfsFileSystem.class));
        assertThat(sut4.equals(sut4), is(true));
        assertThat(sut3.equals(sut4), is(false));
        assertThat(sut4.equals(sut3), is(false));
    }

    @Test
    public void getAttributes_throwsExceptionIfFileNotExists() throws IOException {
        final JvfsPath sut = new JvfsPath(createPath(true, "bar"), fs);
        when(fs.getFileAttributes(sut.toString())).thenReturn(null);
        thrown.expect(NoSuchFileException.class);
        sut.getAttributes();
    }

    @Test
    public void getAttributes() throws IOException {
        final JvfsPath sut = new JvfsPath(createPath(true, "bar"), fs);
        final JvfsFileAttributes attrs = mock(JvfsFileAttributes.class);
        when(fs.getFileAttributes(sut.toString())).thenReturn(attrs);
        assertThat(sut.getAttributes(), is(sameInstance((BasicFileAttributes) attrs)));
    }

    @Test
    public void readAttributes() throws IOException {
        final JvfsPath sut = new JvfsPath(createPath(true, "bar"), fs);
        when(fs.getFileAttributes(sut.toString()))
            .thenReturn(new JvfsFileAttributes(JvfsFileEntry.newFile(DIR_SEP + "bar")));
        final Map<String, Object> attrs = sut.readAttributes("isDirectory,creationTime,size");
        assertThat(attrs.size(), is(3));
        assertThat(attrs, allOf(
            hasEntry("isDirectory", (Object) false),
            hasEntry("creationTime", (Object) FileTime.from(0L, TimeUnit.SECONDS)),
            hasEntry("size", (Object) 0L)
        ));
    }

    @Test
    public void setAttribute_throwsExceptionIfReadonlyAttribute() throws IOException {
        final JvfsPath sut = new JvfsPath(createPath(true, "bar"), fs);
        thrown.expect(UnsupportedOperationException.class);
        sut.setAttribute("size", 23);
    }

    @Test
    public void setAttribute() throws IOException {
        final JvfsPath sut = spy(new JvfsPath(createPath(true, "bar"), fs));
        final FileTime time1 = FileTime.from(1L, TimeUnit.SECONDS);
        sut.setAttribute("creationTime", time1);
        verify(sut, times(1)).setTimes(null, null, time1);

        final FileTime time2 = FileTime.from(2L, TimeUnit.SECONDS);
        sut.setAttribute("lastAccessTime", time2);
        verify(sut, times(1)).setTimes(null, time2, null);

        final FileTime time3 = FileTime.from(3L, TimeUnit.SECONDS);
        sut.setAttribute("lastModifiedTime", time3);
        verify(sut, times(1)).setTimes(time3, null, null);
    }

}
