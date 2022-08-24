package com.youming.youche.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import org.apache.commons.lang.StringEscapeUtils;

public class HtmlEncoder {
    private static final String[] htmlCode = new String[256];

    public HtmlEncoder() {
    }

    public static String encode(String string, boolean isView) {
        if (string == null) {
            return null;
        } else {
            int n = string.length();
            StringBuffer buffer = new StringBuffer();

            for(int i = 0; i < n; ++i) {
                char character = string.charAt(i);

                try {
                    if (isView) {
                        buffer.append(htmlCode[character]);
                    } else if (character == '\n') {
                        buffer.append("\n");
                    } else {
                        buffer.append(htmlCode[character]);
                    }
                } catch (ArrayIndexOutOfBoundsException var7) {
                    buffer.append(character);
                }
            }

            return buffer.toString();
        }
    }

    public static String encode(String input) {
        if (input != null && !"".equals(input)) {
            StringBuilder sb = new StringBuilder(input.length());
            int i = 0;

            for(int c = input.length(); i < c; ++i) {
                char ch = input.charAt(i);
                switch(ch) {
                    case '"':
                        sb.append("&quot;");
                        break;
                    case '&':
                        sb.append("&amp;");
                        break;
                    case '\'':
                        sb.append("&apos;");
                        break;
                    case '<':
                        sb.append("&lt;");
                        break;
                    case '>':
                        sb.append("&gt;");
                        break;
                    default:
                        sb.append(ch);
                }
            }

            return sb.toString();
        } else {
            return input;
        }
    }

    public static String[] encode(String[] params) {
        String[] arry = null;
        if (params != null) {
            arry = new String[params.length];

            for(int i = 0; i < params.length; ++i) {
                arry[i] = encode(params[i]);
            }
        }

        return arry;
    }

    public static String decode(String input) {
        return input == null ? input : StringEscapeUtils.unescapeHtml(input.replaceAll("&apos;", "'"));
    }

    static {
        int i;
        for(i = 0; i < 10; ++i) {
            htmlCode[i] = "&#00" + i + ";";
        }

        for(i = 10; i < 32; ++i) {
            htmlCode[i] = "&#0" + i + ";";
        }

        for(i = 32; i < 128; ++i) {
            htmlCode[i] = String.valueOf((char)i);
        }

        htmlCode[10] = "<br>\n";
        htmlCode[34] = "&quot;";
        htmlCode[38] = "&amp;";
        htmlCode[60] = "&lt;";
        htmlCode[62] = "&gt;";

        for(i = 128; i < 256; ++i) {
            htmlCode[i] = "&#" + i + ";";
        }

    }
}
