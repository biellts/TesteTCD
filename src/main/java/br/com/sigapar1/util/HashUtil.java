package br.com.sigapar1.util;

import org.mindrot.jbcrypt.BCrypt;

public class HashUtil {

    // Método principal para gerar hash
    public static String hash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    // Alias usado no seu service: gerarHash()
    public static String gerarHash(String senha) {
        return hash(senha);
    }

    // Verifica se a senha digitada bate com o hash
    public static boolean verificar(String senhaDigitada, String senhaHash) {
        return BCrypt.checkpw(senhaDigitada, senhaHash);
    }
}
