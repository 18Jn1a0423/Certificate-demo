package com.mss.demo.keycloak;

//import java.util.Collection;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.security.authentication.AbstractAuthenticationToken;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtClaimNames;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
//import org.springframework.stereotype.Component;
//
//@Component
//public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtAuthConverter.class);
//
//    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//
//    private final JwtAuthConverterProperties properties;
//
//    public JwtAuthConverter(JwtAuthConverterProperties properties) {
//        this.properties = properties;
//    }
//
//    @Override
//    public AbstractAuthenticationToken convert(Jwt jwt) {
//        // Log the JWT claims for debugging
//        logger.debug("JWT Claims: {}", jwt.getClaims());
//
//        // Extract and log authorities
//        Collection<GrantedAuthority> authorities = Stream.concat(
//                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
//                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());
//        logger.debug("Extracted Authorities: {}", authorities);
//
//        // Create and return the JwtAuthenticationToken
//        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
//    }
//
//    private String getPrincipalClaimName(Jwt jwt) {
//        String claimName = JwtClaimNames.SUB;
//        if (properties.getPrincipalAttribute() != null) {
//            claimName = properties.getPrincipalAttribute();
//        }
//        String principalClaim = jwt.getClaim(claimName);
//
//        // Log the extracted principal claim
//        logger.debug("Principal Claim Name: {}, Value: {}", claimName, principalClaim);
//
//        return principalClaim;
//    }
//
//    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
//        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
//        Map<String, Object> resource;
//        Collection<String> resourceRoles;
//        if (resourceAccess == null
//                || (resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId())) == null
//                || (resourceRoles = (Collection<String>) resource.get("roles")) == null) {
//            // Log if resource roles are not found
//            logger.debug("Resource roles not found in JWT");
//            return Set.of();
//        }
//
//        // Convert and log the resource roles
//        Collection<GrantedAuthority> roles = resourceRoles.stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                .collect(Collectors.toSet());
//        logger.debug("Extracted Resource Roles: {}", roles);
//
//        return roles;
//    }
//}
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public class KeycloakServerinfo {

    private static Keycloak keycloak = null;
    private static final String serverUrl = "http://localhost:8080/auth/";
    private static final String realm = "SpringBootDemo";
    private static final String clientId = "springboot-demo-client";
    private static final String clientSecret = "y4Ki2AMFpUK6owsZ6LAuPyPzJqEXuImi"; // Add your client secret if required
    private static final String userName = "app_admin";
    private static final String password = "app_admin";

    private KeycloakServerinfo() {
    }

    public static Keycloak getInstance() {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .username(userName)
                .password(password)
                .clientId(clientId)
//                .clientSecret(clientSecret) // If client secret is required
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
        }
        return keycloak;
    }

    public static List<String> getAllRoles() {
        List<String> availableRoles = keycloak.realm(realm).roles().list().stream()
            .map(role -> role.getName())
            .collect(Collectors.toList());
        return availableRoles;
    }

    public void addRealmRoleToUser(String userName, String roleName) {
        String userId = keycloak.realm(realm).users().search(userName).get(0).getId();
        UserResource user = keycloak.realm(realm).users().get(userId);
        List<RoleRepresentation> roleToAdd = new LinkedList<>();
        roleToAdd.add(keycloak.realm(realm).roles().get(roleName).toRepresentation());
        user.roles().realmLevel().add(roleToAdd);
    }

    public void addRealmRole(String role) {
        if (!getAllRoles().contains(role)) {
            RoleRepresentation roleRep = new RoleRepresentation();
            roleRep.setName(role);
            roleRep.setDescription("role_" + role);

            ClientRepresentation clientRep = keycloak.realm(realm).clients().findByClientId(clientId).get(0);
            keycloak.realm(realm).clients().get(clientRep.getId()).roles().create(roleRep);
        }
    }
}
