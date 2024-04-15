package modules.game_chesslib.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        Collections.shuffle(newGameMoves);
        ExecutorService executor = Executors
                .newFixedThreadPool(Math.min(newGameMoves.size(),
                        Runtime.getRuntime().availableProcessors()));
        double bestMove = Double.NEGATIVE_INFINITY;
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
        if (board.isKingAttacked()) {
            return Math.pow(10, 10);
        }
        List<Move> newGameMoves = board.legalMoves();
        Collections.shuffle(newGameMoves);
        if (isMaximisingPlayer) {
            double bestMove = Double.NEGATIVE_INFINITY;
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
            double bestMove = Double.POSITIVE_INFINITY;
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

    static int getPieceWeight(PieceType pieceType, boolean end) {
        if (pieceType.equals(PieceType.PAWN))
            return 100;
        if (pieceType.equals(PieceType.KNIGHT))
            return 280;
        if (pieceType.equals(PieceType.ROOK))
            return 479;
        if (pieceType.equals(PieceType.BISHOP))
            return 320;
        if (pieceType.equals(PieceType.QUEEN))
            return 929;
        if (pieceType.equals(PieceType.KING) && end)
            return 60000;
        return 60000;
    }

    static int getPieceWeight(PieceType pieceType) {
        return getPieceWeight(pieceType, false);
    }

    static double getPieceValue(Piece piece, Square square) {
        double ans = 0;
        boolean isWhite = piece.getPieceSide().equals(Side.WHITE);
        Position pos = mapSquareToPosition(square);
        if (piece.getPieceType().equals(PieceType.PAWN)) {
            ans = getPieceWeight(piece.getPieceType())
                    + (isWhite ? pawnEvalWhite[pos.getRow()][pos.getCol()] : pawnEvalBlack[pos.getRow()][pos.getCol()]);
        } else if (piece.getPieceType().equals(PieceType.ROOK)) {
            ans = getPieceWeight(piece.getPieceType())
                    + (isWhite ? rookEvalWhite[pos.getRow()][pos.getCol()] : rookEvalBlack[pos.getRow()][pos.getCol()]);
        } else if (piece.getPieceType().equals(PieceType.KNIGHT)) {
            ans = getPieceWeight(piece.getPieceType()) + (isWhite ? knightEvalWhite[pos.getRow()][pos.getCol()]
                    : knightEvalBlack[pos.getRow()][pos.getCol()]);
        } else if (piece.getPieceType().equals(PieceType.BISHOP)) {
            ans = getPieceWeight(piece.getPieceType()) + (isWhite ? bishopEvalWhite[pos.getRow()][pos.getCol()]
                    : bishopEvalBlack[pos.getRow()][pos.getCol()]);
        } else if (piece.getPieceType().equals(PieceType.QUEEN)) {
            ans = getPieceWeight(piece.getPieceType()) + (isWhite ? evalQueenWhite[pos.getRow()][pos.getCol()]
                    : evalQueenBlack[pos.getRow()][pos.getCol()]);
        } else if (piece.getPieceType().equals(PieceType.KING)) {
            ans = getPieceWeight(piece.getPieceType())
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
            { 100, 100, 100, 100, 105, 100, 100, 100 },
            { 78, 83, 86, 73, 102, 82, 85, 90 },
            { 7, 29, 21, 44, 40, 31, 44, 7 },
            { -17, 16, -2, 15, 14, 0, 15, -13 },
            { -26, 3, 10, 9, 6, 1, 0, -23 },
            { -22, 9, 5, -11, -10, -2, 3, -19 },
            { -31, 8, -7, -37, -36, -14, 3, -31 },
            { 0, 0, 0, 0, 0, 0, 0, 0 }
    };

    static double[][] pawnEvalBlack = reverseArray(pawnEvalWhite);

    static double[][] knightEvalWhite = {
            { -66, -53, -75, -75, -10, -55, -58, -70 },
            { -3, -6, 100, -36, 4, 62, -4, -14 },
            { 10, 67, 1, 74, 73, 27, 62, -2 },
            { 24, 24, 45, 37, 33, 41, 25, 17 },
            { -1, 5, 31, 21, 22, 35, 2, 0 },
            { -18, 10, 13, 22, 18, 15, 11, -14 },
            { -23, -15, 2, 0, 2, 0, -23, -20 },
            { -74, -23, -26, -24, -19, -35, -22, -69 }
    };

    static double[][] knightEvalBlack = reverseArray(knightEvalWhite);

    static double[][] bishopEvalWhite = {
            { -59, -78, -82, -76, -23, -107, -37, -50 },
            { -11, 20, 35, -42, -39, 31, 2, -22 },
            { -9, 39, -32, 41, 52, -10, 28, -14 },
            { 25, 17, 20, 34, 26, 25, 15, 10 },
            { 13, 10, 17, 23, 17, 16, 0, 7 },
            { 14, 25, 24, 15, 8, 25, 20, 15 },
            { 19, 20, 11, 6, 7, 6, 20, 16 },
            { -7, 2, -15, -12, -14, -15, -10, -10 }
    };

    static double[][] bishopEvalBlack = reverseArray(bishopEvalWhite);

    static double[][] rookEvalWhite = {
            { 35, 29, 33, 4, 37, 33, 56, 50 },
            { 55, 29, 56, 67, 55, 62, 34, 60 },
            { 19, 35, 28, 33, 45, 27, 25, 15 },
            { 0, 5, 16, 13, 18, -4, -9, -6 },
            { -28, -35, -16, -21, -13, -29, -46, -30 },
            { -42, -28, -42, -25, -25, -35, -26, -46 },
            { -53, -38, -31, -26, -29, -43, -44, -53 },
            { -30, -24, -18, 5, -2, -18, -31, -32 }
    };

    static double[][] rookEvalBlack = reverseArray(rookEvalWhite);

    static double[][] evalQueenWhite = {
            { 6, 1, -8, -104, 69, 24, 88, 26 },
            { 14, 32, 60, -10, 20, 76, 57, 24 },
            { -2, 43, 32, 60, 72, 63, 43, 2 },
            { 1, -16, 22, 17, 25, 20, -13, -6 },
            { -14, -15, -2, -5, -1, -10, -20, -22 },
            { -30, -6, -13, -11, -16, -11, -16, -27 },
            { -36, -18, 0, -19, -15, -15, -21, -38 },
            { -39, -30, -31, -13, -31, -36, -34, -42 }
    };

    static double[][] evalQueenBlack = reverseArray(evalQueenWhite);

    static double[][] kingEvalWhite = {
            { 4, 54, 47, -99, -99, 60, 83, -62 },
            { -32, 10, 55, 56, 56, 55, 10, 3 },
            { -62, 12, -57, 44, -67, 28, 37, -31 },
            { -55, 50, 11, -4, -19, 13, 0, -49 },
            { -55, -43, -52, -28, -51, -47, -8, -50 },
            { -47, -42, -43, -79, -64, -32, -29, -32 },
            { -4, 3, -14, -50, -57, -18, 13, 4 },
            { 17, 30, -3, -14, 6, -1, 40, 18 }
    };

    static double[][] kingEvalBlack = reverseArray(kingEvalWhite);

    static double[][] kingEvalWhite_End = {
            { -50, -40, -30, -20, -20, -30, -40, -50 },
            { -30, -20, -10, 0, 0, -10, -20, -30 },
            { -30, -10, 20, 30, 30, 20, -10, -30 },
            { -30, -10, 30, 40, 40, 30, -10, -30 },
            { -30, -10, 30, 40, 40, 30, -10, -30 },
            { -30, -10, 20, 30, 30, 20, -10, -30 },
            { -30, -30, 0, 0, 0, 0, -30, -30 },
            { -50, -30, -30, -30, -30, -30, -30, -50 }
    };

    static double[][] kingEvalBlack_End = reverseArray(kingEvalWhite_End);
}
