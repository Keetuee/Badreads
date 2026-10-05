package Library;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;
public class Library{
    private final String apiKey;
    public Library() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("API_KEY");
    }

    public void main(String[] args) {
        String isbn = getISBN();
        String response = bookData(isbn);
        tulli(response);
    }
    private String getISBN(){
        //Creating the input scanner
        Scanner idScanner =  new Scanner(System.in);
        System.err.print("Input ISBN "); 
        String isbn = idScanner.nextLine();
        //Removing spaces and dashes from the ISBN
        String cleanedIsbn = isbn.replaceAll("[\\s-]", "");
        
        //restricts the valid inputs for the ISBN
        if (cleanedIsbn.matches("^([0-9X]{10}|[0-9]{13})$")) {
            System.out.print("Valid ISBN");
        }
        else if (cleanedIsbn.isEmpty()) {
            System.out.println("Input empty");
        }
        else System.out.print("This ISBN is not valid");
        
        idScanner.close();
        //Returns the new ISBN to main function
        return cleanedIsbn;
        }
    /**
     * Queries the api using a HTTP client
     * @param isbn gives the function the inputted ISBN number
     * @return response.body if it finds the book from the given ISBN and null if the query fails
     * @throws IOExpection  if the systems fails to query
     * @throws InterruptedException if the user interrupts the query
     */
    private String bookData(String isbn){
        //HTTPS fetch 
        String targetUrl = String.format("https://www.googleapis.com/books/v1/volumes?q=:%s&key=%s&country=FI",isbn,apiKey);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .GET()
                .build();
        System.out.println("Looking for a book matching the ISBN");
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    System.out.println("--- API Response Data ---");
                    System.out.println(response.body());
                    return response.body();
                } 
                else {
                    System.out.println("Failed to fetch data. HTTP Status: " + response.statusCode());
                    return null;
                }
            }
        catch (IOException | InterruptedException e) {
                System.err.println("An error occurred during the request: " + e.getMessage());
                return null;
        }
    }
    public void tulli(String response){
        if (response == null) {
            System.out.println("No books found :C");
        }
        else if (response.contains("\"totalItems\": 0")) {
        }
        else{
            
        }
    }

}

