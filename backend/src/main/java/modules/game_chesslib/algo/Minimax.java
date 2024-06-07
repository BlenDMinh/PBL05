package modules.game_chesslib.algo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
        if (board.isDraw()) {
            if (totalEvaluation < 0) {
                return 1000;
            } else {
                return -1000;
            }
        }
        return totalEvaluation;
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
            put("n", 320d);
            put("r", 500d);
            put("b", 330d);
            put("q", 900d);
            put("k", 20000d);
            put("k_e", 20000d);
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
            { 50, 50, 50, 50, 50, 50, 50, 50 },
            { 10, 10, 20, 30, 30, 20, 10, 10 },
            { 5, 5, 10, 25, 25, 10, 5, 5 },
            { 0, 0, 0, 20, 20, 0, 0, 0 },
            { 5, -5, -10, 0, 0, -10, -5, 5 },
            { 5, 10, 10, -20, -20, 10, 10, 5 },
            { 0, 0, 0, 0, 0, 0, 0, 0 }
    };

    static double[][] pawnEvalBlack = reverseArray(pawnEvalWhite);

    static double[][] knightEvalWhite = {
            { -50, -40, -30, -30, -30, -30, -40, -50 },
            { -40, -20, 0, 0, 0, 0, -20, -40 },
            { -30, 0, 10, 15, 15, 10, 0, -30 },
            { -30, 5, 15, 20, 20, 15, 5, -30 },
            { -30, 0, 15, 20, 20, 15, 0, -30 },
            { -30, 5, 10, 15, 15, 10, 5, -30 },
            { -40, -20, 0, 5, 5, 0, -20, -40 },
            { -50, -40, -30, -30, -30, -30, -40, -50 }
    };

    static double[][] knightEvalBlack = reverseArray(knightEvalWhite);

    static double[][] bishopEvalWhite = {
            { -20, -10, -10, -10, -10, -10, -10, -20 },
            { -10, 0, 0, 0, 0, 0, 0, -10 },
            { -10, 0, 5, 10, 10, 5, 0, -10 },
            { -10, 5, 5, 10, 10, 5, 5, -10 },
            { -10, 0, 10, 10, 10, 10, 0, -10 },
            { -10, 10, 10, 10, 10, 10, 10, -10 },
            { -10, 5, 0, 0, 0, 0, 5, -10 },
            { -20, -10, -40, -10, -10, -40, -10, -20 }
    };

    static double[][] bishopEvalBlack = reverseArray(bishopEvalWhite);

    static double[][] rookEvalWhite = {
            { 0, 0, 0, 0, 0, 0, 0, 0 },
            { 5, 10, 10, 10, 10, 10, 10, 5 },
            { -5, 0, 0, 0, 0, 0, 0, -5 },
            { -5, 0, 0, 0, 0, 0, 0, -5 },
            { -5, 0, 0, 0, 0, 0, 0, -5 },
            { -5, 0, 0, 0, 0, 0, 0, -5 },
            { -5, 0, 0, 0, 0, 0, 0, -5 },
            { 0, 0, 0, 5, 5, 0, 0, 0 }
    };

    static double[][] rookEvalBlack = reverseArray(rookEvalWhite);

    static double[][] evalQueenWhite = {
            { -20, -10, -10, -5, -5, -10, -10, -20 },
            { -10, 0, 0, 0, 0, 0, 0, -10 },
            { -10, 0, 5, 5, 5, 5, 0, -10 },
            { -5, 0, 5, 5, 5, 5, 0, -5 },
            { 0, 0, 5, 5, 5, 5, 0, -5 },
            { -10, 5, 5, 5, 5, 5, 0, -10 },
            { -10, 0, 5, 0, 0, 0, 0, -10 },
            { -20, -10, -10, -5, -5, -10, -10, -20 }
    };

    static double[][] evalQueenBlack = reverseArray(evalQueenWhite);

    static double[][] kingEvalWhite = {
            { 20, 30, 10, 0, 0, 10, 30, 20 },
            { 20, 20, 0, 0, 0, 0, 20, 20 },
            { -10, -20, -20, -20, -20, -20, -20, -10 },
            { -20, -30, -30, -40, -40, -30, -30, -20 },
            { -30, -40, -40, -50, -50, -40, -40, -30 },
            { -30, -40, -40, -50, -50, -40, -40, -30 },
            { -30, -40, -40, -50, -50, -40, -40, -30 },
            { -30, -40, -40, -50, -50, -40, -40, -30 }
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
