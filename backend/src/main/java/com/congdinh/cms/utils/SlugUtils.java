package com.congdinh.cms.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for generating URL-friendly slugs from text.
 */
public final class SlugUtils {
    
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-+");
    
    private SlugUtils() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Generate a URL-friendly slug from the given text.
     * Handles Vietnamese characters and special characters.
     * 
     * Example: "Tin Tức Công Nghệ" -> "tin-tuc-cong-nghe"
     * 
     * @param input the text to convert
     * @return the generated slug
     */
    public static String toSlug(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        
        String result = input.trim().toLowerCase(Locale.ROOT);
        
        // Convert Vietnamese characters to ASCII equivalents
        result = removeVietnameseAccents(result);
        
        // Replace whitespace with dashes
        result = WHITESPACE.matcher(result).replaceAll("-");
        
        // Remove non-latin characters except dashes
        result = NONLATIN.matcher(result).replaceAll("");
        
        // Replace multiple dashes with single dash
        result = MULTIPLE_DASHES.matcher(result).replaceAll("-");
        
        // Remove leading/trailing dashes
        result = result.replaceAll("(^-+)|(-+$)", "");
        
        return result;
    }
    
    /**
     * Remove Vietnamese diacritical marks (accents) from text.
     * 
     * @param text the Vietnamese text
     * @return text with accents removed
     */
    private static String removeVietnameseAccents(String text) {
        // Special Vietnamese character mappings
        text = text.replace("đ", "d").replace("Đ", "d");
        
        // Normalize and remove combining diacritical marks
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        
        return pattern.matcher(normalized).replaceAll("");
    }
    
    /**
     * Generate a unique slug by appending a suffix if the base slug already exists.
     * 
     * @param baseSlug the base slug
     * @param suffix the numeric suffix to append
     * @return the unique slug
     */
    public static String toSlugWithSuffix(String baseSlug, int suffix) {
        return baseSlug + "-" + suffix;
    }
}
