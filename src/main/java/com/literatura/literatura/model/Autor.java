
package com.literatura.literatura.model;
import com.literatura.literatura.model.DatosAutor;
import com.literatura.literatura.model.Libro;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String nombre;
    private Integer fechaNacimiento;
    private Integer fechaFallecimiento;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Libro> listaLibros;

    public Autor() {
    }

    public Autor(DatosAutor datosAutor) {
        this.nombre = datosAutor.nombre();
        this.fechaNacimiento = datosAutor.fechaNacimiento();
        this.fechaFallecimiento = datosAutor.fechaFallecimiento();

    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Integer fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getFechaFallecimiento() {
        return fechaFallecimiento;
    }

    public void setFechaFallecimiento(Integer fechaFallecimiento) {
        this.fechaFallecimiento = fechaFallecimiento;
    }

    public List<String> getListaLibros() {
        return listaLibros.stream()
                .map(Libro::getTitulo)
                .collect(Collectors.toList());
    }

    public void setListaLibros(Libro libro) {
        if (this.listaLibros == null) {
            this.listaLibros = new ArrayList<>();
        }
        this.listaLibros.add(libro);
        libro.setAutor(this);
    }

    @Override
    public String toString() {
        return "------------------Autor------------------" + "\n" +
                "   Autor: " + nombre  + "\n" +
                "   fechaNacimiento: " + fechaNacimiento + "\n" +
                "   fechaFallecimiento: " + fechaFallecimiento + "\n" +
                "   libros: " + getListaLibros() + "\n" +
                "----------------------------------------";
    }

}
