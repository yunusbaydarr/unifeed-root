package app.unifeed.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service; public AuthController(AuthService service){this.service=service;}
    @PostMapping("/register") Map<String,Object> register(@Valid @RequestBody RegisterRequest r,Locale locale){return Map.of("userId",service.register(r.email,r.password,r.displayName,locale));}
    @PostMapping("/verify-otp") ResponseEntity<AuthService.AuthTokens> verify(@Valid @RequestBody OtpRequest r){return response(service.verifyOtp(r.email,r.otp));}
    @PostMapping("/refresh") ResponseEntity<AuthService.AuthTokens> refresh(@CookieValue("refreshToken") String token){return response(service.rotate(token));}
    private ResponseEntity<AuthService.AuthTokens> response(AuthService.AuthTokens t){return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,t.refreshCookie()).body(t);}
    record RegisterRequest(@Email String email,@Size(min=8,max=128) String password,@NotBlank String displayName){}
    record OtpRequest(@Email String email,@Size(min=6,max=6) String otp){}
}
