package org.cdc.framework.utils;

import java.io.File;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/9
 */
public class Files {
    /**
     * Returns the file name without its <a
     * href="http://en.wikipedia.org/wiki/Filename_extension">file extension</a> or path. This is
     * similar to the {@code basename} unix command. The result does not include the '{@code .}'.
     *
     * @param file The name of the file to trim the extension from. This can be either a fully
     *     qualified file name (including a path) or just a file name.
     * @return The file name without its path or extension.
     * @since 14.0
     */
    public static String getNameWithoutExtension(String file) {
        String fileName = new File(file).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    /**
     * Returns the <a href="http://en.wikipedia.org/wiki/Filename_extension">file extension</a> for
     * the given file name, or the empty string if the file has no extension. The result does not
     * include the '{@code .}'.
     *
     * <p><b>Note:</b> This method simply returns everything after the last '{@code .}' in the file's
     * name as determined by {@link File#getName}. It does not account for any filesystem-specific
     * behavior that the {@link File} API does not already account for. For example, on NTFS it will
     * report {@code "txt"} as the extension for the filename {@code "foo.exe:.txt"} even though NTFS
     * will drop the {@code ":.txt"} part of the name when the file is actually created on the
     * filesystem due to NTFS's <a
     * href="https://learn.microsoft.com/en-us/archive/blogs/askcore/alternate-data-streams-in-ntfs">Alternate
     * Data Streams</a>.
     *
     * @since 11.0
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}
