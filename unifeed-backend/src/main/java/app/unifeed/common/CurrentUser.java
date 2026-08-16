package app.unifeed.common;

import java.util.UUID;
import org.springframework.security.core.Authentication;

/** Centralizes the application's UUID-based authenticated principal convention. */
public final class CurrentUser {
    private CurrentUser() { }
    public static UUID id(Authentication authentication) { return UUID.fromString(authentication.getName()); }
}
