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

import java.util.List;
import java.util.StringTokenizer;

/**
 * Helpers to deal with string paths.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
final class JvfsPathUtil {

    /**
     * Splits the given string by the directory separator.
     *
     * @param path must not be {@code null}
     * @return never {@code null}
     */
    static List<String> tokenize(final String path) {
        JvfsAssertions.notNull(path, "path");
        final StringTokenizer tokenizer = new StringTokenizer(path, JvfsPath.DIR_SEP);
        final List<String> tokens = JvfsCollections.newArrayList();

        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }

        return tokens;
    }

    /**
     * Normalizes the tokenized view of the path.
     *
     * @param tokens must not be {@code null}
     * @param absolute whether the tokenized path was absolute or not
     * @return never {@code null}
     */
    static String normalize(final List<String> tokens, boolean absolute) {
        assert tokens != null : "path must be specified";

        // Remove unnecessary references to this dir
        if (tokens.contains(JvfsPath.DIR_THIS)) {
            tokens.remove(JvfsPath.DIR_THIS);
            normalize(tokens, absolute);
        }

        // Remove unnecessary references to the back dir, and its parent
        final int indexDirBack = tokens.indexOf(JvfsPath.DIR_UP);

        if (indexDirBack != -1) {
            if (indexDirBack > 0) {
                tokens.remove(indexDirBack);
                tokens.remove(indexDirBack - 1);
                normalize(tokens, absolute);
            } else {
                throw new IllegalArgumentException("Cannot specify to go back \"../\" past the root");
            }
        }

        // Nothing left to do; reconstruct
        final StringBuilder sb = new StringBuilder();

        if (absolute) {
            sb.append(JvfsPath.DIR_SEP);
        }

        for (int i = 0; i < tokens.size(); i++) {
            if (i > 0) {
                sb.append(JvfsPath.DIR_SEP);
            }
            sb.append(tokens.get(i));
        }

        return sb.toString();
    }

}
