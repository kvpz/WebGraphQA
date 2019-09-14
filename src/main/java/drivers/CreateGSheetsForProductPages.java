package drivers;

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
import com.google.api.services.sheets.v4.model.*;
import com.wg.MyUtil;
import com.wg.SheetsQuickstart;
import com.wg.WebGraphFile;
import gstore.Page;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Create Google Sheets for each product page
 * The Sheet for each page will have the URL slug as its name
 */
public class CreateGSheetsForProductPages {
    static NetHttpTransport HTTP_TRANSPORT;
    static String spreadsheetId;
    private static Sheets service;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS,SheetsScopes.DRIVE);

    private static Sheets establishConnection() {
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

    public static void sheetSetup() {
        try {
            // Build a new authorized API client service.
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            // Goldmine_Gstore spreadsheet
            spreadsheetId = "1QP-6zhtAJeOHtL2tTHyf_clrXe4haRDgIyU6Y2tEr1k";

            service = establishConnection();

        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    public static void createSheet(String sheetname) {


    }

    /**
     * Create sheet for page
     * @param args
     */
    public static String getUrlSlug(Page page) {
        // create url from page directory name
        String url = MyUtil.CreateURLFromWebpageDirName(page.getDirectory().getName());

        // extract url slug
        String[] urlArr = url.split("/");

        return urlArr[urlArr.length - 1];
    }


    public static void main(String[] args) throws IOException {

        sheetSetup();
        TreeSet<String> sheetNames = new TreeSet<>();

        // iterate through all product pages
        for(File pageDir : WebGraphFile.GetProductPages()) {
            Page page = new Page(pageDir);
            String urlSlug = getUrlSlug(page);
            sheetNames.add(urlSlug);
        }

        List<Request> requests = new ArrayList<>();

        for(String urlSlug : sheetNames) {
            System.out.println(urlSlug);

            //Sheet sheet = new Sheet();
            SheetProperties properties = new SheetProperties();
            properties.setTitle(urlSlug);
            GridProperties gridProperties = new GridProperties();
            gridProperties.setRowCount(100);
            gridProperties.setColumnCount(100);
            properties.setGridProperties(gridProperties);
            //sheet.setProperties(properties);
            AddSheetRequest addSheetRequest = new AddSheetRequest();
            addSheetRequest.setProperties(properties);
            requests.add(new Request().setAddSheet(addSheetRequest));

        }

        BatchUpdateSpreadsheetRequest updateRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);

        service.spreadsheets().batchUpdate(spreadsheetId, updateRequest).execute();

    }
}
