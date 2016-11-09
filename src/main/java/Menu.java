/**
 * Created by doctor-googler on 11/9/2016.
 */
import com.sun.javafx.binding.StringFormatter;

import java.util.Scanner;

import static java.lang.System.*;
public class Menu {
    private enum Commands {
        CONNECT("connect"),
        DISCONNECT("disconnect"),
        HELP("help"),
        CD("cd"),
        DIR("dir"),
        DOWNLOAD("dwl"),
        UPLOAD("upl"),
        EXIT("exit");

        private String command;
        Commands(String command) {
            this.command = command;
        }
        String getCommand() {
            return command;
        }
    }
    public void start(String... arguments) {
        FTPManager manager = new FTPManager();
        String input = null;
        printGreeting();
        Scanner scn = new Scanner(in);
        while (true) {
            input = scn.nextLine();
            String[] splitted = input.split(" ");
            if (splitted.length <= 0) {
                out.println("ERROR: Invalid command");
                continue;
            }
            if (Commands.CONNECT.getCommand().equals(splitted[0])) {
                out.println(String.format("Attempt to login with '%s' to '%s' host. Success: %s", splitted[2], splitted[1],
                        manager.connect(splitted[1], splitted[2], splitted[3]).getContent()));
            } else if (Commands.DIR.getCommand().equals(splitted[0])) {
                //TODO add support for custom path
                out.println(manager.fileList().getContent());
            } else if (Commands.CD.getCommand().equals(splitted[0])) {
                out.println(manager.changeDirectory(splitted[1]).getContent());
            } else if (Commands.DOWNLOAD.getCommand().equals(splitted[0])) {
                out.println(manager.download(splitted[1], splitted[2]).getContent());
            } else if (Commands.DISCONNECT.getCommand().equals(splitted[0])) {
                out.println(manager.disconnect().getContent());
            } else if (Commands.EXIT.getCommand().equals(splitted[0])) {
                manager.disconnect();
                return;
            } else if (Commands.HELP.getCommand().equals(splitted[0])) {
                out.println("Commands:" +
                        "\nconnect <host> <username> <password>   -   connect to host with provided credentials." +
                        "\ndisconnect                             -   disconnects from connected host." +
                        "\nhelp                                   -   prints this help." +
                        "\ncd <dirname>                           -   changes current directory to <dirname>." +
                        "\ndir                                    -   prints file list of current directory." +
                        "\ndwl <remote> <local>                   -   downloads and writes <remote> file to the <local>." +
                        "\nupl <local> <remote>                   -   uploads <local> file to the <remote>." +
                        "\nexit                                   -   perform disconnect and then closes this program.");
            } else {
                out.println("ERROR: Invalid command");
                continue;
            }
        }
    }

    private void printGreeting() {
        out.println("===========================================================================================================");
        out.println("***********************************************************************************************************");
        out.println("*    ____ _     ___      _____ _____ ____        ____ _     ___ _____ _   _ _____          ___   _        *");
        out.println("*   / ___| |   |_ _|    |  ___|_   _|  _ \\      / ___| |   |_ _| ____| \\ | |_   _| __   __/ _ \\ / | __ _  *");
        out.println("*  | |   | |    | |     | |_    | | | |_) |    | |   | |    | ||  _| |  \\| | | |   \\ \\ / / | | || |/ _` | *");
        out.println("*  | |___| |___ | |     |  _|   | | |  __/     | |___| |___ | || |___| |\\  | | |    \\ V /| |_| || | (_| | *");
        out.println("*   \\____|_____|___|    |_|     |_| |_|         \\____|_____|___|_____|_| \\_| |_|     \\_/  \\___(_)_|\\__,_| *");
        out.println("*                                                                                                         *");
        out.println("***********************************************************************************************************");
        out.println("===========================================================================================================");
        out.println("For additional info type 'help'");
    }
}
