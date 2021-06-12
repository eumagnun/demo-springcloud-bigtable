package com.danielamaral.gft.demogcpbt.controller;

import com.danielamaral.gft.demogcpbt.configuration.Configuration;
import com.danielamaral.gft.demogcpbt.model.Asset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/api")
public class AssetController {

    @Autowired
    Configuration configuration;



    @GetMapping("/asset/{assetKey}")
    public Asset getAssetsByKey(@PathVariable String assetKey) {
        return new Asset();
    }

    @GetMapping("/asset/{assetKey}/start/{start}/end/{end}")
    public List<Asset> getAssetByRange(@PathVariable String assetKey, @PathVariable String start, @PathVariable String end) {

        List<Asset> assetList = new ArrayList<>();
        return assetList;
    }

    @PostMapping("/asset/load")
    public void loadAsset() {
    }

    @GetMapping("/admin/config")
    public String getConfiguration() {
        return configuration.toString();
    }

    @PostMapping("/admin/table")
    public void createTable() {

    }
}