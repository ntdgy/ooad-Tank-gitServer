package ooad.Tank.gitServer.backend;

import java.sql.SQLException;

public class Backend {

    public record RepoStore(int userId, int repoId) {
    }

    public static RepoStore resolveRepo(String username, String reponame) {
        try (var conn = DataSource.getConnection()) {
            var pre = conn.prepareStatement("""
                        select repo.owner_id as user_id, repo.id as repo_id from repo
                            join users uo on uo.id = repo.owner_id where repo.name = ? and uo.name = ?;
                    """);
            pre.setString(1, reponame);
            pre.setString(2, username);
            var result = pre.executeQuery();
            result.next();
            return new RepoStore(result.getInt("user_id"), result.getInt("repo_id"));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
