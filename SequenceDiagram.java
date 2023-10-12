import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class SequenceDiagram {
    public static void main(String[] args) {
        System.out.print("prompt> ");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while ((input = br.readLine()) != null) {
                try {
                    executeCommands(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("prompt> ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executeCommands(String commandLine) throws IOException {
        String[] commands = commandLine.split(";");
        for (String command : commands) {
            command = command.trim();
            if (command.startsWith("(") && command.endsWith(")")) {
                // Handle commands within parentheses
                String innerCommands = command.substring(1, command.length() - 1);
                List<String> innerCommandsList = Arrays.asList(innerCommands.split("&"));
                for (String innerCommand : innerCommandsList) {
                    executeCommand(innerCommand);
                }
            } else {
                executeCommand(command);
            }
        }
    }

    private static void executeCommand(String command) throws IOException {
        String[] cmdArray = command.split("\\|");
        if (cmdArray.length > 1) {
            // Handle pipe commands
            ProcessBuilder pb1 = new ProcessBuilder(Arrays.asList(cmdArray[0].trim().split(" ")));
            ProcessBuilder pb2 = new ProcessBuilder(Arrays.asList(cmdArray[1].trim().split("")));
            Process process1 = pb1.start();
            Process process2 = pb2.start();
            process1.getInputStream().transferTo(process2.getOutputStream());
        } else {
            // Handle single command
            ProcessBuilder pb = new ProcessBuilder(Arrays.asList(command.trim().split(" ")));
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process process = pb.start();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
