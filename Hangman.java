// Assignment 3: Hangman
// By: Javier Arevalo
// Section: Thursdays 330

import acm.program.*;
import acm.util.*;
import java.io.*;    // for File
import java.util.*;  // for Scanner

public class Hangman extends HangmanProgram {

	public void run() {
		intro();
		boolean playAgain = true;
		String filename = promptUserForFile("Dictionary filename? ", "res");
		while (playAgain) {
			gamesCount (playAgain);
			playOneGame(getRandomWord(filename));
			playAgain = readBoolean("Play again (Y/N)?", "Y", "N");
		}
		stats(games, wins, best);

	}

	// The following code will print the lines at the beggining that tell the user what the game is about. 
	private void intro() {

		println("I will think of a random word.");
		println("You'll try to guess its letters.");
		println("Every time you guess is a letter");
		println("that isn't my word, a new body");
		println("part of the hanging man appears.");
		println("Guess correctly to avoid the gallows!");
		println();
	}	

	//This is the main body of the program. In it the player plays one game of Hangman and all the counting and replacing hints are done in it. 
	private int playOneGame(String secretWord) {

		//The program will compare chars between the guess and the secret word. To be able to do this, both are converted to uppercase. 
		String uppercaseSecretWord = "";
		for (int i = 0; i < secretWord.length(); i ++) {
			char ch = secretWord.charAt(i);
			char upperCase = Character.toUpperCase(ch);
			uppercaseSecretWord += upperCase;
		}		

		//The following code will declare the variables that are going to be used during the playOneGame method. 
		int guessesLeft = 8; 
		String hint = "";
		String guess = "";
		String guesses = "";
		String wrongGuesses = "";

		//The following code will create the hint so that its length matches the length of the secret word. 
		for (int i = 0; i < secretWord.length(); i ++) {
			char createHint = '_';
			hint = hint + createHint;
		}


		//The following code is the main body of the play once method. In it the program keeps running until either the user runs out of guesses or the user guesses the secret word. 
		while (guessesLeft > 0 &&  gameStillNotOver( uppercaseSecretWord,  hint) ) {
			canvas.clear();
			displayHangman(guessesLeft);
			println("Hint: " + hint);
			println("Wrong guesses: " + wrongGuesses);
			println("Guesses left: " + guessesLeft);


			guess = readLine("Your guess? ");	

			//This code checks that the the guess is only one letter. 
			int guessLength = guess.length();
			while ( guessLength != 1) {
				println("Type a single letter from A-Z.");
				println(); 
				guess = readLine("Your guess? ");
				guessLength =guess.length();
			}	

			//This code passes the guess to uppercase so that its chars can latter be compared with the chars in the secret word (also uppercase). 
			String newGuess = "";
			for (int i = 0; i < guess.length(); i ++) {
				char ch = guess.charAt(i);
				char upperCase = Character.toUpperCase(ch);
				newGuess += upperCase;
			}
			guess = newGuess;

			//The following code checks if the guess entered has already been entered. 
			//If it has already been entered, the boolean returns true and the code prompts the user for a new guess. 
			//The new guess has to be converted to uppercase to allow char comparison later in the program. 
			while (checkForRepeatedGuess(guess, guesses)) {
				println("You already guessed this letter. ");
				guess = readLine("What is your guess? ");
				char lowerCaseGuess = guess.charAt(0);
				char upperCaseGuess = Character.toUpperCase(lowerCaseGuess);
				String momentaryGuess = "" + upperCaseGuess;
				guess = momentaryGuess;
				checkForRepeatedGuess(guess, guesses);
			} 

			guesses = guesses + guess;

			//The following code will check if there is a match between the guess and any letter in the secret word.
			checkLetters(uppercaseSecretWord, guess);

			//The following code will work if there is at least 1 letter that is in the password. This code will find where it is and then replace the letter and update the hint. 
			if (checkLetters(uppercaseSecretWord, guess)) {
				hint = createHint( uppercaseSecretWord,  guess,  hint);
			} else {

				//The previous code worked when the guess was correct and needed to update the hint multiple times. The following code is for when the guess is incorrect. 
				if (checkLetters(uppercaseSecretWord, guess) == false) {
					println("Incorrect");
					guessesLeft--;
					wrongGuesses= wrongGuesses + guess;
					gameStillNotOver( uppercaseSecretWord,  hint);
				}
				println();	

			}


			//The following code will run if the game is over. The first if statement is for when the game ended because the user correctly guessed teh secret word. 
			if (gameStillNotOver (uppercaseSecretWord, hint)== false) {
				println("You win! The word was: " + secretWord );
				//The boolean is what allows the variable of games won to update. It is inside the if statement that satisfies the condition that the game was won, so it is able to assume this. Then it calls the method to update it. 
				boolean userWonGame = true;
				gamesWon(userWonGame);
				boolean playAgain = true;
				bestGame (playAgain,  guessesLeft);
			} else {
				//The following code is for when the game ended because the user ran out of guesses. 
				if (guessesLeft <= 0) {
					canvas.clear();
					displayHangman( guessesLeft);
					println("You lost. ");
					println("The secret word was: " + secretWord );
				}
			}
		}
		return  guessesLeft;


	}

