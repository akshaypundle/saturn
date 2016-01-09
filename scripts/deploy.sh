gradle distZip &&
scp -i ~/.ssh/saturn1.pem build/distributions/saturn.zip ec2-user@ec2-52-33-109-109.us-west-2.compute.amazonaws.com: &&
ssh -i ~/.ssh/saturn1.pem ec2-user@ec2-52-33-109-109.us-west-2.compute.amazonaws.com unzip -ou saturn.zip && 
echo "
----------------------------------------------------------------------------------------
Make the following crontab entries:

Superuser crontab: 
   */6 * * * * /home/ec2-user/saturn/scripts/monitoring.sh /home/ec2-user/saturn/src/web

ec2-user crontab: 
   1-5 22 * * 1-5 cd /home/ec2-user/saturn && /home/ec2-user/saturn/bin/saturn

----------------------------------------------------------------------------------------"
