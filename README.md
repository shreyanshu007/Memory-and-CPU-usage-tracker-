# Memory-and-CPU-usage-tracker-


To compile

$ javac -cp mail.jar:activation.jar:. CSL202_2016csb1060.java

To run 

java -cp mail.jar:activation.jar:. CSL202_2016csb1060 

To do things

You can change the senders email and password int the java file.

line 355 for email
line 356 for password

You can change the recivers mail id in the setting.properties files

You can also change the value of 
window, 
cpu.usage.duration.limit, 
cpu.usage.limit, 
memory.usage.duration.limit, 
memory.usage.limit
in the setting.properties file.




------------------------------------------------------------------------

Assumptions:

I am storing the values according to pid.
You have to run every time you start your system.
It will run as long as window exist.

------------------------------------------------------------------------

For details i have commented in the code.
