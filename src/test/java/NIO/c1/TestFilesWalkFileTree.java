package NIO.c1;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class TestFilesWalkFileTree {
    public static void main(String[] args) throws IOException {
        //删除文件夹，内部有文件则无法删除
        //Files.delete(Paths.get("G:\\test"));
        Files.walkFileTree(Paths.get("G:\\test"),new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                //System.out.println(file);
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                //System.out.println("<============退出"+dir);
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }
}
