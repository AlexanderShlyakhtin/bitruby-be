package kg.bitruby.authserver.config.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record CustomPasswordUser(String username, Collection<GrantedAuthority> authorities) {

}
