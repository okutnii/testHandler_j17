package search;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static search.SearchEngine.*;

class MainTest {

    static List<String> testData = Arrays.asList("Kristofer Galley", "Kristofer Nix nix-kris@gmail.com", "Malena Gray");

    static Map<String, List<Integer>> testDataMap = new HashMap<>();

    static {
        testDataMap.put("kristofer", Arrays.asList(0, 1));
        testDataMap.put("galley", List.of(0));
        testDataMap.put("nix", List.of(1));
        testDataMap.put("nix-kris@gmail.com", List.of(1));
        testDataMap.put("malena", List.of(2));
        testDataMap.put("gray", List.of(2));
    }

    @Test
    public void getMappedDataTest() {

        Map<String, List<Integer>> actual = getMappedData(testData);

        assertEquals(testDataMap, actual);
    }

    @Test
    public void computeQueryStrategyAllTest_withQuery() {
        List<String> expected = List.of("Kristofer Galley", "Kristofer Nix nix-kris@gmail.com");

        List<String> actual = computeQuery(testData, testDataMap, "Kristofer", Strategy.ALL);

        assertEquals(expected, actual);
    }

    @Test
    public void computeQueryStrategyNoneTest_withQuery() {
        List<String> expected = List.of("Malena Gray");

        List<String> actual = computeQuery(testData, testDataMap, "Kristofer", Strategy.NONE);

        assertEquals(expected, actual);
    }

    @Test
    public void computeQueryStrategyAnyTest_withQuery() {
        List<String> expected = List.of("Kristofer Galley", "Malena Gray");

        List<String> actual = computeQuery(testData, testDataMap, "Galley Gray", Strategy.ANY);

        assertEquals(expected, actual);
    }

    @Test
    public void computeQueryStrategyAllTest_withEmptyQuery() {
        List<String> expected = List.of();

        List<String> actual = computeQuery(testData, testDataMap, "", Strategy.ALL);

        assertEquals(expected, actual);
    }

    @Test
    public void computeQueryStrategyNoneTest_withEmptyQuery() {
        List<String> expected = List.of("Kristofer Galley", "Kristofer Nix nix-kris@gmail.com", "Malena Gray");

        List<String> actual = computeQuery(testData, testDataMap, "", Strategy.NONE);

        assertEquals(expected, actual);
    }

    @Test
    public void computeQueryStrategyAnyTest_withEmptyQuery() {
        List<String> expected = List.of();

        List<String> actual = computeQuery(testData, testDataMap, "", Strategy.ANY);

        assertEquals(expected, actual);
    }

    @Test
    public void computeQueryStrategyAllTest_withoutMatches() {
        List<String> expected = List.of();

        List<String> actual = computeQuery(testData, testDataMap, "Anna Gray", Strategy.ALL);

        assertEquals(expected, actual);
    }

    @Test
    public void computeQueryStrategyNoneTest_withoutMatches() {
        List<String> expected = List.of();

        List<String> actual = computeQuery(testData, testDataMap, "Kristofer Gray Anna", Strategy.NONE);

        assertEquals(expected, actual);
    }

    @Test
    public void computeQueryStrategyAnyTest_withoutMatches() {
        List<String> expected = List.of();

        List<String> actual = computeQuery(testData, testDataMap, "Anna", Strategy.ANY);

        assertEquals(expected, actual);
    }
}