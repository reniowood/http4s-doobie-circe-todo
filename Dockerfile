FROM mozilla/sbt

COPY . /workspace
WORKDIR /workspace
RUN sbt compile

CMD ["sbt", "run"]

EXPOSE 8080