	//This code is one of the most important methods in Hangman. In it the for loop checks if there is a match between the one letter guess the user entered and any letter in the secret word. If there is a match it returns true. 
	private boolean checkLetters(String uppercaseSecretWord, String guess ) {
		for (int i = 0; i < uppercaseSecretWord.length(); i ++) {
			int j = 0;
			//Guess has length one as checked before so variable j can be a constant so that it allows for char comparison. 
			if(guess.charAt(j)== uppercaseSecretWord.charAt(i)) {
				return true;
			}
		}
		return false;

	}

	//This method builds on the previous one. When the previous one is true, this method will find the index of the match and return it. This int is used in the create hint method
	//with the previous boolean to replace the index where the match was with the letter the user entered to update the hint. 
	private int checkIndex(String uppercaseSecretWord, String guess ) {
		for (int i = 0; i < uppercaseSecretWord.length(); i ++) {
			int j = 0;
			//Guess has length one as checked before so variable j can be a constant so that it allows for char comparison. 
			if(guess.charAt(j)== uppercaseSecretWord.charAt(i)) {
				return i;
			}
		}
		return 0;
	}

	//This boolean will return true if it finds that the correct guess from the user appears multiple times in the secret word. If it is true, the code in create hint will use
	//this information to do additional steps to replace the correct guess everytime it appears on the secret word. 
	//Precondition: the guess is in the secret word at least one time. It assumes this because this code is inside the loop that runs if the guess is in the secret word. 
	//Postcondition: if the guess appears more than once, then this code will return true and allow the following code to replace the guess everytime it appears on the secret word 
	private boolean checkRepeatedLetters(String uppercaseSecretWord, String guess, int indexChange ) {
		for (int i = indexChange+1; i < uppercaseSecretWord.length(); i ++) {
			int j = 0;
			//Guess has length one as checked before so variable j can be a constant so that it allows for char comparison. 
			if(guess.charAt(j)== uppercaseSecretWord.charAt(i)) {
				return true;
			}
		}
		return false;

	}

	//This code will run whenever the previous code finds that the guess appears more than once in the secret word. This code will find the index of the next match and will continue finding this index until
	//the letter no longer appears in the secret word. This code will be used by create hing method to find all the indexes of the match and replace each one. 
	private int checkIndexRepeated(String uppercaseSecretWord, String guess, int newIndexChange ) {
		for (int i = newIndexChange + 1; i < uppercaseSecretWord.length(); i ++) {
			int j = 0;
			//Guess has length one as checked before so variable j can be a constant so that it allows for char comparison. 
			if(guess.charAt(j)== uppercaseSecretWord.charAt(i)) {
				return i;
			}
		}
		return 0;

	}

	//This boolean checks if the game should continue or not. Whenever the hint matches the secret word it means the game is over so it will return false. 
	private boolean gameStillNotOver(String uppercaseSecretWord, String hint) {
		if (uppercaseSecretWord.equalsIgnoreCase(hint)) {
			return false;
		}

		return true;

	}

	//This code is similar to the code that checks for a match. What this one does is that it checks if the guess the user entered has already been previously entered. If it is 
	//it returns true and the play Once code reprompts the user for a new guess until he enters a guess that has not been previosuly entered. 
	private boolean checkForRepeatedGuess(String guess, String guesses) {
		for (int i = 0; i < guesses.length(); i ++) {
			int j = 0;
			if (guesses.charAt(i) == guess.charAt(j)) {
				return true;
			}
		}
		return false;
	}

