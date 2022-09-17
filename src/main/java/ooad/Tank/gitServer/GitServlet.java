package ooad.Tank.gitServer;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "gitServer", urlPatterns = {"/*"},
        loadOnStartup = 1,
        initParams = {
                @WebInitParam(name = "base-path", value = "../repo-store"),
                @WebInitParam(name = "export-all", value = "true")
        })
public class GitServlet extends org.eclipse.jgit.http.server.GitServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        // 自定义
        setRepositoryResolver(new GitHttpResolver());
        super.init(config);
    }

}