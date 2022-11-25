package ooad.Tank.gitServer.backend;

import org.apache.commons.codec.digest.DigestUtils;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class Backend {

    private static final int COLLABORATOR_READ = 1;
    private static final int COLLABORATOR_WRITE = 2;

    public static final int VISIBLE_PUBLIC = 0;

    public record Repo(int repoId, String repoName, int ownerId, String ownerName, boolean isPublic) {
    }

    public static Repo resolveRepo(String username, String reponame) {
        try (var conn = DataSource.getConnection()) {
            var pre = conn.prepareStatement("""
                        select repo.id, repo.name, repo.owner_id, uo.name, repo.visible from repo
                            join users uo on uo.id = repo.owner_id where repo.name = ? and uo.name = ?;
                    """);
            pre.setString(1, reponame);
            pre.setString(2, username);
            var result = pre.executeQuery();
            if (!result.next())
                return null;
            return new Repo(result.getInt(1), result.getString(2), result.getInt(3), result.getString(4), result.getInt(5) == VISIBLE_PUBLIC);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static boolean checkUserRepoPermission(String ownerName, String repoName, int currentUserId, int permission) {
        try (var conn = DataSource.getConnection()) {
            var pre = conn.prepareStatement("""
                        select count(*) from repo join users uo on uo.id = repo.owner_id
                            join user_repo ur on repo.id = ur.repo_id
                         where uo.name = ? and repo.name =? and ur.user_id = ? and permission & ? > 0
                    """);
            pre.setString(1, ownerName);
            pre.setString(2, repoName);
            pre.setInt(3, currentUserId);
            pre.setInt(4, permission);
            var res = pre.executeQuery();
            res.next();
            return res.getInt(1) > 0;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static boolean checkUserReadPermission(String ownerName, String repoName, int userId) {
        return checkUserRepoPermission(ownerName, repoName, userId, COLLABORATOR_READ);
    }
    public static boolean checkUserWritePermission(String ownerName, String repoName, int userId) {
        return checkUserRepoPermission(ownerName, repoName, userId, COLLABORATOR_WRITE);
    }

    public static int authenticateUser(String username, String password) {
        try (var conn = DataSource.getConnection()) {
            var pre = conn.prepareStatement("select id from users where name = ? and password = ?;");
            pre.setString(1, username);
            pre.setString(2, hashPassword(password));
            var result = pre.executeQuery();
            if (!result.next()) return 0;
            else return result.getInt(1);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getSHA1(String raw) {
        return DigestUtils.sha1Hex(raw);
    }

    private static final String salt = "fRaNkss";

    public static String hashPassword(String raw) {
        return getSHA1(getSHA1(raw) + salt);
    }
}
