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

import java.util.Map;

/**
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public class JvfsOptions {

    public static final JvfsOptions DEFAULT = builder().create();
    private final Map<String, Object> env;

    public JvfsOptions(final Map<String, Object> env) {
        super();
        this.env = env;
    }

    public Map<String, Object> getEnv() {
        return env;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static JvfsOptions fromValue(final Map<String, Object> env) {
        return new JvfsOptions(env);
    }

    public boolean isReadOnly() {
//        return Option.READONLY.type.cast(env.get(Option.READONLY.key));
        return (boolean)env.get(Option.READONLY.key);
    }

    public static final class Builder {

        private JvfsQuantity capacity = JvfsQuantity.EMPTY;
        private boolean readOnly;

        private Builder() {
            super();
        }

        public Builder capacity(final String qunatity) {
            capacity = JvfsQuantity.forValue(qunatity);
            return this;
        }

        public Builder readOnly(final boolean flag) {
            readOnly = flag;
            return this;
        }

        public JvfsOptions create() {
            final Map<String, Object> env = JvfsCollections.newHashMap();
            env.put(Option.CAPACITY.key, capacity);
            env.put(Option.READONLY.key, readOnly);
            return new JvfsOptions(env);
        }
    }

    private enum Option {
        CAPACITY("capacity", JvfsQuantity.class),
        READONLY("readonly", Boolean.class);
        private final String key;
        private final Class<?> type;

        Option(final String key, final Class<?> type) {
            this.key = key;
            this.type = type;
        }

    }
}
