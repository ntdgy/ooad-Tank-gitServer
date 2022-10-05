package ooad.Tank.gitServer.backend;

import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

public class AuthManager {


    public record AuthInfo(String username, String password) {
    }

    public static final String AUTH_READ = "tank.git.auth.read";
    public static final String AUTH_WRITE = "tank.git.auth.write";
//    public static final String USER_ID = "tank.git.auth.user_id";
    public static final String REPO = "tank.git.auth.repo";

    private static final Logger logger = LoggerFactory.getLogger(AuthManager.class);

    public static boolean checkReadPermission(HttpServletRequest request, Backend.Repo repo) {
        var read = request.getAttribute(AUTH_READ);
        if (read != null && (Boolean) read) return true;

        AuthInfo auth = parseAuthInfo(request);
        if (!repo.isPublic()) {
            if (auth == null || auth.username() == null || auth.password() == null)
                return false;
            int userId = Backend.authenticateUser(auth.username(), auth.password());
            if (userId == 0) return false;

            if (repo.ownerId() == userId) return true;
            return Backend.checkUserReadPermission(repo.ownerName(), repo.repoName(), userId);
        }

        request.setAttribute(AUTH_READ, true);
        return true;
    }

    public static boolean checkWritePermission(HttpServletRequest request, Backend.Repo repo) {
        var write = request.getAttribute(AUTH_WRITE);
        if (write != null && (Boolean) write) return true;

        AuthInfo auth = parseAuthInfo(request);
        if (auth == null) return false;

        int userId = Backend.authenticateUser(auth.username(), auth.password());
        if (userId == 0) return false;

        if (repo.ownerId() == userId) return true;

        return Backend.checkUserWritePermission(repo.ownerName(), repo.repoName(), userId);
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
