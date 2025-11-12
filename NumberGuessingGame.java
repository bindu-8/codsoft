import java.util.Random;
import java.util.Scanner;

public class NumberGuessingGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        int score = 0;
        int rounds = 0;
        
        System.out.println("🎮 Welcome to the Number Guessing Game!");

        while (true) {
            int number = random.nextInt(100) + 1; // Random number from 1 to 100
            int attemptsLeft = 5; // Limit attempts
            rounds++;

            System.out.println("\nRound " + rounds + " started!");
            System.out.println("Guess the number between 1 and 100.");

            while (attemptsLeft > 0) {
                System.out.print("\nAttempts left: " + attemptsLeft + " | Enter your guess: ");
                
                // Input validation
                if (!scanner.hasNextInt()) {
                    System.out.println("⚠️ Please enter a valid number!");
                    scanner.next(); // Clear invalid input
                    continue;
                }

                int guess = scanner.nextInt();

                if (guess == number) {
                    System.out.println("✅ Correct! You guessed the number! 🎉");
                    score++;
                    break;
                } else if (guess > number) {
                    System.out.println("📉 Too high! Try again.");
                } else {
                    System.out.println("📈 Too low! Try again.");
                }
                
                attemptsLeft--;
            }

            if (attemptsLeft == 0) {
                System.out.println("❌ You ran out of attempts! The number was: " + number);
            }

            System.out.println("🏆 Current Score: " + score + " / " + rounds);

            System.out.print("\nDo you want to play another round? (yes/no): ");
            String playAgain = scanner.next().toLowerCase();

            if (!playAgain.equals("yes")) {
                System.out.println("\n✨ Thank you for playing! 👋");
                System.out.println("Final Score: " + score + " / " + rounds);
                break;
            }
        }

        scanner.close();
    }
} 
