# TextAdventurer Server

[https://www.textadventurer.tk](https://www.textadventurer.tk)

Basic HTTP (/w Websocket) webserver for launching command-line
applications.


# Overview

This is the server backend for
[textadventurer](https://www.textadventurer.tk). The backend exposes games
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


# Building

This is a standard java + maven project. Run:

```
mvn package
```

Which will produce a fat jar at `target/textadventuer-server-x.x.x.jar`.


# Booting

Once a `config.yml` file is made (one line) and a games directory is
setup, the `textadventurer-server` is booted from the command line:

```
java -jar textadventurer-server-x.x.x.jar 8080 example-config/config.yml
```

This boots the webserver on port `8080`.


# Production Configuration

The `textadventuer-server` HTTP API does not handle authentication
credentials so, in principle, you *can* host it as-is without
encryption. However, because most ISPs use broken proxy
implementations, the server's websockets may not work for some
users. Also, the server will not have a frontend.

Because of that, it is recommended to deploy `textadventuer-server`
behind a reverse proxy such as `nginx` or `Apache` under `/api` with
full HTTPS/WSS encryption enabled. `textadventuer-ui` should then be
deployed behind `/`.


Here is an nginx example, directly from the production textadventurer server:

```
http {
     server {
            server_name www.textadventurer.tk;
            listen 80;

            return 301 https://$server_name$request_uri;
     }

     server {
            server_name www.textadventurer.tk;

            listen 443 ssl;
            add_header Strict-Transport-Security "max-age=31536000";


            location / {
                     root /var/www/textadventurer;
            }

            location /api {
                     proxy_pass http://localhost:8080;

                     rewrite ^/api/(.*) /$1 break;

                     # Websockets
                     proxy_http_version 1.1;
                     proxy_set_header Upgrade $http_upgrade;
                     proxy_set_header Connection "upgrade";
                     proxy_read_timeout 86400;
            }

            ssl_certificate /etc/letsencrypt/live/www.textadventurer.tk/fullchain.pem; # managed by Certbot
            ssl_certificate_key /etc/letsencrypt/live/www.textadventurer.tk/privkey.pem; # managed by Certbot
            include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
    }
}
```
