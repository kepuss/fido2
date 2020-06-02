/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.fido2.service.exception;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.gluu.fido2.exception.Fido2RuntimeException;
import org.slf4j.Logger;

/**
 * Fido2 RP resteasy exception handler
 *
 * @author Yuriy Movchan Date: 01/03/2019
 */
@ApplicationScoped
@Provider
public class Fido2ExceptionHandler implements ExceptionMapper<Fido2RuntimeException> {

    @Inject
    private Logger log;

    @Override
    public Response toResponse(Fido2RuntimeException ex) {
        log.error("Handled Fido2 RP exception", ex);

        return Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).build();
    }

}
