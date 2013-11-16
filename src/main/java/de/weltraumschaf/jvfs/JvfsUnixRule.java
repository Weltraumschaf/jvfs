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

import org.junit.rules.ExternalResource;

/**
 * This rule registers the JVFS Unix style virtual file system as JVM systems default file system.
 *
 * <pre>
 * public static class HasUnixJvfs {
 * 	&#064;Rule
 * 	public JvfsUnixRule folder= new JvfsUnixRule();
 *
 * 	&#064;Test
 * 	public void testSomething() throws IOException {
 * 		// Nothing special to do here.
 * 	}
 * }
 * </pre>
 *
 * @see de.weltraumschaf.jvfs.JvfsFileSystems
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsUnixRule extends ExternalResource {

    @Override
    //CHECKSTYLE:OFF
    protected void before() throws Throwable {
    //CHECKSTYLE:ON
        JvfsFileSystems.registerUnixAsDefault();
    }

    @Override
    protected void after() {
        JvfsFileSystems.unregisterDefault();
    }

}
