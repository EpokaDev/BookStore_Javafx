package application.bookstore.models;

import java.util.ArrayList;

public class Author {

    int authorId;
    private String fullName;
    private final ArrayList<Book> authorBooks;


    public Author(String fullName) {
        authorBooks=new ArrayList<>();
        this.authorId++;
        this.fullName = fullName;
    }

    public void addBook(Book book)
    {
        authorBooks.add(book);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getNumberOfBooks() {
        return authorBooks.size();
    }

    public ArrayList<Book> getAuthorBooks()
    {
        return this.authorBooks;
    }
}
