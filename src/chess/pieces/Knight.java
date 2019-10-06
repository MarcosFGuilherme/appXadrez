package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Knight extends ChessPiece {

	public Knight(Board board, Color color) {
		super(board, color);
	}

	private boolean canMove(Position position) {
		ChessPiece p = (ChessPiece) getBoard().piece(position);
		return p == null || p.getColor() != getColor();
	}

	@Override
	public String toString() {
		return "N";
	}

	@Override
	public boolean[][] PossibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		Position p = new Position(0, 0);
		Position k = new Position(position.getRow(), position.getColumn());

		for (int l = -2; l <= 2; l++) {
			for (int c = -2; c <= 2; c++) {
				p.setValues(position.getRow() + l, position.getColumn() + c);
				if (p.getRow() != k.getRow() && p.getColumn() != k.getColumn() && (Math.abs(l) != Math.abs(c))) {
					if (getBoard().positionExists(p) && canMove(p)) {
						mat[p.getRow()][p.getColumn()] = true;
					}
				}
			}
		}

		return mat;
	}

}
