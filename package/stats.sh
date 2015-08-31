#!/bin/sh

while [ "1" = "1" ]; do
	netstat -na|grep tcp|awk '{print $NF}'|sort|uniq -c|sort -nr
done