	//This is the other main code that allows the Hangman program to work. This code uses many of the previous methods and uses their return values to create the new hint and keep updating it. 
	//The main idea is that the hint starts as a strings of '_' that matches the length of the secret word. Whenever there is a correct guess, the hint will update to show the correct guess each time it appears on the secret word. 
	private String createHint(String uppercaseSecretWord, String guess, String hint) {
		println();
		//Assuming that it is correct, get i from the method checkIndex. 
		int indexChange = (checkIndex(uppercaseSecretWord, guess));

		//Given the index of match between the guess and secret word, we can modify the hint being displayed.
		String newHint = "";

		//This code will put the char _ until index change or leave any existing letter that has already been guessed. 
		for (int i = 0; i < indexChange; i ++) {
			if (hint.charAt(i) == '_') {
				char ch = '_';
				newHint = newHint + ch;
			} else {
				char leave = hint.charAt(i);
				newHint = newHint + leave;
			}
		}

		//Now when we reach i, the code will replace the _ char with the guess the user entered. 
		char change = uppercaseSecretWord.charAt(indexChange);
		newHint = newHint + change;

		//After replacing the correct guess in the hint, the following code will check if the letter correctly guessed appears multiple times in the secret word. 
		checkRepeatedLetters(uppercaseSecretWord,  guess, indexChange );

		//The following variable is what allows the code to start running when it is checking for the second match in secret word. 
		int newIndexChange = indexChange;

		while (checkRepeatedLetters(uppercaseSecretWord, guess, newIndexChange)) {
			//The otherIndexChange variable will be used as the last match (between guess and secret word) that was replaced. 
			int otherIndexChange = newIndexChange; 
			//The newIndexChanged variable is the index where the match after otherIndexChange occurred and the ones that needs to be changed. 
			newIndexChange = (checkIndexRepeated(uppercaseSecretWord, guess, newIndexChange));

			//This code will use the previous variables to move from one match to the next or from otherIndexChanged to newIndexChange. 
			for (int i = otherIndexChange+1; i < newIndexChange; i ++) {
				if (hint.charAt(i) == '_') {
					char ch = '_';
					newHint = newHint + ch;
				} else {
					char leave = hint.charAt(i);
					newHint = newHint + leave;
				}
			}

			//Once it reaches the match, it will replace it. 
			char changeAgain = uppercaseSecretWord.charAt(newIndexChange);
			newHint = newHint + changeAgain;

			//After replacing the match at newIndexChange, the code will check if the correct guess appears another time in the secret word, and if it does it will run again. 
			checkRepeatedLetters(uppercaseSecretWord,  guess, newIndexChange );
		}


		//This part prints the remaining part of the hint. If there is already a letter in a space it will move on
		//It starts at newIndexChange because that was the last time in the code that there was a match, implying. that there should not be any more changes in the hint.
		for (int i = newIndexChange+1; i < uppercaseSecretWord.length(); i ++) {
			if (hint.charAt(i) == '_') {
				char ch = '_';
				newHint = newHint + ch;
			} else {
				char leave = hint.charAt(i);
				newHint = newHint + leave;
			}
		}

		println();
		hint = newHint;
		//The following code checks if the user has already completely guessed the secret word or not.
		gameStillNotOver(uppercaseSecretWord,  hint);
		return hint;

	} 


	// TODO: comment this method
	private char readGuess(String guessedLetters) {
		// TODO: write this method
		return '?';
	}

