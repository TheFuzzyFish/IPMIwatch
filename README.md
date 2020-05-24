<h1>IPMIwatch</h1>
<body>
  <h2>Summary</h2>
  <p>IPMIwatch is a lightweight Java daemon to monitor sensors on an IPMI-capable system and notify you should the sensors (particularly the fans or temperatures) show readings above predefined thresholds. As of now, the only alerting mechanism (beyond reporting to the console or a log file) is through Discord webhooks, but you're certainly welcome to add more alerting protocols! (if you're interested in that, check out the docs on alertAbstract.java)</p>
  <br>
  <h2>Installation</h2>
  <p>You can either clone this repository and compile the code yourself (not recommended), or you can just download my precompiled .jar file (recommended) from <a href="https://github.com/TheFuzzyFish/IPMIwatch/releases">the latest release.</a>
  IPMIwatch doesn't do all the heavy lifting on its own though, you're going to need to install <a href="https://github.com/ipmitool/ipmitool">ipmitool</a> as well. IPMIwatch was developed under ipmitool v1.8.18, and I'll do my best to update it if the format changes.</p>
  <br>
  <h2>Usage</h2>
  <pre>
IPMIwatch is a program to monitor IPMI-capable sensors and notify you in real-time should something exceed a specified threshold.
It has the ability to send alerts via the Discord Webhook API should you choose to select that option. I made the alerting mechanism
as modular as possible, so feel free to add a new alerting module if you're interested!

Usage:
    java -jar IPMIwatch.jar <options>

Examples:
    java -jar IPMIwatch.jar --temp 55 --fan 40
    java -jar IPMIwatch.jar --temp 60 --log /var/log/IPMIwatch-log.txt
    java -jar IPMIwatch.jar --fan 30 --alert https://discordapp.com/api/webhooks/136156426833869378/VMfizhohu7t0UHkfcu9xUqPGJ8rMv8PAWD36HHfkBBFBOa6N9JQfWs8iSen4J33P01Wr

Options:
    -h, -H, --help      Displays this handy dandy help file!
    -v, -V, --version   Displays program version info
    -t, -T, --temp      Sets the threshold temperature (C) when action should be taken
    -f, -F, --fan       Sets the threshold fan speed (%) when action should be taken
    --log <path>        Logs events to a file with timestamps
    --alert <address>   Generates an alert should sensors exceed thresholds.
                        This can currently only be a Discord Webhook URL, but we may
                        also support email addresses here in the future. To use that,
                        enter the Webhook URL as the alert address
</pre>
<p>By default, IPMIwatch will simply print out to the console when a sensor exceeds your set threshold, but you can also redirect that to a log file with the <pre>--log</pre> flag or issue an <pre>--alert</pre>.</p>
<br>
<h2>Alerts</h2>
<p>So as of right now, the only alerting mechanism are Discord Webhooks. Why? Because that's what I use to monitor my infrastructure. If you use something else (like SMTP) and want to add that alerting mechanism, I did my best to make the alerts as modular as possible, and you're welcome to submit a pull request with your changes! A good place to get started is alertAbstract.java
IPMItool will attempt to suppress repeated alerts like you would see printed out in the console, but if your sensors fluctuate rapidly, you may still recieve several alerts.
The Discord bot that sends you a message will have the username of your server's hostname, unless it can't be determined, in which case it will default to "IPMItool."</p>
</body>
