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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.PropertyConfigurator;

public class MyMainTask2 {

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
			String pathOfMil = "WikipediaPages_oneDocPerLine_1m.txt";
			// String pathOfMil = "testsmall.txt";

			InputStream inputStream = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			InputStream inputStreamMil = new FileInputStream(pathOfMil);
			BufferedReader readerMil = new BufferedReader(new InputStreamReader(inputStreamMil));

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

			result.forEach((k, v) -> System.out.println(String.format("%s ->  %d", k, v)));
			System.out.println("---------------------dictionary--------------------------------------------------");
			System.out.println(result.keySet());

			Map<String, Integer> pgRankMap = new HashMap<String, Integer>();
			Stream<String> perPageContent = readerMil.lines().parallel()
					.flatMap(line -> Arrays.stream(line.trim().split("<doc id=")));
			perPageContent.forEach(i -> {
				int pageRank = 0;
				int pagenum = 0;
				String wikiPgUrl = "";
				System.out.println("each page lenth is ===" + i.length());

				String[] wordlist = i.split(" ");

				for (int j = 0; j < wordlist.length; j++) {
					String wordToSearch = wordlist[j];
					// System.out.println("j========" + j + wordToSearch);
					if (wordToSearch.contains("url")) {
						wikiPgUrl = wordToSearch.split("=")[2];
					} else if (result.containsKey(wordToSearch))
						pageRank = pageRank + 1;
				}
				pagenum = pagenum + 1;
				System.out.println("wikiPgUrl======" + wikiPgUrl + " page rank===" + pageRank);
				if (wikiPgUrl.length() > 0)
					pgRankMap.put(wikiPgUrl, pageRank);

			});

			Map<String, Integer> pgRankMapFinal = pgRankMap.entrySet().parallelStream()
					.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(50)
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
							LinkedHashMap::new));

			System.out.println("---------------------top 20 ranked pages are--------------------------------------------------");
			pgRankMapFinal.forEach((k, v) -> System.out.println(String.format("%s ->  %d", k, v)));

		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (ArrayIndexOutOfBoundsException aIndout) {
			System.err.println(aIndout.getMessage());
			aIndout.printStackTrace();
			System.exit(1);
		}

	}

}
