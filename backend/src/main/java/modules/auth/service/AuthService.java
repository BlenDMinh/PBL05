package modules.auth.service;

import java.util.Random;

import common.dto.UserPasswordDto;
import modules.auth.AuthRepository;
import modules.auth.dto.RegisterIdAndVerifyCodeDto;

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

    public UserPasswordDto getUser(String email) {
        return authRepository.getUserByEmail(email);
    }

    public RegisterIdAndVerifyCodeDto register(String displayName, String email, String password) {
        String passwordHash = bcryptService.getHash(password);
        int length = 6;
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomNumber = random.nextInt(10);
            sb.append(randomNumber);
        }
        String code = sb.toString();
        String id = authRepository.insertIntoPlayerRegister(displayName, email, passwordHash, code);
        return new RegisterIdAndVerifyCodeDto(id, code);
    }

    public boolean verifyAndCreateAccount(String id, String code) {
        return authRepository.deletePlayerRegisterAndCreatePlayer(id, code);
    }
}