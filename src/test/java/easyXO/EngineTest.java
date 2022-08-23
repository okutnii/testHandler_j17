package easyXO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {

    Mark[][] testField = {
            {Mark.Empty,    Mark.Empty, Mark.Empty},
            {Mark.Empty,    Mark.Empty, Mark.Empty},
            {Mark.Empty,    Mark.Empty, Mark.Empty}
    };
    Engine subject;


    @AfterAll
    public static void initialization()  {

    }

    @AfterEach
    public void preparation(){

        testField = new Mark[][]{
                {Mark.Empty, Mark.Empty, Mark.Empty},
                {Mark.Empty, Mark.Empty, Mark.Empty},
                {Mark.Empty, Mark.Empty, Mark.Empty}
        };
    }

    @Test
    public void testChangeFieldState(){
        Cord cord = new Cord(1,1);
        subject = new Engine();

        subject.changeFieldState(cord, testField);

        assertFalse(testField[cord.y][cord.x].isEmpty());
    }

    @Test
    public void testCheckDrawTrue(){
        subject = new Engine();
        testField = new Mark[][]{
                {Mark.X, Mark.O, Mark.X},
                {Mark.O, Mark.O, Mark.X},
                {Mark.X, Mark.X, Mark.O}
        };

        subject.isDraw(testField);

        assertSame(subject.gameState, GameState.DRAW);
    }

    @Test
    public void testCheckDrawFalse(){
        subject = new Engine();
        testField = new Mark[][]{
                {Mark.X, Mark.O, Mark.X},
                {Mark.O, Mark.Empty, Mark.X},
                {Mark.X, Mark.X, Mark.O}
        };

        subject.isDraw(testField);

        assertNotSame(subject.gameState, GameState.DRAW);
    }

    @Test
    public void testCheckWinConditionsTrue1(){
        subject = new Engine();
        testField = new Mark[][]{
                {Mark.X, Mark.X, Mark.X},
                {Mark.Empty, Mark.Empty, Mark.Empty},
                {Mark.Empty, Mark.Empty, Mark.Empty}
        };

        assertTrue(subject.isWinConditionMet(testField));
    }

    @Test
    public void testCheckWinConditionsTrue2(){
        subject = new Engine();
        Mark[][] testField = {
                {Mark.Empty,    Mark.O, Mark.Empty},
                {Mark.Empty,    Mark.O, Mark.Empty},
                {Mark.Empty,    Mark.O, Mark.Empty}
        };

        assertTrue(subject.isWinConditionMet(testField));
    }

    @Test
    public void testCheckWinConditionsTrue3(){
        subject = new Engine();
        Mark[][] testField = {
                {Mark.Empty,    Mark.Empty, Mark.X},
                {Mark.Empty,    Mark.X, Mark.Empty},
                {Mark.X,    Mark.Empty, Mark.Empty}
        };

        assertTrue(subject.isWinConditionMet(testField));
    }

    @Test
    public void testCheckWinConditionsTrue4(){
        subject = new Engine();
        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.O},
                {Mark.Empty,    Mark.X, Mark.Empty},
                {Mark.O,    Mark.Empty, Mark.X}
        };

        assertTrue(subject.isWinConditionMet(testField));
    }

    @Test
    public void testCheckWinConditionsFalse(){
        subject = new Engine();
        Mark[][] testField = {
                {Mark.Empty,    Mark.Empty, Mark.X},
                {Mark.Empty,    Mark.O, Mark.Empty},
                {Mark.X,    Mark.Empty, Mark.Empty}
        };

        assertFalse(subject.isWinConditionMet(testField));
    }

    @Test
    public void testEasyBotMove1(){
        subject = new Engine();

        Cord cord = subject.getEasyBotMove(testField);

        assertTrue(testField[cord.y][cord.x].isEmpty());
    }

    @Test
    public void testEasyBotMove2(){
        subject = new Engine();

        Cord preparationCord = subject.getEasyBotMove(testField);
        testField[preparationCord.y][preparationCord.x] = Mark.X;

        Cord cord = subject.getEasyBotMove(testField);

        assertTrue(testField[cord.y][cord.x].isEmpty());
    }

    @Test
    public void testRemoveMark(){
        subject = new Engine();

        Cord cord = new Cord(1,2);
        testField[cord.y][cord.x] = Mark.X;

        subject.removeMark(cord, testField);

        assertTrue(testField[cord.y][cord.x].isEmpty());
    }

    @Test
    public void testGetWinCordIfExistsFalse(){
        subject = new Engine();
        Mark[][] testField = {
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty}
        };

        Cord cord = subject.getWinCordIfExists(testField);

        assertNull(cord);
    }

    @Test
    public void testGetWinCordIfExistsTrue(){
        subject = new Engine();
        Mark[][] testField = {
                {Mark.O,    Mark.X, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.O,    Mark.X, Mark.Empty}
        };

        Cord cord = subject.getWinCordIfExists(testField);

        assertNotNull(cord);
    }

    @Test
    public void testMediumBotMove1(){
        subject = new Engine();
        Mark[][] testField = {
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty}
        };

        Cord cord = subject.getMediumBotMove(testField);

        assertNotNull(cord);
    }

    @Test
    public void testMediumBotMove2(){
        subject = new Engine();
        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.X}
        };

        Cord cord = subject.getMediumBotMove(testField);

        testField[cord.y][cord.x] = Mark.X;

        assertTrue(subject.isWinConditionMet(testField));
    }

    @Test
    public void testCountCapturedCorners1(){
        subject = new Engine();
        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.X},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.X}
        };

        int actual = subject.countCapturedCorners(testField, Mark.X.value);

        assertEquals(3, actual);
    }

    @Test
    public void testCountCapturedCorners2(){
        subject = new Engine();
        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.X},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.X}
        };

        int actual = subject.countCapturedCorners(testField, Mark.O.value);

        assertEquals(0, actual);
    }

    @Test
    public void testCountCapturedCorners3(){
        subject = new Engine();

        int actual = subject.countCapturedCorners(testField, Mark.X.value);

        assertEquals(0, actual);
    }

    @Test
    public void testIsFreeCornerExists1(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.X},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.X}
        };

        assertTrue(subject.isFreeCornerExists(testField));
    }

    @Test
    public void testIsFreeCornerExists2(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.O,    Mark.Empty, Mark.X}
        };

        assertTrue(subject.isFreeCornerExists(testField));
    }

    @Test
    public void testIsFreeCornerExists3(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.O},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.O,    Mark.Empty, Mark.X}
        };

        assertFalse(subject.isFreeCornerExists(testField));
    }

    @Test
    public void testTakeRandomCorner1(){
        subject = new Engine();

        Cord cord = subject.takeRandomCorner(testField);

        assertNotNull(cord);
    }

    @Test
    public void testTakeRandomCorner2(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.X},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.X}
        };

        Cord cord = subject.takeRandomCorner(testField);

        assertNotNull(cord);
    }

    @Test
    public void testTakeRandomCorner3(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.X},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.X,    Mark.Empty, Mark.X}
        };

        Cord cord = subject.takeRandomCorner(testField);

        assertNull(cord);
    }

    @Test
    public void testTakeRandomCorner4(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.X},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.X,    Mark.Empty, Mark.X}
        };

        Cord cord = subject.takeRandomCorner(testField);

        assertNull(cord);
    }

    @Test
    public void testGetCordToPreventDefeatIfExists1(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.O,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.O,    Mark.Empty, Mark.Empty}
        };

        Cord expected = new Cord(1, 2);

        Cord cord = subject.getCordToPreventDefeatIfExists(testField);

        assertEquals(expected, cord);
    }

    @Test
    public void testGetCordToPreventDefeatIfExists2(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.O,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.O}
        };

        Cord expected = new Cord(2, 2);

        Cord cord = subject.getCordToPreventDefeatIfExists(testField);

        assertEquals(expected, cord);
    }

    @Test
    public void testGetCordToPreventDefeatIfExists3(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.O,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.O,    Mark.Empty, Mark.O}
        };

        Cord cord = subject.getCordToPreventDefeatIfExists(testField);

        assertNotNull(cord);
    }

    @Test
    public void testGetCordToPreventDefeatIfExists4(){
        subject = new Engine();

        Cord cord = subject.getCordToPreventDefeatIfExists(testField);

        assertNull(cord);
    }

    @Test
    public void testHardBotMove1(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty}
        };
        Cord expected = new Cord(2,2);

        Cord actual = subject.getHardBotMove(testField);

        assertEquals(expected, actual);
    }

    @Test
    public void testHardBotMove2(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.Empty,    Mark.Empty, Mark.O},
                {Mark.Empty,    Mark.X, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.Empty}
        };

        Cord actual = subject.getHardBotMove(testField);

        boolean condition = actual.x != 1 && actual.y != 1
                && testField[actual.y][actual.x].isEmpty();

        assertTrue(condition);
    }

    @Test
    public void testHardBotMove3(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.O},
                {Mark.Empty,    Mark.X, Mark.Empty},
                {Mark.O,    Mark.Empty, Mark.Empty}
        };
        Cord expected = new Cord(3,3);

        Cord actual = subject.getHardBotMove(testField);

        assertEquals(expected, actual);
    }

    @Test
    public void testHardBotMove4(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.O},
                {Mark.Empty,    Mark.X, Mark.Empty},
                {Mark.Empty,    Mark.Empty, Mark.O}
        };
        Cord expected = new Cord(3,2);

        Cord actual = subject.getHardBotMove(testField);

        assertEquals(expected, actual);
    }

    @Test
    public void testHardBotMove5(){
        subject = new Engine();

        Mark[][] testField = {
                {Mark.X,    Mark.Empty, Mark.O},
                {Mark.Empty,    Mark.X, Mark.Empty},
                {Mark.Empty,    Mark.O, Mark.Empty}
        };

        Cord actual = subject.getHardBotMove(testField);

        assertNotNull(actual);
    }
}