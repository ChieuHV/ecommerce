package com.ecommerce.product.util;

import com.github.slugify.Slugify;

public final class SlugUtil {

    private static final Slugify SLUGIFY = Slugify.builder().build();

    private SlugUtil() {
    }

    public static String toSlug(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return SLUGIFY.slugify(value);
    }
}