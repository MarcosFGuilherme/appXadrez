package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	public Pawn(Board board, Color color) {
		super(board, color);
	}
	
	@Override
	public String toString() {
		return "P";
	}
	
	@Override
	public boolean[][] PossibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];

		Position p = new Position(0, 0);
		boolean p1 = false;
		
		int l = (getColor() == Color.WHITE)?-1:1;
	
		for (int c=-1;c<=1;c++) {
			p.setValues(position.getRow()+l, position.getColumn()+c);
			if(getBoard().positionExists(p)) {
				if (c==0 && !getBoard().thereIsAPiece(p)) {
					mat[p.getRow()][p.getColumn()] = true;
					p1 = true;
				}
				else if (c!=0 && IsThereOpponentPiece(p)) {
					mat[p.getRow()][p.getColumn()] = true;
				}
			}
		}
		if (getMoveCount() == 0) {
			p.setValues(position.getRow()+l*2, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().thereIsAPiece(p) && p1) {
				mat[p.getRow()][p.getColumn()] = true;
			}
		}
			
		return mat;
	}
	
}
