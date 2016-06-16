FROM jkuoctanner/activator-1.3.10:latest
MAINTAINER Joseph Ku <joseph.ku@octanner.com>

CMD ["bin/todobackend-play"]

ENV _JAVA_OPTIONS="-Xms128m -Xmx300m"

COPY stage /application
WORKDIR /application
