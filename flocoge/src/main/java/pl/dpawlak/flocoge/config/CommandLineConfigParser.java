package pl.dpawlak.flocoge.config;

import java.io.File;

/**
 * Created by dpawlak on Dec 14, 2014
 */
public class CommandLineConfigParser {
    
    private File diagramPath;
    private File srcFolder;
    private String packageName;
    private Configuration configuration;
    
    public boolean parse(String[] args) {
        if (args.length >= 3) {
            boolean result = true;
            for (int i = 0; i < args.length - 3; i++) {
                if ("--help".equals(args[i])) {
                    printHelp();
                    return false;
                }
            }
            if (result) {
                result = parseDiagramPath(args[args.length - 3]);
            }
            if (result) {
                result = parseSrcFolder(args[args.length - 2]);
            }
            if (result) {
                result = parsePackageName(args[args.length - 1]);
            }
            if (result) {
                configuration = new Configuration(diagramPath, srcFolder, packageName);
            }
            return result;
        } else {
            printHelp();
            return false;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }
    
    private boolean parseDiagramPath(String path) {
        diagramPath = new File(path);
        if (diagramPath.exists() && diagramPath.isFile()) {
            return true;
        } else {
            System.err.println("Diagram file does not exist (" + path + ")");
            return false;
        }
    }
    
    private boolean parseSrcFolder(String path) {
        srcFolder = new File(path);
        if (srcFolder.exists() && srcFolder.isDirectory()) {
            return true;
        } else {
            System.err.println("Source folder does not exist (" + path + ")");
            return false;
        }
    }
    
    private boolean parsePackageName(String name) {
        if (name.matches("[a-z]{2,3}(\\.[a-zA-Z][a-zA-Z_$0-9]*)*")) {
            packageName = name;
            return true;
        } else {
            System.err.println("Invalid package name (" + name + ")");
            return false;
        }
    }
    
    private void printHelp() {
        System.out.println("Draw.io Flowchart Code Generator");
        System.out.println("Usage:");
        System.out.println("[flags] <diagram path> <generated source folder> <package name>");
        System.out.println("Flags:");
        System.out.println("--help - print this help message");
    }
}

