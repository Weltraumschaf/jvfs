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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JvfsFileChannel}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileChannelTest {

    private final SeekableByteChannel ch = mock(SeekableByteChannel.class);
    private final JvfsFileChannel sut = new JvfsFileChannel(ch);

    @Test
    public void read_buffer() throws IOException {
        final ByteBuffer buffer = mock(ByteBuffer.class);
        sut.read(buffer);
        verify(ch, times(1)).read(buffer);
    }

    @Test
    public void read_bufferAndPosition() throws IOException {
        final ByteBuffer buffer = mock(ByteBuffer.class);
        final long position = 5L;
        sut.read(buffer, position);
        verify(ch, times(1)).position(position);
        verify(ch, times(1)).read(buffer);
    }

    @Test
    public void write_buffer() throws IOException {
        final ByteBuffer buffer = mock(ByteBuffer.class);
        sut.write(buffer);
        verify(ch, times(1)).write(buffer);
    }

    @Test
    public void write_bufferAndPosition() throws IOException {
        final ByteBuffer buffer = mock(ByteBuffer.class);
        final long position = 5L;
        sut.write(buffer, position);
        verify(ch, times(1)).position(position);
        verify(ch, times(1)).write(buffer);
    }

    @Test
    public void getPosition() throws IOException {
        sut.position(1L);
        verify(ch, times(1)).position(1L);
    }

    @Test
    public void setPosition() throws IOException {
        when(ch.position()).thenReturn(1L);
        assertThat(sut.position(), is(1L));
        verify(ch, times(1)).position();
    }

    @Test
    public void size() throws IOException {
        when(ch.size()).thenReturn(1L);
        assertThat(sut.size(), is(1L));
        verify(ch, times(1)).size();
    }

    @Test
    public void truncate() throws IOException {
        sut.truncate(2L);
        verify(ch, times(1)).truncate(2L);
    }

}
