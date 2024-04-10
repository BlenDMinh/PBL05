package modules.game_chesslib.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;

public class Minimax {

    public Move getBestMove(int depth, Board board) throws InterruptedException {
        List<Move> newGameMoves = board.legalMoves();
        ExecutorService executor = Executors
                .newFixedThreadPool(Math.min(newGameMoves.size(),
                        Runtime.getRuntime().availableProcessors()));
        double bestMove = -9999;
        Move bestMoveFound = null;

        List<Future<Double>> futures = new ArrayList<>();
        Callable<Double> callable;
        Future<Double> future;
        for (Move newGameMove : newGameMoves) {
            callable = new Callable<Double>() {
                public Double call() throws Exception {
                    Board childBoard = new Board();
                    childBoard.loadFromFen(board.getFen());
                    childBoard.doMove(newGameMove);
                    return alphaBeta(depth - 1, childBoard, -10000, 10000, false);
                };
            };
            future = executor.submit(callable);
            futures.add(future);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            // Running ...
        }
        for (int i = 0; i < futures.size(); i++) {
            try {
                double value = futures.get(i).get();
                if (value >= bestMove) {
                    bestMove = value;
                    bestMoveFound = newGameMoves.get(i);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return bestMoveFound;
    }

    double alphaBeta(int depth, Board board, double alpha, double beta, boolean isMaximisingPlayer) {
        if (depth == 0) {
            return -evaluateBoard(board);
        }

        List<Move> newGameMoves = board.legalMoves();

        if (isMaximisingPlayer) {
            double bestMove = -9999;
            for (Move newGameMove : newGameMoves) {

                Board childBoard = new Board();
                childBoard.loadFromFen(board.getFen());
                childBoard.doMove(newGameMove);
                double value = alphaBeta(depth - 1, childBoard, alpha, beta, !isMaximisingPlayer);
                bestMove = Math.max(bestMove, value);
                alpha = Math.max(alpha, bestMove);
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            return bestMove;
        } else {
            double bestMove = 9999;
            for (Move newGameMove : newGameMoves) {
                Board childBoard = new Board();
                childBoard.loadFromFen(board.getFen());
                childBoard.doMove(newGameMove);
                double value = alphaBeta(depth - 1, childBoard, alpha, beta, !isMaximisingPlayer);
                bestMove = Math.min(bestMove, value);
                beta = Math.min(beta, bestMove);
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            return bestMove;
        }
    }

    public static double evaluateBoard(Board board) {
        double totalEvaluation = 0;
        Piece[] pieces = board.boardToArray();
        for (Piece piece : pieces) {
            if (piece == null || piece.getPieceType() == null) {
                continue;
            }
            if (board.getFistPieceLocation(piece).equals(Square.NONE)) {
                continue;
            }
            totalEvaluation += getPieceValue(piece, board.getFistPieceLocation(piece));
        }
        return totalEvaluation;
    }

    static double getPieceValue(Piece piece, Square square) {
        double ans = 0;
        boolean isWhite = piece.getPieceSide().equals(Side.WHITE);
        Position pos = mapSquareToPosition(square);
        if (piece.getPieceType().equals(PieceType.PAWN)) {
            ans = 10
                    + (isWhite ? pawnEvalWhite[pos.getRow()][pos.getCol()] : pawnEvalBlack[pos.getRow()][pos.getCol()]);
        } else if (piece.getPieceType().equals(PieceType.ROOK)) {
            ans = 50
                    + (isWhite ? rookEvalWhite[pos.getRow()][pos.getCol()] : rookEvalBlack[pos.getRow()][pos.getCol()]);
        } else if (piece.getPieceType().equals(PieceType.KNIGHT)) {
            ans = 30 + knightEval[pos.getRow()][pos.getCol()];
        } else if (piece.getPieceType().equals(PieceType.BISHOP)) {
            ans = 30 + (isWhite ? bishopEvalWhite[pos.getRow()][pos.getCol()]
                    : bishopEvalBlack[pos.getRow()][pos.getCol()]);
        } else if (piece.getPieceType().equals(PieceType.QUEEN)) {
            ans = 90 + evalQueen[pos.getRow()][pos.getCol()];
        } else if (piece.getPieceType().equals(PieceType.KING)) {
            ans = 900
                    + (isWhite ? kingEvalWhite[pos.getRow()][pos.getCol()] : kingEvalBlack[pos.getRow()][pos.getCol()]);
        }
        return isWhite ? ans : -ans;
    }

    static Position mapSquareToPosition(Square square) {
        int row = square.getRank().ordinal();
        int col = square.getFile().ordinal();
        return new Position(row, col);
    }

    static double[][] reverseArray(double[][] array) {
        double[][] reversedArray = new double[array.length][];
        for (int i = 0; i < array.length; i++) {
            reversedArray[i] = Arrays.copyOf(array[array.length - 1 - i], array[array.length - 1 - i].length);
        }
        return reversedArray;
    }

    static double[][] pawnEvalWhite = {
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0 },
            { 1.0, 1.0, 2.0, 3.0, 3.0, 2.0, 1.0, 1.0 },
            { 0.5, 0.5, 1.0, 2.5, 2.5, 1.0, 0.5, 0.5 },
            { 0.0, 0.0, 0.0, 2.0, 2.0, 0.0, 0.0, 0.0 },
            { 0.5, -0.5, -1.0, 0.0, 0.0, -1.0, -0.5, 0.5 },
            { 0.5, 1.0, 1.0, -2.0, -2.0, 1.0, 1.0, 0.5 },
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 }
    };

    static double[][] pawnEvalBlack = reverseArray(pawnEvalWhite);

    static double[][] knightEval = {
            { -5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0 },
            { -4.0, -2.0, 0.0, 0.0, 0.0, 0.0, -2.0, -4.0 },
            { -3.0, 0.0, 1.0, 1.5, 1.5, 1.0, 0.0, -3.0 },
            { -3.0, 0.5, 1.5, 2.0, 2.0, 1.5, 0.5, -3.0 },
            { -3.0, 0.0, 1.5, 2.0, 2.0, 1.5, 0.0, -3.0 },
            { -3.0, 0.5, 1.0, 1.5, 1.5, 1.0, 0.5, -3.0 },
            { -4.0, -2.0, 0.0, 0.5, 0.5, 0.0, -2.0, -4.0 },
            { -5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0 }
    };

    static double[][] bishopEvalWhite = {
            { -2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0 },
            { -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0 },
            { -1.0, 0.0, 0.5, 1.0, 1.0, 0.5, 0.0, -1.0 },
            { -1.0, 0.5, 0.5, 1.0, 1.0, 0.5, 0.5, -1.0 },
            { -1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, -1.0 },
            { -1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0 },
            { -1.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, -1.0 },
            { -2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0 }
    };

    static double[][] bishopEvalBlack = reverseArray(bishopEvalWhite);

    static double[][] rookEvalWhite = {
            { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
            { 0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5 },
            { -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5 },
            { -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5 },
            { -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5 },
            { -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5 },
            { -0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5 },
            { 0.0, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.0 }
    };

    static double[][] rookEvalBlack = reverseArray(rookEvalWhite);

    static double[][] evalQueen = {
            { -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0 },
            { -1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0 },
            { -1.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0 },
            { -0.5, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5 },
            { 0.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5 },
            { -1.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0 },
            { -1.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, -1.0 },
            { -2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0 }
    };

    static double[][] kingEvalWhite = {
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0 },
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0 },
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0 },
            { -3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0 },
            { -2.0, -3.0, -3.0, -4.0, -4.0, -3.0, -3.0, -2.0 },
            { -1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0 },
            { 2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 2.0, 2.0 },
            { 2.0, 3.0, 1.0, 0.0, 0.0, 1.0, 3.0, 2.0 }
    };

    static double[][] kingEvalBlack = reverseArray(kingEvalWhite);

}
