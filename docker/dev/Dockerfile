FROM jkuoctanner/activator-1.3.10:latest
MAINTAINER Joseph Ku <joseph.ku@octanner.com>

CMD ["activator", "test"]

ENV _JAVA_OPTIONS="-Xms128m -Xmx350m"

COPY . /application
WORKDIR /application
