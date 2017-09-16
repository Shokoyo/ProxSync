package de.dieser1memesprech.proxsync._9animescraper;

public class _9AnimeUrlExtender {
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
        return Integer.toString(o - 33);
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
}
