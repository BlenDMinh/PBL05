package modules.game_chesslib.algo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
    public Move getBestMoveAlphaBeta(Board board, int depth, Side side) {
        return getBestMoveAlphaBeta(board, depth, side, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true)
                .getBestMove();
    }

    PairMoveValue getBestMoveAlphaBeta(Board board, int depth, Side side, double alpha, double beta,
            boolean isMaximizingPlayer) {
        List<Move> newGameMoves = board.legalMoves();
        Collections.shuffle(newGameMoves);
        if (depth == 0 || newGameMoves.size() == 0) {
            return new PairMoveValue(null, evaluateBoard(board, side));
        }
        Move bestMove = null, currMove;

        if (isMaximizingPlayer) {
            double bestEval = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < newGameMoves.size(); i++) {
                currMove = newGameMoves.get(i);
                board.doMove(currMove);
                double eval = getBestMoveAlphaBeta(board, depth - 1, side, alpha, beta, !isMaximizingPlayer).getValue();
                board.undoMove();
                if (bestEval < eval) {
                    bestEval = eval;
                    bestMove = currMove;
                }
                if (alpha < bestEval) {
                    alpha = bestEval;
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return new PairMoveValue(bestMove, bestEval);
        } else {
            double bestEval = Double.POSITIVE_INFINITY;
            for (int i = 0; i < newGameMoves.size(); i++) {
                currMove = newGameMoves.get(i);
                board.doMove(currMove);
                double eval = getBestMoveAlphaBeta(board, depth - 1, side, alpha, beta, !isMaximizingPlayer).getValue();
                board.undoMove();
                if (bestEval > eval) {
                    bestEval = eval;
                    bestMove = currMove;
                }
                if (beta > bestEval) {
                    beta = bestEval;
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return new PairMoveValue(bestMove, bestEval);
        }
    }

    public PairMoveValue getBestMoveAlphaBeta(Board board, int depth, double sum, Side side) {
        return getBestMoveAlphaBeta(board, depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                true, sum, side);
    }

    PairMoveValue getBestMoveAlphaBeta(Board board, int depth, double alpha, double beta,
            boolean isMaximizingPlayer, double sum, Side side) {
        List<Move> newGameMoves = board.legalMoves();
        Collections.shuffle(newGameMoves);
        if (depth == 0 || newGameMoves.size() == 0) {
            return new PairMoveValue(null, sum);
        }

        double maxValue = Double.NEGATIVE_INFINITY;
        double minValue = Double.POSITIVE_INFINITY;
        Move bestMove = null, currMove;
        for (int i = 0; i < newGameMoves.size(); i++) {
            currMove = newGameMoves.get(i);
            LastMoveInfo lastMoveInfo = new LastMoveInfo();
            lastMoveInfo.setMove(currMove);
            lastMoveInfo.setPieceMove(mapPieceTypeToString.get(board.getPiece(currMove.getFrom()).getPieceType()));
            Piece captured = board.getPiece(currMove.getTo());
            if (captured != null && !captured.getFanSymbol().equals("NONE")) {
                lastMoveInfo.setPieceCapture(mapPieceTypeToString.get(board.getPiece(currMove.getTo()).getPieceType()));
            }
            lastMoveInfo.setPromotion(mapPieceTypeToString.get(currMove.getPromotion().getPieceType()));
            lastMoveInfo.setSide(board.getSideToMove());

            board.doMove(currMove);
            double newSum = evaluateBoard(board, lastMoveInfo, sum, side);
            PairMoveValue pairMoveValue = getBestMoveAlphaBeta(
                    board,
                    depth - 1,
                    alpha,
                    beta,
                    !isMaximizingPlayer,
                    newSum,
                    side);
            board.undoMove();

            if (isMaximizingPlayer) {
                if (pairMoveValue.getValue() > maxValue) {
                    maxValue = pairMoveValue.getValue();
                    bestMove = currMove;
                }
                if (pairMoveValue.getValue() > alpha) {
                    alpha = pairMoveValue.getValue();
                }
            } else {
                if (pairMoveValue.getValue() < minValue) {
                    minValue = pairMoveValue.getValue();
                    bestMove = currMove;
                }
                if (pairMoveValue.getValue() < beta) {
                    beta = pairMoveValue.getValue();
                }
            }
            // Alpha-beta pruning
            if (alpha >= beta) {
                break; // cut off
            }
        }
        if (isMaximizingPlayer) {
            return new PairMoveValue(bestMove, maxValue);
        } else {
            return new PairMoveValue(bestMove, minValue);
        }
    }

    double evaluateBoard(Board chess, LastMoveInfo lastMoveInfo, double prevSum, Side side) {
        if (chess.isMated()) {
            // Opponent is in checkmate (good for player side)
            if (lastMoveInfo.getSide().equals(side)) {
                return Math.pow(10, 10);
            }
            // Player side’s king is in checkmate (bad for player side)
            else {
                return -Math.pow(10, 10);
            }
        }
        if (chess.isDraw()) {
            return 0;
        }
        if (chess.isKingAttacked()) {
            if (lastMoveInfo.getSide().equals(side)) {
                prevSum += 50;
            }
            // Player side’s king is in check
            else {
                prevSum -= 50;
            }
        }
        if (prevSum < -1500) {
            if (lastMoveInfo.getPieceMove().equals("k")) {
                lastMoveInfo.setPieceMove("k_e");
            }
        }
        int rowTo = mapSquareToPosition(lastMoveInfo.getMove().getTo()).row;
        int colTo = mapSquareToPosition(lastMoveInfo.getMove().getTo()).col;
        int rowFrom = mapSquareToPosition(lastMoveInfo.getMove().getFrom()).row;
        int colFrom = mapSquareToPosition(lastMoveInfo.getMove().getFrom()).col;

        if (lastMoveInfo.getPieceCapture() != null && lastMoveInfo.getPromotion() != null) {

            if (lastMoveInfo.getPieceCapture() != null) {
                if (lastMoveInfo.getSide().equals(side)) {
                    prevSum += weights.get(lastMoveInfo.getPieceCapture()) +
                            pst.get(lastMoveInfo.getSide()).get(lastMoveInfo.getPieceCapture())[rowTo][colTo];
                }
                // player[color]’s piece was captured (bad for player[color])
                else {
                    prevSum -= weights.get(lastMoveInfo.getPieceCapture()) +
                            pst.get(lastMoveInfo.getSide()).get(lastMoveInfo.getPieceCapture())[rowTo][colTo];
                }
            }

            if (lastMoveInfo.getPromotion() != null) {
                lastMoveInfo.setPromotion("q");
                if (lastMoveInfo.getSide().equals(side)) {
                    prevSum -= weights.get(lastMoveInfo.getPieceMove()) +
                            pst.get(lastMoveInfo.getSide()).get(lastMoveInfo.getPieceMove())[rowFrom][colFrom];
                    prevSum += weights.get(lastMoveInfo.getPromotion()) +
                            pst.get(lastMoveInfo.getSide()).get(lastMoveInfo.getPromotion())[rowTo][colTo];
                } else {
                    prevSum += weights.get(lastMoveInfo.getPieceMove()) +
                            pst.get(lastMoveInfo.getSide()).get(lastMoveInfo.getPieceMove())[rowFrom][colFrom];
                    prevSum -= weights.get(lastMoveInfo.getPromotion()) +
                            pst.get(lastMoveInfo.getSide()).get(lastMoveInfo.getPromotion())[rowTo][colTo];
                }

            }
        } else {
            if (!lastMoveInfo.getSide().equals(side)) {
                prevSum += pst.get(lastMoveInfo.getSide()).get(lastMoveInfo.getPieceMove())[rowFrom][colFrom];
                prevSum -= pst.get(lastMoveInfo.getSide()).get(lastMoveInfo.getPieceMove())[rowTo][colTo];
            } else {
                prevSum -= pst.get(lastMoveInfo.getSide()).get(lastMoveInfo.getPieceMove())[rowFrom][colFrom];
                prevSum += pst.get(lastMoveInfo.getSide()).get(lastMoveInfo.getPieceMove())[rowTo][colTo];
            }
        }
        return prevSum;
    }

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
                    return alphaBeta(depth - 1, childBoard, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
                    // return minimax(depth, board, false);
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
                if (value > bestMove) {
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

    double minimax(int depth, Board board, boolean isMaximisingPlayer) {
        if (depth == 0) {
            return -evaluateBoard(board);
        }
        List<Move> newGameMoves = board.legalMoves();
        Collections.shuffle(newGameMoves);
        ExecutorService executor = Executors
                .newFixedThreadPool(Math.min(newGameMoves.size(),
                        Runtime.getRuntime().availableProcessors()));
        double bestMove = Double.NEGATIVE_INFINITY;
        double worstMove = Double.POSITIVE_INFINITY;

        List<Future<Double>> futures = new ArrayList<>();
        Callable<Double> callable;
        Future<Double> future;
        for (Move newGameMove : newGameMoves) {
            callable = new Callable<Double>() {
                public Double call() throws Exception {
                    Board childBoard = new Board();
                    childBoard.loadFromFen(board.getFen());
                    childBoard.doMove(newGameMove);
                    return minimax(depth - 1, childBoard, !isMaximisingPlayer);
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
                if (isMaximisingPlayer) {
                    if (value > bestMove) {
                        bestMove = value;
                    }
                } else {
                    if (value < worstMove) {
                        worstMove = value;
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return isMaximisingPlayer ? bestMove : worstMove;
    }

    double evaluateBoard(Board board) {
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

    double evaluateBoard(Board board, Side side) {
        if (board.isMated()) {
            if (board.getSideToMove().equals(side)) {
                return -Math.pow(10, 10);
            } else {
                return Math.pow(10, 10);
            }
        }
        double totalEvaluation = 0;
        int remainWhite = 0, remainBlack = 0;
        Piece[] remainPieces = board.boardToArray();
        for (Piece piece : remainPieces) {
            if (!piece.getFanSymbol().equals("NONE")) {
                if (piece.getPieceSide().equals(Side.WHITE)) {
                    ++remainWhite;
                } else {
                    ++remainBlack;
                }
            }
        }

        boolean end = remainWhite <= 5 || remainBlack <= 5;
        if (board.isKingAttacked()) {
            if (board.getSideToMove().equals(side)) {
                totalEvaluation = -50;
                if (end) {
                    totalEvaluation = -200;
                }
            } else {
                totalEvaluation = 50;
                if (end) {
                    totalEvaluation = 200;
                }
            }
        }
        for (Square square : Square.values()) {
            Piece piece = board.getPiece(square);
            if (piece != null && !piece.getFanSymbol().equals("NONE")) {
                if (piece.getPieceSide().equals(side))
                    totalEvaluation += getPieceValue(piece, square, end);
                else
                    totalEvaluation -= getPieceValue(piece, square, end);
            }
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

    static HashMap<PieceType, String> mapPieceTypeToString = new HashMap<PieceType, String>() {
        {
            put(PieceType.PAWN, "p");
            put(PieceType.KNIGHT, "n");
            put(PieceType.ROOK, "r");
            put(PieceType.BISHOP, "b");
            put(PieceType.QUEEN, "q");
            put(PieceType.KING, "k");
        }
    };

    static double getPieceValue(Piece piece, Square square) {
        return getPieceValue(piece, square, false);
    }

    static double getPieceValue(Piece piece, Square square, boolean end) {
        int row = mapSquareToPosition(square).getRow();
        int col = mapSquareToPosition(square).getCol();
        if (end && piece.getPieceType().equals(PieceType.KING)) {
            return weights.get("k_e") + pst.get(piece.getPieceSide()).get("k_e")[row][col];
        }
        return weights.get(mapPieceTypeToString.get(piece.getPieceType()))
                + pst.get(piece.getPieceSide()).get(mapPieceTypeToString.get(piece.getPieceType()))[row][col];
    }

    static HashMap<String, double[][]> pst_w, pst_b;

    static HashMap<Side, HashMap<String, double[][]>> pst;

    public Minimax() {
        pst_w = new HashMap<String, double[][]>() {
            {
                put("p", pawnEvalWhite);
                put("n", knightEvalWhite);
                put("r", rookEvalWhite);
                put("b", bishopEvalWhite);
                put("q", evalQueenWhite);
                put("k", kingEvalWhite);
                put("k_e", kingEvalWhite_End);
            }
        };
        pst_b = new HashMap<String, double[][]>() {
            {
                put("p", pawnEvalBlack);
                put("n", knightEvalBlack);
                put("r", rookEvalBlack);
                put("b", bishopEvalBlack);
                put("q", evalQueenBlack);
                put("k", kingEvalBlack);
                put("k_e", kingEvalBlack_End);
            }
        };
        pst = new HashMap<Side, HashMap<String, double[][]>>() {
            {
                put(Side.WHITE, pst_w);
                put(Side.BLACK, pst_b);
            }
        };
    }

    static Position mapSquareToPosition(Square square) {
        int row = 7 - square.getRank().ordinal();
        int col = square.getFile().ordinal();
        return new Position(row, col);
    }

    static HashMap<String, Double> weights = new HashMap<String, Double>() {
        {
            put("p", 100d);
            put("n", 280d);
            put("r", 479d);
            put("b", 320d);
            put("q", 929d);
            put("k", 60000d);
            put("k_e", 60000d);
        }
    };

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
