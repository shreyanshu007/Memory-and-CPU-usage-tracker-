//spackage com.journaldev.files;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.regex.*; 
import static java.lang.Math.sqrt;
import static java.util.concurrent.TimeUnit.*;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;
import java.sql.Time;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

import java.text.DateFormat;


import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Map;

import java.util.*;  
import javax.mail.*;  
import javax.mail.internet.*;  
import javax.activation.*; 



public class CSL202_2016csb1060
{

    public static void main(String[] args) 
    {


            // --------------------------------------------------------------
            //variable storing the values coming form setting.properties file.
            int window = 0;
            int cpu_dur_lim = 0;
            int cpu_lim = 0;
            int mem_dur_lim = 0;
            int mem_lim = 0;
            String email = "";

            //reading form the file setting.properties
            String file_data = new String();
            BufferedReader br = null;
            try 
            {
                br = new BufferedReader(new FileReader("setting.properties"));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) 
                {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }

                //storing whole data into a string file_data
                file_data = sb.toString();
                br.close();
            } 
            catch(Exception E)
            { 
                System.out.print(E);
            }
            
            //spliting the file data into multiple lines
            String file_data_line[] = file_data.split("\n");

            //number of lines in the file setting.properties
            int file_line = file_data_line.length;

            int count = 0;
            for(int i = 0; i<file_line ;i++)
            {    
                String temp2[] = file_data_line[i].split("\\s+");

                //extracting the values of variables from string array and storing in the respective variables.
                if(file_data_line[i].startsWith("#") == false)
                {
                    count++;

                    switch (count)
                    {
                        case 1:     window      = Integer.parseInt(temp2[2]);       break;
                        case 2:     cpu_dur_lim = Integer.parseInt(temp2[2]);       break;
                        case 3:     cpu_lim     = Integer.parseInt(temp2[2]);       break;
                        case 4:     mem_dur_lim = Integer.parseInt(temp2[2]);       break;
                        case 5:     mem_lim     = Integer.parseInt(temp2[2]);       break;
                        case 6:     email       = temp2[2];                         break;

                    }
                }    
            }

            /*
            System.out.println(window);
            System.out.println(cpu_dur_lim);
            System.out.println(cpu_lim);
            System.out.println(mem_dur_lim);
            System.out.println(mem_lim);
            System.out.println(email);
            */
            // --------------------------------------------------------------------------------------------------------



            
            // calling the beep function which will run 
            BeeperControl beep = new BeeperControl();
            beep.beepForAnHour(window, cpu_dur_lim, cpu_lim, mem_dur_lim, mem_lim, email);

            


    }


}


// runs the shell command and extracts all the values of cpu usage
class TopReader
{
    public HashMap<String,Double[]> foo1()
    {


        ShellCommandExecutor com = new ShellCommandExecutor();
        String top_details = com.exe_command("top -b -n1");

        //spliting with blank line
        String filter_detail[] = top_details.split("\n\n");

        //spliting each lines into different lines
        String line_detail[] = filter_detail[1].split("\n");

        //line_len is the count of (PID + 1)
        int line_len = line_detail.length;


        Double pid[] = new Double[line_len-1];  //stores PID        [0]
        Double cpu[] = new Double[line_len-1];  //stores %CPU       [8]
        Double mem[] = new Double[line_len-1];  //stores %MEM       [9]
        //Double time[] = new Double[line_len-1]; //stores TIME+      [10]
        //Double comm[] = new Double[line_len-1]; //stores COMMAND    [11]


        //storing all the values in the following strings
        for(int i=0; i<line_len-1 ;i++)
        {

            String temp3 = line_detail[i+1];

                int k=0;
                while(k < temp3.length())
                {
                    if(temp3.charAt(k) == ' ')
                        k++;
                    else
                    break;
                }
                temp3 = temp3.substring(k,temp3.length());


            String temp1[] = temp3.split("\\s+");
            //System.out.println(temp1[10]);
            //System.out.println(temp1[11]);
            pid[i] = Double.parseDouble(temp1[0]);
            cpu[i] = Double.parseDouble(temp1[8]);
            mem[i] = Double.parseDouble(temp1[9]);
            //time[i] = Double.parseDouble(temp1[10]);
            //comm[i] = Double.parseDouble(temp1[11]);
        }

        HashMap<String,Double[]> hm = new HashMap<String,Double[]>();  
        hm.put("pid",pid); 
        hm.put("cpu",cpu); 
        hm.put("mem",mem); 
        //hm.put("time",time); 
        //hm.put("comm",comm);  

        return hm;


        /*
        for(int i=0 ; i< line_len-1;i++)
        {
            System.out.println(cpu[i]);
        }
        */
    }
}


