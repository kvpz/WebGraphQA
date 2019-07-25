package gstore.products;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.wg.SheetsQuickstart;
import com.wg.Timer;
import gstore.Products;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Interface for the products sheet under the spreadsheet Goldmine_GStore
 *
 *
 */
public class SpreadsheetIO {

    NetHttpTransport HTTP_TRANSPORT;
    String spreadsheetId;
    private static Sheets service;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS,SheetsScopes.DRIVE); //Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static List<List<Object>> testTrackUrls;
    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = SheetsQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("kevin.perez@foxholeqa.com");
    }

    /**
     * Get the products listed in the XML file.  This defining information, i.e. the source of correctness.
     * @return
     */
    public Products getProductsFromXML() {
        Products products = null;

        try {
            JAXBContext xml_products = JAXBContext.newInstance(Products.class);
            Unmarshaller unmarshalled_products = xml_products.createUnmarshaller();
            products = (Products) unmarshalled_products.unmarshal(Products.GetXMLFile());
        }
        catch(Exception e) {
            System.out.println(e);
        }

        return products;
    }

    @Before
    public void Before() {
        try {
            // Build a new authorized API client service.
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            // Goldmine_Gstore spreadsheet
            //spreadsheetId = "1QP-6zhtAJeOHtL2tTHyf_clrXe4haRDgIyU6Y2tEr1k";
            // TestTrack_GStore spreadsheet
            spreadsheetId = "1wxtYsECO4-huFPGru1T7ymqECxnSdJ4SotD85GbWOXc";
            service = establishConnection();

        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    private Sheets establishConnection() {
        Sheets service = null;
        try {
            service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .build();
        }
        catch(Exception e) {
            System.out.println(e);
        }

        return service;
    }

    /**
     *
     * @param range range in A1 format
     * @param valueRange encapsulates List<List<Object>> containing cell values
     * @return
     */
    private UpdateValuesResponse updateSheet(String range, ValueRange valueRange) {
        UpdateValuesResponse response = null;

        try {
            response = service.spreadsheets().values()
                    .update(spreadsheetId, range, valueRange)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
        }
        catch(Exception e) {
            System.out.println(e);
        }

        return response;
    }

    /**
     * Uploads and overwrites products to the range A2:Z1000 of Products sheet
     */
    @Test
    public void uploadProductsFromXMLToSheets() {

        String range = "Products!A2:Z1000";
        try {
            // Get products from XML
            Products prods = getProductsFromXML();
            List<List<Object>> products_values = new ArrayList<List<Object>>();
            for(Product prod : prods.getProducts()) {
                products_values.add(prod.toList());
            }
            // Initialize object that will carry values to sheet
            ValueRange goldmine = new ValueRange().setValues(products_values);

            System.out.println(updateSheet(range, goldmine));
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    /** For spreadsheet TestTrack_GStore **/

    @Test
    public void getUrlsFromTestTrack() {
        String range = "Legend!A2:A2000";

        try {
            Sheets.Spreadsheets.Values.Get g = service.spreadsheets().values().get(spreadsheetId, range);
            ValueRange result = g.execute();
            testTrackUrls = result.getValues();
        }
        catch(Exception e) {
            System.out.println(e);
            System.out.println("Exception in SpreadsheetIO::getUrlsFromTestTrack()");
        }
    }

    /**
     * Update the list of URLs in the TestTrack spreadsheet.
     */
    public void updateTestTrackUrls() {

    }

    /**
     * Updload the results of the diff test of the copy across the website.
     */
    @Test
    public void uploadCopyDiffTestResults() throws IOException {
        Timer timer = new Timer();
        final int copyColumn = 1; // the column for the copy diff results
        final String range = "DiffTest!A:B";
        // iterate through the copy diff report
        File reportFile = new File("TestResults/CopyDiffWebGraph_2019_7_25");

        ValueRange valueRange = service.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> values = valueRange.getValues();

        for(String line : Files.readAllLines(Paths.get(reportFile.toURI()))) {
            String[] reportLine = line.split(" ");
            String url = reportLine[0];
            String diffResult = reportLine[1];

            // find the row for the page in the remote google sheet
            for(List<Object> row : values) {
                // get the cell in the row corresponding to the copy diff column
                if(row.get(0).equals(url)) {
                    // add to array of values that will be uploaded
                    row.add(copyColumn,  diffResult);
                }
            }

        }

        // batch update
        valueRange = new ValueRange().setValues(values);
        service.spreadsheets().values().update(spreadsheetId, range, valueRange)
                .setValueInputOption("RAW")
                .execute();

        System.out.println("Copy diff results uploaded. Duration: ");
        timer.End();
    }
}
