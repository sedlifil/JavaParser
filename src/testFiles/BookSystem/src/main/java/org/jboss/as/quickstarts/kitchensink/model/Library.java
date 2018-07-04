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
import java.util.Set;

/**
 * Created by Filip on 24.10.2016.
 */
@Entity
@XmlRootElement
@Table(name = "library")
public class Library implements Serializable {
    public Library() {}

    @Id
    @Column(name = "library_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Size(min = 1, max = 25)
    @Pattern(regexp = "[A-Za-z ]*", message = "musí obsahovat pouze písmena a mezery")
    private String name;
    @NotNull
    @Size(min = 1, max = 50)
    private String address;

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    //@JoinTable(name = "library_book",
    //        joinColumns = @JoinColumn(name = "library_id", nullable = false, updatable = false),
    //        inverseJoinColumns = @JoinColumn(name = "book_id", nullable = false, updatable = false))
    @JoinTable(name = "lib_book")
    private Set<Book> books;



    private JsonArray booksToJson(){
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Book y : books) {
            jsonArrayBuilder.add(y.toJsonOnlyBook());
        }
         return jsonArrayBuilder.build();
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("name", name)
                .add("address", address)
                .add("books", booksToJson())
                .build();
    }

    public JsonObject toJsonOnlyLibrary() {
        return Json.createObjectBuilder()
                .add("id", id)
                .add("name", name)
                .add("address", address)
                .build();
    }

    @Override
    public String toString() {
        return "Library{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
