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
 * Tests for {@link JvfsOptions}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsOptionsTest {

    @Test
    public void testSomeMethod() {
        final JvfsOptions opts = JvfsOptions.builder()
            .readonly(true)
            .capacity("1k")
            .create();

        assertThat(opts.isReadonly(), is(true));
        assertThat(opts.getCapacity(), is(equalTo(JvfsQuantity.forValue(1024L))));
    }

}
