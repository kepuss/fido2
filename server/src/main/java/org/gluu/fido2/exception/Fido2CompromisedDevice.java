/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2020, Gluu
 */

package org.gluu.fido2.exception;

public class Fido2CompromisedDevice extends RuntimeException {

    private static final long serialVersionUID = -318563205092295773L;

	public Fido2CompromisedDevice(String message, Throwable cause) {
		super(message, cause);
	}

	public Fido2CompromisedDevice(String message) {
		super(message);
	}

	public Fido2CompromisedDevice(Throwable cause) {
		super(cause);
	}

}
