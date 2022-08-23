package search;

import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * Class to perform queries by strategy
 */
public class SearchEngine {

    private static final String regexSplitterWhitespace = " ";

    /**
     * Calls a method depending on the strategy and returns the result
     *
     * @param data     list of data
     * @param dataMap  data map
     * @param target   searching target
     * @param strategy string of strategy. Can be one of the following values : ALL, ANY, NONE
     * @return result of performed query
     */
    public static List<String> computeQuery(List<String> data, Map<String, List<Integer>> dataMap, String target, Strategy strategy) {
        return switch (strategy) {
            case ALL -> findAll(data, dataMap, target);
            case ANY -> findAny(data, dataMap, target);
            case NONE -> findNone(data, dataMap, target);
        };
    }

    /**
     * Selects strings that have no occurrences with the target
     *
     * @param data    list of data
     * @param dataMap data map
     * @param targets targets to perform a search
     * @return result of performed search
     */
    private static List<String> findNone(List<String> data, Map<String, List<Integer>> dataMap, String targets) {
        List<String> results = new ArrayList<>();
        Set<Integer> setInd = new TreeSet<>();

        for (String target : targets.split(regexSplitterWhitespace)) {
            List<Integer> list = dataMap.get(target.toLowerCase(Locale.ROOT));
            if (!CollectionUtils.isEmpty(list))
                setInd.addAll(list);
        }
        for (int i = 0; i < data.size(); i++) {
            if (!setInd.contains(i)) {
                results.add(data.get(i));
            }
        }
        return results;
    }

    /**
     * Selects strings that have all occurrences with the target
     *
     * @param data    list of data
     * @param dataMap data map
     * @param targets targets to perform a search
     * @return result of performed search
     */
    private static List<String> findAll(List<String> data, Map<String, List<Integer>> dataMap, String targets) {
        List<String> result = data;

        for (String target : targets.split(regexSplitterWhitespace)) {
            result = findAny(result, dataMap, target.toLowerCase(Locale.ROOT));
            dataMap = getMappedData(result);
        }
        return result;
    }

    /**
     * Selects strings that have any occurrences with the target
     *
     * @param data    list of data
     * @param dataMap data map
     * @param targets targets to perform a search
     * @return result of performed search
     */
    private static List<String> findAny(List<String> data, Map<String, List<Integer>> dataMap, String targets) {
        Set<String> resultSet = new TreeSet<>();

        for (String target : targets.split(regexSplitterWhitespace)) {
            List<Integer> indexes = dataMap.get(target.toLowerCase(Locale.ROOT));

            if (indexes != null) {
                for (int i : indexes) {
                    resultSet.add(data.get(i));
                }
            }
        }
        return new ArrayList<>(resultSet);
    }


    /**
     * Collects data in a map with key pair of word and ids of its occurrence in data list
     *
     * @param data data list
     * @return mapped data
     */
    public static Map<String, List<Integer>> getMappedData(List<String> data) {
        Map<String, List<Integer>> dataMap = new HashMap<>();

        for (int i = 0; i < data.size(); i++) {
            String[] line = data.get(i).split(regexSplitterWhitespace);

            for (String key : line) {
                List<Integer> values = dataMap.get(key.toLowerCase(Locale.ROOT));

                if (CollectionUtils. isEmpty(values)) {
                    values = new ArrayList<>();
                }
                values.add(i);
                dataMap.put(key.toLowerCase(Locale.ROOT), values);
            }
        }
        return dataMap;
    }
}
