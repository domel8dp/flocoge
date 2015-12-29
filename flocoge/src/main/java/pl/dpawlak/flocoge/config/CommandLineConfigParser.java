package pl.dpawlak.flocoge.config;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.dpawlak.flocoge.diagram.ModelNamesUtils;
import pl.dpawlak.flocoge.log.Logger;

public class CommandLineConfigParser {

    private final Logger log;

    private File diagramPath;
    private File srcFolder;
    private String packageName;
    private String diagramName;
    private Configuration configuration;
    private boolean stacktrace;
    private boolean verbose;
    private boolean printBareModel;
    private boolean printModel;
    private boolean trace;

    public CommandLineConfigParser(Logger log) {
        this.log = log;
    }

    public boolean parse(String[] args) {
        if (args.length >= 3) {
            boolean result = parseFlags(args);
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
                configuration = new Configuration(diagramPath, srcFolder, diagramName, packageName, stacktrace, verbose,
                    printBareModel, printModel, trace);
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
                log.error("Diagram file name can not be used as a Java class name ({})", fileName);
                return false;
            }
        } else {
            log.error("Diagram file does not exist ({})", path);
            return false;
        }
    }

    private boolean parseFlags(String[] args) {
        for (int i = 0; i < args.length - 3; i++) {
            String arg = args[i];
            if ("--help".equals(arg) || "-h".equals(arg)) {
                printHelp();
                return false;
            } else if ("--stacktrace".equals(arg) || "-s".equals(arg)) {
                stacktrace = true;
            } else if ("--verbose".equals(arg) || "-v".equals(arg)) {
                verbose = true;
            } else if ("--print-bare".equals(arg) || "-b".equals(arg)) {
                printBareModel = true;
            } else if ("--print-model".equals(arg) || "-p".equals(arg)) {
                printModel = true;
            } else if ("--trace".equals(arg) || "-t".equals(arg)) {
                trace = true;
                stacktrace = true;
            } else {
                log.error("Unrecognized flag: ({}), use --help to see available options", arg);
                return false;
            }
        }
        return true;
    }

    private boolean parseSrcFolder(String path) {
        srcFolder = new File(path);
        if (srcFolder.exists()) {
            if (srcFolder.isDirectory()) {
                return true;
            } else {
                log.error("Sources folder is not a directory ({})", path);
                return false;
            }
        } else {
            if (srcFolder.mkdirs()) {
                return true;
            } else {
                log.error("Sources folder could not be created ({})", path);
                return false;
            }
        }
    }

    private boolean parsePackageName(String name) {
        boolean result = name.matches("[a-zA-Z_$][a-zA-Z_$0-9]*(\\.[a-zA-Z_$][a-zA-Z_$0-9]*)*");
        if (result) {
            packageName = name;
            for (String part : name.split("\\.")) {
                result &= !ModelNamesUtils.isReservedWord(part);
            }
        }
        if (!result) {
            log.error("Invalid package name ({})", name);
        }
        return result;
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
        log.log("-------");
        log.log("Draw.io Flowchart Code Generator");
        log.log("-------");
        log.log("Usage:");
        log.log("[flags] <diagram path> <output folder> <package name>");
        log.log("");
        log.log("Flags:");
        log.log("--help -h : print this help message");
        log.log("--verbose -v : print some information about execution");
        log.log("");
        log.log("Troubleshooting flags:");
        log.log("--print-bare -b : print bare model before validation and transformation");
        log.log("--print-model -p : print model after validation and transformation");
        log.log("--stacktrace -s : print full stacktrace for exception");
        log.log("--trace -t : print every step, implies --verbose and --stacktrace flags");
        log.log("-------");
    }
}
