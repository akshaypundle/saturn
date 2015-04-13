gradle distZip &&
scp -i ~/.ssh/covered-call-app.pem build/distributions/saturn.zip ec2-user@ec2-54-186-84-174.us-west-2.compute.amazonaws.com: &&
ssh -i ~/.ssh/covered-call-app.pem ec2-user@ec2-54-186-84-174.us-west-2.compute.amazonaws.com unzip -ou saturn.zip && 
echo "superuser crontab: */6 * * * * /home/ec2-user/saturn/scripts/monitoring.sh /home/ec2-user/saturn/src/web" &&
echo "user crontab: 1-5 22 * * 1-5 cd /home/ec2-user/saturn && /home/ec2-user/saturn/bin/saturn"
