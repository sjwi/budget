# Monthly Cash Budgeting Application
A simple budgeting application to help me calculate withdrawals for my cash envelopes.  
[Spring Boot Web Application]

See demo at https://stephenky.com/budget-demo

# Configuration
To quickly standup application on a tomcat server, simply edit the application.properties with the following values or set environment variables with these values.  
NOTE: If you want to use a database besides the H2 in-memory database update your configuration accordingly 

spring.datasource.driver-class-name=org.h2.Driver  
spring.datasource.driverClassName=com.mysql.jdbc.Driver  
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect  
spring.datasource.url=jdbc:h2:mem:Budget;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE  
spring.datasource.username=sa  
spring.datasource.password=sa  
spring.datasource.initialization-mode=always  

com.sjwi.settings.smtp={{SMTP SERVER}}  
com.sjwi.settings.smtpPort={{SMTP PORT}}  
com.sjwi.settings.mail_pw={{MAIL PASSWORD}}  
com.sjwi.settings.mail_un={{MAIL USERNAME}}  
com.sjwi.settings.adminDistributionList={{DEFAULT MAILING LIST}}  
com.sjwi.settings.mailLevel={{LIVE or DISABLED depending on if you want to actually send emails}}  

# Deployment
git clone ...  
cd budget  
mvn clean install package  
Deploy .war in package directory on tomcat server  

# Login/Security
This application uses Spring Security 
To login using the H2 in-memory database, a user with credentials admin/admin is automatically populated
