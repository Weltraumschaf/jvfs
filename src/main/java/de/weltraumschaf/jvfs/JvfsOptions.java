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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Holds options of a JVFS file system.
 *
 * To create options use the builder:<br/>
 * <code>
 final JvfsOptions opts = JvfsOptions.builder()
      .readonly(true)
      .capacity("1k")
      .identifier("a name")
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
    private final Map<String, ?> env;

    /**
     * Dedicated constructor.
     *
     * @param env must not be {@literal null}
     */
    JvfsOptions(final Map<String, ?> env) {
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
        return Collections.unmodifiableMap(env);
    }

    @Override
    public int hashCode() {
        return env.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof JvfsOptions)) {
            return false;
        }

        final JvfsOptions other = (JvfsOptions) obj;
        return env.equals(other.env);
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(getClass().getSimpleName() + "{");
        boolean first = true;

        for (final Map.Entry<String, ?> entry : env.entrySet()) {
            if (!first) {
                buffer.append(", ");
            }

            buffer.append(entry.getKey()).append("=").append(Objects.toString(entry.getValue()));
            first = false;
        }

        buffer.append('}');
        return buffer.toString();
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
     * @return by default {@literal false}
     */
    public boolean isReadonly() {
        if (env.containsKey(Option.READONLY.key)) {
            final Object value = env.get(Option.READONLY.key);

            if (value instanceof String) {
                return Boolean.valueOf((String) value);
            } else if (value instanceof Boolean) {
                return (Boolean) value;
            } else {
                throw new IllegalArgumentException(Objects.toString(value));
            }

        }

        return Builder.DEFAULT_READONLY;
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
                throw new IllegalArgumentException(Objects.toString(value));
            }
        }

        return Builder.DEFAULT_CAPACITY;
    }

    /**
     * Get the id of the file system.
     *
     * @return never {@literal null}, empty string by default
     */
    public String identifier() {
        if (env.containsKey(Option.ID.key)) {
            return Objects.toString(env.get(Option.ID.key));
        }

        return Builder.DEFAULT_ID;
    }

    /**
     * Builder to create options.
     */
    public static final class Builder {

        /**
         * Default value for capacity option.
         */
        private static final JvfsQuantity DEFAULT_CAPACITY = JvfsQuantity.EMPTY;
        /**
         * Default value for readonly option.
         */
        private static final boolean DEFAULT_READONLY = false;
        /**
         * Default value for identifier option.
         */
        private static final String DEFAULT_ID = "";
        /**
         * Capacity for created options.
         */
        private JvfsQuantity capacity = DEFAULT_CAPACITY;
        /**
         * Readonly flag for created options.
         */
        private boolean readOnly = DEFAULT_READONLY;
        /**
         * Id for created options.
         */
        private String identifier = DEFAULT_ID;

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
         * Set the capacity.
         *
         * @param id must not be {@code null}
         * @return builder itself
         */
        public Builder identifier(final String id) {
            JvfsAssertions.notNull(id, "id");
            this.identifier = id;
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
            env.put(Option.ID.key, identifier);
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
     * // ...
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
         * Key for identifier.
         */
        ID("id");
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
