package com.wehear.util;

import java.text.Normalizer;

public class SlugUtil {
	
	public static String toSlug(String input) {
        if (input == null) return "";
        
        String slug = input.replace("đ", "d").replace("Đ", "D");
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        slug = slug.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        return slug;
    }

}
