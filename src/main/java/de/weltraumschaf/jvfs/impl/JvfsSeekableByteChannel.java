/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.weltraumschaf.jvfs.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;

/**
 * {@link SeekableByteChannel} implementation backed by an auto-resizing byte array; thread-safe.
 *
 * Can hold a maxiumum of {@link Integer#MAX_VALUE} bytes.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class JvfsSeekableByteChannel implements SeekableByteChannel {

    /**
     * Current position; guarded by "this".
     */
    private int position;

    /**
     * Whether or not this {@link SeekableByteChannel} is open; volatile instead of sync is acceptable because this
     * field participates in no compound computations or invariants with other instance members.
     */
    private volatile boolean open;

    /**
     * Internal buffer for contents; guarded by "this".
     */
    private byte[] contents;

    /**
     * Creates a new instance with 0 size and 0 position, and open.
     */
    JvfsSeekableByteChannel() {
        this(new byte[0]);
    }

    /**
     * Dedicated constructor.
     *
     * @param contents must not be null
     */
    JvfsSeekableByteChannel(final byte[] contents) {
        super();
        assert null != contents : "contents must be defined";
        this.open = true;

        // Set fields
        synchronized (this) {
            this.position = 0;
            this.contents = contents;
        }
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public void close() throws IOException {
        // XXX: Consider if necessary.
        this.open = false;
    }

    @Override
    public int read(final ByteBuffer destination) throws IOException {

        // Precondition checks
        this.checkClosed();
//        position(0); // XXX Consider not reusing channel and return always new channelswith position 0.

        if (destination == null) {
            throw new IllegalArgumentException("Destination buffer must be supplied");
        }

        // Init
        final int spaceInBuffer = destination.remaining();
        final int numBytesRemainingInContent;
        final int numBytesToRead;

        // Sync up before getting at shared mutable state
        synchronized (this) {
            numBytesRemainingInContent = this.contents.length - this.position;

            // Set position was greater than the size? Just return.
            if (numBytesRemainingInContent <= 0) {
                return -1;
            }

            // We'll read in either the number of bytes remaining in content or the amount of space in the buffer,
            // whichever is smaller
            numBytesToRead = numBytesRemainingInContent >= spaceInBuffer ? spaceInBuffer : numBytesRemainingInContent;
            // Copy a sub-array of the bytes we'll put into the buffer
            destination.put(this.contents, this.position, numBytesToRead);

            // Set the new position
            this.position += numBytesToRead;
        }

        // Return the number of bytes read
        return numBytesToRead;
    }

    @Override
    public int write(final ByteBuffer source) throws IOException {

        // Precondition checks
        this.checkClosed();
//        position(0); // XXX Consider not reusing channel and return always new channelswith position 0.

        if (source == null) {
            throw new IllegalArgumentException("Source buffer must be supplied");
        }

        // Put the bytes to be written into a byte[]
        final int totalBytes = source.remaining();
        final byte[] readContents = new byte[totalBytes];
        source.get(readContents);

        // Sync up, we're gonna access shared mutable state
        synchronized (this) {

            // Append the read contents to our internal contents
            this.contents = this.concat(this.contents, readContents, this.position);

            // Increment the position of this channel
            this.position += totalBytes;

        }

        // Return the number of bytes read
        return totalBytes;
    }

    /**
     * Creates a new array which is the concatenated result of the two inputs, at the designated position (to be filled
     * with 0x00) in the case of a gap).
     *
     * @param input1 must not be null
     * @param input2 must not be null
     * @param pos non negative number
     * @return never null
     */
    private byte[] concat(final byte[] input1, final byte[] input2, final int pos) {
        // Preconition checks
        assert input1 != null : "Input 1 must be specified";
        assert input2 != null : "Input 2 must be specified";
        assert pos >= 0 : "Position must be 0 or higher";
        // Allocate a new array of enough space (either current size or position + input2.length, whichever is greater)
        final int newSize = pos < input1.length ? input1.length + input2.length : pos + input2.length;
        final byte[] merged = new byte[newSize];
        // Copy in the contents of input 1 with 0 offset
        System.arraycopy(input1, 0, merged, 0, input1.length);
        // Copy in the contents of input2 with offset the length of input 1
        System.arraycopy(input2, 0, merged, pos, input2.length);
        return merged;
    }

    @Override
    public long position() throws IOException {
        synchronized (this) {
            return this.position;
        }
    }

    @Override
    public SeekableByteChannel position(final long newPosition) throws IOException {
        // Precondition checks
        if (newPosition > Integer.MAX_VALUE || newPosition < 0) {
            throw new IllegalArgumentException("Valid position for this channel is between 0 and " + Integer.MAX_VALUE);
        }
        synchronized (this) {
            this.position = (int) newPosition;
        }
        return this;
    }

    @Override
    public long size() throws IOException {
        synchronized (this) {
            return this.contents.length;
        }
    }

    @Override
    public SeekableByteChannel truncate(final long size) throws IOException {

        // Precondition checks
        if (size < 0 || size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("This implementation permits a size of 0 to " + Integer.MAX_VALUE
                    + " inclusive");
        }

        // Sync up for mucking w/ shared mutable state
        synchronized (this) {

            final int newSize = (int) size;
            final int currentSize = (int) this.size();

            // If the current position is greater than the given size, set to the given size (by API spec)
            if (this.position > newSize) {
                this.position = newSize;
            }

            // If we've been given a size smaller than we currently are
            if (currentSize > newSize) {
                // Make new array
                final byte[] newContents = new byte[newSize];
                // Copy in the contents up to the new size
                System.arraycopy(this.contents, 0, newContents, 0, newSize);
                // Set the new array as our contents
                this.contents = newContents;
            }
            // If we've been given a size greater than we are
            if (newSize > currentSize) {
                // Reset the position only
                this.position = newSize;
            }
        }

        // Return this reference
        return this;
    }

    /**
     * Get the bytes as input stream.
     *
     * @return never {@code null}
     */
    InputStream getContents() {
        final byte[] copy;
        synchronized (this) {
            final int length = this.contents.length;
            copy = new byte[length];
            System.arraycopy(this.contents, 0, copy, 0, this.contents.length);
        }

        return new ByteArrayInputStream(copy);
    }

    /**
     * Checks if closed.
     *
     * @throws ClosedChannelException if not open
     */
    private void checkClosed() throws ClosedChannelException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
    }

    /**
     * Creates deep copy.
     *
     * @return never {@code null}
     */
    JvfsSeekableByteChannel copy() {
        synchronized (this) {
            final byte[] copy = new byte[contents.length];
            System.arraycopy(contents, 0, copy, 0, contents.length);
            return new JvfsSeekableByteChannel(copy);
        }
    }

}
