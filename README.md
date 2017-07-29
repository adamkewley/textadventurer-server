# TextAdventurer Server

[https://textadventurer.tk](https://textadventurer.tk)

Basic HTTP (/w Websocket) webserver for launching command-line
applications.


# Overview

This is the server backend for
[textadventurer](https://textadventurer.tk). The backend exposes games
that can be launched (via `GET /games`). A game object in the JSON
response contains a URL to the game (via `play.url`). That is then
built into a websocket URL (via `wss://server/url`). When connect to
the websocket, `textadventurer-server` will then boot the
game. Websocket messages from the client will be forwarded into the
game's `stdin` while output from the game's `stdout` will be forwarded
to the client.


# Configuring

Example configuration (used in dev) is available in
`example-config/config.yml`. It contains only one configuration
property (`games:`), which points the server to a directory containing
game subdirectories.

The game subdirectories should each contain a `details.yml` file (see
example) which explains the game and contains information about how to
boot the game. `textadventurer-server` uses this information to boot
the game.


# Booting

Once a `config.yml` file is made (one line) and a games directory is
setup, the `textadventurer-server` is booted from the command line:

```
java -jar textadventurer-server-x.x.x.jar 8080 example-config/config.yml
```

This boots the webserver on port `8080`.


# Production Config

The `textadventuer-server` HTTP API does not handle authentication
credentials so, in principle, you *can* host it as-is without
encryption. However, because most ISPs use broken proxy
implementations, the server's websockets may not work for some
users. Also, the server will not have a frontend.

Because of that, it is recommended to deploy `textadventuer-server`
behind a reverse proxy such as `nginx` or `Apache` under `/api` with
full HTTPS/WSS encryption enabled. `textadventuer-ui` should then be
deployed behind `/`.

An example nginx configuration:


```
events {
	worker_connections 768;
}

http {
	server {

	server_name www.textadventurer.tk;

	location / {
		root /var/www/textadventurer
	}

	location /api {
		proxy_pass http://localhost:8080;
	}

	listen 80;
	listen 443 ssl;
	
	ssl_certificate /etc/letsencrypt/live/www.textadventurer.tk/fullchain.pem;
	ssl_certificate_key /etc/letsencrypt/live/www.textadventurer.tk/privkey.pem;
	include /etc/letsencrypt/options-ssl-nginx.conf;

	if ($scheme != "https") {
		return 301 https://$host$request_uri;
	}
}
```