	//The following method will display the hangman drawing on the canvas. It checks the number of guesses left and draws the hangman accordingly. 
	private void displayHangman(int guessesLeft) {
		if (guessesLeft == 8) {
			try {
				Scanner input = new Scanner (new File ("res/display8.txt"));
				while (input.hasNextLine()) {
					String line = input.nextLine();
					canvas.println(line);
				}
				input.close();
			} catch (FileNotFoundException e) {
				println("File not found.");
			}


		}
		if (guessesLeft == 7) {
			try {
				Scanner input = new Scanner (new File ("res/display7.txt"));
				while (input.hasNextLine()) {
					String line = input.nextLine();
					canvas.println(line);
				}
				input.close();
			} catch (FileNotFoundException e) {
				println("File not found.");
			}


		}if (guessesLeft == 6) {
			try {
				Scanner input = new Scanner (new File ("res/display6.txt"));
				while (input.hasNextLine()) {
					String line = input.nextLine();
					canvas.println(line);
				}
				input.close();
			} catch (FileNotFoundException e) {
				println("File not found.");
			}


		}if (guessesLeft == 5) {
			try {
				Scanner input = new Scanner (new File ("res/display5.txt"));
				while (input.hasNextLine()) {
					String line = input.nextLine();
					canvas.println(line);
				}
				input.close();
			} catch (FileNotFoundException e) {
				println("File not found.");
			}


		}if (guessesLeft == 4) {
			try {
				Scanner input = new Scanner (new File ("res/display4.txt"));
				while (input.hasNextLine()) {
					String line = input.nextLine();
					canvas.println(line);
				}
				input.close();
			} catch (FileNotFoundException e) {
				println("File not found.");
			}


		}if (guessesLeft == 3) {
			try {
				Scanner input = new Scanner (new File ("res/display3.txt"));
				while (input.hasNextLine()) {
					String line = input.nextLine();
					canvas.println(line);
				}
				input.close();
			} catch (FileNotFoundException e) {
				println("File not found.");
			}


		}if (guessesLeft == 2) {
			try {
				Scanner input = new Scanner (new File ("res/display2.txt"));
				while (input.hasNextLine()) {
					String line = input.nextLine();
					canvas.println(line);
				}
				input.close();
			} catch (FileNotFoundException e) {
				println("File not found.");
			}


		}if (guessesLeft == 1) {
			try {
				Scanner input = new Scanner (new File ("res/display1.txt"));
				while (input.hasNextLine()) {
					String line = input.nextLine();
					canvas.println(line);
				}
				input.close();
			} catch (FileNotFoundException e) {
				println("File not found.");
			}


		}
		if (guessesLeft == 0) {
			try {
				Scanner input = new Scanner (new File ("res/display0.txt"));
				while (input.hasNextLine()) {
					String line = input.nextLine();
					canvas.println(line);
				}
				input.close();
			} catch (FileNotFoundException e) {
				println("File not found.");
			}


		}
	}

	//This method will count the number of wins from the user. The variable win is declared outside the method to avoid the variable reseting to 0 each time is called. Having it outside allows it to update. 
	//Precondition: the code will run if the boolean is true and the game is already over. It is called whenever the game has ended and the user won. 
	//Postcondition: if the boolean is true, the code will update the variable of games won. 
	int wins = 0;
	private int gamesWon (boolean userWonGame) {

		if (userWonGame) {
			wins ++;
		}
		return wins;
	}

	//The following method is very similar to the previous one. This code is called everytime the user enters yes to play another game and updates the number of games the user plays. 
	int games = 0;
	private int gamesCount (boolean playAgain) {

		if (playAgain) {
			games ++;
		}

		return games;
	}

	//This method will return the best play game by giving the number of hints for the best game. 
	//The variable best is declared outside at 0 and whenever their is a guessesLeft higher than it, it will be replaced. Each time the game is won, this method is called 
	//to check if that game was better (more guesses left) than the current best. If it is it will replace it. 
	int best = 0;
	private int bestGame (boolean playAgain, int guessesLeft) {

		if (guessesLeft >= best) {
			best = guessesLeft;
		}

		return best;
	}

	//This method is called when the user no longer wants to play and prints the overall statistics. It uses previous methods that were continuously being updated
	//such as games won and games played. 
	private void stats(int gamesCount, int gamesWon, int bestGame) {
		double winPercent = ((double)gamesWon/gamesCount) * 100;
		println("Overall statistics: ");
		println("Games played: " + gamesCount);
		println("Games won: " + gamesWon);
		println("Win percent: " + winPercent + "%.");
		println("Best game: " + bestGame + " guess(es) remaining.");
		println("Thanks for playing!");
	}

	// This method opens the file at the beginning of the game. The user is prompted to open the file, and this
	//method will read it and if it can not find it, it will re-prompt the user using a try-catch block code. 
	private String getRandomWord(String filename) {
		try {
			
			Scanner input = new Scanner (new File(filename));
			//The code will then read the number of words in the list and get a random integer, n, from 0 to the number of words. 
			int numberOfWordsInFile = input.nextInt();
			int wordNumber = RandomGenerator.getInstance().nextInt(0, numberOfWordsInFile);
			//The previous number will be used to see where to stop. The code will enter n -1 spaces and stop at n. 
			for (int i = 0; i < wordNumber-1; i ++) {
				if (input.hasNext())
					input.next();
			}  
			
			String randomWord = input.next();
			input.close();
			return randomWord;
			
		} catch (FileNotFoundException e) {
			println("File not found.");
		}
		return "";

	}
}


