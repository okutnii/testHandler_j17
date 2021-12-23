package easyXO;

import java.util.Random;
import java.util.Scanner;


public class Main {

    static final Mark[][] field = {   // 0 - empty, 1 - x, 2 - o
            {Mark.Empty,    Mark.Empty, Mark.Empty},
            {Mark.Empty,    Mark.Empty, Mark.Empty},
            {Mark.Empty,    Mark.Empty, Mark.Empty}
    };

    static Mode mode;
    static BotLvl lvl;
    static PlayerTurn turn = PlayerTurn.FIRST;
    static GameState gameState = GameState.LAST;

    public static void main(String[] args){
        try(Scanner sc = new Scanner(System.in)){

            initMode(sc);

            startGame(sc);
        }
    }

    private static void startGame(Scanner sc) {

        if(mode == Mode.BOT) {
            playWithBot(sc);
        }
        else {
            printField();
            playWithUser(sc);
        }
    }

    private static void playWithUser(Scanner sc) {

        while(gameState == GameState.LAST) {
            Cord c = getUserMove(sc);

            changeFieldState(c);

            printField();

            if(checkState()) {
                gameState = GameState.OVER;

                endGame();
            }

            checkDraw();

            turn = turn.switchTurn();
        }
    }

    private static void playWithBot(Scanner sc) {

        while (gameState == GameState.LAST){

            Cord c = getBotOrUserMove(sc);

            changeFieldState(c);

            printField();

            if(checkState()) {
                gameState = GameState.OVER;

                endGame();
            }

            checkDraw();

            turn = turn.switchTurn();
        }
    }

    private static Cord getBotOrUserMove(Scanner sc) {

        Cord cord;

        if(turn == PlayerTurn.FIRST)
            cord = getBotMove();
        else
            cord = getUserMove(sc);

        return cord;
    }

    private static Cord getBotMove() {

        return switch (lvl) {
            case EASY -> easyBotMove();
            case MEDIUM -> mediumBotMove();
            case HARD -> hardBotMove();
            default -> throw new IllegalArgumentException("smth went wrong with bot lvl");
        };
    }

    private static Cord hardBotMove() {

        Cord cord = null;

        //  take the middle
        if(field[1][1].isEmpty())
            cord = new Cord(2,2);


        //  counting quantity of occupied corners
        //  if there is no X's corner, set it up
        if(cord == null && countCorners() == 0)
            cord = takeCorner();

        //  trying to win
        if(cord == null)
            cord = canWin();

        if(cord == null)
            cord = preventDefeat();

        if(cord == null)
            cord = easyBotMove();

        return cord;
    }

    private static Cord preventDefeat() {
        Cord cord;

        turn = turn.switchTurn();

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){

                if(field[i][j].isEmpty()) {

                    cord = new Cord(j+1, i+1);

                    changeFieldState(cord);

                    if(checkState()) {
                        turn = turn.switchTurn();

                        removeMark(cord);
                        return cord;
                    }
                    removeMark(cord);
                }
            }
        }
        turn = turn.switchTurn();
        return null;
    }

    private static Cord takeCorner() {
        Random random = new Random();

        int x, y;

        x = random.nextInt(2);
        y = random.nextInt(2);

        if (x == 1)
            ++x;

        if (y == 1)
            ++y;

        return new Cord(x+1, y+1);
    }

    private static int countCorners() {
        int count = 0;

        for(int i = 0; i < 3; i++){

            if(i == 1)continue;

            for(int j = 0; j < 3; j++){

                if(j == 1)
                    continue;

                if(field[i][j] == Mark.X) {
                    count++;
                }
            }
        }

        return count;
    }

    private static Cord mediumBotMove() {

        Cord move = canWin();

        if(move == null)
            move = easyBotMove();

        return move;
    }

    private static Cord canWin() {

        Cord cord;

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){

                if(field[i][j].isEmpty()) {

                    cord = new Cord(j+1, i+1);

                    changeFieldState(cord);

                    if(checkState()) {
                        removeMark(cord);

                        return cord;
                    }

                    removeMark(cord);
                }
            }
        }
        return null;
    }

    private static void removeMark(Cord cord) {
        field[cord.y][cord.x] = Mark.Empty;
    }

    private static Cord easyBotMove() {

        Random random = new Random();

        int x, y;

        do{
            x = random.nextInt(3);
            y = random.nextInt(3);

        } while (!field[y][x].isEmpty());

        return new Cord(x+1, y+1);
    }

    //  returns true if game is over
    private static boolean checkState() {

        // check obliquely
        boolean winConditional1 = !field[0][0].isEmpty() &&
                field[0][0] == field[1][1] &&
                field[1][1] == field[2][2];

        boolean winConditional2 = !field[1][1].isEmpty() &&
                field[0][2] == field[1][1] &&
                field[1][1] == field[2][0];

        for(int i = 0; i < 3; i++){

            //  checking rows
            boolean winConditional3 = !field[i][0].isEmpty() &&
                    field[i][0] == field[i][1] &&
                    field[i][1] == field[i][2];

            // checking cols
            boolean winConditional4 = !field[0][i].isEmpty() &&
                    field[0][i] == field[1][i] &&
                    field[1][i] == field[2][i];

            if(winConditional1 | winConditional2 | winConditional3 | winConditional4) {

                return true;
            }
        }
        return false;
    }

    private static void checkDraw() {

        boolean drawFlag = true;

        for(Mark[] row: field) {
            for (Mark mark : row) {
                if (mark.isEmpty()) {
                    drawFlag = false;
                    break;
                }
            }
        }

        if(drawFlag && gameState == GameState.LAST){
            gameState = GameState.DRAW;

            endGame();
        }
    }

    private static void endGame() {
        if(gameState == GameState.OVER) {
            System.out.println(turn.toString() + " player wins");
        }else {
            System.out.println("DRAW!");
        }
    }

    private static Cord getUserMove(Scanner sc) {

        Cord cord;

        int x = 0;
        int y = 0;

        if(sc.hasNextInt()) {
            x = sc.nextInt();
        }
        if(sc.hasNextInt()) {
            y = sc.nextInt();
        }

        sc.nextLine();

        cord = new Cord(x,y);

        if(x < 1 || x > 3 || y > 3 || y < 1){

            System.out.println("Wrong input. Try again!");

            cord = getUserMove(sc);
        }

        return cord;
    }

    private static void changeFieldState(Cord c) {

        if(!field[c.y][c.x].isEmpty()) {

            System.out.println("Choose empty point!");

            turn = turn.switchTurn();   //  offset switch in check method

            return;
        }

        if(turn == PlayerTurn.FIRST) {
            field[c.y][c.x] = Mark.X;
        } else {
            field[c.y][c.x] = Mark.O;
        }

    }

    private static void printField() {

        System.out.println("  -----  ");

        for(Mark[] row: field){

            System.out.print("| ");

            for (Mark point: row) {
                System.out.print(getPointChar(point) + " ");
            }
            System.out.println("|");
        }
        System.out.println("  -----  ");

    }

    private static char getPointChar(Mark point) {
        return switch (point) {
            case Empty -> ' ';
            case X -> 'X';
            case O -> 'O';
        };
    }

    private static void initMode(Scanner sc) {
        System.out.println("Input mode(1 - bot, 2 - user): ");

        int tmp = 0;

        if(sc.hasNextInt())
            tmp = sc.nextInt();

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

    private static void initLvl(Scanner sc) {
        System.out.println("Choose bot's lvl(1 - easy, 2 - medium, 3 - hard): ");

        int tmp = 0;
        if(sc.hasNextInt())
            tmp = sc.nextInt();

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