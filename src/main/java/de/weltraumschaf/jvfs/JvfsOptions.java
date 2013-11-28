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
 * Holds options of a JVFS file system.
 *
 * To create options use the builder:<br/>
 * <code>
 * final JvfsOptions opts = JvfsOptions.builder()
 *      .readonly(true)
 *      .capacity("1k")
 *      .create();
 * </code>
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public final class JvfsOptions {

    /**
     * Default options.
     */
    public static final JvfsOptions DEFAULT = builder().create();
    /**
     * Default for read only option.
     */
    private static final boolean DEFAULT_READONLY = false;
    /**
     * Default for auto mount option.
     */
    private static final boolean DEFAULT_AUTOMOUNT = true;
    /**
     * Default for capacity option.
     */
    private static final JvfsQuantity DEFAULT_EMPTY = JvfsQuantity.EMPTY;
    /**
     * Holds the options.
     */
    private final Map<String, ?> env;

    /**
     * Dedicated constructor.
     *
     * @param env must not be {@literal null}
     */
    private JvfsOptions(final Map<String, ?> env) {
        super();
        JvfsAssertions.notNull(env, "env");
        this.env = env;
    }

    /**
     * Get the options as map.
     *
     * @return never {@literal null}
     */
    public Map<String, ?> getEnv() {
        return env;
    }

    /**
     * Creates new builder.
     *
     * @return never {@literal null}, always new instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates options by a hash map.
     *
     * @param env must not be {@literal null}
     * @return never {@literal null}
     */
    public static JvfsOptions forValue(final Map<String, ?> env) {
        return new JvfsOptions(env);
    }

    /**
     * Option which indicates if the file system is readonly or not.
     *
     * @return by default {@link #DEFAULT_READONLY}
     */
    public boolean isReadonly() {
        if (env.containsKey(Option.READONLY.key)) {
            final Object value = env.get(Option.READONLY.key);
            return castToBoolean(value);
        }

        return DEFAULT_READONLY;
    }

    /**
     * Whether the file system should auto mount a root file system ("/").
     *
     * @return by default {@link #DEFAULT_AUTOMOUNT}
     */
    public boolean isAutoMount() {
        if (env.containsKey(Option.AUTO_MOUNT.key)) {
            final Object value = env.get(Option.AUTO_MOUNT.key);
            return castToBoolean(value);
        }

        return DEFAULT_AUTOMOUNT;
    }

    /**
     * Try to cast a boolean from {@link java.lang.String} or {@link java.lang.Boolean}.
     *
     * Throws an {@link IllegalArgumentException} if unrecognized type.
     *
     * @param value must not be {@literal null}
     * @return {@literal true} for string "true" or {@link java.lang.Boolean#TRUE}, else false
     */
    private boolean castToBoolean(final Object value) {
        JvfsAssertions.notNull(value, "value");

        if (value instanceof String) {
            return Boolean.valueOf((String) value);
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Get the capacity of the file system.
     *
     * @return never {@literal null}, {@link JvfsQuantity#EMPTY} by default
     */
    public JvfsQuantity getCapacity() {
        if (env.containsKey(Option.CAPACITY.key)) {
            final Object value = env.get(Option.CAPACITY.key);

            if (value instanceof JvfsQuantity) {
                return (JvfsQuantity) value;
            } else if (value instanceof String) {
                return JvfsQuantity.forValue((String) value);
            } else if (value instanceof Long) {
                return JvfsQuantity.forValue((Long) value);
            } else {
                throw new IllegalArgumentException();
            }
        }

        return DEFAULT_EMPTY;
    }

    /**
     * Builder to create options.
     */
    public static final class Builder {

        /**
         * Capacity for created options.
         */
        private JvfsQuantity capacity = DEFAULT_EMPTY;
        /**
         * Readonly flag for created options.
         */
        private boolean readOnly = DEFAULT_READONLY;
        /**
         * If a file system for a path is requested and there is no file system, then a root file system ("/") will be
         * automatically mounted.
         */
        private boolean autoMount = DEFAULT_AUTOMOUNT;

        /**
         * Use {@link JvfsOptions#builder()} to get instance.
         */
        private Builder() {
            super();
        }

        /**
         * Set the capacity.
         *
         * @param qunatity see {@link JvfsQuantity#forValue(java.lang.String)}
         * @return builder itself
         */
        public Builder capacity(final String qunatity) {
            capacity = JvfsQuantity.forValue(qunatity);
            return this;
        }

        /**
         * Set the readonly flag.
         *
         * @param flag {@literal true} for readonly file system, else {@literal false}
         * @return builder itself
         */
        public Builder readonly(final boolean flag) {
            readOnly = flag;
            return this;
        }

        /**
         * Set the auto mount flag.
         *
         * @param flag {@literal true} for auto mounted file system, else {@literal false}
         * @return builder itself
         */
        public Builder autoMount(final boolean flag) {
            autoMount = flag;
            return this;
        }

        /**
         * Create a new options instance.
         *
         * If you call this method without setting any option by {@link #capacity(java.lang.String)} or
         * {@link #readonly(boolean)} then an instance equal to {@link JvfsOptions#DEFAULT} will be created.
         *
         * @return never {@literal null}, always new instance
         */
        public JvfsOptions create() {
            final Map<String, Object> env = JvfsCollections.newMap();
            env.put(Option.CAPACITY.key, capacity);
            env.put(Option.READONLY.key, readOnly);
            env.put(Option.AUTO_MOUNT.key, autoMount);
            return new JvfsOptions(env);
        }
    }

    /**
     * Keys for environment map.
     *
     * You can create options like this:<br/>
     * <code>
     * final Map&lt;String, ?&gt; map = new HashMap&lt;String, ?&gt;();
     * map.put(JvfsOption.Option.CAPACITY.key(), "12M");
     * map.put(JvfsOption.Option.READONLY.key(), true);
     *
     * final JvfsOption opts = JvfsOption.forValue(map);
     * </code>
     */
    public enum Option {

        /**
         * Key for capacity.
         */
        CAPACITY("capacity"),
        /**
         * Key for readonly flag.
         */
        READONLY("readonly"),
        /**
         * Key for auto mount flag.
         */
        AUTO_MOUNT("autoMount");

        /**
         * The key for the map.
         */
        private final String key;

        /**
         * Dedicated constructor.
         *
         * @param key must not be {@literal null} or empty
         */
        Option(final String key) {
            assert null != key : "key must be specified";
            assert !key.isEmpty() : "key must not be empty";
            this.key = key;
        }

        /**
         * Get the key.
         *
         * @return never {@literal null} or empty
         */
        public String key() {
            return key;
        }

    }
}
