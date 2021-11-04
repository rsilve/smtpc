/**
 * smtpc aims to provide a simple non-blocking smtp client based on Netty.<br>
 *
 * smtpc has three object types:<br>
 *  - MessageFactory which will provide the messages to transfer during the STMP session.<br>
 *  - SmtpSessionListener which will allow to connect treatments at each step of the SMTP session (log for example).<br>
 *  - SmtpSession which contains the information of the STMP session (connection information, MessageFactory, listener).<br>
 *  SmtpClient which will execute the session.
 */
package net.silve.smtpc;