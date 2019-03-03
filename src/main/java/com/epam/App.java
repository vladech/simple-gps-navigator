package com.epam;

import com.epam.api.GpsNavigator;
import com.epam.api.Path;

import java.io.*;
import java.util.*;

public class App {

    public static void main(String[] args) {
        try {
            final GpsNavigator navigator = new StubGpsNavigator();
            navigator.readData("D:\\road_map.ext");

            final Path path = navigator.findPath("C", "A");
            System.out.println(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class StubGpsNavigator implements GpsNavigator {

        ArrayList<String> names;
        ArrayList<ArrayList<Pair<Integer, Integer>>> arrays;
        PriorityQueue<Pair<Integer, Integer>> queue;
        HashMap<String,Integer> indexes;

        @Override
        public void readData(String filePath) throws IOException {
            FastScanner fs = new FastScanner(filePath);
            names = new ArrayList<>();
            arrays = new ArrayList<>();
            indexes = new HashMap<>();
            int counter = 0;
            try {
                while (true) {
                    String a = fs.next();
                    String b = fs.next();
                    int p = fs.nextInt();
                    int l = fs.nextInt();
                    //int indexOfA = names.indexOf(a);
                    if (Objects.equals(indexes.get(a),null)) {
                        names.add(a);
                        arrays.add(new ArrayList<>());
                        indexes.put(a, counter);
                        counter++;
                        if (Objects.equals(indexes.get(b),null)) {
                            names.add(b);
                            arrays.add(new ArrayList<>());
                            indexes.put(b, counter);
                            counter++;
                            arrays.get(indexes.get(a)).add(new Pair<>(indexes.get(b), p * l));
                        } else {
                            arrays.get(indexes.get(a)).add(new Pair<>(indexes.get(b), p * l));
                        }
                    } else {
                        if (Objects.equals(indexes.get(b),null)) {
                            names.add(b);
                            arrays.add(new ArrayList<>());
                            indexes.put(b, counter);
                            counter++;
                            arrays.get(indexes.get(a)).add(new Pair<>(indexes.get(b), p * l));
                        } else {
                            arrays.get(indexes.get(a)).add(new Pair<>(indexes.get(b), p * l));
                        }
                    }
                }
            } catch (EOFException e) { }
        }

        @Override
        public Path findPath(String pointA, String pointB) throws Exception {
            int way[] = new int[names.size()];
            boolean visited[] = new boolean[names.size()];
            int fullCost[] = new int[names.size()];
            for (int k = 0; k < names.size(); k++) {
                fullCost[k] = Integer.MAX_VALUE;
            }
            queue = new PriorityQueue<>(Comparator.comparing(Pair::getKey));
            int indexOfA = indexes.get(pointA);
            int indexOfB = indexes.get(pointB);

            queue.add(new Pair<>(0, indexOfA));
            way[indexOfA] = -1;
            fullCost[indexOfA] = 0;
            while (!visited[indexOfB] && !queue.isEmpty()) {
                int x = queue.peek().getValue();
                int l = queue.poll().getKey();
                visited[x] = true;
                if (l <= fullCost[x]) {
                    for (Pair<Integer, Integer> p : arrays.get(x)) {
                        if (!visited[p.getKey()]) {
                            int cost = l + p.getValue();
                            if (fullCost[p.getKey()] > cost) {
                                fullCost[p.getKey()] = cost;
                                queue.add(new Pair<>(cost, p.getKey()));
                                way[p.getKey()] = x;
                            }
                        }
                    }
                }
            }

            int ind = indexOfB;
            ArrayList<String> fullWay = new ArrayList<>();
            fullWay.add(pointB);
            while (ind != indexOfA) {
                fullWay.add(names.get(way[ind]));
                ind = way[ind];
            }
            Collections.reverse(fullWay);
            if (fullCost[indexOfB] == Integer.MAX_VALUE) {
                throw new Exception("Такого пути не существует");
            }
            return new Path(fullWay, fullCost[indexOfB]);
        }

        class FastScanner {
            BufferedReader reader;
            StringTokenizer tokenizer;

            FastScanner(String fileName) throws IOException {
                reader = new BufferedReader(new FileReader(fileName));
            }

            String next() throws IOException {
                while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                    String line = reader.readLine();
                    if (line == null) {
                        throw new EOFException();
                    }
                    tokenizer = new StringTokenizer(line);
                }
                return tokenizer.nextToken();
            }

            int nextInt() throws IOException {
                return Integer.parseInt(next());
            }
        }

        class Pair<K, V> {

            private K key;

            K getKey() {
                return key;
            }

            private V value;

            V getValue() {
                return value;
            }

            Pair(K key, V value) {
                this.key = key;
                this.value = value;
            }
        }
    }
}
