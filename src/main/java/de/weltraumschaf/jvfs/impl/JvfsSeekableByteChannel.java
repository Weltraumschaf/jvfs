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

import de.weltraumschaf.jvfs.impl.JvfsCollections;
import de.weltraumschaf.jvfs.impl.JvfsAssertions;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.List;

/**
 * Implementation of a seekable byte channel.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
final class JvfsSeekableByteChannel implements SeekableByteChannel {

    /**
     * Holds the data.
     */
    private List<Byte> data = JvfsCollections.newArrayList();
    /**
     * Whether the channel is open or not.
     */
    private volatile boolean open;
    /**
     * Current position in the channel.
     */
    private volatile int position;

    /**
     * Dedicated constructor.
     *
     * Initializes {@link #open} with {@literal true}.
     */
    JvfsSeekableByteChannel() {
        super();
        this.open = true;
    }

    @Override
    public int read(final ByteBuffer dst) throws IOException {
        for (final Byte b : data.subList(position, data.size())) {
            dst.put(b);
            position++;
        }

        return position;
    }

    @Override
    public int write(final ByteBuffer src) throws IOException {
        JvfsAssertions.notNull(src, "src");

        while (src.hasRemaining()) {
            data.add(position, src.get());
            position++;
        }

        return position;
    }

    @Override
    public long position() throws IOException {
        return position;
    }

    @Override
    public SeekableByteChannel position(final long newPosition) throws IOException {
        position = (int) newPosition;
        return this;
    }

    @Override
    public long size() throws IOException {
        return data.size();
    }

    @Override
    public SeekableByteChannel truncate(final long size) throws IOException {
        data = data.subList(0, (int) size);
        return this;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() throws IOException {
        open = false; // Ignore already closed.
    }

}