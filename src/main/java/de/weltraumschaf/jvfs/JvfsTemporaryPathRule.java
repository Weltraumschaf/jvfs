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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import org.apache.log4j.Logger;
import org.junit.rules.ExternalResource;

/**
 * The TemporaryFolder Rule allows creation of files and folders that are guaranteed to be deleted when the test method
 * finishes (whether it passes or fails):
 *
 * <pre>
 * public static class HasTempFolder {
 *      &#064;Rule
      public JvfsTemporaryPathRule folder= new JvfsTemporaryPathRule();

      &#064;Test
 *      public void testUsingTempFolder() throws IOException {
 *          final Path createdFile= folder.newFile(&quot;myfile.txt&quot;);
 *          final Path createdFolder= folder.newFolder(&quot;subfolder&quot;);
 *          // ...
 *      }
 * }
 * </pre>
 *
 * Inspired by {@code org.junit.rules.TemporaryFolder} from <a href="http://junit.org/">JUnit</a>.
 * This class enhances the original in that way that it uses Java 7 {@link java.nio.file.Files NIO API}
 * instead of the old {@link java.lang.File file API}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsTemporaryPathRule extends ExternalResource {

    /**
     * Logging facility.
     */
    private static final Logger LOG = Logger.getLogger(JvfsTemporaryPathRule.class);
    /**
     * Root folder.
     */
    private Path folder;

    @Override
    //CHECKSTYLE:OFF
    protected void before() throws Throwable {
    //CHECKSTYLE:ON
        create();
    }

    @Override
    protected void after() {
        try {
            delete();
        } catch (final IOException e) {
            logError(String.format("Can't delete temporary folder '%s'!", Objects.toString(folder)), e);
        }
    }

    /**
     * Logs IOExceptions.
     *
     * @param message must not be {@code nul} or empty
     * @param e may be {@code null}
     */
    protected void logError(final String message, final IOException e) {
        JvfsAssertions.notEmpty(message, "message");
        LOG.error(message, e);
    }

    /**
     * for testing purposes only.
     *
     * Do not use.
     *
     * @throws IOException on any I/O error
     */
    public void create() throws IOException {
        folder = Files.createTempDirectory("junit");
        Files.delete(folder);
        Files.createDirectories(folder);
    }

    /**
     * Returns a new fresh file with the given name under the temporary folder.
     *
     * @param fileName must not be {@code null} or empty
     * @return never {@code null}
     * @throws IOException IOException on any I/O error
     */
    public Path newFile(final String fileName) throws IOException {
        if (null == fileName) {
            throw new NullPointerException("Parameter 'fileName' must not be null!");
        }

        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'fileName' must not be empty!");
        }

        return Files.createFile(folder.resolve(fileName));
    }

    /**
     * Returns a new fresh folder with the given name under the temporary folder.
     *
     * @param folderName must not be {@code null} or empty
     * @return never {@code null}
     * @throws IOException IOException on any I/O error
     */
    public Path newFolder(final String folderName) throws IOException {
        if (null == folderName) {
            throw new NullPointerException("Parameter 'folderName' must not be null!");
        }

        if (folderName.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'folderName' must not be empty!");
        }

        return Files.createDirectory(folder.resolve(folderName));
    }

    /**
     * Get the location of this temporary folder.
     *
     * @return never {@code null}
     */
    public Path getRoot() {
        return folder;
    }

    /**
     * Delete all files and folders under the temporary folder.
     *
     * Usually not called directly, since it is automatically
     * applied by the {@link org.junit.Rule}.
     *
     * @throws IOException on any I/O error
     */
    public void delete() throws IOException {
        recursiveDelete(folder);
    }

    /**
     * Walks directory recursively and deletes all files.
     *
     * @param dir must not be {@code null}
     * @throws IOException on any I/O error
     */
    private void recursiveDelete(final Path dir) throws IOException {
        if (null == dir) {
            throw new NullPointerException("Parameter 'dir' must not be null!");
        }

        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return CONTINUE;
                } else {
                    throw exc;
                }
            }

        });
    }
}
