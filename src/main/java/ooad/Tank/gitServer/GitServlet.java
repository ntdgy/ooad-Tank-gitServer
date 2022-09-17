package ooad.Tank.gitServer;


import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

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
        setReceivePackFactory(new ReceivePackFactory<HttpServletRequest>() {
            @Override
            public ReceivePack create(HttpServletRequest req, Repository db) throws ServiceNotEnabledException, ServiceNotAuthorizedException {
                final ReceivePack rp = new ReceivePack(db);
                return rp;
            }
        });
        super.init(config);
    }

}