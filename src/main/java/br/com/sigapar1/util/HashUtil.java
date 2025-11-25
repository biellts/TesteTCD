package br.com.sigapar1.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {

    public static String hash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public static boolean verificar(String senhaDigitada, String senhaHash) {
        return BCrypt.checkpw(senhaDigitada, senhaHash);
    }
}
