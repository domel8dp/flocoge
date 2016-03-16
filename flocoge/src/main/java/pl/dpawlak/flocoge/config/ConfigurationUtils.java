package pl.dpawlak.flocoge.config;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.dpawlak.flocoge.diagram.ModelNamesUtils;

public final class ConfigurationUtils {
    
    private ConfigurationUtils() { }

    public static boolean diagramExists(File diagramPath) {
        return diagramPath.exists() && diagramPath.isFile();
    }

    public static boolean srcFolderExists(File srcFolder) {
        return srcFolder.exists() && srcFolder.isDirectory();
    }

    public static boolean createIfMissing(File srcFolder) {
        return !srcFolder.exists() && srcFolder.mkdirs();
    }

    public static boolean packageNameValid(String name) {
        boolean result = name.matches("[a-zA-Z_$][a-zA-Z_$0-9]*(\\.[a-zA-Z_$][a-zA-Z_$0-9]*)*");
        if (result) {
            for (String part : name.split("\\.")) {
                result &= !ModelNamesUtils.isReservedWord(part);
            }
        }
        return result;
    }

    public static boolean diagramNameValid(String name) {
        return name.matches("[^a-zA-Z]*[a-zA-Z].*");
    }

    public static boolean diagramNameValid(File diagramPath) {
        return diagramNameValid(getRawDiagramName(diagramPath));
    }

    public static void check(Configuration config) throws InvalidConfigurationException {
        if (!diagramExists(config.diagramPath)) {
            throw new InvalidConfigurationException("Diagram file does not exist (" + config.diagramPath.getPath() +
                ")");
        }
        if (!config.dry && !srcFolderExists(config.srcFolder) && !createIfMissing(config.srcFolder)) {
            throw new InvalidConfigurationException("Sources folder is not available and could not be created (" +
                config.srcFolder.getPath() + ")");
        }
        if (!packageNameValid(config.packageName)) {
            throw new InvalidConfigurationException("Invalid package name (" + config.packageName + ")");
        }
        checkDiagramName(config.diagramPath, config.name);
    }

    public static String getDiagramName(Configuration config) {
        return convertDiagramName(config.name != null ? config.name : getRawDiagramName(config.diagramPath));
    }

    private static void checkDiagramName(File diagramPath, String name) throws InvalidConfigurationException {
        if (name != null) {
            if (!diagramNameValid(name)) {
                throw new InvalidConfigurationException("Given name can not be used as a Java class name (" +
                    name + ")");
            }
        } else if (!diagramNameValid(diagramPath)) {
            throw new InvalidConfigurationException("Diagram file name can not be used as a Java class name (" +
                diagramPath.getName() + ")");
        }
    }

    private static String convertDiagramName(String name) {
        String[] parts = name.replaceAll("\\W", " ").trim().split(" ");
        StringBuilder nameBuilder = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                nameBuilder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
            }
        }
        name = nameBuilder.toString();
        if (name.matches("\\d+.*")) {
            Matcher matcher = Pattern.compile("\\d+").matcher(name);
            matcher.find();
            String digits = matcher.group();
            int position = matcher.end();
            nameBuilder = new StringBuilder(name.length() + 1);
            nameBuilder
                .append(Character.toUpperCase(name.charAt(position)))
                .append(name.substring(position + 1))
                .append('_')
                .append(digits);
            name = nameBuilder.toString();
        }
        return name;
    }

    private static String getRawDiagramName(File diagramPath) {
        String fileName = diagramPath.getName();
        int lastDotPosition = fileName.lastIndexOf('.');
        return lastDotPosition > 0 ? fileName.substring(0, lastDotPosition) : fileName;
    }
}
