agent-smith
===========
agent-smith tries to enhance your website's rank on google's search results by generating fake requests to pages you provide

Mechanism
---------

The basic idea is to mimic unique users. each request will be sent using a unique user-agent(HTTP Header Parameter).
Requests will be sent via http proxies to be identified as different users.
list of HTTP proxies will be fetched from different proxy-list websites.

Prerequisites
-------------
1. Oracle JDK 1.8
1. MySQL 5.7
1. Apache Maven 3.3

Usage
-----
1. `cd ./bin`
1. edit file: `launcher.sh` and change the properties as needed.(list of the preperties is described below)
1. provide the list of URLs that need to be called in a file. Each line should contain one URL.
1. run it: `./launcher.sh`

Provided Properties
-------------------
1. `TOTAL_CLIENT_COUNT` : total number of unique users to be built
1. `CONCURRENT_CLIENT_COUNT` : number of concurrent users to be working. each user will `GET` the whole list of provided URLs.
1. `SLEEP_BETWEEN_REQUESTS` : each user will wait `SLEEP_BETWEEN_REQUESTS` miliseconds between two `GET` requests.
1. `AGENTS_FILE` : address to the file containing the list of user-agents. there's one provided at `./bin/agents`.
1. `TARGETS_FILE` : address to the file containg the URLs to be fetched by users.
1. `USE_MASTER_PROXY` : set to `"true"` if you want the whole java process to use a socks proxy.
1. `SOCKS_PROXY_HOST` : You need to provide this if `USE_MASTER_PROXY` is set.
1. `SOCKS_PROXY_PORT` : You need to provide this if `USE_MASTER_PROXY` is set.
1. `MYSQL_ROOT_PASSWORD` : provide the root password of MySQL server to create the needed database on startup.
1. `RELOAD_PROXIES_ON_BOOT` : forces the project to fetch the list of HTTP-Proxies from the web. must be set to `true` on first run. 
1. `BUILD_BEFOREHAND` : runs `mvn clean install` before launching the project. must be set to `"true"` on first run.


