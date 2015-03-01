package pl.dpawlak.flocoge.config;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dpawlak on Dec 14, 2014
 */
public class CommandLineConfigParser {
    
    private File diagramPath;
    private File srcFolder;
    private String packageName;
    private String diagramName;
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
                configuration = new Configuration(diagramPath, srcFolder, diagramName, packageName);
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
            String fileName = diagramPath.getName();
            int lastDotPosition = fileName.lastIndexOf('.');
            diagramName = lastDotPosition > 0 ? fileName.substring(0, lastDotPosition) : fileName;
            if (diagramName.matches("[^a-zA-Z]*[a-zA-Z].*")) {
                convertDiagramName();
                return true;
            } else {
                System.err.println("Diagram file name can not be used as a Java class name (" + fileName + ")");
                return false;
            }
        } else {
            System.err.println("Diagram file does not exist (" + path + ")");
            return false;
        }
    }
    
    private boolean parseSrcFolder(String path) {
        srcFolder = new File(path);
        if (srcFolder.exists()) {
            if (srcFolder.isDirectory()) {
                return true;
            } else {
                System.err.println("Sources folder is not a directory (" + path + ")");
                return false;
            }
        } else {
            if (srcFolder.mkdirs()) {
                return true;
            } else {
                System.err.println("Sources folder could not be created (" + path + ")");
                return false;
            }
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
    
    private void convertDiagramName() {
        String[] parts = diagramName.replaceAll("\\W", " ").trim().split(" ");
        StringBuilder nameBuilder = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                nameBuilder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
            }
        }
        diagramName = nameBuilder.toString();
        if (diagramName.matches("\\d+.*")) {
            Matcher matcher = Pattern.compile("\\d+").matcher(diagramName);
            matcher.find();
            String digits = matcher.group();
            int position = matcher.end();
            nameBuilder = new StringBuilder(diagramName.length() + 1);
            nameBuilder
                .append(Character.toUpperCase(diagramName.charAt(position)))
                .append(diagramName.substring(position + 1))
                .append('_')
                .append(digits);
            diagramName = nameBuilder.toString();
        }
    }
    
    private void printHelp() {
        System.out.println("Draw.io Flowchart Code Generator");
        System.out.println("Usage:");
        System.out.println("[flags] <diagram path> <generated sources folder> <package name>");
        System.out.println("Flags:");
        System.out.println("--help - print this help message");
    }
}

