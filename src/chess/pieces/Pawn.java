package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	private ChessMatch chessMath;
	
	public Pawn(Board board, Color color, ChessMatch chessMath) {
		super(board, color);
		this.chessMath = chessMath;
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
			
		// #specialmove En Passant
		int iPos, iMov;
		
		if (getColor() == Color.WHITE) {
			iPos = 7-position.getRow();
			iMov = -1;
		}
		else {
			iPos = position.getRow();
			iMov = 1;
		}
		
		if (iPos == 4) {
			for (int c=-1; c<=1; c++) {
				if (c!=0) {
					Position oPiece = new Position(position.getRow(), position.getColumn() + c);
					if (getBoard().positionExists(oPiece) && IsThereOpponentPiece(oPiece) && getBoard().piece(oPiece) == chessMath.getEnPassantVulnerable()) {
						mat[oPiece.getRow() + iMov][oPiece.getColumn()] = true;
					}
				}
			}
		}
		
		return mat;
	}
	
}
