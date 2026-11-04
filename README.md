# Badreads

## Table of contents

[Running the program](#how-to-run-the-program)  
 [Project goal](#project-goal)  
[Project Implementation](#project-implementation)  
[Stage 1 CL stage](#stage-1-cl-stage)

- [1.1 getISBN Function](#11-getibsn-funciton)
- [1.2 bookData Function](#12-bookdata-function)
- [1.3 tulli Function](#13-tulli-function)

## How to run the program

To be added when project reaches stage 3 - 4

## Project goal

The goal of the project is the creation of a library web application, which will be able to fetch information from an api, parse it and then display a book cover in a gallery that upon being clicked opens to a new page with the said books information.

The project is for my own personal use after its finished and fully functional, though it might be made available as open source software depending on when it's finished.

## Project implementation

The project currently referred to as "Badreads" as a play on "Goodreads" will go through several distinct phases of development to aid me in learning to use java and other tech stack options such as databases or learning fullstack development as well as develope my understanding of object orientated programming.

### Stage 1 CL stage

The project uses Maven to import several non native java libraries.  
In stage 1 it uses the google gson libraries to parse JSON files and during the building phase used a .env file for an API key as during development the orignal database used was Google books via the Google books api which would rate limit the program without an API key  
The switch to Openlibrary was decided on a few factors.

During development several issues and bugs with Google books api was highlighted that made testing difficult.

And the more and more i worked with Google's API the more problems became apparent, upon swapping to Openlibrary in a test branch all the previous problems stopped.

It came to my attention that Openlibary offered high resolution photos in their database for the book covers which would be useful later.  

#### 1.1 getIBSN Funciton

The first form the app will take is a Command Line program which focuses on taking in a user inputted book ISBN through the scanner object this occurs inside the **getISBN function**.

Upon receiving the input it cleans the ISBN from any excess characters like spaces or dashes and returns it to the **main function**, where it will be passed to the first HTTP request **bookData function** by using parameters.

#### 1.2 bookData Function

The HTTP request will construct a query using javas HTTP library in the form of a web URL in the form of **`https://openlibrary.org/search.json?isbn=ISBN&fields=FIELDS`** where the ISBN and the search fields are added while ISBN is given by the user the search fields are static and are always composed of 8 parts:

key = the unique OLID path for the books record in Openlibrary

title = the books title

author_name = the array holding the name or names of authors

first_publish_year = takes the orignal publishing year of the edition

number_of_pages_median = the median ammount of pages of the edition

cover_i = the openlibrary cover IDfor the edition.

isbn = the ISBN of the edition this is mostly in case of ISBN search fail as the fallback method uses author and book name to search for works.

This information is contained inside a JSON object which contains nested arrays referred as "docs".

In case of an interrupted search the function throws an exception using IOexception of Interruptedexception and returns null to the **main function**

In case of the search getting a HTTP status code other than 200 it returns null to the **main function.**

#### 1.3 tulli Function

Next the returned data from the **bookData function** will be passed to the **tulli** where it will first check if the passed data wrapped in the string variable response is either null or contains the string "numFound:0".

If it finds the condition to be true it runs a fallback search where it promps the user for the book's name and the author's name.

Afterwards it runs a similar HTTP request with the difference being it we use URL encoding with **StandardCharsets.UTF_8** to encode the search terms (The author's and book's name) this time the search URL looks like this:
**`https://openlibrary.org/search.json?title=ENCODEDBOOKTITLE&author=ENCODEDAUTHORNAME&language=eng&fields=FIELDS`**

This time the URL contains **language=en** which limits the received results to books only listed as being written in english as the **ISBN** search finds a single specific work it doesn't need a specific language search filter but as we know are looking at a broader non specific search we include it to filter some results.
