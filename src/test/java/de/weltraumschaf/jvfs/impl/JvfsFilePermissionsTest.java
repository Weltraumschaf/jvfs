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

import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 * Tests for {@link JvfsFilePermissions}.
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsFilePermissionsTest {

    @Test
    public void defaults() {
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(
                PosixFilePermissions.asFileAttribute(new HashSet<PosixFilePermission>()));
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void ownerRead() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        permissions.add(PosixFilePermission.OWNER_READ);
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(
                PosixFilePermissions.asFileAttribute(permissions));
        assertThat(sut.ownerRead(), is(true));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void ownerWrite() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        permissions.add(PosixFilePermission.OWNER_WRITE);
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(
                PosixFilePermissions.asFileAttribute(permissions));
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(true));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void ownerExecute() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        permissions.add(PosixFilePermission.OWNER_EXECUTE);
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(
                PosixFilePermissions.asFileAttribute(permissions));
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(true));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void groupRead() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        permissions.add(PosixFilePermission.GROUP_READ);
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(
                PosixFilePermissions.asFileAttribute(permissions));
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(true));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void groupWrite() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        permissions.add(PosixFilePermission.GROUP_WRITE);
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(
                PosixFilePermissions.asFileAttribute(permissions));
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(true));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void groupExecute() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        permissions.add(PosixFilePermission.GROUP_EXECUTE);
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(
                PosixFilePermissions.asFileAttribute(permissions));
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(true));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void othersRead() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        permissions.add(PosixFilePermission.OTHERS_READ);
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(
                PosixFilePermissions.asFileAttribute(permissions));
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(true));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void othersWrite() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        permissions.add(PosixFilePermission.OTHERS_WRITE);
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(
                PosixFilePermissions.asFileAttribute(permissions));
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(true));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void othersExecute() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        permissions.add(PosixFilePermission.OTHERS_EXECUTE);
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(
                PosixFilePermissions.asFileAttribute(permissions));
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(true));
    }

    @Test
    public void setAndUnsetOwnerRead() {
        final JvfsFilePermissions sut = new JvfsFilePermissions();
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));

        sut.ownerRead(true);
        assertThat(sut.ownerRead(), is(true));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));

        sut.ownerRead(false);
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void setAndUnsetOwnerWrite() {
        final JvfsFilePermissions sut = new JvfsFilePermissions();
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));

        sut.ownerWrite(true);
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(true));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));

        sut.ownerWrite(false);
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void setAndUnsetOwnerExecute() {
        final JvfsFilePermissions sut = new JvfsFilePermissions();
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));

        sut.ownerExecute(true);
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(true));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));

        sut.ownerExecute(false);
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test
    public void testHashCode() {
        final Set<PosixFilePermission> permissions1 = new HashSet<PosixFilePermission>();
        permissions1.add(PosixFilePermission.OWNER_READ);
        permissions1.add(PosixFilePermission.OWNER_WRITE);
        final Set<PosixFilePermission> permissions2 = new HashSet<PosixFilePermission>();
        permissions2.add(PosixFilePermission.OWNER_WRITE);
        final JvfsFilePermissions sut1 = new JvfsFilePermissions(permissions1);
        final JvfsFilePermissions sut2 = new JvfsFilePermissions(permissions1);
        final JvfsFilePermissions sut3 = new JvfsFilePermissions(permissions2);

        assertThat(sut1.hashCode(), is(sut1.hashCode()));
        assertThat(sut1.hashCode(), is(sut2.hashCode()));
        assertThat(sut2.hashCode(), is(sut1.hashCode()));
        assertThat(sut2.hashCode(), is(sut2.hashCode()));

        assertThat(sut3.hashCode(), is(sut3.hashCode()));
        assertThat(sut3.hashCode(), is(not(sut1.hashCode())));
        assertThat(sut3.hashCode(), is(not(sut2.hashCode())));
    }

    @Test
    public void equals() {
        final Set<PosixFilePermission> permissions1 = new HashSet<PosixFilePermission>();
        permissions1.add(PosixFilePermission.OWNER_READ);
        permissions1.add(PosixFilePermission.OWNER_WRITE);
        final Set<PosixFilePermission> permissions2 = new HashSet<PosixFilePermission>();
        permissions2.add(PosixFilePermission.OWNER_WRITE);
        final JvfsFilePermissions sut1 = new JvfsFilePermissions(permissions1);
        final JvfsFilePermissions sut2 = new JvfsFilePermissions(permissions1);
        final JvfsFilePermissions sut3 = new JvfsFilePermissions(permissions2);

        //CHECKSTYLE:OFF
        assertThat(sut1.equals(null), is(false));
        assertThat(sut1.equals(""), is(false));
        //CHECKSTYLE:ON

        assertThat(sut1.equals(sut1), is(true));
        assertThat(sut1.equals(sut2), is(true));
        assertThat(sut2.equals(sut1), is(true));
        assertThat(sut2.equals(sut2), is(true));

        assertThat(sut3.equals(sut3), is(true));
        assertThat(sut3.equals(sut1), is(false));
        assertThat(sut3.equals(sut2), is(false));
    }

    @Test
    public void testToString() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        assertThat(new JvfsFilePermissions(permissions).toString(), is(equalTo("---------")));
        permissions.add(PosixFilePermission.OWNER_READ);
        permissions.add(PosixFilePermission.OWNER_WRITE);
        assertThat(new JvfsFilePermissions(permissions).toString(), is(equalTo("rw-------")));
        permissions.add(PosixFilePermission.GROUP_EXECUTE);
        assertThat(new JvfsFilePermissions(permissions).toString(), is(equalTo("rw---x---")));
        permissions.add(PosixFilePermission.OTHERS_READ);
        permissions.add(PosixFilePermission.OTHERS_WRITE);
        permissions.add(PosixFilePermission.OTHERS_EXECUTE);
        assertThat(new JvfsFilePermissions(permissions).toString(), is(equalTo("rw---xrwx")));
    }

    @Test
    public void copy() {
        final Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
        permissions.add(PosixFilePermission.OWNER_READ);
        permissions.add(PosixFilePermission.OWNER_WRITE);
        permissions.add(PosixFilePermission.GROUP_EXECUTE);
        final JvfsFilePermissions sut = new JvfsFilePermissions(permissions);

        assertThat(sut.ownerRead(), is(true));
        assertThat(sut.ownerWrite(), is(true));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(true));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));

        final JvfsFilePermissions copy = sut.copy();
        assertThat(copy.ownerRead(), is(true));
        assertThat(copy.ownerWrite(), is(true));
        assertThat(copy.ownerExecute(), is(false));
        assertThat(copy.groupRead(), is(false));
        assertThat(copy.groupWrite(), is(false));
        assertThat(copy.groupExecute(), is(true));
        assertThat(copy.othersRead(), is(false));
        assertThat(copy.othersWrite(), is(false));
        assertThat(copy.othersExecute(), is(false));

        assertThat(sut, is(equalTo(copy)));
        assertThat(sut, is(not(sameInstance(copy))));
    }

    @SuppressWarnings("unchecked")
    @Test public void forVsalue_withNullCreatesDefault() {
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(null);
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

    @Test public void forVsalue_withEmptyCreatesDefault() {
        final JvfsFilePermissions sut = JvfsFilePermissions.forValue(new FileAttribute[] {});
        assertThat(sut.ownerRead(), is(false));
        assertThat(sut.ownerWrite(), is(false));
        assertThat(sut.ownerExecute(), is(false));
        assertThat(sut.groupRead(), is(false));
        assertThat(sut.groupWrite(), is(false));
        assertThat(sut.groupExecute(), is(false));
        assertThat(sut.othersRead(), is(false));
        assertThat(sut.othersWrite(), is(false));
        assertThat(sut.othersExecute(), is(false));
    }

}
