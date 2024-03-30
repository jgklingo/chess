package ui;

import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;
    Scanner scanner = new Scanner(System.in);
    public Repl(String serverUrl) {
        client = new Client(serverUrl, this);
    }
    public void run() {
        System.out.println(SET_TEXT_BOLD + "Welcome to 240 Chess! Use one of the following commands to get started:"
                + RESET_TEXT_BOLD);
        System.out.print(client.help());

        String result = null;
        while (!Objects.equals(result, "")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_WHITE + result);
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(SET_TEXT_COLOR_RED + msg + SET_TEXT_COLOR_WHITE + "\n");  // set color to red for errors
            }
        }
        System.out.print("quitting...");
        System.out.println();
    }
    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> " + SET_TEXT_COLOR_GREEN);
    }
    public String prompt(String promptText) {
        System.out.print(SET_TEXT_COLOR_WHITE + promptText + SET_TEXT_COLOR_GREEN);
        return scanner.nextLine();
    }
}
