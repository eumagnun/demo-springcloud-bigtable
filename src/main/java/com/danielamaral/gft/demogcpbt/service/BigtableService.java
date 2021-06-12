package com.danielamaral.gft.demogcpbt.service;

import com.danielamaral.gft.demogcpbt.configuration.Configuration;
import com.danielamaral.gft.demogcpbt.model.Asset;
import com.google.api.gax.rpc.ServerStream;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminSettings;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BigtableService {

    @Autowired
    private Configuration  configuration;

    private BigtableDataClient bigtableDataClient;
    private BigtableTableAdminClient bigtableTableAdminClient;
    private List<Asset> assetList = new ArrayList<>();

    private  BigtableDataClient createDataClient() {

        BigtableDataClient dataClient = null;
        try {
            dataClient = BigtableDataClient.create(configuration.getBtProjectId(), configuration.getBtInstanceId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataClient;
    }

    private BigtableTableAdminClient createAdminClient() {
        try {
            BigtableTableAdminSettings adminSettings =
                    BigtableTableAdminSettings.newBuilder()
                            .setProjectId(configuration.getBtProjectId())
                            .setInstanceId(configuration.getBtInstanceId())
                            .build();

            return BigtableTableAdminClient.create(adminSettings);
        } catch (IOException e) {
            System.out.println("Error createAdmin");
            e.printStackTrace();
        }
        return null;
    }

    private void initClients(){
        if(this.bigtableDataClient==null) {
            this.bigtableDataClient = createDataClient();
        }

        if(this.bigtableTableAdminClient == null) {
            this.bigtableTableAdminClient = createAdminClient();
        }
    }

    public void createTable() {
        initClients();
        try {
            this.bigtableTableAdminClient.createTable(
                    CreateTableRequest.of(configuration.getBtTable())
                            .addFamily(configuration.getBtFamilyName())
            );
        } catch (Exception e) {
            System.out.println("Error createTable");
            e.printStackTrace();
        }
    }

    public void loadData() {
        initClients();
        generateDummyData();
        BulkMutation bulkMutation = BulkMutation.create(configuration.getBtTable());

        try {

            int i = 0;
            for(Asset asset:assetList ){
                bulkMutation.add(
                        asset.getKey(),
                        Mutation.create()
                                .setCell(
                                        configuration.getBtFamilyName(),
                                        Asset.BT_COL_NAME,
                                        asset.getName())
                                .setCell(
                                        configuration.getBtFamilyName(),
                                        Asset.BT_COL_DATE,
                                        asset.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                                .setCell(
                                        configuration.getBtFamilyName(),
                                        Asset.BT_COL_OPEN_PRICE,
                                        String.valueOf(asset.getOpenPrice()))
                                .setCell(
                                        configuration.getBtFamilyName(),
                                        Asset.BT_COL_CLOSE_PRICE,
                                        String.valueOf(asset.getClosePrice()))
                                .setCell(
                                        configuration.getBtFamilyName(),
                                        Asset.BT_COL_VARIATION,
                                        String.valueOf(asset.getVariation()))

                );


            }

            this.bigtableDataClient.bulkMutateRows(bulkMutation);


        } catch (Exception e) {
            System.out.println("Error writeBatch");
            e.printStackTrace();
        }
    }

    private void generateDummyData(){

        assetList.add(new Asset("MGLU3-20200117","MGLU3", LocalDate.of(2020,1,17),140.0,120.0,0));
        assetList.add(new Asset("MGLU3-20201111","MGLU3", LocalDate.of(2020,11,11),130.0,120.0,0));
        assetList.add(new Asset("MGLU3-20201130","MGLU3", LocalDate.of(2020,11,30),160.0,120.0,0));
        assetList.add(new Asset("MGLU3-20201030","MGLU3", LocalDate.of(2020,10,30),190.0,120.0,0));
        assetList.add(new Asset("MGLU3-20201130","MGLU3", LocalDate.of(2020,11,30),133.0,120.0,0));

        assetList.add(new Asset("PETR4-20211011","PETR4", LocalDate.of(2021,10,11),177.0,120.0,0));
        assetList.add(new Asset("PETR4-20211021","PETR4", LocalDate.of(2021,10,21),178.0,120.0,0));
        assetList.add(new Asset("PETR4-20210913","PETR4", LocalDate.of(2021,9,13),111.0,120.0,0));
        assetList.add(new Asset("PETR4-20210914","PETR4", LocalDate.of(2021,9,14),123.0,120.0,0));
        assetList.add(new Asset("PETR4-20211115","PETR4", LocalDate.of(2021,11,15),144.0,120.0,0));
        assetList.add(new Asset("PETR4-20211116","PETR4", LocalDate.of(2021,11,16),199.0,120.0,0));

    }

    public List<Asset> getRecordsByKeyRange(String start, String end) {
        initClients();
        List<Asset> assets = new ArrayList<>();
        try {
            Query query = Query.create(configuration.getBtTable()).range(start, end);
            ServerStream<Row> rows = this.bigtableDataClient.readRows(query);

            for (Row row : rows) {
                assets.add(parseRow(row));
            }
        } catch (Exception e) {
            System.out.println("Error getRecordsByKeyRange");
            e.printStackTrace();
        }

        return assets;
    }

    private Asset parseRow(Row row) throws ParseException {

        Asset asset = new Asset();
        asset.setKey(row.getKey().toStringUtf8());

        for (RowCell cell : row.getCells()) {

            switch(cell.getQualifier().toStringUtf8()) {
                case Asset.BT_COL_CLOSE_PRICE:
                    asset.setClosePrice(Double.parseDouble(cell.getValue().toStringUtf8()));
                    break;
                case Asset.BT_COL_DATE:
                    LocalDate date = LocalDate.parse(cell.getValue().toStringUtf8(),DateTimeFormatter.ofPattern("yyyyMMdd"));
                    asset.setDate(date);
                    break;
                case Asset.BT_COL_NAME:
                    asset.setName(cell.getValue().toStringUtf8());
                    break;
                case Asset.BT_COL_OPEN_PRICE:
                    asset.setOpenPrice(Double.parseDouble(cell.getValue().toStringUtf8()));
                    break;
                case Asset.BT_COL_VARIATION:
                    asset.setVariation(Double.parseDouble(cell.getValue().toStringUtf8()));
                    break;
            }
        }

        return asset;
    }

}
