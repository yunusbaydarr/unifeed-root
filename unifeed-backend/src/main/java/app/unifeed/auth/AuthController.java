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
    @PostMapping("/login") ResponseEntity<AuthService.AuthTokens> login(@Valid @RequestBody LoginRequest r){return response(service.login(r.email,r.password));}
    @PostMapping("/refresh") ResponseEntity<AuthService.AuthTokens> refresh(@CookieValue("refreshToken") String token){return response(service.rotate(token));}
    @PostMapping("/forgot-password") ResponseEntity<Void> forgot(@Valid @RequestBody ForgotPasswordRequest r,Locale locale){service.requestPasswordReset(r.email,locale);return ResponseEntity.accepted().build();}
    @PostMapping("/reset-password") ResponseEntity<Void> reset(@Valid @RequestBody ResetPasswordRequest r){service.resetPassword(r.email,r.token,r.newPassword);return ResponseEntity.noContent().build();}
    @PostMapping("/logout") ResponseEntity<Void> logout(org.springframework.security.core.Authentication authentication){service.logout(java.util.UUID.fromString(authentication.getName()));return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE,"refreshToken=; Path=/api/v1/auth; Max-Age=0; HttpOnly; SameSite=Strict").build();}
    private ResponseEntity<AuthService.AuthTokens> response(AuthService.AuthTokens t){return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,t.refreshCookie()).body(t);}
    record RegisterRequest(@Email String email,@Size(min=8,max=128) String password,@NotBlank String displayName){}
    record OtpRequest(@Email String email,@Size(min=6,max=6) String otp){}
    record LoginRequest(@Email String email,@NotBlank String password){}
    record ForgotPasswordRequest(@Email String email){}
    record ResetPasswordRequest(@Email String email,@Size(min=6,max=6) String token,@Size(min=8,max=128) String newPassword){}
}
