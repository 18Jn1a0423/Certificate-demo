Fields Included-
CertificateName,FileType,UserEmail,CreatedBy,CreatedDate,ExpiryDate,ReportManager,ReportDirector

BASED ON REQUIREMENT-
 1. Monitor Expiring Certificates:
   The system needs to monitor certificates that are nearing their expiration date, with checks happening for certificates expiring within:
   - One week (weekly check): Certificates expiring within 7 days.
   - One month (monthly check): Certificates expiring within 30 days.

 2. Scheduled Task:
   - A scheduled job runs daily at midnight to check for certificates expiring within the next week and month.
   - If certificates are found to be expiring soon, specific actions are triggered, such as logging the number of expiring certificates and potentially sending notifications.

 3. Email Notification:
   - If certificates are found expiring within the next week or month, the system should notify relevant parties by sending an email with the details of the expiring certificates.

 4. API Endpoint:
   - The system exposes a REST API endpoint `/expiring-soon` that:
     - Returns a list of certificates that are expiring within the next 7 days (weekly) and within the next 30 days (monthly).
     - Optionally, the scheduled task for expiring certificates is triggered manually when this API is called.

 Functional Requirements Summary:
1. Certificate Expiration Check (Weekly and Monthly):
   - Retrieve certificates expiring in 7 days and in 30 days.

2. Scheduled Job:
   - Automatically runs at midnight every day to check for weekly and monthly certificate expirations.
   - Log the number of expiring certificates and trigger email notifications if certificates are about to expire.

3. REST API (`/expiring-soon`):
   - Provide the client with lists of certificates expiring within the next 7 days and 30 days.
   - Trigger the certificate expiration check when the endpoint is hit.

4. Email Notifications:
   - Notify users of certificates that are expiring soon, with details of those certificates.

API's
**POST-localhost:8081/api/certificates/add**

{

    "certificateName": "GCP",
    
    "fileType": "xls",
    
    "userEmail": "litchiroyal@example.com",
    
    "createdBy": "Admin",
    
    "reportManager": "Manager@gmail.com",
    
    "reportDirector": "Director@gmail.com"
    
   
}


**GET-localhost:8081/api/certificates/all**

**GET-localhost:8081/api/certificates/fileType?fileType=csv**

**GET-localhost:8081/api/certificates/download?fileType=pdf&Id=1**

![image](https://github.com/user-attachments/assets/327e5404-6ab1-4cff-9b96-1838a2ab8036)


**GET-localhost:8081/api/certificates/download/**

**GET-http://localhost:8081/api/certificates/check-expirations**

**PUT-http://localhost:8081/api/certificates/update/{id}**

{

    "certificateName": "Security",
   
    "userEmail": "user@example.com",
    
    "fileType": "CSV",
  
    "reportManager":"selvan@mail.com"
    
}

DELETE-http://localhost:8081/api/certificates/delete?userEmail=prakashdevagalla@gmail.com

By using these api's we send the email notification to the user
