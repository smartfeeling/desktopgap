package org.smartly.application.desktopgap.impl.app.utils;

import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;
import org.smartly.commons.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

/**
 * .
 */
public class Utils {

    // ------------------------------------------------------------------------
    //                      c o n s t a n t s
    // ------------------------------------------------------------------------

    private static final String APP_EXT = IDesktopConstants.APP_EXT;
    private static final String MANIFEST = IDesktopConstants.FILE_MANIFEST;
    private static final String INSTALL_DIR = IDesktopConstants.INSTALL_DIR;
    private static final String TEMP_DIR = IDesktopConstants.TEMP_DIR;


    // ------------------------------------------------------------------------
    //                      f i l e s
    // ------------------------------------------------------------------------

    public static boolean isManifest(final String path) {
        final String unixPath = PathUtils.toUnixPath(path);
        return PathUtils.getFilename(unixPath).equalsIgnoreCase(MANIFEST);
    }

    public static boolean isAppFolder(final String path) {
        final String manifest = PathUtils.concat(path, MANIFEST);
        return PathUtils.exists(manifest);
    }

    public static Set<String> getAppDirectories(final String root) throws IOException {
        final Set<String> files = new HashSet<String>();
        if (isAppFolder(root)) {
            files.add(root);
        } else {
            final Set<FileVisitOption> options = new HashSet<FileVisitOption>();
            options.add(FileVisitOption.FOLLOW_LINKS);
            Files.walkFileTree(Paths.get(root), options, 2, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(final Path file, final BasicFileAttributes attrs)
                        throws IOException {
                    final String path = PathUtils.toUnixPath(file.toString());
                    if (!path.equalsIgnoreCase(root) && isAppFolder(path)) {
                        files.add(path);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return files;
    }

    public static Set<String> getDirectories(final String root) throws IOException {
        final Set<String> files = new HashSet<String>();
        final Set<FileVisitOption> options = new HashSet<FileVisitOption>();
        options.add(FileVisitOption.FOLLOW_LINKS);
        Files.walkFileTree(Paths.get(root), options, 2, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                final String path = PathUtils.toUnixPath(file.toString());
                if (!path.equalsIgnoreCase(root)) {
                    files.add(path);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }

    public static Set<String> getFiles(final String root) throws IOException {
        final Set<String> files = new HashSet<String>();
        Files.walkFileTree(Paths.get(root), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                final String path = PathUtils.toUnixPath(file.toString());
                if (!path.equalsIgnoreCase(root)) {
                    files.add(path);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }

    public static void copyToInstallFolder(final String fileName,
                                           final boolean move) throws IOException {
        final String destination = PathUtils.concat(Smartly.getAbsolutePath(INSTALL_DIR),
                PathUtils.getFilename(fileName, true));
        final File source = new File(fileName);
        FileUtils.copy(source, new File(destination));
        if (move) {
            source.delete();
        }
    }

    // ------------------------------------------------------------------------
    //                      a p p s
    // ------------------------------------------------------------------------

    public static boolean isPackage(final String packagePath) {
        return APP_EXT.equalsIgnoreCase(PathUtils.getFilenameExtension(packagePath, true));
    }

    public static boolean install(final String packagePath,
                                  final String appFolder) throws IOException {
        if (StringUtils.hasText(packagePath) && StringUtils.hasText(appFolder)) {
            // remove old (need keep clean between versions)
            FileUtils.delete(appFolder);

            // unzip new
            ZipUtils.unzip(packagePath, appFolder);

            // remove source from install dir
            Files.delete(Paths.get(packagePath));

            return true;
        }
        return false;
    }


}
