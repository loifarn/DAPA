# DAPA-SERVER

## Requirements
- Java 8
- MySQL Server

### External Dependencies
- [GSON 2.8.0](https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.0)
- [MySQL Connector/J 5.1.41](https://mvnrepository.com/artifact/mysql/mysql-connector-java/5.1.41)
- [Tyrus Server 1.9](https://mvnrepository.com/artifact/org.glassfish.tyrus/tyrus-server/1.9)
- [Tyrus Grizzly Server Container 1.9](https://mvnrepository.com/artifact/org.glassfish.tyrus/tyrus-container-grizzly-server/1.9)
- [WebSocket Server API 1.1](https://mvnrepository.com/artifact/javax.websocket/javax.websocket-api/1.1)
- [Emoji Java 3.2.0](https://github.com/vdurmont/emoji-java)

### MySQL Tables
```sql
CREATE TABLE `channels` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`channel_id` VARCHAR(50) NOT NULL DEFAULT '0',
	`name` VARCHAR(255) NOT NULL DEFAULT '0',
	`display_name` VARCHAR(255) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `Index 2` (`name`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
```
```sql
CREATE TABLE `users` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`user_id` VARCHAR(50) NOT NULL DEFAULT '0',
	`display_name` VARCHAR(255) NOT NULL DEFAULT '0',
	`username` VARCHAR(50) NOT NULL DEFAULT '0',
	`password` VARCHAR(255) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `Index 2` (`user_id`)
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
```

### Ngnix
If you wish to use the server using a reversed proxy with Nginx the following configuration can be used. You can then connect
using ws://example.com/chat
```text
server {
	server_name example.com;
	location / {
		try_files $uri $uri /  = 404;
	}
	location / chat {
		proxy_pass http: //127.0.0.1:yourport/chat;
		proxy_http_version 1.1;
		proxy_set_header Upgrade $http_upgrade;
		proxy_set_header Connection 'upgrade';
		proxy_set_header Host $host;
		proxy_cache_bypass $http_upgrade;
	}
}
```



### Commands
**Private message**
- Syntax: "/t 'username' 'content'"
- Example: "/t R2D2 beepboop" would whisper *'beepboop'* to the user *'R2D2'*

### Credits
We thank the following authors for allowing us to use their art & imagery for our project.
- [vectors-market from flaticon ](www.flaticon.com/authors/vectors-market)
- [Essential Collectionfrom flaticon](http://www.flaticon.com/packs/essential-collection)



