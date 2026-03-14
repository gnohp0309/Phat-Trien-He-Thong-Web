package vn.edu.demo_spmvc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.demo_spmvc.DTO.AuthResponse;
import vn.edu.demo_spmvc.DTO.LoginRequest;
import vn.edu.demo_spmvc.DTO.RegisterRequest;
import vn.edu.demo_spmvc.entity.AppUser;
import vn.edu.demo_spmvc.entity.Role;
import vn.edu.demo_spmvc.exception.AppException;
import vn.edu.demo_spmvc.repository.UserRepository;
import vn.edu.demo_spmvc.security.JwtService;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements vn.edu.demo_spmvc.service.AuthService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest req) {
        String username = req.getUsername().trim();
        if (userRepo.existsByUsername(username)) {
            throw new AppException(HttpStatus.CONFLICT, "USER_EXISTS", "Username already exists");
        }

        AppUser u = new AppUser();
        u.setUsername(username);
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setRoles(req.isAdmin() ? Set.of(Role.ADMIN, Role.USER) : Set.of(Role.USER));
        u = userRepo.save(u);

        Set<String> roles = u.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        String token = jwtService.generateToken(u.getUsername(), Map.of("roles", roles));
        return new AuthResponse(token, u.getUsername(), roles);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        AppUser u = userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Invalid credentials"));
        Set<String> roles = u.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        String token = jwtService.generateToken(u.getUsername(), Map.of("roles", roles));
        return new AuthResponse(token, u.getUsername(), roles);
    }
}

