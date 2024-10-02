import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.List;

public class LogProcessor{

    // Define the queue and stack
    private Queue<String> logQueue = new LinkedList<>();
    private Stack<String> errorStack = new Stack<>();

    // Counters for different log levels
    private int infoCount = 0;
    private int warnCount = 0;
    private int errorCount = 0;
    private int memoryWarningCount = 0;

    // Recent 100 error logs
    private LinkedList<String> recentErrors = new LinkedList<>();

    // Process the log file and populate the queue
    public void readLogFile(List<String> logs) {
        for (String log : logs) {
            logQueue.add(log);
        }
    }

    // Dequeue all logs, push errors to stack, and perform analysis
    public void processLogs() {
        while (!logQueue.isEmpty()) {
            String logEntry = logQueue.poll();
            analyzeLog(logEntry);
        }
    }

    // Analyze each log entry
    private void analyzeLog(String logEntry) {
        if (logEntry.contains("INFO")) {
            infoCount++;
        } else if (logEntry.contains("WARN")) {
            warnCount++;
            if (logEntry.contains("Memory")) {
                memoryWarningCount++;
            }
        } else if (logEntry.contains("ERROR")) {
            errorCount++;
            errorStack.push(logEntry);

            // Keep track of the last 100 error logs
            if (recentErrors.size() >= 100) {
                recentErrors.pollFirst(); // Remove the oldest error if limit reached
            }
            recentErrors.add(logEntry);
        }
    }

    // Display the results
    public void displayResults() {
        System.out.println("INFO Count: " + infoCount);
        System.out.println("WARN Count: " + warnCount);
        System.out.println("ERROR Count: " + errorCount);
        System.out.println("Memory Warnings Count: " + memoryWarningCount);
        System.out.println("Last 100 Errors: " + recentErrors);
    }

    // Pop errors from stack (optional task)
    public void popErrorsFromStack() {
        while (!errorStack.isEmpty()) {
            System.out.println("Popped Error: " + errorStack.pop());
        }
    }

    public static void main(String[] args) {

        // Create an instance of LogProcessor and process logs
        LogProcessor processor = new LogProcessor();

        try {
            // Load file from the resources folder or classpath
            ClassLoader classLoader = LogProcessor.class.getClassLoader();
            Path filePath = Paths.get(classLoader.getResource("log-data.csv").toURI());

            // Read the file content
            List<String> logs = Files.readAllLines(filePath);

            processor.readLogFile(logs);
            processor.processLogs();
            processor.displayResults();

            processor.popErrorsFromStack();
        } catch (IOException | NullPointerException e) {
            System.out.println("Error reading log file: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

