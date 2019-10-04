package application;

import java.util.Scanner;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ChessMatch chessMath = new ChessMatch();
		while (true) {
			UI.printBoard(chessMath.getPieces());
			System.out.println();
			System.out.print("Source: ");
			ChessPosition source = UI.ReadChessPosition(sc);
			
			System.out.println();
			System.out.print("Target: ");
			ChessPosition target = UI.ReadChessPosition(sc);
			
			ChessPiece capturePiece = chessMath.PerformChessMove(source, target);
			
			
		}
		
	}
}
