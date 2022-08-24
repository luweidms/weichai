package com.youming.youche.encrypt;



public final class K {
    private static byte[] key = new byte[]{119, 115, 97, 99, 114, 101, 100, 64, 103, 109, 97, 105, 108, 46, 99, 111, 109};

    public K() {
    }

    public static String j(String plain) throws Exception {
        RC2 rc2 = new RC2();
        return rc2.encrypt_rc2_array_base64(plain.getBytes(), key);
    }

    public static String k(String cipher) throws Exception {
        RC2 rc2 = new RC2();
        return rc2.decrypt_rc2_array_base64(cipher.getBytes(), key);
    }

    public static String j_s(String cipher) throws Exception {
        return "{RC2}" + j(cipher);
    }

    public static String k_s(String cipher) throws Exception {
        String rtn = null;
        if (cipher != null && cipher.indexOf("{RC2}") != -1) {
            rtn = k(cipher.substring(5));
        } else {
            rtn = cipher;
        }

        return rtn;
    }

    public static void main2(String[] args) throws Exception {
        String a = k_s("{RC2}ZsvOsYXTXADHEUJ1QQ0=");
        System.out.println(a);
    }

    public static void main(String[] args) throws Exception {
        String a = "wengxk中文432￥@!";
        String b = j(a);
        System.out.println(b);
        String c = k(b);
        System.out.println(c);
        main2(args);
    }
}
