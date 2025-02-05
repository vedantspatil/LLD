import java.util.*;

// Base class representing a file or directory
class FileNode {
    String name;
    boolean isDirectory;
    String path;
    
    FileNode(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.path = name;
    }
    
    String getPath() {
        return path;
    }
}

// Represents a Directory, which can have children nodes
class Directory extends FileNode {
    List<FileNode> children;
    
    Directory(String name) {
        super(name, true);
        children = new ArrayList<>();
    }
    
    void add(FileNode node) {
        children.add(node);
    }
}

// Represents a File, which has a size
class File extends FileNode {
    int size;
    
    File(String name, int size) {
        super(name, false);
        this.size = size;
    }
}

// Constraint Interface
interface Constraint {
    boolean isSatisfied(File file);
}

// Concrete constraints
class NameConstraint implements Constraint {
    String name;
    
    NameConstraint(String name) {
        this.name = name;
    }
    
    public boolean isSatisfied(File file) {
        return file.name.equals(name);
    }
}

class SizeConstraint implements Constraint {
    int minSize, maxSize;
    
    SizeConstraint(int minSize, int maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
    }
    
    public boolean isSatisfied(File file) {
        return file.size >= minSize && file.size <= maxSize;
    }
}

class ExtensionConstraint implements Constraint {
    String extension;
    
    ExtensionConstraint(String extension) {
        this.extension = extension;
    }
    
    public boolean isSatisfied(File file) {
        return file.name.endsWith(extension);
    }
}

// Main class for searching files
class Search {
    List<String> search(FileNode root, List<Constraint> constraints) {
        Queue<FileNode> queue = new LinkedList<>();
        queue.add(root);
        List<String> matches = new ArrayList<>();
        
        while (!queue.isEmpty()) {
            FileNode currentNode = queue.poll();
            
            if (currentNode instanceof File) {
                File file = (File) currentNode;
                boolean satisfiesAll = true;
                for (Constraint constraint : constraints) {
                    if (!constraint.isSatisfied(file)) {
                        satisfiesAll = false;
                        break;
                    }
                }
                if (satisfiesAll) {
                    matches.add(file.getPath());
                }
            }
            
            if (currentNode instanceof Directory) {
                Directory directory = (Directory) currentNode;
                queue.addAll(directory.children);
            }
        }
        return matches;
    }
}

public class Main {
    public static void main(String[] args) {
        // Creating directory structure
        Directory root = new Directory("root");
        File file1 = new File("file1.txt", 500);
        File file2 = new File("file2.mp4", 200);
        Directory dir1 = new Directory("dir1");
        
        root.add(file1);
        root.add(file2);
        root.add(dir1);
        
        // Search for files with constraints
        Search searchApi = new Search();
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(new SizeConstraint(0, 500));  // Size range
        constraints.add(new ExtensionConstraint(".mp4"));  // File extension
        
        List<String> results = searchApi.search(root, constraints);
        for (String result : results) {
            System.out.println(result);
        }
    }
}
