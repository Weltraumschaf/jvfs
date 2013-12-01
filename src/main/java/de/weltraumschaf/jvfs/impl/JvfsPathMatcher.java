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
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Implementation to match paths.
 *
 * Supported syntax is either the standard Unix glob or java Regex syntax.
 *
 * This class implements simple methods to scan glob syntax. Implementation is
 * copied from the Zip file example from Sun ({@code com.sun.nio.zipfs.ZipFileSystem}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 * @author Xueming Shen
 */
final class JvfsPathMatcher implements PathMatcher {

    /**
     * Identifier for Unix glob syntax.
     */
    private static final String GLOB_SYNTAX = "glob";
    /**
     * Identifier for Java Regex syntax.
     */
    private static final String REGEX_SYNTAX = "regex";
    /**
     * Meta characters for Unix glob syntax.
     */
    private static final String GLOB_META_CHARACTERS = "\\*?[{";
    /**
     * Meta characters for Java Regex syntax.
     */
    private static final String REGEX_META_CHARACTERS = ".^$+{[]|()";
    /**
     * Indicates end of line.
     */
    private static final char EOL = 0;
    /**
     * Ready to use compiled pattern.
     */
    private final Pattern pattern;

    /**
     * Dedicated constructor.
     *
     * Use {@link #newMatcher(java.lang.String) factory method} to create matchers.
     *
     * @param expr must not be {@code null} or empty
     */
    private JvfsPathMatcher(final String expr) {
        super();
        this.pattern = Pattern.compile(expr);
    }

    @Override
    public boolean matches(final Path path) {
        return pattern.matcher(path.toString()).matches();
    }

    /**
     * Get the compiled Java Regex pattern as string.
     *
     * @return never {@code null}
     */
    String getPattern() {
        return pattern.pattern();
    }

    @Override
    public int hashCode() {
        return getPattern().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof JvfsPathMatcher)) {
            return false;
        }

        final JvfsPathMatcher other = (JvfsPathMatcher) obj;
        return getPattern().equals(other.getPattern());
    }

    @Override
    public String toString() {
        return pattern.toString();
    }

    /**
     * Creates a new matcher.
     *
     * Syntax for parameter: {@code "SYNTAX:EXPRESSION"}. For syntax use either {@link #GLOB_SYNTAX} or
     * {@link #REGEX_SYNTAX}. As expression the according glob or Regex.
     *
     * @param syntaxAndPattern must not be {@code null} or empty
     * @return never {@code null}
     */
    static JvfsPathMatcher newMatcher(final String syntaxAndPattern) {
        JvfsAssertions.notEmpty(syntaxAndPattern, "syntaxAndPattern");
        final int pos = syntaxAndPattern.indexOf(':');

        if (pos <= 0 || pos == syntaxAndPattern.length()) {
            throw new IllegalArgumentException(syntaxAndPattern);
        }

        final String syntax = syntaxAndPattern.substring(0, pos).trim();
        final String input = syntaxAndPattern.substring(pos + 1).trim();
        final String expr;

        switch (syntax) {
            case GLOB_SYNTAX:
                expr = toRegexPattern(input);
                break;
            case REGEX_SYNTAX:
                expr = input;
                break;
            default:
                throw new UnsupportedOperationException("Syntax '" + syntax
                        + "' not recognized");
        }

        return new JvfsPathMatcher(expr);
    }

    /**
     * Determines if given character is a glob syntax meta character.
     *
     * @param c any character
     * @return {@code true} if it is a glob meta character, else {@code false}
     */
    private static boolean isGlobMeta(char c) {
        return GLOB_META_CHARACTERS.indexOf(c) != -1;
    }

    /**
     * Determines if given character is a Regex syntax meta character.
     *
     * @param c any character
     * @return {@code true} if it is a Regex meta character, else {@code false}
     */
    private static boolean isRegexMeta(char c) {
        return REGEX_META_CHARACTERS.indexOf(c) != -1;
    }

    /**
     * Get character from string.
     *
     * Returns {@link #EOL} if index is not less than the inputs length.
     *
     * @param str must not be {@code null}
     * @param index any non negative number
     * @return {@link #EOL} if index is at the end.
     */
    private static char next(final String str, int index) {
        assert null != str : "str must be defined";
        assert index >= 0 : "index must not be negative";

        if (index < str.length()) {
            return str.charAt(index);
        }

        return EOL;
    }

    /**
     * Creates a Java Regex pattern from the given glob expression.
     *
     * @param globPattern must not be {@literal null}
     * @return never {@literal null}
     */
    private static String toRegexPattern(final String globPattern) {
        assert null != globPattern : "globPattern must be defined";
        boolean inGroup = false;
        final StringBuilder regex = new StringBuilder("^");

        int i = 0;
        while (i < globPattern.length()) {
            final char c = globPattern.charAt(i++);

            switch (c) {
                case '\\':
                    i = handleEscapeCharacter(regex, globPattern, i);
                    break;
                case '/':
                    regex.append(c);
                    break;
                case '[':
                    i = handleClassStartCharacter(regex, globPattern, i);
                    break;
                case '{':
                    if (inGroup) {
                        throw new PatternSyntaxException("Cannot nest groups",
                                globPattern, i - 1);
                    }

                    regex.append("(?:(?:");
                    inGroup = true;
                    break;
                case '}':
                    if (inGroup) {
                        regex.append("))");
                        inGroup = false;
                    } else {
                        regex.append('}');
                    }

                    break;
                case ',':
                    if (inGroup) {
                        regex.append(")|(?:");
                    } else {
                        regex.append(',');
                    }

                    break;
                case '*':
                    if (next(globPattern, i) == '*') {
                        // crosses directory boundaries
                        regex.append(".*");
                        i++;
                    } else {
                        // within directory boundary
                        regex.append("[^/]*");
                    }

                    break;
                case '?':
                    regex.append("[^/]");
                    break;
                default:
                    if (isRegexMeta(c)) {
                        regex.append('\\');
                    }

                    regex.append(c);
            }
        }

        if (inGroup) {
            throw new PatternSyntaxException("Missing '}", globPattern, i - 1);
        }

        return regex.append('$').toString();
    }

    /**
     * Handles the "[" glob meta character.
     *
     * @param regex must not be {@code null}
     * @param globPattern must not be {@code null}
     * @param start must be non negative
     * @return next position to scan
     */
    private static int handleClassStartCharacter(final StringBuilder regex, final String globPattern, final int start) {
        assert regex != null : "regex must be defined";
        assert globPattern != null : "globPattern must be defined";
        assert start >= 0 : "start must not be negative";
        char c = 0;
        int position = start;
        // don't match name separator in class
        regex.append("[[^/]&&[");

        if (next(globPattern, position) == '^') {
            // escape the regex negation char if it appears
            regex.append("\\^");
            position++;
        } else {
            // negation
            if (next(globPattern, position) == '!') {
                regex.append('^');
                position++;
            }

            // hyphen allowed at start
            if (next(globPattern, position) == '-') {
                regex.append('-');
                position++;
            }
        }

        boolean hasRangeStart = false;
        char last = 0;
        while (position < globPattern.length()) {
            c = globPattern.charAt(position++);
            if (c == ']') {
                break;
            }

            if (c == '/') {
                throw new PatternSyntaxException("Explicit 'name separator' in class",
                        globPattern, position - 1);
            }

            // TBD: how to specify ']' in a class?
            if (c == '\\' || c == '['
                    || c == '&' && next(globPattern, position) == '&') {
                // escape '\', '[' or "&&" for regex class
                regex.append('\\');
            }

            regex.append(c);

            if (c == '-') {
                if (!hasRangeStart) {
                    throw new PatternSyntaxException("Invalid range",
                            globPattern, position - 1);
                }

                c = next(globPattern, position++);

                if (c == EOL || c == ']') {
                    break;
                }

                if (c < last) {
                    //CHECKSTYLE:OFF
                    throw new PatternSyntaxException("Invalid range", globPattern, position - 3);
                    //CHECKSTYLE:ON
                }

                regex.append(c);
                hasRangeStart = false;
            } else {
                hasRangeStart = true;
                last = c;
            }
        }

        if (c != ']') {
            throw new PatternSyntaxException("Missing ']", globPattern, position - 1);
        }

        regex.append("]]");
        return position;
    }

    /**
     * Handles the "\\" glob meta character.
     *
     * @param regex must not be {@code null}
     * @param globPattern must not be {@code null}
     * @param start must be non negative
     * @return next position to scan
     */
    private static int handleEscapeCharacter(final StringBuilder regex, final String globPattern, final int start) {
        int position = start;

        // escape special characters
        if (position == globPattern.length()) {
            throw new PatternSyntaxException("No character to escape",
                    globPattern, position - 1);
        }

        final char next = globPattern.charAt(position++);

        if (isGlobMeta(next) || isRegexMeta(next)) {
            regex.append('\\');
        }

        regex.append(next);
        return position;
    }
}
