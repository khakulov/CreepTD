/**
CreepTD is an online multiplayer towerdefense game
formerly created under the name CreepSmash as a project
at the Hochschule fuer Technik Stuttgart (University of Applied Science)

CreepTD (Since version 0.7.0+) Copyright (C) 2011 by
 * Daniel Wirtz, virtunity media
http://www.creeptd.com

CreepSmash (Till version 0.6.0) Copyright (C) 2008 by
 * Andreas Wittig
 * Bernd Hietler
 * Christoph Fritz
 * Fabian Kessel
 * Levin Fritz
 * Nikolaj Langner
 * Philipp Schulte-Hubbert
 * Robert Rapczynski
 * Ron Trautsch
 * Sven Supper
http://creepsmash.sf.net/

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package com.creeptd.client.network;

/** try to determine MAC address of local network card; this is done
using a shell to run ifconfig (linux) or ipconfig (windows). The
output of the processes will be parsed.

<p>

To run the whole thing, just type java FindMac

<p>

Current restrictions:

<ul>
<li>Will probably not run in applets

<li>Tested Windows / Linux / Mac OSX only

<li>Tested J2SDK 1.4 only

<li>If a computer has more than one network adapters, only
one MAC address will be returned

<li>will not run if user does not have permissions to run
ifconfig/ipconfig (e.g. under linux this is typically
only permitted for root)
</ul>

 */
import java.net.InetAddress;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public final class FindMac {

    public final static String getMacAddress() {
        String mac = "";
        String os = System.getProperty("os.name");

        try {
            if (os.startsWith("Windows")) {
                mac = windowsParseMacAddress(windowsRunIpConfigCommand());
            } else if (os.startsWith("Linux")) {
                mac = linuxParseMacAddress(linuxRunIfConfigCommand());
            } else if (os.startsWith("Mac OS X")) {
                mac = osxParseMacAddress(osxRunIfConfigCommand());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mac;
    }

    /*
     * Linux stuff
     */
    private final static String linuxParseMacAddress(String ipConfigResponse)
            throws ParseException {
        String localHost = null;
        try {
            localHost = InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage(), 0);
        }

        StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
        String lastMacAddress = null;

        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();
            boolean containsLocalHost = line.indexOf(localHost) >= 0;

            // see if line contains IP address
            if (containsLocalHost && lastMacAddress != null) {
                return lastMacAddress;
            }

            // see if line contains MAC address
            int macAddressPosition = line.indexOf("HWaddr");
            if (macAddressPosition <= 0) {
                continue;
            }

            String macAddressCandidate = line.substring(macAddressPosition + 6).trim();
            if (linuxIsMacAddress(macAddressCandidate)) {
                lastMacAddress = macAddressCandidate;
                continue;
            }
        }

        ParseException ex = new ParseException("cannot read MAC address for " + localHost + " from [" + ipConfigResponse + "]", 0);
        ex.printStackTrace();
        throw ex;
    }

    private final static boolean linuxIsMacAddress(String macAddressCandidate) {
        Pattern macPattern = Pattern.compile("[0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}");
        Matcher m = macPattern.matcher(macAddressCandidate);
        return m.matches();
    }

    private final static String linuxRunIfConfigCommand() throws IOException {
        Process p = Runtime.getRuntime().exec("ifconfig");
        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

        StringBuffer buffer = new StringBuffer();
        for (;;) {
            int c = stdoutStream.read();
            if (c == -1) {
                break;
            }
            buffer.append((char) c);
        }
        String outputText = buffer.toString();

        stdoutStream.close();

        return outputText;
    }

    /*
     * Windows stuff
     */
    private final static String windowsParseMacAddress(String ipConfigResponse)
            throws ParseException {
        String localHost = null;
        try {
            localHost = InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage(), 0);
        }

        StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");
        String lastMacAddress = null;

        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();

            // see if line contains IP address
            if (line.contains(localHost) && lastMacAddress != null) {
                return lastMacAddress;
            }

            // see if line contains MAC address
            int macAddressPosition = line.indexOf(":");
            if (macAddressPosition <= 0) {
                continue;
            }

            String macAddressCandidate = line.substring(macAddressPosition + 1).trim();
            if (windowsIsMacAddress(macAddressCandidate)) {
                lastMacAddress = macAddressCandidate;
                continue;
            }
        }

        ParseException ex = new ParseException("cannot read MAC address from [" + ipConfigResponse + "]", 0);
        ex.printStackTrace();
        throw ex;
    }

    private final static boolean windowsIsMacAddress(String macAddressCandidate) {
        Pattern macPattern = Pattern.compile("[0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}");
        Matcher m = macPattern.matcher(macAddressCandidate);
        return m.matches();
    }

    private final static String windowsRunIpConfigCommand() throws IOException {
        Process p = Runtime.getRuntime().exec("ipconfig /all");
        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());

        StringBuffer buffer = new StringBuffer();
        for (;;) {
            int c = stdoutStream.read();
            if (c == -1) {
                break;
            }
            buffer.append((char) c);
        }
        String outputText = buffer.toString();

        stdoutStream.close();

        return outputText;
    }

    /*
     * Mac OS X Stuff
     */
    private final static String osxParseMacAddress(String ipConfigResponse)
            throws ParseException {
        String localHost = null;

        try {
            localHost = InetAddress.getLocalHost().getHostAddress();
        } catch (java.net.UnknownHostException ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage(), 0);
        }

        StringTokenizer tokenizer = new StringTokenizer(ipConfigResponse, "\n");

        while (tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();
            //boolean containsLocalHost = line.indexOf(localHost) >= 0;
            // see if line contains MAC address
            int macAddressPosition = line.indexOf("ether");
            if (macAddressPosition != 0) {
                continue;
            }
            String macAddressCandidate = line.substring(macAddressPosition + 6).trim();
            if (osxIsMacAddress(macAddressCandidate)) {
                return macAddressCandidate;
            }
        }

        ParseException ex = new ParseException("cannot read MAC address for " + localHost + " from [" + ipConfigResponse + "]", 0);
        ex.printStackTrace();
        throw ex;
    }

    private final static boolean osxIsMacAddress(String macAddressCandidate) {
        Pattern macPattern = Pattern.compile("[0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}[-:][0-9a-fA-F]{2}");
        Matcher m = macPattern.matcher(macAddressCandidate);
        return m.matches();
    }

    private final static String osxRunIfConfigCommand() throws IOException {
        Process p = Runtime.getRuntime().exec("ifconfig");
        InputStream stdoutStream = new BufferedInputStream(p.getInputStream());
        StringBuffer buffer = new StringBuffer();
        for (;;) {
            int c = stdoutStream.read();
            if (c == -1) {
                break;
            }
            buffer.append((char) c);
        }
        String outputText = buffer.toString();
        stdoutStream.close();
        return outputText;
    }

    /*
     * Main
     */
    public final static void main(String[] args) {
        try {
            System.out.println("Network info:");

            System.out.println("  Operating System: " + System.getProperty("os.name"));
            System.out.println("  IP/Localhost: " + InetAddress.getLocalHost().getHostAddress());
            System.out.println("  MAC Address: " + getMacAddress());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
