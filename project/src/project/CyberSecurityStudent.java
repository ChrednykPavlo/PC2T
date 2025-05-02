package project;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class CyberSecurityStudent extends Student {
    public CyberSecurityStudent(int id, String firstName, String lastName, int birthYear) {
        super(id, firstName, lastName, birthYear);
    }

    @Override
    public void performSkill() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((firstName + lastName).getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            System.out.println("SHA-256 hash: " + hexString);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Hash error");
        }
    }
}