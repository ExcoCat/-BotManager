public class Account {
    String nickname="", email="", password="", pin="", script="", proxyIp="", proxyPort="", proxyUsername="", proxyPassword="", launchParams="", javaParams="";
    private String seperator = "|@|";
    public Account(String[] details) {
        nickname = details[0];
        script = details[1];
        email = details[2];
        password = details[3];
        pin = details[4];
        proxyIp = details[5];
        proxyPort = details[6];
        proxyUsername = details[7];
        proxyPassword = details[8];
        launchParams = details[9];
        javaParams = details[10];
    }
    public Account() {

    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(nickname);
        b.append(seperator);
        b.append(script);
        b.append(seperator);
        b.append(email);
        b.append(seperator);
        b.append(password);
        b.append(seperator);
        b = pin.equals("") ? b.append("null") : b.append(pin);
        b.append(seperator);
        b = proxyIp.equals("") ? b.append("null") : b.append(proxyIp);
        b.append(seperator);
        b = proxyPort.equals("") ? b.append("null") : b.append(proxyPort);
        b.append(seperator);
        b = proxyUsername.equals("") ? b.append("null") : b.append(proxyUsername);
        b.append(seperator);
        b = proxyPassword.equals("") ? b.append("null") : b.append(proxyPassword);
        b.append(seperator);
        b = launchParams.equals("") ? b.append("null") : b.append(launchParams);
        b.append(seperator);
        b = javaParams.equals("") ? b.append("null") : b.append(javaParams);

        return b.toString();
    }
}
