# smtpc

Very simple smtp client base on netty.

The construction of the message in MIME format is not covered by smtpc. 
It only covers the management of the SMTP protocol.

## Usage

Very basic example :

```
byte[] contentBytes = /* get mime message as bytes */

SmtpClient client = new SmtpClient();
RecyclableSmtpSession session = new RecyclableSmtpSession(HOST, PORT);
session.setGreeting("greeting.tld")
        .setSender(SENDER)
        .setRecipient(RECIPIENT)
        .setChunks(Builder.chunks(contentBytes).iterator())
        .setListener(new LogListener());

client.runAndClose(session);
```


See examples in the sources.


## START SSL

STARTTLS is supported by default

For now it is not possible to use a specific certificate


