package com.agi.qa.core;

/**
 * Single source of truth for the Dog API endpoint paths.
 *
 * <p>Paths are relative to the configured base path ({@code /api}).
 * Keeping them here avoids magic strings scattered across the tests.
 */
public final class Endpoints {

    /** GET /breeds/list/all - lists every breed and its sub-breeds. */
    public static final String BREEDS_LIST_ALL = "/breeds/list/all";

    /** GET /breed/{breed}/images - all images for a given breed. */
    public static final String BREED_IMAGES = "/breed/{breed}/images";

    /** GET /breeds/image/random - a single random dog image. */
    public static final String RANDOM_IMAGE = "/breeds/image/random";

    private Endpoints() {
        // Constants holder - not instantiable.
    }
}
