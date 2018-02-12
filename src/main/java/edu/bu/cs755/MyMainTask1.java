package edu.bu.cs755;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.PropertyConfigurator;

public class MyMainTask1 {

	public static String extractText(String line) {

		return "";
	}

	public static void printVal(Map<String, Integer> result, Predicate<Map<String, Integer>> p) {
		if (p.test(result))
			System.out.println(result.keySet());

	}

	public static void main(String[] args) {

		// Configure the log4j
		PropertyConfigurator.configure("log4j.properties");

		try {
			String path = "WikipediaPages_oneDocPerLine_1000Lines_small.txt";
			InputStream inputStream = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			// Stream<String> lines=reader.lines().parallel();
			// System.out.println("Number of Lines: "+ lines.count());
			// lines.forEach(System.out::println);

			Map<String, Integer> wordCount = reader.lines().parallel()
					.map(line -> line.split(">")[1].replaceAll("<[^>]+>", "")) //
					.flatMap(line -> Arrays.stream(line.trim().split(" ")))
					.map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase().trim())
					.filter(word -> word.length() > 0).map(word -> new SimpleEntry<>(word, 1))
					.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (v1, v2) -> v1 + v2));

			// Now, we want to sort and get the top 50 values.
			Map<String, Integer> result = wordCount.entrySet().parallelStream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(50)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
							LinkedHashMap::new));
			System.out.println(
					"-----------------------top 50 values-----------------------------------------------------------");
			result.forEach((k, v) -> System.out.println(String.format("%s ->  %d", k, v)));
			System.out.println(
					"---------------------dictionary----------------------------------------------------------------");
			System.out.println(result.keySet());

			System.out.println(
					"-----------------------------------------------------------------------------------------------");
			System.out.println(
					"----------------------------------task1 starts-------------------------------------------------------------");
			Stream<String> stream = Stream.of("during", "and", "time", "protein", "car");
			stream.forEach(i -> {
				if (result.containsKey(i))
					System.out.println("word=" + i + "	::frquency=" + result.get(i));
				else
					System.out.println("word=" + i + " not found in dictionary. Hence frequency=" + -1);
			});
			System.out.println(
					"----------------------------------task1 ends---------------------------------------------------------------");

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

	}

}
