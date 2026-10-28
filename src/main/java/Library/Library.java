package Library;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Library {
    private final Scanner idScanner;
    private String searchedTitle = null;
    public Library() {
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
        String isbn;
        do {
            searchedTitle = null;
            isbn = getISBN();
        if(isbn == null || isbn.equalsIgnoreCase("q")) {
            System.out.println("Exiting program. Goodbye!");
            break;
        }
        String response = bookData(isbn);
        String checkedResponse = tulli(response); 
        //parses the checkedResponse and finds how many books were found
        JsonObject root = JsonParser.parseString(checkedResponse).getAsJsonObject();
        int numFound = root.get("numFound").getAsInt();
        if (numFound == 1) {
            lastChanceSingle(checkedResponse);
        } 
        else {
            lastChanceMulti(checkedResponse);
        }   
        }
        while (true);
        
    }

    /**
     * Takes user inputted ISBN and runs a do while loop until it 
     * gets a valid ISBN
     * Unless the exit input is given "q"
     * @return cleanedISBN with whitespace and hyphens removed
     * @return cleanedISBN with the letter q which quits the program
     */
    private String getISBN() {
        String cleanedIsbn;

        //runs a do while loop until a valid ISBN is given
        //or until the exit input is given
        do {
        System.out.println("Input ISBN to search for books");
        System.out.println("Input \"q\" to quit");
        String isbn = this.idScanner.nextLine();
        //Removing whitespace and hyphens from the ISBN
        cleanedIsbn = isbn.replaceAll("[\\s-]", "");
        if (cleanedIsbn.isEmpty()) {
            System.out.println("The input cannot be empty");
        } 
        else if (cleanedIsbn.equals("q")) {
            return cleanedIsbn;
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
        
        String fields = "key,title,author_name,first_publish_year,number_of_pages_median,cover_i,isbn";
        String targetUrl = "https://openlibrary.org/search.json?isbn=" + isbn + "&fields=" + fields; 
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .GET()
                .build();
        System.out.println("Looking for a book matching the ISBN");
            try {                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());                
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
     * @return response if it's not null or doesn't contain "\"numFound\":0"
     * @return newResponse.body if a second search is needed and null if the search fails
     * @param response gives the function the unparsed json file data from the ISBN quarry
     * 
     * @throws IOException if the systems fails to query
     * @throws InterruptedException if the user interrupts the query
     */
    private String tulli(String response) {
        if (response == null || response.contains("\"numFound\":0") ) {
            System.out.println("Failed to find book using ISBN");
            System.out.println("Insert book name");
            String kirja = this.idScanner.nextLine();
            this.searchedTitle = kirja.trim();
            System.out.println("Insert author name");
            String author = this.idScanner.nextLine();
            //HTTP Starts again
            String encodedKirjaQuery = URLEncoder.encode(kirja, StandardCharsets.UTF_8);
            String encodedAuthorQuery = URLEncoder.encode(author, StandardCharsets.UTF_8);
            String fields = "key,title,author_name,first_publish_year,number_of_pages_median,cover_i,isbn";
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
     * lastChanceSingle is the final check function for when only 1 book is found usually by ISBN
     * it either prints a fail message in the case of an error during the search
     * or if no book was found with the ISBN or the name and author of the book
     * After this if there is a json file with data to parse, it parses it and prints the data
     * in a easy to view format using imported google.gson classes
     * 
     * 
     * @param checkedResponse the received data that was checked in the tulli function
     */
    private void lastChanceSingle(String checkedResponse) {
        if (checkedResponse == null) {
            System.out.println("An error occured during search");
        }
        else if (checkedResponse.contains("\"numFound\":0")) {
            System.out.println("No books found");
        }
        else {
            //Turns the raw .JSON string into a GSON object so we can pull fields out of it
            JsonObject root = JsonParser.parseString(checkedResponse).getAsJsonObject();
            //Creates an array with a single result at index 0 the book we want
            JsonArray docs = root.getAsJsonArray("docs");
            JsonObject book = docs.get(0).getAsJsonObject();
            // the following code grabs the wanted data from the created book variable
            //title
            String title = book.has("title")
            ? book.get("title").getAsString() : "Unknown";
            //Page count
            int pages = book.has("number_of_pages_median")
            ? book.get("number_of_pages_median").getAsInt() : 0;
            //Original publishing year
            int year = book.has("first_publish_year")
            ? book.get("first_publish_year").getAsInt() : 0;
            //The cover id number for future use
            Integer coverId = book.has("cover_i")
            ? book.get("cover_i").getAsInt() : null;
            //Author name
                String author = "Unknown";
            if (book.has("author_name") && book.getAsJsonArray("author_name").size() > 0) {
                author = book.getAsJsonArray("author_name").get(0).getAsString();
            }
            // ISBN: prefer the first 13-digit one, fall back to whatever comes first
            String isbn = "Unknown";
            if (book.has("isbn")) {
                JsonArray isbns = book.getAsJsonArray("isbn");
                for (JsonElement el : isbns) {
                    String candidate = el.getAsString();
                        if (candidate.length() == 13) {
                        isbn = candidate;
                        break;
                        }
                }
                if (isbn.equals("Unknown") && isbns.size() > 0) {
                    isbn = isbns.get(0).getAsString();
                }
            }
            System.out.println("\n==================================");
            System.out.println("BOOK INFORMATION");
            System.out.println("==================================");
            System.out.println("Title: " + title);
            System.out.println("Author: " + author);
            System.out.println("Page count: " + (pages == 0 ? "Unknown" : pages));
            System.out.println("First published: " + year);
            System.out.println("Image ID: " + coverId);
            System.out.println("ISBN: " + isbn);
            System.out.println("==================================");
        }
    }
    //TODO: work on the filter methods and codes
    private void lastChanceMulti(String checkedResponse){
        if (checkedResponse == null) {
            System.out.println("An error occured during search");
        }
        else if (checkedResponse.contains("\"numFound\":0")) {
            System.out.println("No books found");
        }
        else {
            JsonObject root = JsonParser.parseString(checkedResponse).getAsJsonObject();
            JsonArray docs = root.getAsJsonArray("docs");
            int number = 1;
            for (JsonElement element : docs) {
                JsonObject book = element.getAsJsonObject();
                // Skip books that don't have every field we want
                //By calling the hasValue and hasNonEmptyArray funtions
                if (!hasValue(book, "title")
                    || !hasNonEmptyArray(book, "author_name")
                    || !hasValue(book, "number_of_pages_median")
                    || !hasValue(book, "first_publish_year")
                    || !hasValue(book, "cover_i")
                    || !hasNonEmptyArray(book, "isbn")) {                    
                    continue;
                }
            String title = book.get("title").getAsString();
            // If the user searched by name, skip titles that aren't an exact match
            if (searchedTitle != null && !title.equalsIgnoreCase(searchedTitle)) {
                continue;
            }
            String author = book.getAsJsonArray("author_name").get(0).getAsString();
            String pages = String.valueOf(book.get("number_of_pages_median").getAsInt());
            String year = String.valueOf(book.get("first_publish_year").getAsInt());
            String coverId = String.valueOf(book.get("cover_i").getAsInt());
            // ISBN: prefer the first 13-digit one, fall back to whatever comes first
            String isbn = "Unknown";
            if (book.has("isbn")) {
                JsonArray isbns = book.getAsJsonArray("isbn");
                for (JsonElement el : isbns) {
                    String candidate = el.getAsString();
                        if (candidate.length() == 13) {
                        isbn = candidate;
                        break;
                        }
                }
                if (isbn.equals("Unknown") && isbns.size() > 0) {
                    isbn = isbns.get(0).getAsString();
                }
            }
            System.out.println("\n==================================");
            System.out.println("\n--- Result " + number + " ---");
            System.out.println("==================================");
            System.out.println("Title: " + title);
            System.out.println("Author: " + author);
            System.out.println("Page count: " + pages);
            System.out.println("First published: " + year);
            System.out.println("Image ID: " + coverId);
            System.out.println("ISBN: " + isbn);
            System.out.println("==================================");

            number++;
            }

        }
        }
        private boolean hasValue(JsonObject obj, String key) {
        return obj.has(key) && !obj.get(key).isJsonNull();
        }
         private boolean hasNonEmptyArray(JsonObject obj, String key) {
        return hasValue(obj, key)
                && obj.get(key).isJsonArray()
                && obj.getAsJsonArray(key).size() > 0;
        }
}

