package de.dieser1memesprech.proxsync._9animescraper;

import de.dieser1memesprech.proxsync._9animescraper.config.Configuration;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class _9AnimeUrlExtender {
    private static ArrayList<Character> tsTable;
    private static ArrayList<Character> cusbMapTable;
    // TODO
    public static String getExtraUrlParameter(String id, String ts, String update, String server) {
        String DD = "gIXCaNh";
        String[] params = new String[]{
                id, update, ts, server
        };
        String[] paramNames = new String[]{
                "id", "update", "ts", "server"
        };
        int o = _s(DD);
        for (int i = 0; i < params.length; i++) {
            o += _s(_a(DD + paramNames[i], params[i]));
        }

        System.out.println(Integer.toString(o - 33));
        return Integer.toString(o - 33);
    }

    public static String decodeTs(String ts) {
        System.out.println(ts);
        if(_9AnimeUrlExtender.tsTable == null) {
            initArrays();
        }
        String decoded = "";
        for(char c: ts.toCharArray()) {
            if(!tsTable.contains(c)) {
                decoded += c;
            } else {
                decoded += (char) (65 + tsTable.indexOf(c));
            }
        }
        int missingPadding = decoded.length() % 4;
        if(missingPadding != 0) {
            for(int i = 0; i < 4 - missingPadding; i++) {
                decoded += '=';
            }
        }
        System.out.println(decoded);
        byte[] byteArray = Base64.getDecoder().decode(decoded);
        String res = "";
        try {
            res = new String(byteArray, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static void initArrays() {
        ArrayList<Character> l = new ArrayList<>();
        for(int i = 65; i <= 90; i++) {
            if(i%2 != 0) {
                l.add((char) i);
            }
        }
        for(int i = 65; i <= 90; i++) {
            if(i%2 == 0) {
                l.add((char) i);
            }
        }
        String str = l.toString().replaceAll(",","");
        _9AnimeUrlExtender.tsTable = l;
        l = new ArrayList<>();
        for(int i = 97; i <= 122; i++) {
            if(i%2 != 0) {
                l.add((char) i);
            }
        }
        for(int i = 97; i <= 122; i++) {
            if(i%2 == 0) {
                l.add((char) i);
            }
        }
        _9AnimeUrlExtender.cusbMapTable = l;
    }

    private static int _s(String t) {
        int i = 0;
        for (int j = 0; j < t.length(); j++) {
            i+= (int) t.charAt(j) *j + j;
        }
        return i;
    }

    private static String _a(String t, String e) {
        int n = 0;
        for (int i = 0; i < Math.max(t.length(), e.length()); i++) {
            n += i < e.length() ? (int) e.charAt(i) : 0;
            n += i < t.length() ? (int) t.charAt(i) : 0;
        }
        return Integer.toHexString(n);
    }

    public static String decodeExtraParameter(String param) {
        if(param.charAt(0) == '-') {
            return decodeCusb(param.substring(1));
        } else {
            return rotString(param.substring(1));
        }
    }

    private static String rotString(String param) {
        //int rotBy = 8;
        return new StringTranslator("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", "ijklmnopqrstuvwxyzabcdefghIJKLMNOPQRSTUVWXYZABCDEFGH").translate(param);
    }

    private static String decodeCusb(String param) {
        System.out.println(param);
        if(_9AnimeUrlExtender.cusbMapTable == null) {
            initArrays();
        }
        String decoded = "";
        for(char c: param.toCharArray()) {
            if(!cusbMapTable.contains(c)) {
                decoded += c;
            } else {
                decoded += (char) (97 + cusbMapTable.indexOf(c));
            }
        }
        int missingPadding = decoded.length() % 4;
        if(missingPadding != 0) {
            for(int i = 0; i < 4 - missingPadding; i++) {
                decoded += '=';
            }
        }
        System.out.println(decoded);
        byte[] byteArray = Base64.getDecoder().decode(decoded.trim());
        String res = "";
        try {
            res = new String(byteArray, "UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }
}
