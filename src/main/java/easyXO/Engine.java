package easyXO;

import java.util.Random;
import java.util.Scanner;

/**
 * XO game engine representing class
 */
public class Engine {
    public Mode mode;
    public BotLvl lvl;
    public PlayerTurn turn = PlayerTurn.FIRST;
    public GameState gameState = GameState.LAST;

    /**
     * Initiatesthe game mode for the given field
     *
     * @param sc    output scanner
     * @param field game field
     */
    public void startGame(Scanner sc, Mark[][] field) {

        if (mode == Mode.BOT) {
            playWithBot(sc, field);
        } else {
            printField(field);
            playWithUser(sc, field);
        }
    }

    /**
     * Starts user-vs-user game
     *
     * @param sc    output scanner
     * @param field game field
     */
    public void playWithUser(Scanner sc, Mark[][] field) {

        while (gameState == GameState.LAST) {
            Cord c = getUserMove(sc);

            changeFieldState(c, field);

            printField(field);

            if (isWinConditionMet(field)) {
                gameState = GameState.OVER;

                printEndGame();
            }

            isDraw(field);

            turn = turn.switchTurn();
        }
    }

    /**
     * Starts bot-vs-user game
     *
     * @param sc    output scanner
     * @param field game field
     */
    public void playWithBot(Scanner sc, Mark[][] field) {

        while (gameState == GameState.LAST) {

            Cord c = getBotOrUserMove(sc, field);

            changeFieldState(c, field);

            printField(field);

            if (isWinConditionMet(field)) {
                gameState = GameState.OVER;

                printEndGame();
            }

            isDraw(field);

            turn = turn.switchTurn();
        }
    }

    /**
     * Gets move depends on current turn.
     * If move belongs to bot, then returns lvl-related move.
     * If move belongs to user, then returns move given for input.
     *
     * @param sc    output scanner
     * @param field game field
     * @return coordinates of move
     */
    public Cord getBotOrUserMove(Scanner sc, Mark[][] field) {

        Cord cord;

        if (turn == PlayerTurn.FIRST)
            cord = getBotMove(field);
        else
            cord = getUserMove(sc);

        return cord;
    }

    /**
     * Dispatch method to call the move generation method associated with the level
     *
     * @param field game field
     * @return lvl-related move
     */
    public Cord getBotMove(Mark[][] field) {

        Cord cord;

        switch (lvl) {
            case EASY:
                cord = getEasyBotMove(field);
                break;
            case MEDIUM:
                cord = getMediumBotMove(field);
                break;
            case HARD:
                cord = getHardBotMove(field);
                break;
            default:
                throw new IllegalArgumentException("smth went wrong with bot lvl");
        }

        return cord;
    }


    /**
     * Returns a coordinate by strategy:
     * takes middle, setups corner, tries to win or to prevent defeat
     *
     * @param field game field
     * @return the coordinates of bot move
     */
    public Cord getHardBotMove(Mark[][] field) {
        Cord cord = null;

        if (field[1][1].isEmpty()) {
            cord = new Cord(2, 2);
        }

        if (countCapturedCorners(field, Mark.X.value) == 0 && cord == null) {
            cord = takeRandomCorner(field);
        } else if (cord == null) {
            cord = getWinCordIfExists(field);
            if (cord == null) {
                cord = getCordToPreventDefeatIfExists(field);
                if (cord == null) {
                    cord = getEasyBotMove(field);
                }
            }
        }
        return cord;
    }

