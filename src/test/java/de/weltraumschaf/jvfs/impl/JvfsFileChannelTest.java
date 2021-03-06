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
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link JvfsFileChannel}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFileChannelTest {

    private static final String CONTENT = "abcdefghijklmnopqrstuvwxyz";

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    private JvfsFileEntry file;
    private SeekableByteChannel channel;
    private JvfsFileChannel sut;

    @Before
    public void prepareFixtures() {
        file = JvfsFileEntry.newFile("foo");
        file.setContent(CONTENT.getBytes());
        channel = spy(new JvfsSeekableByteChannel(file));
        sut = new JvfsFileChannel(channel);
    }

    @Test
    public void read_buffer() throws IOException {
        final byte[] data = new byte[10];
        assertThat(sut.position(), is(0L));
        assertThat(sut.read(ByteBuffer.wrap(data)), is(10));
        assertThat(sut.position(), is(10L));
        assertThat(new String(data), is(equalTo("abcdefghij")));
    }

    @Test
    public void read_bufferAndPosition() throws IOException {
        final byte[] data = new byte[10];
        assertThat(sut.read(ByteBuffer.wrap(data), 5), is(10));
        assertThat(sut.position(), is(15L));
        assertThat(new String(data), is(equalTo("fghijklmno")));
    }

    @Test
    public void read_buffers() throws IOException {
        final byte[] data = new byte[30];
        final ByteBuffer[] buffers = new ByteBuffer[] {
            ByteBuffer.wrap(data, 0, 10), ByteBuffer.wrap(data, 10, 10), ByteBuffer.wrap(data, 20 ,10)
        };
        assertThat(sut.position(), is(0L));
        assertThat(sut.read(buffers, 0, buffers.length), is(26L));
        assertThat(sut.position(), is(26L));
        // Last fourcharacters are from 0 bytes.
        assertThat(new String(data), is(equalTo("abcdefghijklmnopqrstuvwxyz    ")));
    }

    @Test
    public void read_buffers_lengthGreaterThanBuffersCount() throws IOException {
        final byte[] data = new byte[30];
        final ByteBuffer[] buffers = new ByteBuffer[] {
            ByteBuffer.wrap(data, 0, 10), ByteBuffer.wrap(data, 10, 10), ByteBuffer.wrap(data, 20 ,10)
        };
        assertThat(sut.position(), is(0L));
        assertThat(sut.read(buffers, 0, 5), is(26L));
        assertThat(sut.position(), is(26L));
        // Last fourcharacters are from 0 bytes.
        assertThat(new String(data), is(equalTo("abcdefghijklmnopqrstuvwxyz    ")));
    }

    @Test
    public void read_buffers_lengthLessThanBuffersCount() throws IOException {
        final byte[] data = new byte[30];
        final ByteBuffer[] buffers = new ByteBuffer[] {
            ByteBuffer.wrap(data, 0, 10), ByteBuffer.wrap(data, 10, 10), ByteBuffer.wrap(data, 20 ,10)
        };
        assertThat(sut.position(), is(0L));
        assertThat(sut.read(buffers, 0, 2), is(20L));
        assertThat(sut.position(), is(20L));
        // Last fourcharacters are from 0 bytes.
        assertThat(new String(data), is(equalTo("abcdefghijklmnopqrst          ")));
    }

    @Test
    public void write_buffer() throws IOException {
        final byte[] data = "1234567890".getBytes();
        assertThat(sut.position(), is(0L));
        sut.write(ByteBuffer.wrap(data));
        assertThat(sut.position(), is(10L));
        // Last fourcharacters are from 0 bytes.
        assertThat(new String(file.getContent()), is(equalTo("1234567890klmnopqrstuvwxyz          ")));
    }

    @Test
    public void write_bufferAndPosition() throws IOException {
        final byte[] data = "1234567890".getBytes();
        assertThat(sut.position(), is(0L));
        sut.write(ByteBuffer.wrap(data), 5L);
        assertThat(sut.position(), is(15L));
        assertThat(new String(file.getContent()), is(equalTo("abcde1234567890pqrstuvwxyz          ")));
    }

    @Test
    public void write_buffers() throws IOException {
        final byte[] data = "123456789012345678901234567890".getBytes();
        final ByteBuffer[] buffers = new ByteBuffer[] {
            ByteBuffer.wrap(data, 0, 10), ByteBuffer.wrap(data, 10, 10), ByteBuffer.wrap(data, 20 ,10)
        };
        assertThat(sut.position(), is(0L));
        assertThat(sut.write(buffers, 0, buffers.length), is(30L));
        assertThat(sut.position(), is(30L));
        // Last fourcharacters are from 0 bytes.
        assertThat(new String(file.getContent()),
                is(equalTo("123456789012345678901234567890                          ")));
    }

    @Test
    public void write_buffers_lengthGreaterThanBuffersCount() throws IOException {
        final byte[] data = "123456789012345678901234567890".getBytes();
        final ByteBuffer[] buffers = new ByteBuffer[] {
            ByteBuffer.wrap(data, 0, 10), ByteBuffer.wrap(data, 10, 10), ByteBuffer.wrap(data, 20 ,10)
        };
        assertThat(sut.position(), is(0L));
        assertThat(sut.write(buffers, 0, 5), is(30L));
        assertThat(sut.position(), is(30L));
        // Last fourcharacters are from 0 bytes.
        assertThat(new String(file.getContent()),
                is(equalTo("123456789012345678901234567890                          ")));
    }

    @Test
    public void write_buffers_lengthLessThanBuffersCount() throws IOException {
        final byte[] data = "123456789012345678901234567890".getBytes();
        final ByteBuffer[] buffers = new ByteBuffer[] {
            ByteBuffer.wrap(data, 0, 10), ByteBuffer.wrap(data, 10, 10), ByteBuffer.wrap(data, 20 ,10),
        };
        assertThat(sut.position(), is(0L));
        assertThat(sut.write(buffers, 0, 2), is(20L));
        assertThat(sut.position(), is(20L));
        // Last fourcharacters are from 0 bytes.
        assertThat(new String(file.getContent()), is(equalTo("12345678901234567890uvwxyz                    ")));
    }

    @Test
    public void getPosition() throws IOException {
        sut.position(1L);
        verify(channel, times(1)).position(1L);
    }

    @Test
    public void setPosition() throws IOException {
        when(channel.position()).thenReturn(1L);
        assertThat(sut.position(), is(1L));
        verify(channel, times(1)).position();
    }

    @Test
    public void size() throws IOException {
        when(channel.size()).thenReturn(1L);
        assertThat(sut.size(), is(1L));
        verify(channel, times(1)).size();
    }

    @Test
    public void truncate() throws IOException {
        sut.truncate(2L);
        verify(channel, times(1)).truncate(2L);
    }

    @Test
    public void force_true_notSupported() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.force(true);
    }

    @Test
    public void force_false_notSupported() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.force(false);
    }

    @Test
    public void transferTo() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.transferTo(0L, 0, null);
    }

    @Test
    public void transferFrom() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.transferFrom(null, 0L, 0);
    }

    @Test
    public void map() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.map(FileChannel.MapMode.PRIVATE, 0L, 0);
    }

    @Test
    public void lock() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.lock(0L, 0L, false);
    }

    @Test
    public void tryLock() throws IOException {
        thrown.expect(UnsupportedOperationException.class);
        sut.tryLock(0L, 0L, false);
    }
}
