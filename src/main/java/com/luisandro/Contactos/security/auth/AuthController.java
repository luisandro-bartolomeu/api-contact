package com.luisandro.Contactos.security.auth;



import com.luisandro.Contactos.security.jwt.JwtService;
import com.luisandro.Contactos.security.user.User;
import com.luisandro.Contactos.security.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints de autenticação (Basic, JWT, OAuth2)")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login com username/senha", description = "Retorna token JWT para usar nas requisições")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. Autentica usando Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Seta autenticação no contexto
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Gera token JWT
        String jwt = jwtService.generateToken(authentication);

        // 4. Busca dados do usuário
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 5. Retorna token
        return ResponseEntity.ok(new LoginResponse(
                jwt,
                user.getUsername(),
                user.getEmail(),
                user.getFullName()
        ));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria uma conta para usar Basic Auth ou JWT")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = authService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getFullName()
        );
        return ResponseEntity.ok(user);
    }
}
