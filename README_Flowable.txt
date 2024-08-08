//Die Flowable Engine kann als JAR Datei in jede Applikation eingebettet werden.

/*Note: Java classes present in the business archive will not be added to the classpath. 
*  All custom classes used in process definitions in the business archive (for example, Java service tasks or event listener implementations) 
*/must be made available on the Flowable engine classpath in order to run the processes.

//Starten eines Worklow "myProcess" (ID des Prozesses) über
ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess");

//Die Workflows werden in einem konsistenten Datenspeicher der Flowable Engine gespeichert.
//Die Interaktionen mit der Flowable Engine erfolgen über die Services (repositoryService)


//Wenn Flowable aufgrund von Changelock Problemen abstürzt dann die Konsole der verbundenen Datenbank öffnen und folgendes eingeben:
	docker exec -ti db1 bash

	psql -U flowable flowable

	und dann:

	UPDATE act_adm_databasechangeloglock SET LOCKED=FALSE, LOCKGRANTED=null, LOCKEDBY=null where ID=1;
	UPDATE act_app_databasechangeloglock SET LOCKED=FALSE, LOCKGRANTED=null, LOCKEDBY=null where ID=1;
	UPDATE act_cmmn_databasechangeloglock SET LOCKED=FALSE, LOCKGRANTED=null, LOCKEDBY=null where ID=1;
	UPDATE act_co_databasechangeloglock SET LOCKED=FALSE, LOCKGRANTED=null, LOCKEDBY=null where ID=1;
	UPDATE act_de_databasechangeloglock SET LOCKED=FALSE, LOCKGRANTED=null, LOCKEDBY=null where ID=1;
	UPDATE act_dmn_databasechangeloglock SET LOCKED=FALSE, LOCKGRANTED=null, LOCKEDBY=null where ID=1;
	UPDATE act_fo_databasechangeloglock SET LOCKED=FALSE, LOCKGRANTED=null, LOCKEDBY=null where ID=1;
	UPDATE flw_ev_databasechangeloglock SET LOCKED=FALSE, LOCKGRANTED=null, LOCKEDBY=null where ID=1;
