/*
 * Copyright (C) 2009-2010, Google Inc. and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0 which is available at
 * https://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.eclipse.jgit.http.server;

import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.eclipse.jgit.http.server.ServletUtils.getRepository;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jgit.http.server.resolver.AsIsFileService;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;

class AsIsFileFilter implements Filter {
	private final AsIsFileService asIs;

	AsIsFileFilter(AsIsFileService getAnyFile) {
		this.asIs = getAnyFile;
	}

	/** {@inheritDoc} */
	@Override
	public void init(FilterConfig config) throws ServletException {
		// Do nothing.
	}

	/** {@inheritDoc} */
	@Override
	public void destroy() {
		// Do nothing.
	}

	/** {@inheritDoc} */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		try {
			final Repository db = getRepository(request);
			asIs.access(req, db);
			chain.doFilter(request, response);
		} catch (ServiceNotAuthorizedException e) {
			res.setHeader("WWW-Authenticate", "Basic realm=\"Tank HTTP Git Authentication required.\"");
			res.sendError(SC_UNAUTHORIZED, e.getMessage());
		} catch (ServiceNotEnabledException e) {
			res.sendError(SC_FORBIDDEN, e.getMessage());
		}
	}
}
