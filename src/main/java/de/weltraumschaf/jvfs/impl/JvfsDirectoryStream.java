/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.weltraumschaf.jvfs.impl;

import de.weltraumschaf.jvfs.JvfsAssertions;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Implementation of a directory stream.
 *
 * TODO Implement class.
 *
 * @author Sven.Strittmatter
 */
final class JvfsDirectoryStream implements DirectoryStream<Path> {

    /**
     * Dedicated constructor.
     *
     * @param path must not be {@code null}
     * @param filter must not be {@code null}
     */
    public JvfsDirectoryStream(final JvfsPath path, final Filter<? super Path> filter) {
        super();
        JvfsAssertions.notNull(path, "path");
        JvfsAssertions.notNull(filter, "filter");
    }

    @Override
    public Iterator<Path> iterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
