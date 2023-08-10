package org.example;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    static Messages messages = new Messages();

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        String ctyStateKey = "";

        System.out.println(messages.USA);

        System.out.println(messages.CTY_STATE_DIRECTORY);
        String ctyStateDirectory = scanner.nextLine();

        System.out.println(messages.ZIP4_DIRECTORY);
        String zip4Directory = scanner.nextLine();

        System.out.println(messages.INPUT_ADDRESS);
        System.out.println(messages.ADDRESS_ELEMENTS);
        String input = scanner.nextLine();

        Address address = new Address(
                input.split(",")[0].toUpperCase().trim(),
                input.split(",")[1].toUpperCase().trim(),
                input.split(",")[2].toUpperCase().trim(),
                input.split(",")[3].toUpperCase().trim(),
                input.split(",")[4].toUpperCase().trim(),
                input.split(",")[5].toUpperCase().trim()
        );


        Set<File> files = getAllFilesInDirectory(ctyStateDirectory);
        for (File file : files) {

            BufferedReader br = new BufferedReader(new FileReader(file));

            ctyStateKey = readCtyStateFilesLines(address, br, file, ctyStateKey);
        }


        Set<File> zip4Files = getAllFilesInDirectory(zip4Directory);
        List<String> records = new ArrayList<>();
        String buildingFirmRecord = "";

        String streetName = extractStreetNameFromGivenAddress(address);
        boolean recordWithBuildingFirmNameFound = false;
        for (File file : zip4Files) {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String streetSuffixAbr = findStreetAbbreviation(address.getStreetName());
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                if(!(currentLine.length() > 30)){
                    continue;
                }

                String currentPostCode = currentLine.substring(1, 6);
                if (currentPostCode.startsWith(" ")) {
                    continue;
                }

                String currentStreet = currentLine.substring(24, 52).trim();

                String buildingFirmName = "";
                if(!address.getBuildingFirmName().isEmpty()) {
                    buildingFirmName = address.getBuildingFirmName().toUpperCase().trim();
                }

                boolean sameStreetName = currentStreet.equals(streetName.trim().toUpperCase());
                boolean samePostCode = currentPostCode.equals(address.getPostCode());
                boolean sameStyStateKey = currentLine.endsWith(ctyStateKey);
                boolean containsStreetSuffixAbbreviation = false;

                if(!streetSuffixAbr.isEmpty()) {
                    containsStreetSuffixAbbreviation = currentLine.contains(streetSuffixAbr);
                } else if (streetSuffixAbr.isEmpty()) {
                    containsStreetSuffixAbbreviation = true;
                }

                if (samePostCode
                        && sameStyStateKey
                        && containsStreetSuffixAbbreviation
                        && sameStreetName
                        && currentLine.contains(buildingFirmName)) {
                    buildingFirmRecord = currentLine;
                    recordWithBuildingFirmNameFound= true;
                    break;
                } else if (samePostCode
                        && sameStyStateKey
                        && containsStreetSuffixAbbreviation
                        && sameStreetName) {
                    records.add(currentLine);
                    System.out.println(currentLine);
                    containsStreetSuffixAbbreviation = false;
                }


            }
            if (recordWithBuildingFirmNameFound) {
                break;
            }
        }
        if (recordWithBuildingFirmNameFound) {
            System.out.println(buildingFirmRecord);
        } else {
            records.forEach(e -> System.out.println());
        }


    }

    private static String extractStreetNameFromGivenAddress(Address address) {
        StringBuilder strName = new StringBuilder();
        String wholeStreetName = address.getStreetName().toUpperCase().trim();
        for (int i = 0; i < wholeStreetName.length(); i++) {
            if (Character.isLetter(wholeStreetName.charAt(i))){
                strName.append(wholeStreetName.charAt(i));
            } else {
                strName.toString();
                break;
            }
        }
        return strName.toString();
    }

    private static String findStreetAbbreviation(String streetName) {
        String abbr;
        if (streetName.contains("RD")){
            return abbr = "RD";
        } else if (streetName.contains("ST")){
            return abbr = "ST";
        } else if (streetName.contains("WAY")){
            return abbr = "ST";
        } else if (streetName.contains("EXT")) {
            return abbr = "EXT";
        } else if (streetName.contains("ALY")) {
            return abbr = "ALY";
        } else if (streetName.contains("AVE")) {
            return abbr = "AVE";
        } else if (streetName.contains("BLVD")) {
            return abbr = "BLVD";
        } else if (streetName.contains("CSWY")) {
            return abbr = "CSWY";
        } else if (streetName.contains("CTR")) {
            return abbr = "CTR";
        } else if (streetName.contains("CIR")) {
            return abbr = "CIR";
        } else if (streetName.contains("CT")) {
            return abbr = "CT";
        } else if (streetName.contains("CV")) {
            return abbr = "CV";
        } else if (streetName.contains("DR")) {
            return abbr = "DR";
        } else if (streetName.contains("EXPY")) {
            return abbr = "EXPY";
        } else if (streetName.contains("HWY")) {
            return abbr = "HWY";
        } else if (streetName.contains("LN")) {
            return abbr = "LN";
        } else if (streetName.contains("PKWY")) {
            return abbr = "PKWY";
        } else if (streetName.contains("TER")) {
            return abbr = "TER";
        } else if (streetName.contains("TRL")) {
            return abbr = "TRL";
        }

        return null;
    }

    private static String readCtyStateFilesLines(Address address,
                                               BufferedReader br,
                                               File file, String key) throws IOException {

        String currentLine;
        while ((currentLine = br.readLine()) != null) {

            if (currentLine.length() <= 6  || currentLine.contains("COPYRIGHT") || currentLine.contains("USPS")) {
                continue;
            }


            String currentPostCode = currentLine.substring(1, 6);
            if (currentPostCode.startsWith(" ")) {
                continue;
            }

            boolean samePostCode = currentPostCode.equals(address.getPostCode());
            boolean containCity = currentLine.contains(address.getCityName().toUpperCase());

            if (containCity && samePostCode && currentLine.startsWith("D")) {
                key = currentLine.substring(6, 12);
                System.out.println("Record found in " + file);
                System.out.println(currentLine);
                System.out.println("ctyStateKey: " + key);
            }
        }
        return key;
    }

    private static Set<File> getAllFilesInDirectory(String directory) {
        return Stream.of(Objects.requireNonNull(new File(directory).listFiles()))
                .filter(file -> !file.isDirectory())
                .filter(file -> !FilenameUtils.isExtension(file.getName(),"tmp"))
                .collect(Collectors.toSet());

    }
}