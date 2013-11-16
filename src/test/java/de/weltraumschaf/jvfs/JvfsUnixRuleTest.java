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
package de.weltraumschaf.jvfs;

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link JvfsUnixRule}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsUnixRuleTest {

    @Test
    public void beforeAndAfter() throws Throwable {
        final JvfsUnixRuleStub sut = new JvfsUnixRuleStub();
        assertThat(
            System.getProperty(JvfsFileSystems.IMPLEMENTATION_PROPERTY_NAME),
            is(anyOf(nullValue(), equalTo((Object) ""))));
        sut.before();
        assertThat(
                System.getProperty(JvfsFileSystems.IMPLEMENTATION_PROPERTY_NAME),
                is(equalTo(JvfsFileSystems.IMPLEMENTATION_CLASS_NAME)));
        sut.after();
        assertThat(
            System.getProperty(JvfsFileSystems.IMPLEMENTATION_PROPERTY_NAME),
            is(anyOf(nullValue(), equalTo((Object) ""))));
    }

    /**
     * Used to access protected methods.
     */
    private static final class JvfsUnixRuleStub extends JvfsUnixRule {

        /**
         * Exposes {@link JvfsUnixRule#before()}.
         */
        public void exposedBefore() throws Throwable {
            before();
        }

        /**
         * Exposes {@link JvfsUnixRule#after()}.
         */
        public void exposedAfter() {
            after();
        }

    }
}
