/*
 * Copyright (c) 2018 Mastercard
 * Copyright (c) 2020 Gluu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.gluu.fido2.service.processor.attestation;

import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.gluu.fido2.ctap.AttestationFormat;
import org.gluu.fido2.model.auth.AuthData;
import org.gluu.fido2.model.auth.CredAndCounterData;
import org.gluu.fido2.model.entry.Fido2RegistrationData;
import org.gluu.fido2.service.Base64Service;
import org.gluu.fido2.service.CertificateService;
import org.gluu.fido2.service.CoseService;
import org.gluu.fido2.service.mds.AttestationCertificateService;
import org.gluu.fido2.service.processors.AttestationFormatProcessor;
import org.gluu.fido2.service.verifier.AuthenticatorDataVerifier;
import org.gluu.fido2.service.verifier.CertificateVerifier;
import org.gluu.fido2.service.verifier.CommonVerifiers;
import org.gluu.fido2.service.verifier.UserVerificationVerifier;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

@ApplicationScoped
public class U2FAttestationProcessor implements AttestationFormatProcessor {

    @Inject
    private Logger log;

    @Inject
    private CommonVerifiers commonVerifiers;

    @Inject
    private AuthenticatorDataVerifier authenticatorDataVerifier;

    @Inject
    private UserVerificationVerifier userVerificationVerifier;

    @Inject
    private AttestationCertificateService attestationCertificateService;

    @Inject
    private CertificateVerifier certificateVerifier;

    @Inject
    private CoseService coseService;

    @Inject
    private Base64Service base64Service;

    @Inject
    private CertificateService certificateService;

    @Override
    public AttestationFormat getAttestationFormat() {
        return AttestationFormat.fido_u2f;
    }

    @Override
    public void process(JsonNode attStmt, AuthData authData, Fido2RegistrationData registration, byte[] clientDataHash,
            CredAndCounterData credIdAndCounters) {
        int alg = -7;

        String signature = commonVerifiers.verifyBase64String(attStmt.get("sig"));
        commonVerifiers.verifyAAGUIDZeroed(authData);

        userVerificationVerifier.verifyUserPresent(authData);
        commonVerifiers.verifyRpIdHash(authData, registration.getDomain());

        if (attStmt.hasNonNull("x5c")) {
            Iterator<JsonNode> i = attStmt.get("x5c").elements();
            ArrayList<String> certificatePath = new ArrayList<String>();
            while (i.hasNext()) {
                certificatePath.add(i.next().asText());
            }
            List<X509Certificate> certificates = certificateService.getCertificates(certificatePath);

            credIdAndCounters.setSignatureAlgorithm(alg);
            List<X509Certificate> trustAnchorCertificates = attestationCertificateService.getAttestationRootCertificates((JsonNode) null, certificates);
//            certificateValidator.saveCertificate(certificates.get(0));
            Certificate verifiedCert = certificateVerifier.verifyAttestationCertificates(certificates, trustAnchorCertificates);
            authenticatorDataVerifier.verifyU2FAttestationSignature(authData, clientDataHash, signature, verifiedCert, alg);
        } else if (attStmt.hasNonNull("ecdaaKeyId")) {
            String ecdaaKeyId = attStmt.get("ecdaaKeyId").asText();
            throw new UnsupportedOperationException("ecdaaKeyId is not supported");
        } else {
            PublicKey publicKey = coseService.getPublicKeyFromUncompressedECPoint(authData.getCosePublicKey());
            authenticatorDataVerifier.verifyPackedSurrogateAttestationSignature(authData.getAuthDataDecoded(), clientDataHash, signature, publicKey, alg);
        }

        credIdAndCounters.setAttestationType(getAttestationFormat().getFmt());
        credIdAndCounters.setCredId(base64Service.urlEncodeToString(authData.getCredId()));
        credIdAndCounters.setUncompressedEcPoint(base64Service.urlEncodeToString(authData.getCosePublicKey()));
    }

}
