# CremlinChat))
thick client for two person RSA encrypted instant chat.
Launch it as server and wait for your partner to connect (you should provide your partner with IP and port that are configured in config.properties file. Dont't forget to type your nick name to config.properties file).
Launch it as client and connect to your waiting partner (ensure proper IP and port are configured in config.properties file)
Your messages are defended by RSA 2048 bit key. New pair of RSA keys (public and private) are generated for every new chat session.
Regret, it doesn't yet work with proxy(\r\n
Known issue: while using chat through standard command prompt window message typed using ciryllic symbols comes corrupted to chat partner, though in IDE console ciryllic mesages are displayed right. 
