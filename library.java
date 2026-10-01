import java.util.Scanner;
public class library {

    public static void main(String[] args) {
        Scanner idScanner =  new Scanner(System.in);
    System.err.print("Input ISBN "); 
    String isbn = idScanner.nextLine();
        String cleanedIsbn = isbn.replaceAll("[\\s-]", "");

    if (cleanedIsbn.matches("^([0-9X]{10}|[0-9]{13})$")) {
            System.out.print("Valid ISBN");
        }
        else if (cleanedIsbn.isEmpty()) {
            System.out.println("Input empty");
        }
        else
            System.out.print("This ISBN is not valid");

    idScanner.close();
    }

}
