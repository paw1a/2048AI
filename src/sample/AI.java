package sample;

import java.util.*;

public class AI {
    private final int size;

    public AI(int size) {
        this.size = size;
    }

    public Direction rootMinimax(int level, int[][] tiles) throws GameOverException {
        Map<Double, Direction> map = new TreeMap<>(Collections.reverseOrder());

        for (Direction direction : Direction.values()) {
            int[][] field = Arrays.stream(tiles).map(int[]::clone).toArray(int[][]::new);
            move(direction, field);
            boolean b = false;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if(field[i][j] != tiles[i][j]) { b = true; break; }
                }
            }
            if(b) {
                double value = minimax(level - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, field);
                map.put(value, direction);
            }
        }

        if(map.isEmpty()) throw new GameOverException();

        return map.entrySet().stream().findFirst().get().getValue();
    }

    private double minimax(int level, double alpha, double beta, boolean isMaximizer, int[][] tiles) {
        if(level == 0) {
            return evaluationFunction(tiles);
        }

        double minmax = isMaximizer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        boolean alphaBetaCut = false;

        if(isMaximizer) {
            for (Direction direction : Direction.values()) {
                if(alphaBetaCut) break;

                int[][] field = Arrays.stream(tiles).map(int[]::clone).toArray(int[][]::new);
                move(direction, field);
                boolean b = false;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if(field[i][j] != tiles[i][j]) { b = true; break; }
                    }
                }
                if(b) {
                    minmax = Math.max(minimax(level - 1, alpha, beta, false, field), minmax);
                    alpha = Math.max(alpha, minmax);
                }

                if(beta <= alpha) alphaBetaCut = true;
            }
        } else {
            List<int[][]> list = generateAll(tiles);
            if(list.isEmpty()) {
                minmax = Math.min(minimax(level - 1, alpha, beta, true, tiles), minmax);
            } else {
                for (int[][] a : list) {
                    if (alphaBetaCut) break;

                    minmax = Math.min(minimax(level - 1, alpha, beta, true, a), minmax);
                    beta = Math.min(beta, minmax);

                    if (beta <= alpha) alphaBetaCut = true;
                }
            }
        }

        return minmax;
    }

    private double evaluationFunction(int[][] tiles) {
        int smoothness = 0;
        for (int i = 0; i < size; i++) {
            int previous = tiles[i][0];
            int counter = 0;
            while (previous == 0 && counter < size-1) {
                counter++;
                previous = tiles[i][counter];
            }

            for (int j = counter+1; j < size; j++) {
                if(tiles[i][j] != 0) {
                    smoothness -= Math.abs(log(previous) - log(tiles[i][j]));
                    previous = tiles[i][j];
                }
            }
        }

        for (int i = 0; i < size; i++) {
            int previous = tiles[0][i];
            int counter = 0;
            while (previous == 0 && counter < size-1) {
                counter++;
                previous = tiles[counter][i];
            }

            for (int j = counter+1; j < size; j++) {
                if(tiles[j][i] != 0) {
                    smoothness -= Math.abs(log(previous) - log(tiles[j][i]));
                    previous = tiles[j][i];
                }
            }
        }

        int mono = 0;
        int[] totals = new int[4];
        for (int j = 0; j < size; j++) {
            int current = 0;
            int next = current+1;
            while ( next<size ) {
                while ( next<size && tiles[next][j] == 0) {
                    next++;
                }
                if (next>=size) { next--; }
                int currentValue = log(tiles[current][j]);
                int nextValue = log(tiles[next][j]);
                if (currentValue > nextValue) {
                    totals[0] += nextValue - currentValue;
                } else if (nextValue > currentValue) {
                    totals[1] += currentValue - nextValue;
                }
                current = next;
                next++;
            }
        }

        for (int i = 0; i < size; i++) {
            int current = 0;
            int next = current+1;
            while ( next<size ) {
                while (next<size && tiles[i][next] == 0) {
                    next++;
                }
                if (next>=size) { next--; }

                int currentValue = log(tiles[i][current]);
                int nextValue = log(tiles[i][next]);

                if (currentValue > nextValue) {
                    totals[2] += nextValue - currentValue;
                } else if (nextValue > currentValue) {
                    totals[3] += currentValue - nextValue;
                }
                current = next;
                next++;
            }
        }
        mono = Math.max(totals[0], totals[1]) + Math.max(totals[2], totals[3]);

        int max = Integer.MIN_VALUE;
        int zeroCount = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(tiles[i][j] > max) max = tiles[i][j];
                if(tiles[i][j] == 0) zeroCount++;
            }
        }

        return smoothness * 0.1 + mono * 1.0 + log(zeroCount) * 2.7 + log(max) * 1.0;
    }

    private List<int[][]> generateAll(int[][] tiles) {
        List<int[][]> list = new ArrayList<>();
        for (int i = 2; i <= 4; i+=2) {
            for (int k = 0; k < size; k++) {
                for (int l = 0; l < size; l++) {
                    if(tiles[k][l] == 0) {
                        int[][] a = Arrays.stream(tiles).map(int[]::clone).toArray(int[][]::new);
                        a[k][l] = i;
                        list.add(a);
                    }
                }
            }
        }
        return list;
    }

    private void move(Direction direction, int[][] tiles) {
        if(direction == Direction.RIGHT) {
            for (int i = 0; i < size; i++) {
                int[] line = compactLine(tiles[i]);
                pairTiles(line);
                tiles[i] = compactLine(line);
            }
        } else if(direction == Direction.DOWN) {
            for (int i = 0; i < size; i++) {
                int[] line = new int[size];
                for (int j = 0; j < size; j++) {
                    line[j] = tiles[j][i];
                }
                line = compactLine(line);
                pairTiles(line);
                line = compactLine(line);
                for (int j = 0; j < size; j++) {
                    tiles[j][i] = line[j];
                }
            }
        } else if(direction == Direction.LEFT) {
            for (int i = 0; i < size; i++) {
                int[] line = new int[size];
                for (int j = size-1; j >= 0; j--) {
                    line[j] = tiles[i][size-1-j];
                }
                line = compactLine(line);
                pairTiles(line);
                line = compactLine(line);
                for (int j = size-1; j >= 0; j--) {
                    tiles[i][size-1-j] = line[j];
                }
            }
        } else if(direction == Direction.UP) {
            for (int i = 0; i < size; i++) {
                int[] line = new int[size];
                for (int j = 0; j < size; j++) {
                    line[j] = tiles[size-1-j][i];
                }
                line = compactLine(line);
                pairTiles(line);
                line = compactLine(line);
                for (int j = 0; j < size; j++) {
                    tiles[size-1-j][i] = line[j];
                }
            }
        }
    }

    private int[] compactLine(int[] tiles) {
        int[] newTiles = new int[tiles.length];
        int k = tiles.length-1;
        for (int i = tiles.length-1; i >= 0; i--) {
            if(tiles[i] != 0) {
                newTiles[k] = tiles[i];
                k--;
            }
        }
        return newTiles;
    }

    private void pairTiles(int[] line) {
        for (int j = size-1; j > 0; j--) {
            if(line[j-1] != 0 && line[j-1] == line[j]) {
                line[j] *= 2;
                line[j-1] = 0;
                j--;
            }
        }
    }

    private int log(int a) {
        if(a == 0) return 0;
        return (int) (Math.log(a) / Math.log(2));
    }
}
