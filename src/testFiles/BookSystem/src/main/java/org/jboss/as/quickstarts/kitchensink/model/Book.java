package org.jboss.as.quickstarts.kitchensink.model;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Filip on 24.10.2016.
 */
@Entity
@XmlRootElement
@Table(name = "book")
public class Book implements Serializable{
    public Book() {

    }

    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z0-9-]*", message = "musí obsahovat pouze písmena, čísla a pomlčku")
    private String isbn;
    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z ]*", message = "musí obsahovat pouze písmena a mezery")
    private String name;
    @NotNull
    @Size(min = 6, max = 10)
    @Pattern(regexp = "[0-9.]*", message = "musí obsahovat pouze číslice a tečky")
    private String publishDate;
    @NotNull
    @Size(min = 1, max = 25)
    private String title;

    //@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    //@JoinTable(name = "author_book",
    //        joinColumns = @JoinColumn(name = "book_id", nullable = false, updatable = false),
    //        inverseJoinColumns = @JoinColumn(name = "author_id", nullable = false, updatable = false))
    //private List<Author> authors;

    @ManyToOne(fetch = FetchType.EAGER)
    private Author author;

    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "books")
    private Set<Library> libraries;

    @ManyToOne(fetch = FetchType.EAGER)
    private Publisher publisher;

    private JsonArray librariesToJson(){
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Library y : libraries) {
            jsonArrayBuilder.add(y.toJsonOnlyLibrary());
        }
        return jsonArrayBuilder.build();
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("isbn", isbn)
                .add("name", name)
                .add("publishDate", publishDate)
                .add("title", title)
                .add("publisher", publisher.toJsonOnlyPublisher())
                .add("author", author.toJsonOnlyAuthor())
                .add("libraries", librariesToJson())
                .build();
    }

    public JsonObject toJsonOnlyBook() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("isbn", isbn)
                .add("name", name)
                .add("publishDate", publishDate)
                .add("title", title)
                .build();
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", name='" + name + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public Set<Library> getLibraries() {
        return libraries;
    }

    public void setLibraries(Set<Library> libraries) {
        this.libraries = libraries;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthors(Author author) {
        this.author = author;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}
