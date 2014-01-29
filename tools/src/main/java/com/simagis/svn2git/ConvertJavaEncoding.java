package com.simagis.svn2git;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;

/**
 * @author Alexei Vylegzhanin alexei.vylegzhanin@gmail.com
 */
public class ConvertJavaEncoding {

    public static void main(String[] args) throws IOException {
        final Path out = Paths.get("../trunk-svn2git-utf8-out");
        Files.createDirectory(out);

        final Charset srcCharset = Charset.forName("windows-1251");
        final Charset dstCharset = Charset.forName("UTF-8");
        final Charset isoCharset = Charset.forName("ISO-8859-1");

        final Path srcRoot = Paths.get("../trunk").toRealPath();
        final Path dstRoot = out.toRealPath();
        Files.walkFileTree(srcRoot, Collections.<FileVisitOption>emptySet(), 64, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.getFileName().toString().equalsIgnoreCase(".svn")) return FileVisitResult.SKIP_SUBTREE;
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                final String name = file.getFileName().toString();
                if (name.endsWith(".java")) {
                    final String isoStr = new String(Files.readAllBytes(file), isoCharset);
                    final String srcStr = new String(Files.readAllBytes(file), srcCharset);
                    if (!isoStr.equals(srcStr)) {
                        final Path srcRel = srcRoot.relativize(file);
                        System.out.println("> " + srcRel);
                        final Path dstFile = dstRoot.resolve(srcRel);
                        Files.createDirectories(dstFile.getParent());
                        Files.copy(new ByteArrayInputStream(srcStr.getBytes(dstCharset)), dstFile);
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
