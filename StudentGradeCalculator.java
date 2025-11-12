import java.util.Scanner;

public class StudentGradeCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Taking input for 5 subjects
        System.out.print("Enter marks of Subject 1: ");
        double sub1 = scanner.nextDouble();

        System.out.print("Enter marks of Subject 2: ");
        double sub2 = scanner.nextDouble();

        System.out.print("Enter marks of Subject 3: ");
        double sub3 = scanner.nextDouble();

        System.out.print("Enter marks of Subject 4: ");
        double sub4 = scanner.nextDouble();

        System.out.print("Enter marks of Subject 5: ");
        double sub5 = scanner.nextDouble();


        // Calculate total and average
        double total = sub1 + sub2 + sub3 + sub4 + sub5;
        double average = total / 5;

        // Grade calculation
        char grade;
        if (average >= 90) {
            grade = 'A';
        } else if (average >= 80) {
            grade = 'B';
        } else if (average >= 70) {
            grade = 'C';
        } else if (average >= 60) {
            grade = 'D';
        } else {
            grade = 'F';
        }

        // Display results
        System.out.println("\n----- Student Result -----");
        System.out.println("Total Marks: " + total);
        System.out.println("Average Percentage: " + average + "%");
        System.out.println("Grade: " + grade);

        scanner.close();
    }
}
