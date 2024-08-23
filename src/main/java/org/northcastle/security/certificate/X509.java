/**
 * X509
 * Common self-signed certificate generation
 *
 * Version v1.0
 *
 * Copyright (c) Rob Richter
 */

package org.northcastle.security.certificate;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Calendar;

import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

/**
 * Convenience class for generating X.509 certificates
 */
public class X509 {

	private static final long MILLISECONDSPERDAY = 24L * 60L * 60L * 1000L;

	/**
	 * Generate an X.509 self signed certificate using SHA256withRSA
	 *
	 * @param subject   certificate subject and issuer information, formatted per
	 *                  X.509 requirements.
	 * @param keyPair   RSA key pair
	 * @param validDays number of days that the certificate will be valid, starting
	 *                  today
	 * @return X.509 certificate
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static X509Certificate generateSelfSignedCertificate(String subject, KeyPair keyPair, int validDays)
			throws GeneralSecurityException, IOException {

		if (keyPair == null) {
			throw new GeneralSecurityException("KeyPair cannot be null");
		}

		PrivateKey privateKey = keyPair.getPrivate();
		X509CertInfo certificateInfo = new X509CertInfo();

		// Calculate expiration date based on number of days
		Calendar today = Calendar.getInstance();
		Calendar expires = Calendar.getInstance();
		expires.setTimeInMillis(today.getTimeInMillis() + validDays * MILLISECONDSPERDAY);

		// Load up the certificate information specific to the request
		certificateInfo.setIssuer(new X500Name(subject));
		certificateInfo.setKey(new CertificateX509Key(keyPair.getPublic()));
		certificateInfo.setSubject(new X500Name(subject));
		certificateInfo.setValidity(new CertificateValidity(today.getTime(), expires.getTime()));

		// predetermined settings
		certificateInfo.setAlgorithmId(new CertificateAlgorithmId(new AlgorithmId(AlgorithmId.SHA256withRSA_oid)));
		certificateInfo.setVersion(new CertificateVersion(CertificateVersion.V3));

		// certificate serial number is random
		certificateInfo.setSerialNumber(new CertificateSerialNumber(new BigInteger(64, new SecureRandom())));

		// return the certificate, signed with the private key
		return X509CertImpl.newSigned(certificateInfo, privateKey, "SHA256withRSA");
	}

	/**
	 * This class does not need to be instantiated
	 */
	private X509() {
	}

}
