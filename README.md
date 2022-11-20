# smtpc

Ssmtp client base on netty.

The construction of the message in MIME format is not covered by smtpc. 
It only covers the management of the SMTP protocol.

## Usage

A very basic example :

```
byte[] contentBytes = /* get mime message as bytes */

SmtpClient client = new SmtpClient();
SmtpSession session = SmtpSession.newInstance(HOST, PORT);
session.setGreeting("greeting.tld")
        .setMessageFactory(
                new Message().setSender(SENDER)
                        .setRecipient(RECIPIENT)
                        .setChunks(Builder.chunks(contentBytes).iterator())
                        .factory()
        );

client.runAndClose(session);
```


See examples in the sources.


## START SSL

STARTTLS is supported by default

For now it is not possible to use a specific certificate


