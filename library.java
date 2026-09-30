import java.util.Scanner;
import java.util.scanner;
public class library {

    public static void main(String[] args) {
        Scanner idScanner =  new Scanner(System.in);
    System.err.print("Input ISBN "); 
    String isbn = idScanner.nextLine();
        if (isbn.matches("[0-9]+")) {
            System.out.print("Only numbers");
        
        }
        else if (isbn.isEmpty()) {
            System.out.println("Input empty");
        }
            else
                System.out.print("This ISBN is not valid");

    idScanner.close();
    }

}
