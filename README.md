# Badreads

## How to run the program

To be added when project reaches stage 3 - 4

## Project goal

The goal of the project is the creation of a library web application, which will be able to fetch information from an api, parse it and then display a book cover in a gallery that upon being clicked opens to a new page with the said books information.

The project is for my own personal use after it's finished and fully functional, though it might be made available as open source software depending on when it's finished.

## Project implementation

The project currently referred to as "Badreads" as a play on "Goodreads" will go through several distinct phases of development to aid me in learning to use java and other tech stack options such as databases or learning fullstack development as well as develope my understanding of object orientated programming.

### Stage 1 CL 

The first form the app will take is a Command Line program which focuses on taking in a user inputted book ISBN through the scanner object.

Upon receiving the input it cleans the ISBN from any excess characters like spaces or dashes and returns it to the main function, where it will be given to a HTTP request function by using parameters.

The HTTP request will construct a query using javas HTTP library in the form of a web URL in the form of `https://openlibrary.org/search.json?isbn=ISBN&fields=fields` where the ISBN and the search fields are added while ISBN is given by the user the search fields are static and are always composed of 8 parts:
key = the unique OLID path for the books record in Openlibrary
title = the books title
author_name = the array holding the name or names of authors
first_publish_year = takes the orignal publishing year of the edition
number_of_pages_median = the medianammount of pages of the edition
cover_i = the openlibrary cover IDfor the edition.
isbn = the ISBN of the edition this is mostly in case of ISBN search fail as the fallback method uses author and book name to search for works
