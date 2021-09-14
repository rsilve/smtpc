# smtpc

Very simple smtp client base on netty.

The construction of the message in MIME format is not covered by smtpc. 
It only covers the management of the SMTP protocol.

## Usage

Very basic example :

```
byte[] contentBytes = /* get mime message as bytes */

SmtpClient client = new SmtpClient();
SmtpSession session = new SmtpSession.Builder()
        .setHost(HOST)
        .setPort(PORT)
        .setSender(SENDER)
        .setReceiver(RECIPIENT)
        .setContent(contentBytes)
        .buildOne();

client.run(session).addListener(future -> client.shutdownGracefully());

```


See examples in the sources.


## START SSL

no support for START SSL at this time


