# TomcatSSLConfigurator

## Stakeholder Request

Install and configure SSL/TLS support on Tomcat 10 and automate the configuration as much as possible using Java/Groovy.

### Guidelines and Requirements

1. The program should be written using Java 11/Groovy 4.0 or higher.
2. A more comprehensive solution is preferred.
3. Defend design decisions and discuss challenges when solution is presented.

### Notes

* It was not specified whether this is intended to update an existing Tomcat install, or establish a new one as the start of a new deployment.
* It was not specified whether a TLS/SSL certificate is available, or if a new self-signed certificate can be used.
* It is not clear what 'comprehensive solution' may include.
* Stakeholder did not request an exclusive solution. All or part of this solution may be reused elsewhere.

## Features

This installation and configuration utility is designed to install and configure basic TLS/SSL support into an existing or
new Tomcat installation. It is designed to be used with other configuration utilities and processes.

* If Tomcat is not installed at the target location, and there is a ZIP image of Tomcat configured, it will install that image.
* It is not necessary to have a TLS/SSL certificate
  * If there is already a certificate installed, and a password was provided, the existing certificate will be used.
  * If there is a staged certificate this will be used, unless there is already a certificate installed.
  * If no certificate is provided, a self-signed certificate will be generated and installed.
  * If no certificate password is provided, a self-signed certificate with a random password will be generated and installed.
* If the server.xml file does not have a TLS/SSL connection defined, one will be created.
  * If there is already a TLS/SSL connection matching the SSL port configured, it will be updated.
* The non-TLS connection specified in the configuration, if located, will be updated so that it redirects to the TLS/SSL port.
  
## Configuration

Configuration is done in the _configuration.properties_ file.

### configurator.certificate.ssl.algorithm

This specifies the encryption algorithm.  RSA is the normal value for this setting.

### configurator.certificate.ssl.keystore

This is the simple name of the key store file. This is the filename only. Directory information should not be included.
If intended, and a directory is included, it will be used, however, Tomcat will need to know where to find it.

### configurator.certificate.ssl.owner

This is the certificate owner and subject. The string needs to be in CN format, similar to: _cn=com.something_


### configurator.certificate.ssl.password

This is the key store password. While this can be in the configuration file, it should be passed into the Java application using the following command line parameter:

* -Dconfigurator.certificate.ssl.password=desired_password


### configurator.certificate.ssl.protocol

This specifies the SSL protocol. It is normally org.apache.coyote.http11.Http11NioProtocol

### configurator.certificate.ssl.source.keystore  [OPTIONAL]

This identifies the location and filename of the key store that will be installed. This can be an absolute path, or relative to where the Java application is run.  The path and filename are expected.

If this configuration option is not present, or the file cannot be found, it will be ignored.

### configurator.target.directory

This is the absolute or relative directory where Tomcat is installed.  The certificate will be installed into the _conf_ subdirectory at this location.



### configurator.tomcat.connector.port.ssl

This is the TLS/SSL port that Tomcat will be using.  It is normally _8443_.

### configurator.tomcat.connector.port

This is the non-TLS/SSL port that Tomcat also listens on.  

### configurator.tomcat.file.catalinaproperties

This is the catalina properties file. It is specified relative to the _configurator.target.directory_ and will normally be _conf/catalina.properties_.

### configurator.tomcat.file.serverxml

This is the server configuratioon XML file. It is specified relative to the _configurator.target.directory_ and will normally be _conf/server.xml_.

### configurator.tomcat.image.directory

If there is an installation image of Tomcat that can be installed when none is located at _configurator.target.directory_, then this configuration setting identifies what directory it will be in.  This is used with _configurator.tomcat.image.zip.file_ to identify the exact file to unzip.


### configurator.tomcat.image.zip.file

This is the installation image file of Tomcat. It is a file name only, with no directory information.  It must be a ZIP file.

### configurator.tomcat.image.zip.stripdirectories

The Tomcat installation image comes packed with Tomcat in a subdirectory that reflects the current version of Tomcat. This is not something that needs to be used when deploying.  Setting this to _1_ will remove that directory when the image is unpacked.  If it is set to _0_ and there is a version-coded base directory in the ZIP, the installation will not work correctly


## Build Notes

This project depends on hidden security classes. It was compiled with the following Maven build compiler arguments:

    <compilerArgs>
       <arg>--add-exports</arg><arg>java.base/sun.security.x509=ALL-UNNAMED</arg>
       <arg>--add-exports</arg><arg>java.base/sun.security.provider=ALL-UNNAMED</arg>
       <arg>--add-exports</arg><arg>java.base/sun.security.pkcs=ALL-UNNAMED</arg>
       <arg>--add-exports</arg><arg>java.base/sun.security.util=ALL-UNNAMED</arg>
    </compilerArgs>
    
    