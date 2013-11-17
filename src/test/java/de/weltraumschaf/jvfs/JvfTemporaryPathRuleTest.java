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
import java.nio.file.Files;
import java.nio.file.Path;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link JvfTemporaryPathRule}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfTemporaryPathRuleTest {

    private final JvfTemporaryPathRule sut = new JvfTemporaryPathRule();

    @Test
    public void beforeAndAfter() throws Throwable {
        final JvfTemporaryPathRule spy = spy(sut);
        spy.before();
        verify(spy, times(1)).create();
        spy.after();
        verify(spy, times(1)).delete();
    }

    @Test
    public void getRoot() throws IOException {
        assertThat(sut.getRoot(), is(nullValue()));
        sut.create();
        final Path tmp = sut.getRoot();
        assertThat(sut.getRoot(), is(not(nullValue())));
        assertThat(Files.exists(tmp), is(true));
        sut.delete();
        assertThat(Files.exists(tmp), is(false));
    }

    @Test
    public void createTemporaryFile() throws IOException {
        sut.create();
        final Path file = sut.newFile("foobar");
        assertThat(Files.exists(file), is(true));
        assertThat(Files.isDirectory(file), is(false));
        sut.delete();
        assertThat(Files.exists(file), is(false));
    }

    @Test
    public void createTemporaryFolder() throws IOException {
        sut.create();
        final Path folder = sut.newFolder("foobar");
        assertThat(Files.exists(folder), is(true));
        assertThat(Files.isDirectory(folder), is(true));
        sut.delete();
        assertThat(Files.exists(folder), is(false));
    }

}
