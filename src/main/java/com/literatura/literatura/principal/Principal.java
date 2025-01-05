package com.literatura.literatura.principal;

import com.literatura.literatura.model.Autor;
import com.literatura.literatura.model.Datos;
import com.literatura.literatura.model.DatosLibros;
import com.literatura.literatura.model.Libro;
import com.literatura.literatura.repository.AutorRepository;
import com.literatura.literatura.repository.LibroRepository;
import com.literatura.literatura.service.ConsumoAPI;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.literatura.literatura.service.ConvierteDatos;


public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository repositorioLibro;
    private AutorRepository repositorioAutor;

    public Principal(LibroRepository repository, AutorRepository repositorioAutor) {
        this.repositorioLibro = repository;
        this.repositorioAutor = repositorioAutor;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar libros 
                    2 - Listar libros registrados          
                    3 - Listar autores registrados         
                    4 - Listar autores vivos en un año    
                    5 - Listar libros por idioma           
                    0 - Salir de la aplicación   
                    
                    
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrado();
                    break;
                case 4:
                    autoresVivosEnUnA();
                    break;
                case 5:
                    listarLibrosIdioma();
                case 0:
                    System.out.println("cerrar aplicacion");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }


    private Datos getDatosLibros() {
        System.out.println("Escribe el nombre del libro  que deseas buscar");
        var nombreLibro = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "%20"));
        System.out.println(json);
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        return datos;
    }

    private void buscarLibro() {

//         ESTO ME DEJA GUARDAR PERO NO HACE LA VALIDACION
//        // Obtener los datos de los libros
//        Datos datos = getDatosLibros();
//
//        // Obtener solo el primer libro del resultado
//        Optional<Libro> primerLibro = datos.resultados().stream()
//                .findFirst() // Obtiene el primer libro de la lista
//                .map(this::convertirDatosLibrosALibro); // Convierte los datos al objeto Libro
//
//        // Si se encuentra un libro, se guarda en el repositorio y se imprime
//        primerLibro.ifPresent(libro -> {
//            repositorio.save(libro);
//            System.out.println(libro);
//        });

        try {
            // Obtener los datos de los libros
            Datos datos = getDatosLibros();

            // Obtener el primer libro de los resultados
            Optional<DatosLibros> newLibro = datos.resultados().stream()
                    .findFirst();

            // Si el libro existe en los resultados
            if (newLibro.isPresent()) {
                // Crear objetos Libro y Autor a partir de los datos
                Libro libro = new Libro(newLibro.get());
                Autor autor = new Autor(newLibro.get().autor().get(0));

                // Comprobar si el libro ya existe en la base de datos
                if (repositorioLibro.existsByTitulo(libro.getTitulo())) {
                    System.out.println("El libro ya está registrado en la base de datos");
                } else {
                    System.out.println("El libro no está en la base de datos. Procesando su registro...");


                    // Buscar el autor en la base de datos
                    var autorExistente = repositorioAutor.findByNombreContainsIgnoreCase(autor.getNombre());

                    // Si el autor ya existe
                    if (autorExistente.isPresent()) {
                        var autorDelLibro = autorExistente.get();
                        libro.setAutor(autorDelLibro);
                        repositorioLibro.save(libro);
                        System.out.println("""
                                El autor ya existía en la base de datos
                                Se ha vinculado el libro con este autor
                                """);

                    } else {
                        // Si el autor no existe, se crea y se vincula el libro
                        autor.setListaLibros(libro);
                        repositorioAutor.save(autor);
                        repositorioLibro.save(libro);
                        System.out.println("El autor y el libro han sido registrados en la base de datos.");


                    }
                    System.out.println("El libro ha sido registrado exitosamente.");

                }
            } else {
                System.out.println("No se encontró ningún libro que coincida con la búsqueda.");


            }
        } catch (Exception e) {
            System.out.println("Se produjo un error al insertar el libro: " + e.getMessage());
        }


    }

    private void listarLibrosRegistrados() {

        System.out.println("""
                
                         Libros registrados en la Base de datos
                """);
        var libros = repositorioLibro.findAll();
        libros.forEach(System.out::println);
    }

    private void listarAutoresRegistrado() {
        System.out.println("Autores registrados en la Base de datos");
        var autores = repositorioAutor.findAll();
        autores.forEach(System.out::println);
    }


    private void autoresVivosEnUnA() {
        System.out.println("Ingrese el año a buscar autor vivo:");
        var year = teclado.nextInt();
        var autoresVivos = repositorioAutor.autoresVivoDuranteEseA(year);
        autoresVivos.forEach(System.out::println);

    }

    private void listarLibrosIdioma() {

        System.out.println("""
                
                Ingrese el idioma a buscar:
                en - Ingles
                es - Español
                
                """);
        var idioma = teclado.nextLine();
        var listaLibros = repositorioLibro.librosPorIdioma(idioma);
        if (listaLibros.isEmpty()) {
            System.out.println("No se encontro ningun libro en ese idioma");


        } else {
            listaLibros.forEach(System.out::println);
        }

    }


    // ESTE METODO FUNCIONA CON LA PARTE COMENTADA DE BUSCAR LIBROS
    private Libro convertirDatosLibrosALibro(DatosLibros datosLibros) {
        // Crear el Autor a partir de DatosAutor
        Autor autor = new Autor(datosLibros.autor().get(0));

        // Crear el Libro y asignar el Autor
        Libro libro = new Libro();
        libro.setTitulo(datosLibros.titulo());
        libro.setIdioma(datosLibros.idioma().get(0));
        libro.setNumeroDescargas(datosLibros.numeroDescargas());
        libro.setAutor(autor);

        // Agregar el libro a la lista de libros del autor
        autor.setListaLibros(libro);

        return libro;
    }

}
