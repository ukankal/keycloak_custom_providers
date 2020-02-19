Build and Deploy the SPI
-------------------------------

Type the below command to build a jar file including all the dependencies.

   ````
  mvn clean compile assembly:single wildfly:deploy
   ````

To deploy this provider you must have <span>Keycloak</span> running in standalone or standalone-ha mode.



Enable the Provider for a Realm
-------------------------------
Login to the <span>Keycloak</span> Admin Console and got to the User Federation tab.   You should now see your deployed providers in the add-provider list box.
Click on add provider and select `IronIQ-postgres-db` from drop down. Provide the database JDBC url, database name, db username and db password and click on save.
The provider is now enabled.


