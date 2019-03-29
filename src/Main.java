import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    static int MAX_N = 1010, MAX_C = 155;
    static int WIDTH, HEIGHT, CUSTOMER_HEADQUARTERS, REPLY_HEADQUARTERS;
    static int dx[] = {0, 1, 0, -1};
    static int dy[] = {1, 0, -1, 0};

    static int values[][] = new int[MAX_N][MAX_N];
    static int customerRow[] = new int[MAX_N];
    static int customerColumn[] = new int[MAX_N];
    static int customerInitialScore[] = new int[MAX_N];
    static char mat[][] = new char[MAX_N][MAX_N];
    static boolean mark[][] = new boolean[MAX_N][MAX_N];
    static int distance[][][];
    static int result[][][] = new int[MAX_C][MAX_N][MAX_N];
    static boolean uzet[][] = new boolean[MAX_N][MAX_N];


    static class Solution {
        int rI[] = new int[MAX_C];
        int rJ[] = new int[MAX_C];
    }

    static class Graph {

        public static void precompute() {
            for (int i=1; i<=CUSTOMER_HEADQUARTERS; i++) {
                if ((i-1) % 10 == 0) {
                    System.out.print(String.format("%02d", i-1));
                }
                else {
                    System.out.print(".");
                }
                dijkstra(i);
            }
        }

        public static void dijkstra(int customerID) {
            PriorityQueue<Map.Entry<Integer, List<Integer>>> pq = new PriorityQueue<>((p1, p2) -> {
                //sort using distance values
                int key1 = p1.getKey();
                int key2 = p2.getKey();
                return key1-key2;
            });

            //Initialize all the distance to infinity
            for (int i=1; i<=HEIGHT; i++) {
                for (int j=1; j<=WIDTH; j++) {
                    distance[customerID][i][j] = Integer.MAX_VALUE;
                    mark[i][j] = false;
                }
            }

            Map.Entry<Integer, List<Integer>> p0 =
                    new AbstractMap.SimpleEntry<>(0, Arrays.asList(customerRow[customerID], customerColumn[customerID]));
            pq.offer(p0);

            distance[customerID][customerRow[customerID]][customerColumn[customerID]] = 0;
            //add it to pq

            //while priority queue is not empty
            while(!pq.isEmpty()) {
                //extract the min
                Map.Entry<Integer, List<Integer>> extractedPair = pq.poll();

                List<Integer> rowColumn = extractedPair.getValue();
                int nodeX = rowColumn.get(0);
                int nodeY = rowColumn.get(1);
                int dis = extractedPair.getKey();

                if (dis != distance[customerID][nodeX][nodeY]) {
                    continue;
                }

                for (int d=0; d<4; d++) {
                    int x = nodeX + dx[d];
                    int y = nodeY + dy[d];
                    if ((values[x][y] == Integer.MAX_VALUE)
                            || mark[x][y]) {
                        continue;
                    }
                    if (dis + values[x][y] < distance[customerID][x][y]) {
                        distance[customerID][x][y] = dis + values[x][y];
                        result[customerID][x][y] = d;
                        List<Integer> position = Arrays.asList(x, y);
                        pq.add(new AbstractMap.SimpleEntry<>(distance[customerID][x][y], position));
                    }
                }

                mark[nodeX][nodeY] = true;
            }

            for (int i=1; i<=HEIGHT; i++) {
                for (int j=1; j<=WIDTH; j++) {
                    if (i == customerRow[customerID] && j == customerColumn[customerID]) {
                        continue;
                    }
                    if (distance[customerID][i][j] == Integer.MAX_VALUE) {
                        continue;
                    }
                    distance[customerID][i][j] -= values[i][j];
                }
            }
        }

        public static Solution solve() {
            Solution solution = new Solution();
            for (int w=1; w<=CUSTOMER_HEADQUARTERS; w++) {
                uzet[customerRow[w]][customerColumn[w]] = true;
            }
            for (int i=1; i<=REPLY_HEADQUARTERS; i++) {
                pickOne(solution, i);
            }
            return solution;
        }

        public static void pickOne(Solution solution, int index) {
            int bestScore = -1;
            int bi = 0, bj = 0;

            for (int i=1; i<=HEIGHT; i++) {
                for (int j=1; j<=WIDTH; j++) {
                    if (uzet[i][j]) {
                        continue;
                    }

                    int score = 0;
                    for (int w=1; w<=CUSTOMER_HEADQUARTERS; w++) {
                        if (customerInitialScore[w] - distance[w][i][j] > 0) {
                            score += customerInitialScore[w] - distance[w][i][j];
                        }
                    }
                    if (score > bestScore) {
                        bestScore = score;
                        bi = i;
                        bj = j;
                    }
                }
            }

            uzet[bi][bj] = true;
            solution.rI[index] = bi;
            solution.rJ[index] = bj;
        }

        public static void main(String[] args) throws IOException {
//            List<String> lines = Files.readAllLines(Paths.get("./world1.in"));
            List<String> lines = Files.readAllLines(Paths.get("./1_victoria_lake.txt"));
//            List<String> lines = Files.readAllLines(Paths.get("./2_himalayas.txt"));
//            List<String> lines = Files.readAllLines(Paths.get("./3_budapest.txt"));
//            List<String> lines = Files.readAllLines(Paths.get("./4_manhattan.txt"));
//            List<String> lines = Files.readAllLines(Paths.get("./5_oceania.txt"));

            int globalRow = 0;

            String line1[] = lines.get(0).split(" ");
            WIDTH = Integer.parseInt(line1[0]);
            HEIGHT = Integer.parseInt(line1[1]);
            CUSTOMER_HEADQUARTERS = Integer.parseInt(line1[2]);
            REPLY_HEADQUARTERS = Integer.parseInt(line1[3]);

            distance = new int[MAX_C][MAX_N][MAX_N];

            globalRow++;

            Map<Character, Integer> maps = new HashMap<>();
            maps.put('#', Integer.MAX_VALUE);
            maps.put('~', 800);
            maps.put('*', 200);
            maps.put('+', 150);
            maps.put('X', 120);
            maps.put('_', 100);
            maps.put('H', 70);
            maps.put('T', 50);

            for (int i=0; i<=HEIGHT+1; i++) {
                for (int j=0; j<=WIDTH+1; j++) {
                    values[i][j] = Integer.MAX_VALUE;
                }
            }

            for (int i=1; i<=CUSTOMER_HEADQUARTERS; i++) {
                String[] customerLine = lines.get(globalRow).split(" ");
                customerColumn[i] += Integer.parseInt(customerLine[0]) + 1;
                customerRow[i] += Integer.parseInt(customerLine[1]) + 1;
                customerInitialScore[i] += Integer.parseInt(customerLine[2]);
                globalRow++;
            }

            for (int i=1; i<=HEIGHT; i++) {
                String mapLine = lines.get(globalRow);
                for (int j=0; j<WIDTH; j++) {
                    mat[i][j+1] = mapLine.charAt(j);
                    values[i][j+1] = maps.get(mat[i][j+1]);
                }

                globalRow++;
            }

            System.out.println("Precomputing...\n");
            precompute();
            System.out.println();
            System.out.println("Done.");

            System.out.println("Finding clusters...\n");
            Solution solution = solve();
            System.out.println();
            System.out.println("Done.");

            for (int to=1; to<=CUSTOMER_HEADQUARTERS; to++) {
                int best = 1;
                for (int from = 1; from <= REPLY_HEADQUARTERS; from++) {
                    int ii = solution.rI[from], jj = solution.rJ[from];
                    if (distance[to][ii][jj] <= customerInitialScore[to]) {
                        char dc[] = {'L', 'U', 'R', 'D'};
                        List<Character> chr = new ArrayList<>();
                        int tx = customerRow[to], ty = customerColumn[to];
                        if (distance[to][ii][jj] >= Integer.MAX_VALUE) {
                            System.out.println(to + " not connected.");
                            return;
                        }
                        System.out.print((jj-1) + " " + (ii-1) + " ");
                        while (!(ii == tx && jj == ty)) {
                            int fr = result[to][ii][jj];
                            chr.add(dc[fr]);
                            ii -= dx[fr];
                            jj -= dy[fr];
                        }

                        for (char c : chr) {
                            System.out.print(c);
                        }
                        System.out.println();
                    }

                    if (distance[to][ii][jj] < distance[to][solution.rI[best]][solution.rJ[best]]) {
                        best = from;
                    }
                }
            }

        }
    }
}