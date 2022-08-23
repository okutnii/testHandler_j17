package easyXO;

import java.util.Scanner;

public class XOMain {
    public static void main(String[] args) {
        execute();
    }


    private static void execute() {
        try (Scanner sc = new Scanner(System.in)) {
            Mark[][] field = {
                    {Mark.Empty, Mark.Empty, Mark.Empty},
                    {Mark.Empty, Mark.Empty, Mark.Empty},
                    {Mark.Empty, Mark.Empty, Mark.Empty}
            };
            Engine main = new Engine();
            main.initMode(sc);
            main.startGame(sc, field);
        }
    }
}