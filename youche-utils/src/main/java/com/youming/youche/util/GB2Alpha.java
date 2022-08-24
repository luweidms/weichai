//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.util;

public class GB2Alpha {
    private char[] chartable = new char[]{'啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', '座'};
    private char[] alphatable = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static final String polyphoneTxt = "重庆|CQ,音乐|YY";
    private int[] table = new int[27];

    public GB2Alpha() {
        for(int i = 0; i < 27; ++i) {
            this.table[i] = this.gbValue(this.chartable[i]);
        }

    }

    public char Char2Alpha(char ch) {
        if (ch >= 'a' && ch <= 'z') {
            return (char)(ch - 97 + 65);
        } else if (ch >= 'A' && ch <= 'Z') {
            return ch;
        } else {
            int gb = this.gbValue(ch);
            if (gb < this.table[0]) {
                return '0';
            } else {
                int i;
                for(i = 0; i < 26 && !this.match(i, gb); ++i) {
                }

                return i >= 26 ? '0' : this.alphatable[i];
            }
        }
    }

    public String String2Alpha(String SourceStr) {
        String Result = "";
        int StrLength = SourceStr.length();

        try {
            for(int i = 0; i < StrLength; ++i) {
                Result = Result + this.Char2Alpha(SourceStr.charAt(i));
            }

            String[] polyWords = "重庆|CQ,音乐|YY".split(",");
            String[] var6 = polyWords;
            int var7 = polyWords.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                String gbAlpha = var6[var8];
                String[] gbAlphaPair = gbAlpha.split("\\|");
                int idx = SourceStr.indexOf(gbAlphaPair[0]);
                if (idx >= 0) {
                    Result = Result.substring(0, idx) + gbAlphaPair[1] + Result.substring(idx + gbAlphaPair[1].length());
                }
            }
        } catch (Exception var12) {
            Result = "";
        }

        return Result;
    }

    private boolean match(int i, int gb) {
        if (gb < this.table[i]) {
            return false;
        } else {
            int j;
            for(j = i + 1; j < 26 && this.table[j] == this.table[i]; ++j) {
            }

            if (j == 26) {
                return gb <= this.table[j];
            } else {
                return gb < this.table[j];
            }
        }
    }

    private int gbValue(char ch) {
        String str = new String();
        str = str + ch;

        try {
            byte[] bytes = str.getBytes("GB2312");
            return bytes.length < 2 ? 0 : (bytes[0] << 8 & '\uff00') + (bytes[1] & 255);
        } catch (Exception var4) {
            return 0;
        }
    }

    public static void main(String[] args) {
        GB2Alpha obj1 = new GB2Alpha();
        System.out.println(obj1.String2Alpha("重音重庆长沙音乐庆乐"));
        System.out.println(obj1.String2Alpha("翁锡逵wengxk"));
    }
}
