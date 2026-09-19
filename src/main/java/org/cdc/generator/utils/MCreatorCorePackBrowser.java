package org.cdc.generator.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.zip.ZipFile;

import static org.cdc.generator.utils.Constants.ARCHIVE_EXTENSIONS;

/**
 * &#064;Developer  user
 * &#064;CreatedIn  2026/9/18
 */
public class MCreatorCorePackBrowser {
    private static MCreatorCorePackBrowser INSTANCE;

    public static MCreatorCorePackBrowser getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MCreatorCorePackBrowser();
        return INSTANCE;
    }

    private final File corePackPath;

    private MCreatorCorePackBrowser(){
        corePackPath = Utils.tryToFindCorePlugin();
    }

    public File getCorePackPath() {
        return corePackPath;
    }

    public InputStream readPath(String path) throws IOException {
        var s = split(path);
        if (s.length == 1){
            return new FileInputStream(path);
        } else if (s.length == 2){
            ZipFile zipFile = new ZipFile(s[0]);
            var ent = zipFile.getEntry(s[1]);
            var input = zipFile.getInputStream(ent);
            zipFile.close();
            return input;
        }
        return null;
    }

    public File generatePreviewFile(String path) throws IOException {
        var temp = File.createTempFile("preview","."+ FilenameUtils.getExtension(path));
        temp.deleteOnExit();
        Files.copy(readPath(path),temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return temp;
    }

    private String[] split(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            throw new IllegalArgumentException("The path can not be null or empty");
        }

        String lower = rawPath.toLowerCase(Locale.ROOT);

        // 从左到右扫描，找到第一个“压缩包扩展名 + 路径分隔符”的位置
        for (int i = 0; i < rawPath.length(); i++) {
            for (String ext : ARCHIVE_EXTENSIONS) {
                if (lower.startsWith(ext, i)) {
                    int afterExt = i + ext.length();

                    // 必须是 .zip\ 或 .zip/ 或 .zip 结尾，避免把 foo.zip.txt 误判
                    if (afterExt == rawPath.length() || rawPath.charAt(afterExt) == '\\'
                            || rawPath.charAt(afterExt) == '/') {

                        String archivePath = rawPath.substring(0, afterExt);
                        String entryPath = rawPath.substring(afterExt);

                        // 去掉 entryPath 开头的一个分隔符
                        if (entryPath.startsWith("\\") || entryPath.startsWith("/")) {
                            entryPath = entryPath.substring(1);
                        }

                        // 压缩包内部路径统一使用 /
                        entryPath = entryPath.replace('\\', '/');

                        return new String[] { archivePath, entryPath };
                    }
                }
            }
        }

        // 没有找到压缩包边界，整个路径视为普通文件
        return new String[] { rawPath };
    }
}
