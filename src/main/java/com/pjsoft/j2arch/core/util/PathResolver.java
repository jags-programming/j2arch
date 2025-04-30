package com.pjsoft.j2arch.core.util;

import java.io.File;

/**
 * PathResolver
 * 
 * Utility class for resolving file paths by combining a base directory with a subdirectory.
 * This class simplifies the process of constructing platform-independent file paths.
 * 
 * Responsibilities:
 * - Resolve and construct file paths by appending a subdirectory to a base directory.
 * - Ensure platform-independent path construction using the appropriate file separator.
 * 
 * Limitations:
 * - Assumes that the provided base directory and subdirectory are valid strings.
 * - Does not validate the existence of the resolved path.
 * 
 * Usage Example:
 * {@code
 * String resolvedPath = PathResolver.resolvePath("/output", "images");
 * System.out.println(resolvedPath); // Outputs: /output/images (on Unix-like systems)
 * }
 * 
 * Author: PJSoft
 * Since: 1.0
 */
public class PathResolver {

    /**
     * Resolves a file path by appending a subdirectory to a base directory.
     * 
     * Responsibilities:
     * - Combine the base directory and subdirectory into a single path.
     * - Use the platform-specific file separator for path construction.
     * 
     * @param baseOutputDir The base directory.
     * @param subDir        The subdirectory to append to the base directory.
     * @return The resolved file path as a string.
     */
    public static String resolvePath(String baseOutputDir, String subDir) {
        return baseOutputDir + File.separator + subDir;
    }
}
