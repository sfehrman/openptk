$DERBY_HOME/bin/ij 
connect 'jdbc:derby:openptk/sampledb';
connect 'jdbc:derby:openptk/sampledb;create=true';
connect 'jdbc:derby:openptk/sampledb;create=true;territory=en_US;collation=TERRITORY_BASED:PRIMARY';
run 'sampledb.sql';
UPDATE employees SET password='Passw0rd' WHERE id!='';
