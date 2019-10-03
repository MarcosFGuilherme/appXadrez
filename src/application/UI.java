package application;

import chess.ChessPiece;

public class UI {
	public static  void printBoard(ChessPiece[][] pieces) {
		for (int lin=0; lin<pieces.length; lin++) {
			System.out.print(8-lin + " ");
			for (int col=0; col<pieces.length;col++) {
				printPiece(pieces[lin][col]);
			}
			System.out.println();
		}
		System.out.println("  a b c d e f g h ");
	}
	
	private static void printPiece(ChessPiece piece) {
		if(piece == null) {
			System.out.print("-");
		}
		else {
			System.out.print(piece);
		}
		System.out.print(" ");
	}
}
