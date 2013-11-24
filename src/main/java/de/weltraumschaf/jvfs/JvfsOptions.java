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
 final JvfsOptions opts = JvfsOptions.builder()
      .readonly(true)
      .capacity("1k")
      .create();
 </code>
 *
 * @author Sven Strittmatter <weltraumschaf@googlemail.com>
 */
public final class JvfsOptions {

    /**
     * Default options.
     */
    public static final JvfsOptions DEFAULT = builder().create();
    /**
     * Holds the options.
     */
    private final Map<String, Object> env;

    /**
     * Dedicated constructor.
     *
     * @param env must not be {@literal null}
     */
    private JvfsOptions(final Map<String, Object> env) {
        super();
        JvfsAssertions.notNull(env, "env");
        this.env = env;
    }

    /**
     * Get the options as map.
     *
     * @return never {@literal null}
     */
    public Map<String, Object> getEnv() {
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
    public static JvfsOptions fromValue(final Map<String, Object> env) {
        return new JvfsOptions(env);
    }

    /**
     * Option which indicates if the file system is readonly or not.
     *
     * @return by default {@literal false}
     */
    public boolean isReadonly() {
//        return Option.READONLY.type.cast(env.get(Option.READONLY.key));
        if (env.containsKey(Option.READONLY.key)) {
            return (boolean) env.get(Option.READONLY.key);
        }

        return false;
    }

    /**
     * Get the capacity of the file system.
     *
     * @return never {@literal null}, {@link JvfsQuantity#EMPTY} by default
     */
    public JvfsQuantity getCapacity() {
        if (env.containsKey(Option.CAPACITY.key)) {
            return (JvfsQuantity) env.get(Option.CAPACITY.key);
        }

        return JvfsQuantity.EMPTY;
    }

    /**
     * Builder to create options.
     */
    public static final class Builder {

        /**
         * Capacity for created options.
         */
        private JvfsQuantity capacity = JvfsQuantity.EMPTY;
        /**
         * Readonly flag for created options.
         */
        private boolean readOnly;

        /**
         * Use {@link JvfsOptions#builder()} to get instance.
         */
        private Builder() {
            super();
        }

        /**
         * Set the capacity.
         *
         * @param qunatity see {@link JvfsQuantity#forValue()}
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
         * Create a new options instance.
         *
         * If you call this method without setting any option by {@link #capacity(java.lang.String)} or
         * {@link #readonly(boolean)} then an instance equal to {@link JvfsOptions#DEFAULT} will be created.
         *
         * @return never {@code null}, always new instance
         */
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
