FROM ubuntu:latest
LABEL authors="Diego"

ENTRYPOINT ["top", "-b"]