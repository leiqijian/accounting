ARG AWS_ACCOUNT_ID
ARG AWS_REGION
FROM $AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/openjdk:11-jre
ENV ACTIVE=dev
ARG JAR_FILE
ADD $JAR_FILE /app.jar
EXPOSE 9003
ENTRYPOINT java -jar -Xmx1792M -Dspring.profiles.active=$ACTIVE -Dstatement.aws.sqs.queue.enable=true /app.jar
