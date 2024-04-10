// package modules.game.custom;

// import java.util.concurrent.Callable;

// public class MinimaxThread implements Callable<Integer> {
//     private final ChessGame game;
//     private final int depth;
//     private final boolean maximizingPlayer;
//     private final Position move;

//     public MinimaxThread(ChessGame game, int depth, boolean maximizingPlayer, Position move) {
//         this.game = game;
//         this.depth = depth;
//         this.maximizingPlayer = maximizingPlayer;
//         this.move = move;
//     }

//     @Override
//     public Integer call() {
//         // Make the move
//         game.makeMove(move);

//         // Evaluate the position using minimax algorithm with alpha-beta pruning
//         int score;
//         if (maximizingPlayer) {
//             score = minimax(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
//         } else {
//             score = minimax(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
//         }

//         // Undo the move
//         game.undoMove();

//         return score;
//     }

//     private int minimax(int depth, int alpha, int beta, boolean maximizingPlayer) {
//         // Base case: If depth is 0 or game is over, return the evaluation of the current position
//         if (depth == 0 || game.isGameOver()) {
//             return game.evaluatePosition();
//         }

//         if (maximizingPlayer) {
//             int maxScore = Integer.MIN_VALUE;
//             for (Position nextMove : game.generateMoves()) {
//                 // Make the move
//                 game.makeMove(nextMove);
//                 // Recur to the next level
//                 int score = minimax(depth - 1, alpha, beta, false);
//                 maxScore = Math.max(maxScore, score);
//                 alpha = Math.max(alpha, score);
//                 // Undo the move
//                 game.undoMove();
//                 // Alpha-beta pruning
//                 if (beta <= alpha) {
//                     break;
//                 }
//             }
//             return maxScore;
//         } else {
//             int minScore = Integer.MAX_VALUE;
//             for (Position nextMove : game.generateMoves()) {
//                 // Make the move
//                 game.makeMove(nextMove);
//                 // Recur to the next level
//                 int score = minimax(depth - 1, alpha, beta, true);
//                 minScore = Math.min(minScore, score);
//                 beta = Math.min(beta, score);
//                 // Undo the move
//                 game.undoMove();
//                 // Alpha-beta pruning
//                 if (beta <= alpha) {
//                     break;
//                 }
//             }
//             return minScore;
//         }
//     }
// }

