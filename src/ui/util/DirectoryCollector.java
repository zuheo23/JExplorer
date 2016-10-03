/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;

/**
 *
 * @author oheo
 */
public class DirectoryCollector {

    public static class PrintDirectories
            extends SimpleFileVisitor<Path> {

        public static TreeItem<String> root = null;
        public static TreeItem<String> ptr = null;
        public static ObservableList<String> files = null;
        public static Path rootPath = null;
        public static Path ptrPath = null;
        public static Set<String> tracker = new HashSet<String>();
        public static int rootNameCount = 0;


        private TreeItem<String> findChild(String name, List<TreeItem<String>> list) {
            for (TreeItem<String> item : list) {
                if (item.getValue().equals(name)) {
                    return item;
                }
            }
            return null;
        }

        public PrintDirectories() {
             
            root = null;
            ptr = null;
            rootPath = null;
            ptrPath = null;
            tracker = new HashSet<String>();
            rootNameCount = 0;
            files = null;
            files = FXCollections.observableArrayList();
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.getParent().equals(rootPath)) {
                System.out.format("FILE: %s%n",file.getFileName());
                files.add(file.toString());
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            Logger.getLogger(PrintDirectories.class.getName()).log(Level.SEVERE, exc.getMessage());
            return CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            
            if (root == null) {
                root = new TreeItem<String>(dir.toString());
                rootPath = dir;
                rootNameCount = dir.getNameCount();
                tracker.add(rootPath.toString());
            } else if (dir.getNameCount() - rootNameCount > 0) {
                ptr = root;
                ptrPath = rootPath;
                
                Iterator<Path> it = rootPath.relativize(dir).iterator();
                while (it.hasNext()) {
                    Path next = it.next();
                    ptrPath = ptrPath.resolve(next);
                    
                    System.out.format("%s%n", ptrPath.toAbsolutePath().toString());
                    if (!tracker.contains(ptrPath.toString())) {
                        ptr.getChildren().add(new TreeItem<String>(ptrPath.toAbsolutePath().toString()));
                        tracker.add(ptrPath.toString());
                        ptr = findChild(ptrPath.toAbsolutePath().toString(), ptr.getChildren());
                    } else {
                        ptr = findChild(ptrPath.toAbsolutePath().toString(), ptr.getChildren());
                    }
                    
                }
            }

            return CONTINUE;
        }
        


    }
}
