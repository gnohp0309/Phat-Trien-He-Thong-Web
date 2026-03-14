package vn.edu.demo_spmvc.service;

import vn.edu.demo_spmvc.DTO.AuthResponse;
import vn.edu.demo_spmvc.DTO.LoginRequest;
import vn.edu.demo_spmvc.DTO.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest req);

    AuthResponse login(LoginRequest req);
}

