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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link JvfsPathMatcher}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsPathMatcherTest {

    @Rule
    //CHECKSTYLE:OFF
    public final ExpectedException thrown = ExpectedException.none();
    //CHECKSTYLE:ON

    @Test
    public void testToString() {
        final JvfsPathMatcher m = JvfsPathMatcher.newMatcher("glob:*.java");
        assertThat(m.toString(), is(equalTo(m.getPattern())));
    }

    @Test
    public void newMatcher_throwsExceptionIfSyntaxAndPatternIsNull() {
        thrown.expect(NullPointerException.class);
        JvfsPathMatcher.newMatcher(null);
    }

    @Test
    public void newMatcher_throwsExceptionIfSyntaxAndPatternIsEmpty() {
        thrown.expect(IllegalArgumentException.class);
        JvfsPathMatcher.newMatcher("");
    }

    @Test
    public void newMatcher_throwsExceptionIfSyntaxNotSupported() {
        thrown.expect(UnsupportedOperationException.class);
        JvfsPathMatcher.newMatcher("foobar:*");
    }

    @Test
    public void newMatcher_globSyntax() {
        assertThat(
            JvfsPathMatcher.newMatcher("glob:?oo*.java").getPattern(),
            is(equalTo("^[^/]oo[^/]*\\.java$")));
        assertThat(
            JvfsPathMatcher.newMatcher("glob:*.java").getPattern(),
            is(equalTo("^[^/]*\\.java$")));
        assertThat(
            JvfsPathMatcher.newMatcher("glob:**.java").getPattern(),
            is(equalTo("^.*\\.java$")));
        assertThat(
            JvfsPathMatcher.newMatcher("glob:/foo/*.java").getPattern(),
            is(equalTo("^/foo/[^/]*\\.java$")));
        assertThat(
            JvfsPathMatcher.newMatcher("glob:foo\\ bar*.java").getPattern(),
            is(equalTo("^foo bar[^/]*\\.java$")));
        assertThat(
            JvfsPathMatcher.newMatcher("glob:[Ff]oobar*.java").getPattern(),
            is(equalTo("^[[^/]&&[Ff]]oobar[^/]*\\.java$")));
    }

    @Test
    public void newMatcher_javaRegexSyntax() {
        final JvfsPathMatcher m = JvfsPathMatcher.newMatcher("regex:^*\\.java$");
        assertThat(m.getPattern(), is(equalTo("^*\\.java$")));
    }
}
