package Library;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import javax.print.DocFlavor.STRING;
import io.github.cdimascio.dotenv.Dotenv;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class library {
    private final Scanner idScanner;
    public library() {
        Dotenv dotenv = Dotenv.load();
        this.idScanner = new Scanner(System.in);
        /*Test ISBNs for scanner
        ISBN-13
        "Steve Jobs" 9781451648546 Walter Isaacson
        "Freakonomics" 9780141019017 Steven D. Levitt, Stephen J. Dubner
        ISBN-10
        "The Road" 0307387895  Cormac McCarthy
        "1984" 0451524934 George Orwell
        WHEN TESTING FAILSAFE SEARCH 1324657890
        */
    }

    public void main(String[] args) {
        String isbn = getISBN();
        String response = bookData(isbn);
        String checkedResponse = tulli(response);
        lastChance(checkedResponse);
        
        //TODO: make a new function that parses JSON
    }

    /**
     * Takes in user inputted ISBN and runs a do while loop until it 
     * gets a valid ISBN
     * 
     * @return cleanedISBN with whitespace and hyphens removed
     */
    private String getISBN() {
        String cleanedIsbn;

        //runs a do while loop until a valid ISBN is given

        do {
        System.out.print("Input ISBN "); 
        String isbn = this.idScanner.nextLine();

        //Removing whitespace and hyphens from the ISBN
        cleanedIsbn = isbn.replaceAll("[\\s-]", "");
        if (cleanedIsbn.isEmpty()) {
            System.out.println("The input cannot be empty");
        } 
        else if (!cleanedIsbn.matches("^([0-9X]{10}|[0-9]{13})$")) {
                System.out.println("Illegal ISBN format");
        }

        }
        while (!cleanedIsbn.matches("^([0-9X]{10}|[0-9]{13})$"));
        
            System.out.println("Valid ISBN");
        
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
    private String bookData(String isbn) {
        
        String fields = "key,title,author_name,first_publish_year,language,number_of_pages_median,cover_i";
        String targetUrl = "https://openlibrary.org/search.json?isbn=" + isbn + "&fields=" + fields; 
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .GET()
                .build();
        System.out.println("Looking for a book matching the ISBN");
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());                
                if (response.statusCode() == 200) {
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
    /**
     * Tulli checks the received data and either runs a failsafe second search
     * using Author name and book name or returns the valid book data
     * @return response if it's not null or doesn't contain "\"num_found\":0"
     * @return newResponse.body if a second search is needed and null if the search fails
     * @param response gives the function the unparsed json file data from the ISBN quarry
     * 
     * @throws IOException if the systems fails to query
     * @throws InterruptedException if the user interrupts the query
     */

    private String tulli(String response) {
        if (response == null || response.contains("\"num_found\":0") ) {
            System.out.println("Failed to find book using ISBN");
        
            System.out.println("Insert book name");
            String kirja = this.idScanner.nextLine();
            System.out.println("Insert author name");
            String author = this.idScanner.nextLine();

            //HTTP Starts again
            String encodedKirjaQuery = URLEncoder.encode(kirja, StandardCharsets.UTF_8);
            String encodedAuthorQuery = URLEncoder.encode(author, StandardCharsets.UTF_8);
            String fields = "key,title,author_name,first_publish_year,language,number_of_pages_median,cover_i";

            String targetUrl = "https://openlibrary.org/search.json?title=" + encodedKirjaQuery +
            "&author=" + encodedAuthorQuery + "&language=eng" + "&fields=" + fields;
            HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .GET()
                    .build();
            System.out.println("Looking for a book based on the name and author");
                try {
                    HttpResponse<String> newResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                    if (newResponse.statusCode() == 200) {
                    return newResponse.body();
                    
                    } 
                    else {
                        System.out.println("Failed to fetch data. HTTP Status: " + newResponse.statusCode());
                        return null;
                    }
                }
        catch (IOException | InterruptedException e) {
                System.err.println("An error occurred during the request: " + e.getMessage());
                return null;
                
        }


        }
        else {
           return response;

        }
    }

    /**
     * lastChance is the final check function it either prints a fail message in the case of 
     * a error during the search or if no book was found with the ISBN or the name and author 
     * of the book
     * After this if there is a json file with data to parse, it parses it and prints the data
     * in a easy to view format using imported google.gson classes
     * 
     * 
     * @param checkedResponse the received data that was checked in the tulli function
     */
    //TODO: make a new function that parses JSON
    private void lastChance(String checkedResponse) {
        if (checkedResponse == null) {
            System.out.println("An error occured during search");
        }
        else if (checkedResponse.contains("\"num_found\":0")) {
            System.out.println("No books found");
        }
        else {
            


            System.out.println("\n==================================");
            System.out.println("BOOK INFORMATION");
            System.out.println("==================================");
            System.out.println(checkedResponse);
        }
    }

}

