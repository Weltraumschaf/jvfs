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

import java.util.regex.PatternSyntaxException;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.mockito.Mockito.mock;

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
    public void testHashCode() {
        final JvfsPathMatcher m1 = JvfsPathMatcher.newMatcher("glob:*.java");
        final JvfsPathMatcher m2 = JvfsPathMatcher.newMatcher("glob:*.java");
        final JvfsPathMatcher m3 = JvfsPathMatcher.newMatcher("glob:*.groovy");

        assertThat(m1.hashCode(), is(m1.hashCode()));
        assertThat(m1.hashCode(), is(m2.hashCode()));
        assertThat(m2.hashCode(), is(m1.hashCode()));
        assertThat(m2.hashCode(), is(m2.hashCode()));

        assertThat(m3.hashCode(), is(m3.hashCode()));
        assertThat(m3.hashCode(), is(not(m2.hashCode())));
        assertThat(m3.hashCode(), is(not(m1.hashCode())));
    }

    @Test
    public void equals() {
        final JvfsPathMatcher m1 = JvfsPathMatcher.newMatcher("glob:*.java");
        final JvfsPathMatcher m2 = JvfsPathMatcher.newMatcher("glob:*.java");
        final JvfsPathMatcher m3 = JvfsPathMatcher.newMatcher("glob:*.groovy");

        //CHECKSTYLE:OFF
        assertThat(m1.equals(null), is(false));
        assertThat(m1.equals(""), is(false));
        //CHECKSTYLE:ON

        assertThat(m1.equals(m1), is(true));
        assertThat(m1.equals(m2), is(true));
        assertThat(m2.equals(m1), is(true));
        assertThat(m2.equals(m2), is(true));

        assertThat(m3.equals(m3), is(true));
        assertThat(m3.equals(m2), is(false));
        assertThat(m3.equals(m1), is(false));
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
    public void newMatcher_throwsExceptionIfNoColon() {
        thrown.expect(IllegalArgumentException.class);
        JvfsPathMatcher.newMatcher("glob");
    }

    @Test
    public void newMatcher_throwsExceptionIfNoPattern() {
        thrown.expect(IllegalArgumentException.class);
        JvfsPathMatcher.newMatcher("glob:");
    }

    @Test
    public void newMatcher_throwsExceptionIfNoSyntax() {
        thrown.expect(IllegalArgumentException.class);
        JvfsPathMatcher.newMatcher(":*.java");
    }

    @Test
    public void newMatcher_throwsExceptionIfSyntaxNotSupported() {
        thrown.expect(UnsupportedOperationException.class);
        JvfsPathMatcher.newMatcher("foobar:*");
    }

    @Test
    public void newMatcher_globSyntax() {
        assertThat(
            JvfsPathMatcher.newMatcher("glob:foo,bar").getPattern(),
            is(equalTo("^foo,bar$")));
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
        assertThat(
            JvfsPathMatcher.newMatcher("glob:[^a]oobar*.java").getPattern(),
            is(equalTo("^[[^/]&&[\\^a]]oobar[^/]*\\.java$")));
        assertThat(
            JvfsPathMatcher.newMatcher("glob:[!a-d]oobar*.java").getPattern(),
            is(equalTo("^[[^/]&&[^a-d]]oobar[^/]*\\.java$")));
        assertThat(
            JvfsPathMatcher.newMatcher("glob:[-d]oobar*.java").getPattern(),
            is(equalTo("^[[^/]&&[-d]]oobar[^/]*\\.java$")));
        assertThat(
            JvfsPathMatcher.newMatcher("glob:[\\[&&]").getPattern(),
            is(equalTo("^[[^/]&&[\\\\\\[\\&&]]$")));
        assertThat(
            JvfsPathMatcher.newMatcher("glob:image.{gif,jpg,png}").getPattern(),
            is(equalTo("^image\\.(?:(?:gif)|(?:jpg)|(?:png))$")));
        assertThat(
            JvfsPathMatcher.newMatcher("glob:foo\\{}bar").getPattern(),
            is(equalTo("^foo\\{}bar$")));
    }

    @Test
    public void newMatcher_globSyntax_throwsExcpetionIfExplicitNameSeparatorInClass() {
        thrown.expect(PatternSyntaxException.class);
        JvfsPathMatcher.newMatcher("glob:[/foo]");
    }

    @Test
    public void newMatcher_globSyntax_throwsExcpetionIfClassNotClosed() {
        thrown.expect(PatternSyntaxException.class);
        JvfsPathMatcher.newMatcher("glob:[foo");
    }

    @Test
    public void newMatcher_globSyntax_throwsExcpetionIfRangeAlreadyStarted() {
        thrown.expect(PatternSyntaxException.class);
        JvfsPathMatcher.newMatcher("glob:[a--d]");
    }

    @Test
    public void newMatcher_globSyntax_throwsExcpetionIfGroupNested() {
        thrown.expect(PatternSyntaxException.class);
        JvfsPathMatcher.newMatcher("glob:image.{gif,jpg,png{a,b,c}}");
    }

    @Test
    public void newMatcher_globSyntax_throwsExcpetionIfGroupNotClosed() {
        thrown.expect(PatternSyntaxException.class);
        JvfsPathMatcher.newMatcher("glob:image.{gif,jpg,png");
    }

    @Test
    public void newMatcher_globSyntax_throwsExcpetionIfRangeInvalid() {
        thrown.expect(PatternSyntaxException.class);
        JvfsPathMatcher.newMatcher("glob:[d-a]");
    }

    @Test
    public void newMatcher_globSyntax_throwsExcpetionIfUnescapingEscape() {
        thrown.expect(PatternSyntaxException.class);
        JvfsPathMatcher.newMatcher("glob:foo\\");
    }

    @Test
    public void newMatcher_javaRegexSyntax() {
        final JvfsPathMatcher m = JvfsPathMatcher.newMatcher("regex:^*\\.java$");
        assertThat(m.getPattern(), is(equalTo("^*\\.java$")));
    }

    @Test
    public void matches() {
        final JvfsPathMatcher m = JvfsPathMatcher.newMatcher("glob:*.{jpg,gif,png}");
        assertThat(
            m.matches(new JvfsPath("baz.jpg", mock(JvfsFileSystem.class))), is(true));
        assertThat(
            m.matches(new JvfsPath("baz.gif", mock(JvfsFileSystem.class))), is(true));
        assertThat(
            m.matches(new JvfsPath("baz.png", mock(JvfsFileSystem.class))), is(true));
        assertThat(
            m.matches(new JvfsPath("baz.txt", mock(JvfsFileSystem.class))), is(false));
    }
}
