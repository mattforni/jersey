/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.client.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Request;
import javax.ws.rs.ext.FilterContext;
import javax.ws.rs.ext.RequestFilter;

/**
 * Simple client-side filter that adds X-Requested-By headers to all state-changing
 * request (i.e. request for methods other than GET, HEAD and OPTIONS).
 * This is to satisfy the requirements of the {@link org.glassfish.jersey.server.filter.CsrfProtectionFilter}
 * on the server side.
 *
 * @see org.glassfish.jersey.server.filter.CsrfProtectionFilter
 *
 * @author Martin Matula (martin.matula at oracle.com)
 */
public class CsrfProtectionFilter implements RequestFilter {

    /**
     * Name of the header this filter will attach to the request.
     */
    public static final String HEADER_NAME = "X-Requested-By";

    private static final Set<String> METHODS_TO_IGNORE;
    static {
        HashSet<String> mti = new HashSet<String>();
        mti.add("GET");
        mti.add("OPTIONS");
        mti.add("HEAD");
        METHODS_TO_IGNORE = Collections.unmodifiableSet(mti);
    }

    private final String requestedBy;

    /**
     * Creates a new instance of the filter with X-Requested-By header value set to empty string.
     */
    public CsrfProtectionFilter() {
        this("");
    }

    /**
     * Initialized the filter with a desired value of the X-Requested-By header.
     *
     * @param requestedBy Desired value of X-Requested-By header the filter
     * will be adding for all potentially state changing requests.
     */
    public CsrfProtectionFilter(final String requestedBy) {
        this.requestedBy = requestedBy;
    }

    @Override
    public final void preFilter(final FilterContext fc) throws IOException {
        final Request request = fc.getRequest();
        if (!METHODS_TO_IGNORE.contains(request.getMethod()) && (request.getHeaders().getHeader(HEADER_NAME) == null)) {
            fc.setRequest(fc.getRequestBuilder().header(HEADER_NAME, requestedBy).build());
        }
    }
}
