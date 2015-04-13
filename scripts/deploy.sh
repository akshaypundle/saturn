./gradlew distZip;
scp -i covered-call-app.pem build/distributions/saturn.zip ec2-user@ec2-54-186-84-174.us-west-2.compute.amazonaws.com:
