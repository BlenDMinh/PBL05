package modules.auth.service;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class BcryptService {
    public String getHash(String content) {
        return BCrypt.withDefaults().hashToString(12, content.toCharArray());
    }

    public boolean verify(String content, String hash) {
        return BCrypt.verifyer().verify(content.toCharArray(), hash).verified;
    }
}
