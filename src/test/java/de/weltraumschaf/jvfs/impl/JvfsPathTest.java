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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.*;

/**
 * Tests for {2link JvfsPath}.
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
    }

    @Test
    public void getNameCount() {
        assertThat(new JvfsPath("", fs).getNameCount(), is(0));
        assertThat(new JvfsPath("foo", fs).getNameCount(), is(1));
        assertThat(new JvfsPath(DIR_SEP, fs).getNameCount(), is(0));
        assertThat(new JvfsPath(createPath(false, "foo", "bar"), fs).getNameCount(), is(2));
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).getNameCount(), is(3));
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
        assertThat(sut.endsWith(new JvfsPath(createPath(false, "foo" + DIR_SEP + "bar" + DIR_SEP + "baz"), fs)), is(true));
        assertThat(sut.endsWith(new JvfsPath(createPath(false, "foo"), fs)), is(false));
    }

    @Test
    public void normalize() {
        final Path sut = new JvfsPath(createPath(false, "foo", ".", "bar", "..", "baz"), fs);
        assertThat(sut.normalize(), is(equalTo((Path) new JvfsPath(createPath(false, "foo", "baz"), fs))));
    }

    @Test @Ignore
    public void resolve_Path() {
    }

    @Test @Ignore
    public void resolve_String() {
    }

    @Test @Ignore
    public void resolveSibling_Path() {
    }

    @Test @Ignore
    public void resolveSibling_String() {
    }

    @Test @Ignore
    public void relativize() {
    }

    @Test
    public void register_3args() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        new JvfsPath(createPath(false, "foo"), fs).register(null, null, null);
    }

    @Test
    public void register_WatchService_WatchEventKindArr() throws Exception {
        thrown.expect(UnsupportedOperationException.class);
        new JvfsPath(createPath(false, "foo"), fs).register(null, null);
    }

    @Test
    public void toUri() throws URISyntaxException {
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).toUri(),
            is(equalTo(new URI("file:///foo/bar/baz"))));
    }

    @Test @Ignore
    public void toAbsolutePath() {
    }

    @Test @Ignore
    public void toRealPath() throws Exception {
    }

    @Test @Ignore
    public void toFile() {
    }

    @Test @Ignore
    public void iterator() {
    }

    @Test @Ignore
    public void compareTo() {
    }

    @Test
    public void testToString() {
        assertThat(new JvfsPath(createPath(false, "foo", "bar", "baz"), fs).toString(),
            is(equalTo(createPath(false, "foo", "bar", "baz"))));
        assertThat(new JvfsPath(createPath(true, "foo", "bar", "baz"), fs).toString(),
            is(equalTo(createPath(true, "foo", "bar", "baz"))));
    }

    @Test @Ignore
    public void testHashCode() {
    }

    @Test @Ignore
    public void equals() {
    }

}
