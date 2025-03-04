package co.analisys.biblioteca.controller;

import co.analisys.biblioteca.model.Libro;
import co.analisys.biblioteca.model.LibroId;
import co.analisys.biblioteca.service.CatalogoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libros")
@Tag(name = "Catálogo de Libros", description = "API para la gestión del catálogo de libros en la biblioteca.")
public class CatalogoController {
    private final CatalogoService catalogoService;

    @Autowired
    public CatalogoController(CatalogoService catalogoService) {
        this.catalogoService = catalogoService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Obtener un libro por ID",
            description = "Devuelve la información de un libro específico basado en su ID.")
    public Libro obtenerLibro(
            @Parameter(description = "ID del libro a consultar", required = true)
            @PathVariable String id) {
        return catalogoService.obtenerLibro(new LibroId(id));
    }

    @GetMapping("/{id}/disponible")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Verificar disponibilidad de un libro",
            description = "Indica si un libro está disponible o no en la biblioteca.")
    public boolean isLibroDisponible(
            @Parameter(description = "ID del libro a consultar", required = true)
            @PathVariable String id) {
        Libro libro = catalogoService.obtenerLibro(new LibroId(id));
        return libro != null && libro.isDisponible();
    }

    @PutMapping("/{id}/disponibilidad")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    @Operation(summary = "Actualizar la disponibilidad de un libro",
            description = "Permite cambiar el estado de disponibilidad de un libro en la biblioteca.")
    public void actualizarDisponibilidad(
            @Parameter(description = "ID del libro a actualizar", required = true)
            @PathVariable String id,
            @RequestBody boolean disponible) {
        catalogoService.actualizarDisponibilidad(new LibroId(id), disponible);
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN') or hasRole('ROLE_ADMIN')")
    @Operation(summary = "Buscar libros en el catálogo",
            description = "Busca libros en el catálogo de la biblioteca según un criterio proporcionado.")
    public List<Libro> buscarLibros(
            @Parameter(description = "Criterio de búsqueda (por título, autor, etc.)", required = true)
            @RequestParam String criterio) {
        return catalogoService.buscarLibros(criterio);
    }
}