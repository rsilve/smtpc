/**
 * smtpc aims to provide a simple non-blocking smtp client based on Netty.<br>
 *
 * smtpc has three object types:
 * <ul>
 *  <li>MessageFactory which will provide the messages to transfer during the STMP session</li>
 *  <li>SmtpSessionListener which will allow to connect treatments at each step of the SMTP session (log for example).</li>
 *  <li>SmtpSession which contains the information of the STMP session (connection information, MessageFactory, listener)</li>
 *  <li>SmtpClient which will execute the session.</li>
 * </ul>
 */
package net.silve.smtpc;