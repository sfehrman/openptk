Run the following scripts with "SYS" "as SYSDBA":  sqlplus sys/Passw0rd as SYSDBA

SQL> @ create_tablespace.sql;
SQL> @ create_user.sql;
SQL> @ create_trigger.sql;

Run the following scripts with "OPENPTK": sqlplus opentpk/openptk

SQL> @ create_tables.sql;
SQL> @ insert_into.sql;

Check the "NLS" settings:
SQL> select * from nls_session_parameters;
