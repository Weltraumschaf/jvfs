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

import de.weltraumschaf.jvfs.JvfsAssertions;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Represents a file channel.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
final class JvfsFileChannel extends FileChannel {

    /**
     * Wrapped byte channel.
     */
    private final SeekableByteChannel channel;

    /**
     * Dedicated constructor.
     *
     * @param channel must not be {@code null}
     */
    JvfsFileChannel(final SeekableByteChannel channel) {
        super();
        JvfsAssertions.notNull(channel, "channel");
        this.channel = channel;
    }

    @Override
    public int read(final ByteBuffer dst) throws IOException {
        return channel.read(dst);
    }

    @Override
    public long read(final ByteBuffer[] dsts, final int offset, final int length) throws IOException {
        JvfsAssertions.notNull(dsts, "dsts");
        JvfsAssertions.greaterThan(offset, -1, "offset");
        JvfsAssertions.greaterThan(length, -1, "offset");
        final int max = Math.min(dsts.length, length);
        final long start = channel.position();

        for (int i = offset; i < max; ++i) {
            final ByteBuffer buffer = dsts[i];
            read(buffer);
        }

        return channel.position() - start;
    }

    @Override
    public int read(ByteBuffer dst, long position) throws IOException {
        channel.position(position);
        return channel.read(dst);
    }

    @Override
    public int write(final ByteBuffer src) throws IOException {
        return channel.write(src);
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
        JvfsAssertions.notNull(srcs, "srcs");
        JvfsAssertions.greaterThan(offset, -1, "offset");
        JvfsAssertions.greaterThan(length, -1, "offset");
        final int max = Math.min(srcs.length, length);
        final long start = channel.position();

        for (int i = offset; i < max; ++i) {
            final ByteBuffer buffer = srcs[i];
            write(buffer);
        }

        return channel.position() - start;
    }


    @Override
    public int write(ByteBuffer src, long position) throws IOException {
        channel.position(position);
        return channel.write(src);
    }

    @Override
    public long position() throws IOException {
        return channel.position();
    }

    @Override
    public FileChannel position(long newPosition) throws IOException {
        channel.position(newPosition);
        return this;
    }

    @Override
    public long size() throws IOException {
        return channel.size();
    }

    @Override
    public FileChannel truncate(long size) throws IOException {
        channel.truncate(size);
        return this;
    }

    @Override
    public void force(final boolean metaData) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long transferTo(final long position, final long count, final WritableByteChannel target) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long transferFrom(final ReadableByteChannel src, final long position, final long count) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MappedByteBuffer map(final MapMode mode, final long position, final long size) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileLock lock(final long position, final long size, final boolean shared) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileLock tryLock(final long position, final long size, final boolean shared) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void implCloseChannel() throws IOException {
        if (!channel.isOpen()) {
            throw new IllegalStateException("Channel not open!");
        }

        channel.close();
    }

}
