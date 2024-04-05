package modules.auth.service;

import modules.auth.AuthRepository;
import modules.auth.dto.UserPasswordDto;

public class AuthService {
    private final BcryptService bcryptService;
    private final AuthRepository authRepository;

    public AuthService() {
        bcryptService = new BcryptService();
        authRepository = new AuthRepository();
    }

    public UserPasswordDto userLogin(String email, String password) {
        UserPasswordDto user = null;
        user = authRepository.getUserByEmail(email);
        if (user == null || !bcryptService.verify(password, user.getPassword()))
            return null;
        return user;
    }
}