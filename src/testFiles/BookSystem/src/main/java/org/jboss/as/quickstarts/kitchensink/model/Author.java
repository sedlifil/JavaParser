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
@Table(name = "author")
public class Author implements Serializable {
    public Author() {
    }
    @Id
    @Column(name = "author_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z ]*", message = "musí obsahovat pouze písmena a mezery")
    private String name;
    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z ]*", message = "musí obsahovat pouze písmena a mezery")
    private String surname;
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "musí obsahovat validní email")
    private String email;
    @NotNull
    @Size(min = 8, max = 10)
    @Pattern(regexp = "[0-9.]*", message = "musí obsahovat pouze číslice a tečky")
    private String dateOfBirth;



    @OneToMany(fetch = FetchType.EAGER,  mappedBy = "author")
    private Set<Book> booksList;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "author_publisher",
            joinColumns = @JoinColumn(name = "author_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "publisher_id", nullable = false, updatable = false))
    private List<Publisher> publishers;

    private JsonArray publishersToJson(){
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Publisher y : publishers) {
            jsonArrayBuilder.add(y.toJsonOnlyPublisher());
        }
        return jsonArrayBuilder.build();
    }

    private JsonArray booksToJson(){
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Book y : booksList) {
            jsonArrayBuilder.add(y.toJsonOnlyBook());
        }
        return jsonArrayBuilder.build();
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("name", name)
                .add("surname", surname)
                .add("email", email)
                .add("dateOfBirth", dateOfBirth)
                .add("publishers", publishersToJson())
                .add("books", booksToJson())
                .build();
    }

    public JsonObject toJsonOnlyAuthor() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("name", name)
                .add("surname", surname)
                .add("email", email)
                .add("dateOfBirth", dateOfBirth)
                .build();
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                '}';
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Book> getBooks() {
        return booksList;
    }

    public void setBooks(Set<Book> books) {
        booksList = books;
    }

    public List<Publisher> getPublishers() {
        return publishers;
    }

    public Set<Book> getBooksList() {
        return booksList;
    }

    public void setBooksList(Set<Book> booksList) {
        this.booksList = booksList;
    }

    public void setPublishers(List<Publisher> publishers) {
        this.publishers = publishers;
    }
}
