import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.StringTokenizer;

/**
 * dp로 풀었다가 살짝 매몰....
 * bfs로 탐색해야 한다
 * 반례 :
 * 8 8 11
 * 3 3
 * 6 3
 * 3 2
 * 5 0
 * 4 0
 * 5 2
 * 3 3
 * 7 3
 * 5 0
 * 2 1
 * 2 2
 */


public class Main {

    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    static StringTokenizer st;
    static StringBuilder sb = new StringBuilder();

    static final int[][] deltas = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    static int R, C, K;
    static int[][] board;
    static Info[] infos;

    static int answer;

    // 골렘의 정보를 나타내는 클래스
    static class Info {
        int row;
        int col;
        int direction;
        boolean isMoved;

        public Info(int row, int col, int direction, boolean isMoved) {
            this.row = row;
            this.col = col;
            this.direction = direction;
            this.isMoved = isMoved;
        }

        @Override
        public String toString() {
            return "Info{" +
                    "row=" + row +
                    ", col=" + col +
                    ", direction=" + direction +
                    ", isMoved=" + isMoved +
                    '}';
        }
    }

    // BFS 탐색시 정령의 정보를 나타내는 클래스
    static class Spirit {
        int row;
        int col;
        boolean exit;

        public Spirit(int row, int col, boolean exit) {
            this.row = row;
            this.col = col;
            this.exit = exit;
        }
    }

    public static void main(String[] args) throws IOException {
        // 변수 초기화
        setInitVariable();

        // 골렘을 가장 남쪽으로 이동시킨다
        for (int i = 1; i <= K; i++) {
            Info info = infos[i];
            // 아래로 움직일 수 있을 때까지 움직이게 함
            do {
                info = move(info);

            } while (info.isMoved);

            // 골렘의 몸 일부가 여전히 숲을 벗어난 상태라면, board를 초기화시킴
            if (info.row <= 1) {
                board = new int[R+1][C+1];
                continue;
            }

            // 도달한 골렘의 정보를 숲(board)에 반영
            board[info.row][info.col] = i;
            for (int[] delta: deltas) {
                board[info.row + delta[0]][info.col + delta[1]] = i;
            }

            // 최종 골렘의 정보를 infos 배열에 업데이트
            infos[i] = info;

            // i번째 골렘에 위치한 정령이 이동할 수 있는 최하단 행의 번호를 구한다
            int maxRow = getMaxRow(i);

            answer += maxRow;

        }

        System.out.println(answer);
    }

    // i번째 골렘에 위치한 정령이 내려갈 수 있는 최하단 행의 번호를 반환
    // bfs 탐색 이용
    private static int getMaxRow(int i) {
        int maxRow = -1;

        ArrayDeque<Spirit> queue = new ArrayDeque<>();
        
        // 방문 배열
        // {i}번째 골렘을 탐색했는지 여부를 저장
        boolean[] visited = new boolean[K+1];
        visited[i] = true;
        
        addQueue(queue, i);

        while (!queue.isEmpty()) {
            Spirit spirit = queue.poll();
            int row = spirit.row;
            int col = spirit.col;
            boolean exit = spirit.exit;

            maxRow = Math.max(maxRow, row);

            // 출구라면, 인접한 골렘이 있는지를 탐색
            if (exit) {
                for (int[] delta: deltas) {
                    int nextRow = row + delta[0];
                    int nextCol = col + delta[1];
                    
                    if (isValidate(nextRow, nextCol)) {
                        int nextId = board[nextRow][nextCol];
                        if (nextId != 0 && !visited[nextId]) {
                            visited[nextId] = true;
                            addQueue(queue, nextId);
                        }
                    }
                }
            }
        }

        return maxRow;
    }

    private static void addQueue(ArrayDeque<Spirit> queue, int i) {
        Info info = infos[i];
        int direction = info.direction;
        int row = info.row;
        int col = info.col;

        queue.add(new Spirit(row+1, col, false));
        queue.add(new Spirit(row + deltas[direction][0], col + deltas[direction][1], true));
    }

    private static boolean isValidate(int row, int col) {
        return row > 0 && row <= R && col > 0 && col <= C;
    }

    private static Info move(Info info) {
        int row = info.row;
        int col = info.col;
        int direction = info.direction;

        // 남쪽으로 내려갈 수 있는지 확인
        if (canMoveDown(row, col)) {
            return new Info(row+1, col, direction, true);
        }
        // 서쪽으로 내려갈 수 있는지 확인
        else if (canMoveLeft(row, col)) {
            return new Info(row+1, col-1, (direction + 3) % 4, true);
        }
        // 동쪽으로 내려갈 수 있는지 확인
        else if (canMoveRight(row, col)) {
            return new Info(row+1, col+1, (direction + 1) % 4, true);
        }
        else {
            return new Info(row, col, direction, false);
        }
    }

    private static boolean canMoveRight(int row, int col) {
        return isBlank(row-1, col+1) && isBlank(row, col+2) && isBlank(row+1, col+1) && isBlank(row+1, col+2) && isBlank(row+2, col+1);
    }

    private static boolean canMoveLeft(int row, int col) {
        return isBlank(row-1, col-1) && isBlank(row, col-2) && isBlank(row+1, col-1) && isBlank(row+1, col-2) && isBlank(row+2, col-1);
    }

    private static boolean canMoveDown(int row, int col) {
        return isBlank(row+1, col-1) && isBlank(row+2, col) && isBlank(row+1, col+1);
    }

    private static boolean isBlank(int row, int col) {
        if (row < 0) return true;

        return row <= R && col > 0 && col <= C && board[row][col] == 0;
    }

    private static void setInitVariable() throws IOException {
        st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        board = new int[R+1][C+1];
        infos = new Info[K+1];
        for (int i=1; i<=K; i++) {
            st = new StringTokenizer(br.readLine());
            int col = Integer.parseInt(st.nextToken());
            int direction = Integer.parseInt(st.nextToken());

            infos[i] = new Info(-1, col, direction, false);
        }

        answer = 0;
    }
}