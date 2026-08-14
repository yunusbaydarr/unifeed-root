package app.unifeed.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class TestDomainAuthStrategy {
    public boolean accepts(String email, boolean allowTestDomains) {
        return allowTestDomains && email != null && email.toLowerCase().endsWith("@gmail.com");
    }
}
