package com.literatura.literatura.model;
import com.literatura.literatura.model.Autor;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String titulo;
    private  String idioma;
    private Integer numeroDescargas;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Autor autor;

    public Libro(){

    }
    public Libro(DatosLibros datosLibros) {
        this.titulo = datosLibros.titulo();
        autor = new Autor(datosLibros.autor().get(0));
        this.idioma = datosLibros.idioma().get(0);
        this.numeroDescargas = datosLibros.numeroDescargas();


    }


    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Integer numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    @Override
    public String toString() {
        return   "---------LIBRO-----\n" +
                "   Titulo: " + titulo + "\n" +
                "   Autor: " + (autor != null ? autor.getNombre() : "N/A") + "\n" +
                "   Idiomas: " + idioma + "\n" +
                "   Numero de Descargas:" + numeroDescargas + "\n" +
                "--------------";
    }
}