// runs the given functions periodically for certain amount of time
class BeeperControl 
{
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    
    HashMap<Double,Double[]> hm_final = new HashMap<Double,Double[]>();


    public void beepForAnHour(int window, int cpu_dur_lim, int cpu_lim, int mem_dur_lim, int mem_lim, String email) 
    {
        final Runnable beeper = new Runnable() 
        {
            public void run() 
            { 
                
                System.out.println("beep");

                HashMap<String,Double[]> hm = new HashMap<String,Double[]>();

                // getting return values of foo1 function and storing into respctive array
                TopReader read = new TopReader();
                hm = read.foo1();

                Double[] pid = hm.get("pid");
                Double[] cpu = hm.get("cpu");
                Double[] mem = hm.get("mem");
                //Double[] time = hm.get("time");
                //Double[] comm = hm.get("comm");

                Double exd_time[] = new Double[2];
                // [0] cpu 
                // [1] mem

                // for loop is checking weather the respective pid is in the hash map or not.
                // adding new pid and increasing their time count if they are exceeding the value.
                int pid_len = pid.length; 
                for( int j = 0 ; j<pid_len ;j++)
                {
                    if(cpu[j] > cpu_lim) //checking for cpu usage
                    {
                        if(hm_final.containsKey(pid[j]))
                        {
                            exd_time = hm_final.get(pid[j]);
                            exd_time[0] = exd_time[0] + 1;
                            exd_time[1] = exd_time[1];
                            hm_final.put(pid[j],exd_time);
                        }
                        else
                        {
                            exd_time[0] = 1.0;
                            exd_time[1] = 0.0;
                            hm_final.put(pid[j],exd_time);
                        }
                    }
                    if(mem[j] > mem_lim) //chekcing for memory usage
                    {
                        if(hm_final.containsKey(pid[j]))
                        {
                            exd_time = hm_final.get(pid[j]);
                            exd_time[0] = exd_time[0];
                            exd_time[1] = exd_time[1] + 1;
                            hm_final.put(pid[j],exd_time);
                        }
                        else
                        {
                            exd_time[0] = 0.0;
                            exd_time[1] = 1.0;
                            hm_final.put(pid[j],exd_time);
                        }
                    }

                }

                




                // itterating through hash map and chacking for excceded time duration.

                Map<Double,Double[]> m_final = new HashMap<Double,Double[]>(hm_final);

                for (Map.Entry<Double,Double[]> entry : m_final.entrySet())
                {
                    SendMailBySite mail = new SendMailBySite();

                    exd_time =  entry.getValue();
                    Double temp4 = entry.getKey();
                    // System.out.println(exd_time[0]);
                    // System.out.println(exd_time[1]);
                    if(exd_time[0] > cpu_dur_lim)
                    {
                        System.out.println("cpu excceded");
                        exd_time[0] = 0.0;
                        hm_final.put(temp4,exd_time);
                        mail.sender(1, email);

                    }
                    if(exd_time[1] > mem_dur_lim)
                    {
                        System.out.println("memory excceded");
                        exd_time[1] = 0.0;
                        hm_final.put(temp4,exd_time);
                        mail.sender(2, email);
                    }
                }


            }
        };
        final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 1, 1, SECONDS);
        scheduler.schedule
        (
            new Runnable() 
            {
                public void run() { beeperHandle.cancel(true); }
            }, window, SECONDS
        );

    }
}


class SendMailBySite {  
 public void sender(int ind, String email) 
 {  
  

        String sub_email[] = email.split(",");
        int count_email = sub_email.length;

        /*for(int j = 0; j<count_email;j++)
        {
            System.out.println(sub_email[j]);
        }*/
        //System.out.print(count_email);

        final String username = "shreyanshushekhar7@gmail.com";   //change accordinglly
        final String password = "123456789abcd";                //change accordinglly

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

       

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });

        try {

           
            Message message = new MimeMessage(session);
          
            message.setFrom(new InternetAddress("2016csb1060@iitrpr.ac.in"));
           
            for(int j = 0; j<count_email;j++)
            {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sub_email[j]));
            }

            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("shreyanshushekhar007@gmail.com"));
           
            message.setSubject("ping");
           
            if(ind == 1)
            {
                message.setText("cpu limit excceded");
            }
            else if(ind == 2)
            {
                message.setText("memory limit excceded");
            }

            Transport.send(message);

            System.out.println("message sent successfully...");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } 
 }  
}  


class ShellCommandExecutor 
{
    public String exe_command(String command) 
    {
        StringBuffer output = new StringBuffer();

        Process p;
        try 
        {
            p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";           
            while ((line = reader.readLine())!= null) 
            {
                output.append(line + "\n");
            }

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        return output.toString();
    }
}






