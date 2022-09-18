package ooad.Tank.gitServer;


import org.eclipse.jgit.http.server.ReceivePackErrorHandler;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.UploadPack;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.eclipse.jgit.transport.resolver.UploadPackFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        setReceivePackFactory(new GitReceivePackFactory());
        setAsIsFileService(null);
        super.init(config);
    }

}