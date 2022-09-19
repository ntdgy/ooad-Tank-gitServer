package ooad.Tank.gitServer.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

public class AuthManager {

    public record AuthInfo(String username, String password) {
    }

    public static final String AUTH_READ = "tank.git.auth.read";
    public static final String AUTH_WRITE = "tank.git.auth.write";

    private static final Logger logger = LoggerFactory.getLogger(AuthManager.class);

    public static boolean checkReadPermission(HttpServletRequest request, String name) {
        var read = request.getAttribute(AUTH_READ);
        if (read != null && (Boolean) read) return true;
        // TODO: check permission and set read and write
        // Now Just Allow read for all repo
        request.setAttribute(AUTH_READ, true);
        return true;
    }

    public static boolean checkWritePermission(HttpServletRequest request) {
        var write = request.getAttribute(AUTH_WRITE);
        if (write != null && (Boolean) write) return true;
        AuthInfo auth = parseAuthInfo(request);
        if (auth == null) return false;

        // TODO: check write permission
        // Now allow password == username ** 2, i.e. admin, adminadmin
        if (auth.password.equals(auth.username + auth.username)) {
            request.setAttribute(AUTH_WRITE, true);
            return true;
        } else {
            return false;
        }
    }

    private static AuthInfo parseAuthInfo(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null) return null;

        String[] auths = auth.split(" ");
        if (auths.length != 2) throw new IllegalArgumentException("Bad Authorization");
        if (!auths[0].equals("Basic")) return null;
        String[] authData = new String(Base64.getDecoder().decode(auths[1])).split(":");
        if (authData.length != 2) throw new IllegalArgumentException("Bad Authorization Data");
        return new AuthInfo(authData[0], authData[1]);
    }

}