    /**
     * @param field game field
     * @return cord to prevent defeat or null
     */
    public Cord getCordToPreventDefeatIfExists(Mark[][] field) {
        Cord cord;
        turn = turn.switchTurn();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j].isEmpty()) {
                    cord = new Cord(j + 1, i + 1);
                    changeFieldState(cord, field);
                    if (isWinConditionMet(field)) {
                        turn = turn.switchTurn();
                        removeMark(cord, field);
                        return cord;
                    }
                    removeMark(cord, field);
                }
            }
        }
        turn = turn.switchTurn();
        return null;
    }

    /**
     * @param field game field
     * @return takes random corner if exists or null
     */
    public Cord takeRandomCorner(Mark[][] field) {
        if (!isFreeCornerExists(field)) {
            return null;
        }
        int x, y;
        Cord result;
        Random random = new Random();
        do {
            x = random.nextInt(2);
            y = random.nextInt(2);
            if (x == 1) {
                ++x;
            }
            if (y == 1) {
                ++y;
            }
            result = new Cord(x + 1, y + 1);
        } while (!field[result.y][result.x].isEmpty());
        return result;
    }

    /**
     * @param field game field
     * @return true if the field has at least one free corner, false otherwise
     */
    public boolean isFreeCornerExists(Mark[][] field) {
        return field[0][0] == Mark.Empty
                || field[2][2] == Mark.Empty
                || field[2][0] == Mark.Empty
                || field[0][2] == Mark.Empty;
    }

    /**
     * @param field game field
     * @param mark  int value of Mark enum. can be multiple
     * @return quantity of captured corners
     */
    public int countCapturedCorners(Mark[][] field, int mark) {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            if (i == 1) {
                continue;
            }
            for (int j = 0; j < 3; j++) {
                if (j == 1) {
                    continue;
                }
                if ((field[i][j].value & mark) != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns a coordinate by strategy:
     * tries to win or takes a random place
     *
     * @param field game field
     * @return the coordinates of bot move
     */
    public Cord getMediumBotMove(Mark[][] field) {
        Cord move = getWinCordIfExists(field);
        if (move == null) {
            move = getEasyBotMove(field);
        }
        return move;
    }

    /**
     * @param field game field
     * @return coordinate to win game if exists, null otherwise
     */
    public Cord getWinCordIfExists(Mark[][] field) {
        Cord cord;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j].isEmpty()) {
                    cord = new Cord(j + 1, i + 1);
                    changeFieldState(cord, field);
                    if (isWinConditionMet(field)) {
                        removeMark(cord, field);
                        return cord;
                    }
                    removeMark(cord, field);
                }
            }
        }
        return null;
    }

    /**
     * Sets definite coordinate as Empty in the field
     *
     * @param cord  coordinate to remove mark
     * @param field game field
     */
    public void removeMark(Cord cord, Mark[][] field) {
        field[cord.y][cord.x] = Mark.Empty;
    }

    /**
     * @param field game field
     * @return random empty cell
     */
    public Cord getEasyBotMove(Mark[][] field) {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(3);
            y = random.nextInt(3);

        } while (!field[y][x].isEmpty());
        return new Cord(x + 1, y + 1);
    }

    /**
     * @param field game field
     * @return true if game is over, false otherwise
     */
    public boolean isWinConditionMet(Mark[][] field) {
        boolean winConditional1 = !field[0][0].isEmpty() &&
                field[0][0] == field[1][1] &&
                field[1][1] == field[2][2];
        boolean winConditional2 = !field[1][1].isEmpty() &&
                field[0][2] == field[1][1] &&
                field[1][1] == field[2][0];
        if (winConditional1 || winConditional2) {
            return true;
        }
        for (int i = 0; i < 3; i++) {
            boolean winConditional3 = !field[i][0].isEmpty() &&
                    field[i][0] == field[i][1] &&
                    field[i][1] == field[i][2];
            boolean winConditional4 = !field[0][i].isEmpty() &&
                    field[0][i] == field[1][i] &&
                    field[1][i] == field[2][i];
            if (winConditional3 || winConditional4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is a draw. If it is draw, sets GameState.DRAW
     * and prints endgame message
     *
     * @param field game field
     */
    public void isDraw(Mark[][] field) {
        boolean drawFlag = true;
        for (Mark[] row : field)
            for (Mark mark : row)
                if (mark.isEmpty()) {
                    drawFlag = false;
                    break;
                }
        if (drawFlag && gameState == GameState.LAST) {
            gameState = GameState.DRAW;
            printEndGame();
        }
    }

    /**
     * Prints endgame message
     */
    public void printEndGame() {
        if (gameState == GameState.OVER) {
            System.out.println(turn.toString() + " player wins");
        } else {
            System.out.println("DRAW!");
        }
    }

    /**
     * @param sc input scanner
     * @return user move coordinate from console
     */
    public Cord getUserMove(Scanner sc) {
        Cord cord;
        int x = 0;
        int y = 0;
        if (sc.hasNextInt()) {
            x = sc.nextInt();
        }
        if (sc.hasNextInt()) {
            y = sc.nextInt();
        }
        sc.nextLine();
        cord = new Cord(x, y);
        if (x < 1 || x > 3 || y > 3 || y < 1) {
            System.out.println("Wrong input. Try again!");
            cord = getUserMove(sc);
        }
        return cord;
    }

    /**
     * Changes field state according to the turn if cell is free
     *
     * @param c     coordinate to setup
     * @param field game field
     */
    public void changeFieldState(Cord c, Mark[][] field) {
        if (!field[c.y][c.x].isEmpty()) {
            System.out.println("Choose empty point!");
            turn = turn.switchTurn();
            return;
        }
        if (turn == PlayerTurn.FIRST) {
            field[c.y][c.x] = Mark.X;
        } else {
            field[c.y][c.x] = Mark.O;
        }
    }

    /**
     * Prints game field
     *
     * @param field game field
     */
    public void printField(Mark[][] field) {
        System.out.println("  -----  ");
        for (Mark[] row : field) {
            System.out.print("| ");
            for (Mark point : row) {
                System.out.print(getPointChar(point) + " ");
            }
            System.out.println("|");
        }
        System.out.println("  -----  ");
    }

    /**
     * @param point mark to convert
     * @return corresponding character
     */
    public char getPointChar(Mark point) {
        return switch (point) {
            case Empty -> ' ';
            case X -> 'X';
            case O -> 'O';
        };
    }

    /**
     * Initiates game mode with console input
     *
     * @param sc input scanner
     */
    public void initMode(Scanner sc) {
        System.out.println("Input mode(1 - bot, 2 - user): ");
        int tmp = 0;
        if (sc.hasNextInt()) {
            tmp = sc.nextInt();
        }
        sc.nextLine();
        switch (tmp) {
            case 1 -> {
                mode = Mode.BOT;
                initLvl(sc);
            }
            case 2 -> mode = Mode.USER;
            default -> {
                System.out.println("Wrong input!");
                initMode(sc);
            }
        }
    }

    /**
     * Initiates bot's level with console input
     *
     * @param sc input scanner
     */
    public void initLvl(Scanner sc) {
        System.out.println("Choose bot's lvl(1 - easy, 2 - medium, 3 - hard): ");
        int tmp = 0;
        if (sc.hasNextInt()) {
            tmp = sc.nextInt();
        }
        sc.nextLine();
        switch (tmp) {
            case 1 -> lvl = BotLvl.EASY;
            case 2 -> lvl = BotLvl.MEDIUM;
            case 3 -> lvl = BotLvl.HARD;
            default -> {
                System.out.println("Wrong input!");
                initLvl(sc);
            }
        }
    }
}