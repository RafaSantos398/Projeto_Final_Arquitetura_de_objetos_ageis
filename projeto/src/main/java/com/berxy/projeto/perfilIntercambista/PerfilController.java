package com.berxy.projeto.perfilIntercambista;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/users/{usuarioId}/perfil")
public class PerfilController {
    private final PerfilService service;
    public PerfilController(PerfilService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<PerfilResponse> cadastrar(@PathVariable Long usuarioId, @Valid @RequestBody PerfilRequest dados) {
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri())
                .body(service.cadastrar(usuarioId, dados));
    }
    @GetMapping
    public PerfilResponse consultar(@PathVariable Long usuarioId) { return service.consultar(usuarioId); }
    @PutMapping
    public PerfilResponse atualizar(@PathVariable Long usuarioId, @Valid @RequestBody PerfilRequest dados) {
        return service.atualizar(usuarioId, dados);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail dadosInvalidos(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
