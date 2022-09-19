package ooad.Tank.gitServer.backend;

import java.sql.SQLException;

public class Backend {

    public record RepoStore(int userId, int repoId) {
    }

    public static RepoStore resolveRepo(String username, String reponame) {
        try (var conn = DataSource.getConnection()) {
            var pre = conn.prepareStatement("""
                    select user_id, repo_id
                    from user_repo
                             join users u on u.id = user_repo.user_id
                             join repo r on r.id = user_repo.repo_id
                    where u.name = ? and r.name = ?;
                    """);
            pre.setString(1, username);
            pre.setString(2, reponame);
            var result = pre.executeQuery();
            result.next();
            return new RepoStore(result.getInt("user_id"), result.getInt("repo_id"));
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
